package iutlens.qdev.trivia;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;


/**
 * The type Game.
 */
public class Game {

  private static final Logger LOGGER = LogManager.getLogger(Game.class.getPackage().getName());

  /**
   * The constant SCIENCE.
   */
  public static final String SCIENCE = "Science";
  /**
   * The constant POP.
   */
  public static final String POP = "Pop";
  /**
   * The constant ROCK.
   */
  public static final String ROCK = "Rock";
  /**
   * The constant SPORTS.
   */
  public static final String SPORTS = "Sport";

  /**
   * The Players.
   */
  List<String> players = new LinkedList<>();
  /**
   * The Places.
   */
  int[] places = new int[6];
  /**
   * The Purses.
   */
  int[] purses = new int[6];
  /**
   * The In penalty box.
   */
  boolean[] inPenaltyBox = new boolean[6];

  private static final int MAX_PLAYER_LOCATION = 12;

  /**
   * The Pop questions.
   */
  List<String> popQuestions = new LinkedList<>();
  /**
   * The Science questions.
   */
  List<String> scienceQuestions = new LinkedList<>();
  /**
   * The Sports questions.
   */
  List<String> sportsQuestions = new LinkedList<>();
  /**
   * The Rock questions.
   */
  List<String> rockQuestions = new LinkedList<>();

  /**
   * The Current player.
   */
  int currentPlayer = 0;
  /**
   * The Is getting out of penalty box.
   */
  boolean isGettingOutOfPenaltyBox;

  /**
   * Instantiates a new Game.
   */
  public Game() {
    for (int i = 0; i < 50; i++) {
      popQuestions.addLast(createQuestion(i, POP));
      scienceQuestions.addLast(createQuestion(i, SCIENCE));
      sportsQuestions.addLast(createQuestion(i, SPORTS));
      rockQuestions.addLast(createQuestion(i, ROCK));
    }
  }

  /**
   * Create question string.
   *
   * @param index the index
   * @param kind  the kind
   * @return the string
   */
  public static String createQuestion(final int index, final String kind) {
    return kind + " Question " + index;
  }

  /**
   * Is playable boolean.
   *
   * @return the boolean
   */
  public boolean isPlayable() {
    return howManyPlayers() >= 2;
  }

  /**
   * Add boolean.
   *
   * @param playerName the player name
   * @return the boolean
   */
  public boolean add(final String playerName) {

    players.add(playerName);
    places[howManyPlayers()] = 0;
    purses[howManyPlayers()] = 0;
    inPenaltyBox[howManyPlayers()] = false;

    LOGGER.info("{} was added", playerName);
    LOGGER.info("They are player number {}", players.size());
    return true;
  }

  /**
   * How many players int.
   *
   * @return the int
   */
  public int howManyPlayers() {
    return players.size();
  }

  /**
   * Get the current player name.
   * @return
   */
  private String getCurrentPlayer() {
    return players.get(currentPlayer);
  }

  /**
   * Get the current player location.
   * @return
   */
  private int getCurrentPlayerLocation() {
    return places[currentPlayer];
  }

  /**
   * Roll.
   *
   * @param roll the roll
   */
  public void roll(final int roll) {
    LOGGER.info("{} is the current player", getCurrentPlayer());
    LOGGER.info("They have rolled a {}", roll);

    if (inPenaltyBox[currentPlayer]) {
      if ((roll % 2) != 0) {
        isGettingOutOfPenaltyBox = true;

        LOGGER.info("{} is getting out of the penalty box", getCurrentPlayer());
        places[currentPlayer] = getCurrentPlayerLocation() + roll;
        if (getCurrentPlayerLocation() > MAX_PLAYER_LOCATION - 1) {
          places[currentPlayer] = getCurrentPlayerLocation() - MAX_PLAYER_LOCATION;
        }

        LOGGER.info("{}'s new location is {}", getCurrentPlayer(), getCurrentPlayerLocation());
        final String currentCategory = currentCategory(getCurrentPlayerLocation());
        LOGGER.info("The category is {}", currentCategory);
        askQuestion();
      } else {
        LOGGER.info("{} is not getting out of the penalty box", getCurrentPlayer());
        isGettingOutOfPenaltyBox = false;
      }

    } else {

      places[currentPlayer] = getCurrentPlayerLocation() + roll;
      if (getCurrentPlayerLocation() > MAX_PLAYER_LOCATION - 1) {
        places[currentPlayer] = getCurrentPlayerLocation() - MAX_PLAYER_LOCATION;
      }

      LOGGER.info("{}'s new location is {}", getCurrentPlayer(), getCurrentPlayerLocation());
      final String currentCategory = currentCategory(getCurrentPlayerLocation());
      LOGGER.info("The category is {}", currentCategory);
      askQuestion();
    }

  }

  /**
   *
   * @return
   */
  private void askQuestion() {
    final String question = getQuestionToAsk();
    LOGGER.info(question);
  }

  /**
   *
   * @return
   */
  private String getQuestionToAsk() {
    return switch (currentCategory(getCurrentPlayerLocation())) {
      case POP -> rockQuestions.removeFirst();
      case SCIENCE -> scienceQuestions.removeFirst();
      case SPORTS -> sportsQuestions.removeFirst();
      case ROCK -> rockQuestions.removeFirst();
      default ->
        // TODO: throw a QuestionNotFound
          null;
    };
  }

  /**
   *
   * @param place
   * @return
   */
  private String currentCategory(final int place) {
    return switch (place) {
      case 0, 4, 8 -> POP;
      case 1, 5, 9 -> SCIENCE;
      case 2, 6, 10 -> SPORTS;
      default -> ROCK;
    };
  }

  /**
   * Was correctly answered boolean.
   *
   * @return the boolean
   */
  public boolean wasCorrectlyAnswered() {
    if (inPenaltyBox[currentPlayer]) {
      if (isGettingOutOfPenaltyBox) {
        LOGGER.info("Answer was correct!!!!");
        purses[currentPlayer]++;
        LOGGER.info("{} now has {} Gold Coins.", getCurrentPlayer(), purses[currentPlayer]);

        final boolean winner = didPlayerWin();
        currentPlayer++;
        if (currentPlayer == players.size()) {
          currentPlayer = 0;
        }

        return winner;
      } else {
        currentPlayer++;
        if (currentPlayer == players.size()) {
          currentPlayer = 0;
        }
        return true;
      }


    } else {

      LOGGER.info("Answer was correct!!!!");
      purses[currentPlayer]++;
      LOGGER.info("{} now has {} Gold Coins.", getCurrentPlayer(), purses[currentPlayer]);

      final boolean winner = didPlayerWin();
      currentPlayer++;
      if (currentPlayer == players.size()) {
        currentPlayer = 0;
      }

      return winner;
    }
  }

  /**
   * Wrong answer boolean.
   *
   * @return the boolean
   */
  public boolean wrongAnswer() {
    LOGGER.info("Question was incorrectly answered");
    LOGGER.info("{} was sent to the penalty box.", getCurrentPlayer());
    inPenaltyBox[currentPlayer] = true;

    currentPlayer++;
    if (currentPlayer == players.size()) {
      currentPlayer = 0;
    }
    return true;
  }


  private boolean didPlayerWin() {
    return purses[currentPlayer] != 6;
  }
}
