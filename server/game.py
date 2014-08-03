
import json
import Queue
import uuid

import webapp2

from google.appengine.ext import db
from google.appengine.api import memcache


class Match(db.Model):
  match_time = db.DateTimeProperty(auto_now_add=True)
  player_one_id = db.StringProperty()
  player_two_id = db.StringProperty()


class MatchRound(db.Model):
  round = db.IntegerProperty()
  player_one_choice = db.StringProperty()
  player_two_choice = db.StringProperty()


class FindOpponentPage(webapp2.RequestHandler):
  def get(self):
    self.response.headers['Content-Type'] = 'text/plain'
    cache = memcache.Client()

    # check if there's already a match for this player
    player_id = self.request.get("player_id")
    match = cache.get("matches:" + player_id)
    if match:
      self.response.write(json.dumps(match))
      return

    # otherwise, check to see if there's another player waiting for a match
    while True:
      other_player_id = cache.gets("matchmaker")
      if other_player_id and other_player_id != player_id:
        # remove this match from cache to indicate that we're taking it
        if not cache.cas("matchmaker", False):
          continue
        match_mdl = Match()
        match_mdl.player_one_id = player_id
        match_mdl.player_two_id = other_player_id
        match_mdl.put()

        match = {'match_id': str(match_mdl.key().id()), 'player_one_id' : player_id, 'player_two_id': other_player_id}
        memcache.set('matches:' + player_id, match)
        memcache.set('matches:' + other_player_id, match)
        memcache.set('matches:' + str(match_mdl.key().id()), match)
        break
      else:
        if not cache.add("matchmaker", player_id, time=10):
          if not cache.cas("matchmaker", player_id, time=10):
            continue
        break

    if not match:
      self.response.write("ERR:NO-OPPONENT")
    else:
      self.response.write(json.dumps(match));


class MatchPage(webapp2.RequestHandler):
  def get(self, match_id):
    self.response.headers['Content-Type'] = 'text/plain'
    cache = memcache.Client()
    
    match = cache.get('matches:' + match_id)
    if not match:
      match_mdl = Match.get_by_id(int(match_id))
      if not match_mdl:
        self.error(404)
        return
      match = {'match_id': str(match_mdl.key().id()), 'player_one_id': match_mdl.player_one_id,
               'player_two_id': match_mdl.player_two_id}
      cache.set('matches:' + match_id, match)

    player_field = None
    player_id = self.request.get('player_id')
    if match['player_one_id'] == player_id:
      player_field = 'player_one'
    elif match['player_two_id'] == player_id:
      player_field = 'player_two'
    else:
      self.error(404)
      return
    player_choice = self.request.get('choice')

    match_round_no = int(self.request.get("round"))
    while True:
      match_round = cache.gets('match-round:' + match_id + ':' + str(match_round_no))
      if not match_round:
        match_round = {'match_id': match_id, 'round': match_round_no, player_field: {'id': player_id, 'choice': player_choice}}
        if not cache.add('match-round:' + match_id + ':' + str(match_round_no), match_round):
          continue
        else:
          break
      else:
        match_round[player_field] = {'id': player_id, 'choice': player_choice}
        if not cache.cas('match-round:' + match_id + ':' + str(match_round_no), match_round):
          continue
        else:
          break

    if 'player_one' in match_round and 'player_two' in match_round:
      parent_key = db.Key.from_path('Match', match_id)
      key = db.Key.from_path('MatchRound', match_round_no, parent=parent_key)
      match_round_mdl = MatchRound.get(key)
      if not match_round_mdl:
        match_round_mdl = MatchRound(parent=parent_key)
      match_round_mdl.round = match_round_no
      match_round_mdl.player_one_choice = match_round['player_one']['choice']
      match_round_mdl.player_two_choice = match_round['player_two']['choice']
      match_round_mdl.put()
      self.response.write(json.dumps(match_round))
    else:
      self.response.write('ERR:NO-RESPONSE')


app = webapp2.WSGIApplication([
    ('/game/find-opponent', FindOpponentPage),
    ('/game/([0-9]+)', MatchPage)
  ], debug=True)
