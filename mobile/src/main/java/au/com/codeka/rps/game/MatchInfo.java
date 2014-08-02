package au.com.codeka.rps.game;

/**
 * Information about a match currently in progress.
 */
public class MatchInfo {
    private final String matchId;
    private final String playerId;

    public MatchInfo(String matchId, String playerId) {
        this.matchId = matchId;
        this.playerId = playerId;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getPlayerId() {
        return playerId;
    }
}
