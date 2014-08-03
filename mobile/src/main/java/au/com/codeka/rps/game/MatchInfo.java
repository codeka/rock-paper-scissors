package au.com.codeka.rps.game;

/**
 * Information about a match currently in progress.
 */
public class MatchInfo {
    private final String matchId;
    private final String playerId;
    private final String otherPlayerId;
    private int currentRound;

    public MatchInfo(String matchId, String playerId, String otherPlayerId) {
        this.matchId = matchId;
        this.playerId = playerId;
        this.otherPlayerId = otherPlayerId;
        this.currentRound = 1;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getRound() {
        return currentRound;
    }

    public void nextRound() {
        currentRound ++;
    }
}
