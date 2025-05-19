package iutlens.qdev.trivia;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

public class Game {

	private static final Logger LOGGER = LogManager.getLogger(Game.class.getPackage().getName());

	public static final String SCIENCE = "Science";
	public static final String POP = "Pop";
	public static final String ROCK = "Rock";
	public static final String SPORTS = "Sport";

    List<String> players = new LinkedList<>();
    int[] places = new int[6];
    int[] purses  = new int[6];
    boolean[] inPenaltyBox  = new boolean[6];

	List<String> popQuestions = new LinkedList<>();
	List<String> scienceQuestions = new LinkedList<>();
	List<String> sportsQuestions = new LinkedList<>();
	List<String> rockQuestions = new LinkedList<>();
    
    int currentPlayer = 0;
    boolean isGettingOutOfPenaltyBox;
    
    public  Game(){
    	for (int i = 0; i < 50; i++) {
			popQuestions.addLast(createQuestion(i, POP));
			scienceQuestions.addLast(createQuestion(i, SCIENCE));
			sportsQuestions.addLast(createQuestion(i, SPORTS));
			rockQuestions.addLast(createQuestion(i, ROCK));
    	}
    }

	public String createQuestion(int index, String kind){
		return kind + " Question " + index;
	}
	
	public boolean isPlayable() {
		return (howManyPlayers() >= 2);
	}

	public boolean add(String playerName) {
		
		
	    players.add(playerName);
	    places[howManyPlayers()] = 0;
	    purses[howManyPlayers()] = 0;
	    inPenaltyBox[howManyPlayers()] = false;
	    
	    LOGGER.info("{} was added", playerName);
	    LOGGER.info("They are player number {}", players.size());
		return true;
	}
	
	public int howManyPlayers() {
		return players.size();
	}

	private String getCurrentPlayer() {
		return players.get(currentPlayer);
	}

	private int getCurrentPlayerLocation() {
		return places[currentPlayer];
	}

	public void roll(int roll) {
		LOGGER.info("{} is the current player", getCurrentPlayer());
		LOGGER.info("They have rolled a {}", roll);
		
		if (inPenaltyBox[currentPlayer]) {
			if (roll % 2 != 0) {
				isGettingOutOfPenaltyBox = true;
				
				LOGGER.info("{} is getting out of the penalty box", getCurrentPlayer());
				places[currentPlayer] = getCurrentPlayerLocation() + roll;
				if (getCurrentPlayerLocation() > 11) places[currentPlayer] = getCurrentPlayerLocation() - 12;
				
				LOGGER.info("{}'s new location is {}", getCurrentPlayer(), getCurrentPlayerLocation());
				String currentCategory = currentCategory(getCurrentPlayerLocation());
				LOGGER.info("The category is {}", currentCategory);
				askQuestion();
			} else {
				LOGGER.info("{} is not getting out of the penalty box", getCurrentPlayer());
				isGettingOutOfPenaltyBox = false;
				}
			
		} else {

			places[currentPlayer] = getCurrentPlayerLocation() + roll;
			if (getCurrentPlayerLocation() > 11) places[currentPlayer] = getCurrentPlayerLocation() - 12;
			
			LOGGER.info("{}'s new location is {}", getCurrentPlayer(), getCurrentPlayerLocation());
			String currentCategory = currentCategory(getCurrentPlayerLocation());
			LOGGER.info("The category is {}", currentCategory);
			askQuestion();
		}
		
	}

	private void askQuestion() {
		String question = getQuestionToAsk();
		LOGGER.info(question);
	}

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
	
	
	private String currentCategory(int place) {
		return switch (place) {
			case 0, 4, 8 -> POP;
			case 1, 5, 9 -> SCIENCE;
			case 2, 6, 10 -> SPORTS;
			default -> ROCK;
		};
	}

	public boolean wasCorrectlyAnswered() {
		if (inPenaltyBox[currentPlayer]){
			if (isGettingOutOfPenaltyBox) {
				LOGGER.info("Answer was correct!!!!");
				purses[currentPlayer]++;
				LOGGER.info("{} now has {} Gold Coins.", getCurrentPlayer(), purses[currentPlayer]);
				
				boolean winner = didPlayerWin();
				currentPlayer++;
				if (currentPlayer == players.size()) currentPlayer = 0;
				
				return winner;
			} else {
				currentPlayer++;
				if (currentPlayer == players.size()) currentPlayer = 0;
				return true;
			}
			
			
			
		} else {
		
			LOGGER.info("Answer was corrent!!!!");
			purses[currentPlayer]++;
			LOGGER.info("{} now has {} Gold Coins.", getCurrentPlayer(), purses[currentPlayer]);
			
			boolean winner = didPlayerWin();
			currentPlayer++;
			if (currentPlayer == players.size()) currentPlayer = 0;
			
			return winner;
		}
	}
	
	public boolean wrongAnswer(){
		LOGGER.info("Question was incorrectly answered");
		LOGGER.info("{} was sent to the penalty box.", getCurrentPlayer());
		inPenaltyBox[currentPlayer] = true;
		
		currentPlayer++;
		if (currentPlayer == players.size()) currentPlayer = 0;
		return true;
	}


	private boolean didPlayerWin() {
		return purses[currentPlayer] != 6;
	}
}
