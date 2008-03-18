package org.gnubridge.search;

import java.util.List;

import junit.framework.TestCase;

import org.gnubridge.core.Card;
import org.gnubridge.core.Direction;
import org.gnubridge.core.Game;
import org.gnubridge.core.Player;
import org.gnubridge.core.deck.Ace;
import org.gnubridge.core.deck.Clubs;
import org.gnubridge.core.deck.Diamonds;
import org.gnubridge.core.deck.Eight;
import org.gnubridge.core.deck.Five;
import org.gnubridge.core.deck.Four;
import org.gnubridge.core.deck.Hearts;
import org.gnubridge.core.deck.King;
import org.gnubridge.core.deck.Nine;
import org.gnubridge.core.deck.NoTrump;
import org.gnubridge.core.deck.Queen;
import org.gnubridge.core.deck.Seven;
import org.gnubridge.core.deck.Six;
import org.gnubridge.core.deck.Spades;
import org.gnubridge.core.deck.Ten;
import org.gnubridge.core.deck.Three;
import org.gnubridge.core.deck.Two;
import org.gnubridge.presentation.GameUtils;

public class SearchTest extends TestCase {

	public void testExaminePositionSetsNextToPlay() {
		Node node = new Node(null);
		Game game = new Game(NoTrump.i());
		GameUtils.initializeSingleColorSuits(game, 2);
		game.setNextToPlay(Direction.SOUTH);
		Search s = new Search(game);
		s.examinePosition(node);
		assertEquals(game.getNextToPlay().getDirection(), node.getPlayerTurn());
		assertEquals(Direction.SOUTH, node.getPlayerTurn());
	}

	public void testExaminePositionInitsChildren() {
		Node node = new Node(null);
		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init(new String[] { "3", "10" });
		game.getPlayer(Direction.NORTH).init(new String[] { "2", "9" });
		game.getPlayer(Direction.SOUTH).init(new String[] { "A", "5" });
		game.getPlayer(Direction.EAST).init(new String[] { "K", "7" });
		game.setNextToPlay(Direction.SOUTH);
		Search s = new Search(game);
		s.examinePosition(node);
		assertEquals(2, node.children.size());
	}

	public void testExaminePositionPushesChildrenOnStack() {
		Node node = new Node(null);
		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init(new String[] { "3", "10" });
		game.getPlayer(Direction.NORTH).init(new String[] { "2", "9" });
		game.getPlayer(Direction.SOUTH).init(new String[] { "A", "5" });
		game.getPlayer(Direction.EAST).init(new String[] { "K", "7" });
		game.setNextToPlay(Direction.SOUTH);
		Search s = new Search(game);
		s.examinePosition(node);
		assertTrue(s.getStack().contains(node.children.get(0)));
		assertTrue(s.getStack().contains(node.children.get(1)));
	}

	public void testDoNotExpandNodesBeyondTrickLimit() {
		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init(new String[] { "3", "10","4" });
		game.getPlayer(Direction.NORTH).init(new String[] { "2", "9", "6" });
		game.getPlayer(Direction.SOUTH).init(new String[] { "A", "5", "J" });
		game.getPlayer(Direction.EAST).init(new String[] { "K", "7", "Q" });
		game.setNextToPlay(Direction.SOUTH);
		Search s = new Search(game);
		s.setMaxTricks(1);
		Node node_0_0_0_0 = new Node(new Node(new Node(new Node(new Node(null)))));
		s.examinePosition(node_0_0_0_0 );
		assertEquals(0, s.getStack().size());
	}	
	
	public void testTricksTallyIsTrickLimit() {
		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init(new String[] { "3", "A","4" });
		game.getPlayer(Direction.NORTH).init(new String[] { "2", "9", "6" });
		game.getPlayer(Direction.SOUTH).init(new String[] { "10", "5", "J" });
		game.getPlayer(Direction.EAST).init(new String[] { "K", "7", "Q" });
		game.setNextToPlay(Direction.SOUTH);
		Search s = new Search(game);
		s.setMaxTricks(1);
		s.search();
		assertEquals(1, s.getRoot().getTricksTaken(Player.WEST_EAST)+s.getRoot().getTricksTaken(Player.NORTH_SOUTH));
	}
	
	
	
