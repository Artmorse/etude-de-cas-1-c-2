
package iutlens.qdev.trivia;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public class GameRunner {

  private static final Logger LOGGER = LogManager.getLogger(
      GameRunner.class.getPackage().getName());

  public static void main(String[] args) {

    LOGGER.info("Welcome in the game.");

    Game aGame = new Game();

    aGame.add("Chet");
    aGame.add("Pat");
    aGame.add("Sue");

    Random rand = new Random();
    boolean notAWinner;
    do {

      aGame.roll(rand.nextInt(5) + 1);

      if (rand.nextInt(9) == 7) {
        notAWinner = aGame.wrongAnswer();
      } else {
        notAWinner = aGame.wasCorrectlyAnswered();
      }


    } while (notAWinner);

  }
}
