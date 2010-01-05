package org.gnubridge.presentation.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import junit.framework.TestCase;

import org.gnubridge.core.Card;
import org.gnubridge.core.Direction;
import org.gnubridge.core.Game;
import org.gnubridge.core.Player;
import org.gnubridge.core.bidding.ScoreCalculator;
import org.gnubridge.presentation.GameUtils;
import org.gnubridge.search.ProductionSettings;

public class PlayAcceptanceTest extends TestCase {

	private static final int TRICKS_PER_DEAL = 6;

	@Override
	public void setUp() {
		GameController.MAX_SECONDS_TO_MOVE = 1;
		ProductionSettings.setMilisecondsToDisplayLastTrick(0);
	}

	@Override
	public void tearDown() {
		ProductionSettings
				.setMilisecondsToDisplayLastTrick(ProductionSettings.DEFAULT_MILISECONDS_TO_DISPLAY_LAST_TRICK);
	}

	public void testPlayGameEndToEndTakeNoTricks() throws InterruptedException, InvocationTargetException {
		preInitializeGameWithSingleColorSuits();
		GBController mainController = makeController();
		mainController.getBiddingController().placeBid(7, "NT");
		mainController.playGame();
		playGameToTheEnd(mainController);
		System.out.println("Game finished. Declarers took "
				+ mainController.getGameController().getGame().getTricksTaken(Player.NORTH_SOUTH) + " tricks.");
		assertEquals(0, mainController.getGameController().getGame().getTricksTaken(Player.NORTH_SOUTH));
	}

	public void testWhenPlayingHumanRetainsHisCardsFromBidding() throws InterruptedException, InvocationTargetException {
		preInitializeGameWithSingleColorSuits();
		GBController mainController = makeController();
		Player humanInBidding = mainController.getBiddingController().getHuman();
		mainController.getBiddingController().placeBid(7, "NT");
		mainController.playGame();
		Direction humanInPlay = mainController.getGameController().getHuman();
		assertEquals(humanInBidding.getHand(), mainController.getGameController().getGame().getPlayer(humanInPlay)
				.getHand());

	}

	public void testPlayRandomGame() throws InterruptedException, InvocationTargetException {
		GameController.MAX_SECONDS_TO_MOVE = 3;
		preInitializeRandomGame();
		GBController mainController = makeController();
		mainController.getBiddingController().placeBid(7, "NT");
		mainController.playGame();
		playGameToTheEnd(mainController);
		System.out.println("Game finished. Declarers took "
				+ mainController.getGameController().getGame().getTricksTaken(Player.NORTH_SOUTH) + " tricks.");
		assertEquals(TRICKS_PER_DEAL, mainController.getGameController().getGame().getTricksTaken(Player.NORTH_SOUTH)
				+ mainController.getGameController().getGame().getTricksTaken(Player.WEST_EAST));
	}

	public void testPlayGameEndToEndTrumpAllTricks() throws InterruptedException, InvocationTargetException {
		preInitializeGameWithSingleColorSuits();
		GBController mainController = makeController();
		mainController.getBiddingController().placeBid(7, "Spades");
		// TODO: what are valid names? hidden in biddingControls - force programmaticaly
		mainController.playGame();
		playGameToTheEnd(mainController);
		System.out.println("Game finished. Declarers took "
				+ mainController.getGameController().getGame().getTricksTaken(Player.NORTH_SOUTH) + " tricks.");
		assertEquals(TRICKS_PER_DEAL, mainController.getGameController().getGame().getTricksTaken(Player.NORTH_SOUTH));
	}