	public void testExaminePositionInitsChildMove() {
		Node node = new Node(null);
		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init(new String[] { "3", "10" });
		game.getPlayer(Direction.NORTH).init(new String[] { "2", "9" });
		game.getPlayer(Direction.SOUTH).init(new String[] { "A", "5" });
		game.getPlayer(Direction.EAST).init(new String[] { "K", "7" });
		game.setNextToPlay(Direction.SOUTH);
		Search s = new Search(game);
		s.examinePosition(node);
		Node child1 = s.getStack().pop();
		Node child2 = s.getStack().pop();
		s.examinePosition(child1);
		assertEquals(Direction.WEST, child1.getPlayerTurn());
		s.examinePosition(child2);
		assertEquals(Direction.WEST, child1.getPlayerTurn());
	}

	public void testExaminePositionExpandsChild() {
		Node node = new Node(null);
		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init(new String[] { "3" },
				new String[] { "10" });
		game.getPlayer(Direction.NORTH).init(new String[] { "2", "9" });
		game.getPlayer(Direction.SOUTH).init(new String[] { "A", "5" });
		game.getPlayer(Direction.EAST).init(new String[] { "K", "7" });
		game.setNextToPlay(Direction.SOUTH);
		Search s = new Search(game);
		s.examinePosition(node);
		Node child1 = s.getStack().pop();
		Node child2 = s.getStack().pop();
		s.examinePosition(child1);
		assertEquals(1, child1.children.size());
		s.examinePosition(child2);
		assertEquals(1, child2.children.size());
	}

	public void testTwoTricks() {
		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init(new String[] { "2" },
				new String[] { "3" });
		game.getPlayer(Direction.NORTH).init(new String[] { "3" },
				new String[] { "2" });
		game.getPlayer(Direction.SOUTH).init(new String[] {},
				new String[] { "K", "10" });
		game.getPlayer(Direction.EAST).init(new String[] { "A" }, new String[] {},
				new String[] { "J" });
		game.setNextToPlay(Direction.NORTH);
		Search s = new Search(game);
		s.search();
		List<Card> bestMoves = s.getBestMoves();
		assertEquals(1, bestMoves.size());
		assertEquals(Two.of(Hearts.i()), bestMoves.get(0));
	}

	public void testTwoTricks2() {
		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init(new String[] { "3" },
				new String[] { "2" });
		game.getPlayer(Direction.NORTH).init(new String[] { "2" },
				new String[] { "3" });
		game.getPlayer(Direction.SOUTH).init(new String[] { "K", "10" });
		game.getPlayer(Direction.EAST).init(new String[] {}, new String[] { "A" },
				new String[] { "J" });
		game.setNextToPlay(Direction.NORTH);
		Search s = new Search(game);
		s.search();
		List<Card> bestMoves = s.getBestMoves();
		assertEquals(1, bestMoves.size());
		assertEquals(Two.of(Spades.i()), bestMoves.get(0));
	}

	public void testOneTrick() {
		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init(new String[] { "3" });
		game.getPlayer(Direction.NORTH).init(new String[] { "2" });
		game.getPlayer(Direction.SOUTH).init(new String[] { "K" });
		game.getPlayer(Direction.EAST).init(new String[] { "J" });
		game.setNextToPlay(Direction.NORTH);
		Search s = new Search(game);
		s.search();
		assertEquals(Player.NORTH_SOUTH, s.getRoot().getCurrentPair());
		assertEquals(1, s.getRoot().getTricksTaken(Player.NORTH_SOUTH));

	}

