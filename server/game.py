
import json
import Queue
import uuid

import webapp2

from google.appengine.api import background_thread
from google.appengine.ext import db


class Match(db.Model):
  match_time = db.DateTimeProperty(auto_now_add=True)
  player_one_id = db.StringProperty()
  player_two_id = db.StringProperty()
  player_one_choice = db.StringProperty()
  player_two_choice = db.StringProperty()
  winner = db.IntegerProperty()


# Wait 7 seconds for a response from the background threads. This needs to be less than 10 seconds, because that's
# when App Engine will kill us anyway.
MAX_WAIT_TIME_SEC = 7




matchmaker_queue = Queue.Queue()

class FindOpponentPage(webapp2.RequestHandler):
  def get(self):
    self.response.headers['Content-Type'] = 'text/plain'

    # We pass this queue to the match-maker thread, to notify with details of our opponent.
    response_queue = Queue.Queue()

    request = {'response_queue': response_queue,
               'id': str(uuid.uuid4())
               }
    matchmaker_queue.put(request)

    try:
      response = response_queue.get(True, MAX_WAIT_TIME_SEC)
      # we got an opponent! write out their details to the response and we're done.
      self.response.write(json.dumps(response));
    except:
      # No opponent within 7 seconds, enqueue again to notify the matchmaker we're giving up
      # and then end the response
      request['abandoned'] = True
      matchmaker_queue.put(request)
      self.response.write('ERR:NO-OPPONENT')

def matchmaker():
  """This is a background thread which waits for two requests to come in. Once we've got two requests, we will
  return both of them with information about each other so that a game can begin."""
  first_opponent = None
  second_opponent = None
  while True:
    first_opponent = matchmaker_queue.get()
    second_opponent = matchmaker_queue.get()
    if 'abandoned' in second_opponent and second_opponent['abandoned']:
      # todo: make sure they're same opponent...
      continue

    match = Match()
    match.player_one_id = first_opponent['id']
    match.player_two_id = second_opponent['id']
    match.put()
    match_id = match.key().id()

    first_opponent['response_queue'].put({'match_id': match_id, 'your_id': first_opponent['id']})
    second_opponent['response_queue'].put({'match_id': match_id, 'your_id': second_opponent['id']})

matchmaker_thread = background_thread.BackgroundThread(target=matchmaker)
matchmaker_thread.start()




match_queue = Queue.Queue()

class MatchPage(webapp2.RequestHandler):
  def get(self, match_id):
    # TODO: disable 'GET' handling (it's just for testing)
    self.process(match_id, self.request.get('player_id'), self.request.get('choice'))

  def post(self, match_id):
    self.process(match_id, self.request.POST.get('player_id'), self.request.POST.get('choice'))

  def process(self, match_id, player_id, choice):
    response_queue = Queue.Queue()
    request = {'match_id': match_id, 'player_id': player_id, 'choice': choice,
               'response_queue': response_queue}
    match_queue.put(request)

    try:
      response = response_queue.get(True, MAX_WAIT_TIME_SEC)
      self.response.write(json.dumps(response));
    except:
      # no response yet, just write an error and get the client to request again.
      self.response.write('ERR:NO-RESPONSE')


def matchprocessor():
  matches = {}
  while True:
    request = match_queue.get()
    if request['match_id'] not in matches:
      match = {'match_id': request['match_id'], 'players': {request['player_id']: request['choice']},
               'response_queues': []}
      matches[request['match_id']] = match
    else:
      match = matches[request['match_id']]
      match['players'][request['player_id']] = request['choice']
    match['response_queues'].append(request['response_queue'])
    if len(match['players']) == 2:
      match_mdl = Match.get_by_id(long(match['match_id']))
      for player_id, choice in match['players'].iteritems():
        if match_mdl.player_one_id == player_id:
          match_mdl.player_one_choice = choice
        elif match_mdl.player_two_id == player_id:
          match_mdl.player_two_choice = choice
      match_mdl.put()
      response = {'players': match['players'], 'winner': 'TODO'}
      for response_queue in match['response_queues']:
        response_queue.put(response)
  #TODO: clean out matches of old matches every now & then


matchprocessor_thread = background_thread.BackgroundThread(target=matchprocessor)
matchprocessor_thread.start()

app = webapp2.WSGIApplication([
    ('/game/find-opponent', FindOpponentPage),
    ('/game/([0-9]+)', MatchPage)
  ], debug=True)
