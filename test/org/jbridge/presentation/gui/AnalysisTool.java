package org.jbridge.presentation.gui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.gnubridge.core.Direction;
import org.gnubridge.core.Game;
import org.gnubridge.core.Hand;
import org.gnubridge.core.South;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.deck.Ace;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Ten;
import org.gnubridge.presentation.GameUtils;
import org.gnubridge.presentation.gui.MainViewImpl;

public class AnalysisTool {
	public static void main(String[] args) throws InterruptedException, InvocationTargetException {

		buildGui();

	}

	private static void buildGui() throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				Game g = createCounterexample();
				MainViewImpl mainView = new MainViewImpl("GNUBridge Analysis Mode");
				AnalysisView pv = new AnalysisView(mainView);
				pv.show();
				mainView.show();
				pv.setGame(g, South.i());
				pv.setContract(new Bid(1, NoTrump.i()));
				pv.setListener(new MockCardPlayedListener());
				pv.displayCurrentTrick();

			}
		});
	}

	private static Game createCounterexample() {
		Game game = new Game(NoTrump.i());
		game.getWest().init(new Hand("", "8,4", "2", ""));//E: AH, W: 4H???
		game.getNorth().init(new Hand("3", "", "5,4", ""));
		game.getEast().init(new Hand("", "A,6,5", "", ""));
		game.getSouth().init(new Hand("", "10", "3", "3"));
		game.setNextToPlay(Direction.EAST);
		game.play(Ace.of(Hearts.i()));
		game.play(Ten.of(Hearts.i()));
		return game;
	}

	private static Game createSampleGame() {
		Game g = new Game(null);
		GameUtils.initializeRandom(g, 13);
		g.setHumanPlayer(g.getSouth());
		return g;
	}
}