	//	 public void testSeveralTricks() {
	//	 Game game = new Game();
	//	 GameTestingUtils.initializeSingleColorSuits(game, 4);
	//	 Search s = new Search(game);
	//	 long start = System.currentTimeMillis();
	//	 s.usePruning(true);
	//	 s.search();
	//	 long stop = System.currentTimeMillis();
	//	 System.out.println("Pruned full search took (sec): "+(stop-start)/1000);
	//	 }

//		public void testMinimaxEquivalentToAlphaBeta() {
//			for (int i = 0; i < 10; i++) {
//				
//				Game game = new Game();
//				GameTestingUtils.initializeRandom(game, 4);
//				Search unpruned = new Search(game);
//				unpruned.usePruning(false);
//				unpruned.search();
//	
//				Search pruned = new Search(game);
//				pruned.usePruning(true);
//				pruned.search();
//	
//				System.out.println("----------********************-----------");
//				game.printHands();
//				unpruned.printStats();
//				pruned.printStats();
//				
//				//assertTrue((pruned.getRunningTime() < unpruned.getRunningTime()) 
//				//		|| (pruned.getPrunedCount() == 0));
//				//assertEquals("Unpruned: "+unpruned.getBestMoves()+", pruned: "+pruned.getBestMoves(), 
//				//		pruned.getBestMoves(), unpruned.getBestMoves());
//				assertEquals("Unpruned: "+unpruned.getRoot().getTricksTaken(Player.WEST_EAST)+", pruned: "+pruned.getRoot().getTricksTaken(Player.WEST_EAST),
//						unpruned.getRoot().getTricksTaken(Player.WEST_EAST), pruned.getRoot().getTricksTaken(Player.WEST_EAST));
//				
//			}
//		}
		
//		public void testViableDepth() {
//			for (int i = 0; i < 4; i++) {
//				
//				Game game = new Game();
//				GameTestingUtils.initializeRandom(game, 7);
//				
//				System.out.println("----------********************-----------");
//				game.printHands();
//	
//				Search pruned = new Search(game);
//				pruned.usePruning(true);
//				pruned.search();
//	
//				pruned.printStats();
//				
//			}
//		}

	public void testAlphaBetaScenario1() {

		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init(
				new Card[] { Nine.of(Clubs.i()), Four.of(Spades.i()), Six.of(Spades.i()),
						Nine.of(Spades.i()) });
		game.getPlayer(Direction.NORTH).init(
				new Card[] { Seven.of(Spades.i()), Ace.of(Spades.i()),
						Eight.of(Spades.i()), Five.of(Clubs.i()) });
		game.getPlayer(Direction.EAST).init(
				new Card[] { Ten.of(Hearts.i()), Three.of(Hearts.i()), Two.of(Spades.i()),
						Eight.of(Clubs.i()) });
		game.getPlayer(Direction.SOUTH).init(
				new Card[] { Six.of(Hearts.i()), Two.of(Hearts.i()), Queen.of(Spades.i()),
						King.of(Clubs.i()) });

		Search pruned = new Search(game.duplicate());
		pruned.usePruning(true);
		pruned.search();
		assertEquals(2, pruned.getRoot().getTricksTaken(Player.WEST_EAST));
	}
	
	public void testAlphaBetaScenario2() {

		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init(new Card[] { Six.of(Spades.i()),Nine.of(Spades.i()) });
		game.getPlayer(Direction.NORTH).init(new Card[] { Ace.of(Spades.i()), Eight.of(Spades.i()) });
		game.getPlayer(Direction.EAST).init(new Card[] { Ten.of(Hearts.i()), Three.of(Hearts.i())});
		game.getPlayer(Direction.SOUTH).init(new Card[] { Six.of(Hearts.i()), Two.of(Hearts.i())});

        game.setNextToPlay(Direction.SOUTH);
		Search pruned = new Search(game.duplicate());
		pruned.usePruning(true);
		pruned.search();
		assertEquals(2, pruned.getRoot().getTricksTaken(Player.WEST_EAST));

	}
	
	public void testLastTrickAutoExpands() {
		Node root = new Node(null);
		Game g = new Game(NoTrump.i());
		GameUtils.initializeSingleColorSuits(g, 1);
		Search s = new Search(g);
		s.examinePosition(root);
		assertEquals(0, root.children.size());
		assertTrue(s.getStack().empty());
	}