	public void testRunningScoreEndToEnd() throws InterruptedException, InvocationTargetException {
		preInitializeGame13Tricks();
		GBController mainController = makeController();
		mainController.getBiddingController().placeBid(7, "Clubs");
		mainController.playGame();
		playGameToTheEnd(mainController);
		Thread.sleep(300);
		int score = new ScoreCalculator(
				mainController.getBiddingController().getAuction().getHighBid(), 
				mainController.getGameController().getGame().getTricksTaken(Player.NORTH_SOUTH)).getDeclarerScore();
		System.out.println("Game finished. Declarers took "
				+ mainController.getGameController().getGame().getTricksTaken(Player.NORTH_SOUTH) + " tricks. Score: " + score);

		assertEquals(1000 + 20 * 7, score);
		
		System.out.println("Running Human Score: " + mainController.getRunningHumanScore());
		System.out.println("Running Computer Score: " + mainController.getRunningComputerScore());
		assertEquals(1000 + 20 * 7, mainController.getRunningHumanScore());
		assertEquals(0, mainController.getRunningComputerScore());

		preInitializeGame13Tricks();
		mainController.getGameController().newGame();
		mainController.getBiddingController().placeBid(7, "Spades");
		
		Thread.sleep(300);
		
		mainController.playGame();
		playGameToTheEnd(mainController);
		score = new ScoreCalculator(
				mainController.getBiddingController().getAuction().getHighBid(), 
				mainController.getGameController().getGame().getTricksTaken(Player.NORTH_SOUTH)).getDefenderScore();
		
		System.out.println("Running Human Score: " + mainController.getRunningHumanScore());
		System.out.println("Running Computer Score: " + mainController.getRunningComputerScore());
		System.out.println("Game finished. Declarers took "
				+ mainController.getGameController().getGame().getTricksTaken(Player.NORTH_SOUTH) + " tricks. Score: " + score);

		assertEquals(1000 + 20 * 7, mainController.getRunningHumanScore());
		assertEquals(50 * 13, mainController.getRunningComputerScore());
	}

	private void playGameToTheEnd(GBController mainController) throws InterruptedException {
		Game game = mainController.getGameController().getGame();
		int cardsPlayed = -1;
		while (!game.isDone()) {
			int previousCardsPlayed = cardsPlayed;
			cardsPlayed = game.getPlayedCards().getCardsHighToLow().size();
			assertTrue("Expecting to play at least one card each time through the main loop",
					previousCardsPlayed < cardsPlayed);
			assertTrue("Game not done, but played cards " + cardsPlayed, cardsPlayed < 52);
			if (mainController.getGameController().humanHasMove()) {
				List<Card> possibleMoves = game.getNextToPlay().getPossibleMoves(game.getCurrentTrick());
				mainController.getGameController().playCard(possibleMoves.get(0));
				continue;
			} else {
				boolean cardPlayed = false;
				for (int i = 0; i < 8000; i++) {
					Thread.sleep(100);
					if (i % 10 == 0) {
						System.out.println("// tick...");
					}
					if (game.getPlayedCards().getCardsHighToLow().size() > cardsPlayed) {
						cardPlayed = true;
						break;
					}

				}
				assertTrue("No card was played in 400 seconds", cardPlayed);
			}
		}
		System.out.println("");
	}
	
	private void preInitializeGame13Tricks() {
		Game g = new Game(null);
		GameUtils.initializeSingleColorSuits(g, 13);
		g.setHumanPlayer(g.getSouth());
		System.out.println("Human's hand: " + g.getSouth().getHand());
		Game.setPreInitializedGame(g);
	}

	private void preInitializeGameWithSingleColorSuits() {
		Game g = new Game(null);
		GameUtils.initializeSingleColorSuits(g, TRICKS_PER_DEAL);
		g.setHumanPlayer(g.getWest());
		System.out.println("Human's hand: " + g.getWest().getHand());
		Game.setPreInitializedGame(g);
	}

	private void preInitializeRandomGame() {
		Game g = new Game(null);
		GameUtils.initializeRandom(g, TRICKS_PER_DEAL);
		g.setHumanPlayer(g.getNorth());
		System.out.println("West's hand: " + g.getWest().getHand());
		System.out.println("Human's hand: " + g.getNorth().getHand());
		System.out.println("East's hand: " + g.getEast().getHand());
		System.out.println("South's hand: " + g.getSouth().getHand());
		Game.setPreInitializedGame(g);
	}

	private GBController makeController() {
		MainView mw = new MockMainView("gnubridge");
		ViewFactory.setMockMainView(mw);
		return new GBController();
	}
}
