package au.com.codeka.rps.game;

/**
 * Created by dean on 2/08/14.
 */
public class ResultInfo {
    private final String matchId;
    private final Result result;
    private final String playerChoice;
    private final String otherChoice;

    public ResultInfo(String matchId, String playerChoice, String otherChoice) {
        this.matchId = matchId;
        this.playerChoice = playerChoice;
        this.otherChoice = otherChoice;

        if (playerChoice.equals(otherChoice)) {
            result = Result.Draw;
        } else if (playerChoice.equals("rock")) {
            result = otherChoice.equals("scissors") ? Result.Win : Result.Loss;
        } else if (playerChoice.equals("scissors")) {
            result = otherChoice.equals("paper") ? Result.Win : Result.Loss;
        } else { // paper
            result = otherChoice.equals("rock") ? Result.Win : Result.Loss;
        }
    }

    public Result getResult() {
        return result;
    }

    public String getPlayerChoice() {
        return playerChoice;
    }

    public String getOtherChoice() {
        return otherChoice;
    }

    public enum Result {
        Win,
        Loss,
        Draw
    }
}