	public void testPrunedParentNoEvaluation() {
		Node root = new Node(null);
		root.setPruned(true, Node.PRUNE_ALPHA);
		Node child = new Node(root);
		Game g = new Game(NoTrump.i());
		GameUtils.initializeSingleColorSuits(g, 2);
		Search s = new Search(g);
		s.examinePosition(child);
		assertEquals(0, child.children.size());
	}
	
	public void testPrunedAncestorNoEvaluation() {
		Node root = new Node(null);
		root.setPruned(true, Node.PRUNE_ALPHA);
		Node child = new Node(root);
		Node grandChild = new Node(child);
		Game g = new Game(NoTrump.i());
		GameUtils.initializeSingleColorSuits(g, 2);
		Search s = new Search(g);
		s.examinePosition(grandChild);
		assertEquals(0, grandChild.children.size());
	}
	
	public void testTrimRoot() {
		int maxWestTricks = 3;
		Node root = new Node(null);
		root.setPlayerTurn(Direction.WEST);
		Node child1 = new Node(root);
		child1.setTricksTaken(Player.WEST_EAST, 1);
		child1.setTricksTaken(Player.NORTH_SOUTH, 2);
		Node child2 = new Node(root);
		child2.setTricksTaken(Player.WEST_EAST, maxWestTricks);
		child2.setTricksTaken(Player.NORTH_SOUTH, 1);
		Search s = new Search(root);
		s.trim(root);
		assertEquals("Poor move not trimmed", null, root.children.get(0));
		assertEquals("Good move trimmed", child2, root.children.get(1));
		assertEquals(maxWestTricks, root.getTricksTaken(Player.WEST_EAST));
	}
	
	public void testLastChildCallsParentTrim() {
		Node root = new Node(null);
		root.setPlayerTurn(Direction.WEST);

		Node child1 = new Node(root);
		child1.setPlayerTurn(Direction.NORTH);
		child1.setTricksTaken(Player.WEST_EAST, 1);
		child1.setTricksTaken(Player.NORTH_SOUTH, 2);
		Node grandChild1 = new Node(child1);
		grandChild1.setPlayerTurn(Direction.EAST);
		grandChild1.setTricksTaken(Player.WEST_EAST, 1);
		grandChild1.setTricksTaken(Player.NORTH_SOUTH, 2);

		Node child2 = new Node(root);
		child2.setPlayerTurn(Direction.NORTH);

		Node grandChild2 = new Node(child2);
		grandChild2.setPlayerTurn(Direction.EAST);
		grandChild2.setTricksTaken(Player.WEST_EAST, 1);
		grandChild2.setTricksTaken(Player.NORTH_SOUTH, 2);
		Search s = new Search(root);
		s.trim(child2);
		assertFalse("Trimmed parent even though another child was not visited",
				root.trimmed());

		s.trim(child1);
		assertTrue(root.trimmed());
	}
	
	public void testNotLastChildNoCallToParentTrim() {
		MockNode root = new MockNode(null);
		root.setPlayerTurn(Direction.WEST);

		Node child1 = new Node(root);
		child1.setPlayerTurn(Direction.NORTH);

		Node grandChild1 = new Node(child1);
		grandChild1.setPlayerTurn(Direction.EAST);
		grandChild1.setTricksTaken(Player.WEST_EAST, 1);
		grandChild1.setTricksTaken(Player.NORTH_SOUTH, 2);

		Node child2 = new Node(root);
		child2.setPlayerTurn(Direction.NORTH);
		child2.setTricksTaken(Player.WEST_EAST, 1);
		child2.setTricksTaken(Player.NORTH_SOUTH, 2);

		Node grandChild2 = new Node(child2);
		grandChild2.setPlayerTurn(Direction.EAST);
		grandChild2.setTricksTaken(Player.WEST_EAST, 1);
		grandChild2.setTricksTaken(Player.NORTH_SOUTH, 2);

		Search s = new Search(root);
		s.trim(child2);
		assertFalse(root.trimmed());
	}
	
	public void testMinMaxTrimmingNorthSpoilsWestPlay() {
		Node root = new Node(null);
		root.setPlayerTurn(Direction.WEST);

		MockNode child1 = new MockNode(root);
		child1.setPlayerTurn(Direction.NORTH);
		child1.setTricksTaken(Player.WEST_EAST, 4);
		child1.setTricksTaken(Player.NORTH_SOUTH, 5);
		child1.trim();

		Node child2 = new Node(root);
		child2.setPlayerTurn(Direction.NORTH);

		Node grandChild1 = new Node(child2);
		grandChild1.setPlayerTurn(Direction.EAST);
		grandChild1.setTricksTaken(Player.WEST_EAST, 3);
		grandChild1.setTricksTaken(Player.NORTH_SOUTH, 6);

		Node grandChild2 = new Node(child2);
		grandChild2.setPlayerTurn(Direction.EAST);
		grandChild2.setTricksTaken(Player.WEST_EAST, 7);
		grandChild2.setTricksTaken(Player.NORTH_SOUTH, 2);

		Search s = new Search(root);
		s.trim(child2);
		assertEquals(child1.getTricksTaken(root.getCurrentPair()), root
				.getTricksTaken(root.getCurrentPair()));
		assertNull(root.children.get(1));
	}
	
	public void testMinMaxTrimmingNorthLesserEvil() {
		Node root = new Node(null);
		root.setPlayerTurn(Direction.WEST);

		MockNode child1 = new MockNode(root);
		child1.setPlayerTurn(Direction.NORTH);
		child1.setTricksTaken(Player.WEST_EAST, 4);
		child1.setTricksTaken(Player.NORTH_SOUTH, 5);
		child1.trim();

		Node child2 = new Node(root);
		child2.setPlayerTurn(Direction.NORTH);

		Node grandChild1 = new Node(child2);
		grandChild1.setPlayerTurn(Direction.EAST);
		grandChild1.setTricksTaken(Player.WEST_EAST, 5);
		grandChild1.setTricksTaken(Player.NORTH_SOUTH, 4);

		Node grandChild2 = new Node(child2);
		grandChild2.setPlayerTurn(Direction.EAST);
		grandChild2.setTricksTaken(Player.WEST_EAST, 7);
		grandChild2.setTricksTaken(Player.NORTH_SOUTH, 2);

		Search s = new Search(root);
		s.trim(child2);
		assertNull(root.children.get(0));
		assertEquals(grandChild1.getTricksTaken(root.getCurrentPair()), root
				.getTricksTaken(root.getCurrentPair()));

	}
	
	public void testTrimTerminatesOnUnexpandedNonLeafNode() {

		MockNode root = new MockNode(null);
		root.setPlayerTurn(Direction.WEST);

		Node child1 = new Node(root);
		child1.setPlayerTurn(Direction.NORTH);
        child1.setLeaf(false);

		Node child2 = new Node(root);
		child2.setPlayerTurn(Direction.NORTH);
		child2.setTricksTaken(Player.WEST_EAST, 1);
		child2.setTricksTaken(Player.NORTH_SOUTH, 2);

		Node grandChild2 = new Node(child2);
		grandChild2.setPlayerTurn(Direction.EAST);
		grandChild2.setTricksTaken(Player.WEST_EAST, 1);
		grandChild2.setTricksTaken(Player.NORTH_SOUTH, 2);
		Search s = new Search(root);
		s.trim(child2);
		assertFalse(root.trimmed());

	}
	
	public void testTrimPruned() {
		Node root = new Node(null);
		root.setPlayerTurn(Direction.WEST);
		root.setPruned(true, Node.PRUNE_ALPHA);
		@SuppressWarnings("unused")
		Node child1 = new Node(root);
		@SuppressWarnings("unused")
		Node child2 = new Node(root);
		Search s = new Search(root);
		s.trim(root);
		assertEquals(null, root.children.get(0));
		assertEquals(null, root.children.get(1));
	}
	
	public void testBestMoveWhenRootDoesNotStartTrick() {
		Game game = new Game(NoTrump.i());
		game.getPlayer(Direction.WEST).init( new Card[] { Nine.of(Clubs.i()), Four.of(Spades.i())});
		game.getPlayer(Direction.NORTH).init( new Card[] { Seven.of(Spades.i()), Queen.of(Hearts.i())});
		game.getPlayer(Direction.EAST).init( new Card[] { Three.of(Clubs.i()), Three.of(Hearts.i()) });
		game.getPlayer(Direction.SOUTH).init(new Card[] { Four.of(Clubs.i()), Two.of(Spades.i())});
		game.doNextCard(0);
		Search s = new Search (game);
		s.search();
		assertEquals(1, s.getBestMoves().size());
		assertEquals(Queen.of(Hearts.i()), s.getBestMoves().get(0));
		
		//triangulate
		Game game2 = new Game(NoTrump.i());
		game2.getPlayer(Direction.WEST).init( new Card[] { Nine.of(Clubs.i()), Four.of(Spades.i())});
		game2.getPlayer(Direction.NORTH).init( new Card[] { Queen.of(Hearts.i()), Seven.of(Spades.i())}); //invert order
		game2.getPlayer(Direction.EAST).init( new Card[] { Three.of(Clubs.i()), Three.of(Hearts.i()) });
		game2.getPlayer(Direction.SOUTH).init(new Card[] { Four.of(Clubs.i()), Two.of(Spades.i())});
		game2.doNextCard(0);
		Search s2 = new Search (game2);
		s2.search();
		assertEquals(1, s2.getBestMoves().size());
		assertEquals(Queen.of(Hearts.i()), s2.getBestMoves().get(0));		
	}
	
	public void testNorthTrumps() {
		Game game = new Game(Spades.i());
		game.getPlayer(Direction.WEST).init( new Card[] { Nine.of(Clubs.i()), Four.of(Clubs.i())});
		game.getPlayer(Direction.NORTH).init( new Card[] { Two.of(Spades.i()), Two.of(Hearts.i())});
		game.getPlayer(Direction.EAST).init( new Card[] { Three.of(Clubs.i()), Three.of(Diamonds.i()) });
		game.getPlayer(Direction.SOUTH).init(new Card[] { Six.of(Clubs.i()), Five.of(Diamonds.i())});
		game.doNextCard(0);
		Search s = new Search (game);
		s.search();
		assertEquals(1, s.getBestMoves().size());
		assertEquals(Two.of(Spades.i()), s.getBestMoves().get(0));
		
		Game game2 = new Game(Spades.i());
		game2.getPlayer(Direction.WEST).init( new Card[] { Nine.of(Clubs.i()), Four.of(Clubs.i())});
		game2.getPlayer(Direction.NORTH).init( new Card[] { Two.of(Hearts.i()), Two.of(Spades.i())}); //order reverted
		game2.getPlayer(Direction.EAST).init( new Card[] { Three.of(Clubs.i()), Three.of(Diamonds.i()) });
		game2.getPlayer(Direction.SOUTH).init(new Card[] { Six.of(Clubs.i()), Five.of(Diamonds.i())});
		game2.doNextCard(0);
		Search s2 = new Search (game2);
		s2.search();
		assertEquals(1, s2.getBestMoves().size());
		assertEquals(Two.of(Spades.i()), s2.getBestMoves().get(0));
		
	}
	
	public void testNorthCannotTrumpBecauseHasColor() {
		Game game = new Game(Spades.i());
		game.getPlayer(Direction.WEST).init( new Card[] { Nine.of(Clubs.i()), Four.of(Spades.i())});
		game.getPlayer(Direction.NORTH).init( new Card[] { Two.of(Spades.i()), Two.of(Clubs.i())});
		game.getPlayer(Direction.EAST).init( new Card[] { Three.of(Diamonds.i()), Three.of(Hearts.i()) });
		game.getPlayer(Direction.SOUTH).init(new Card[] { Six.of(Diamonds.i()), Five.of(Hearts.i())});
		game.doNextCard(0);
		Search s = new Search (game);
		s.search();
		assertEquals(1, s.getBestMoves().size());
		assertEquals(Two.of(Clubs.i()), s.getBestMoves().get(0));
	}


}

