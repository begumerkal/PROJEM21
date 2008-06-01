package org.gnubridge.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gnubridge.core.deck.Color;


public class Hand {
	List<Card> cards;
	
	/**
	 * Caching optimization for pruning played cards
	 * and perhaps others
	 */
	List<Card> orderedCards;
	Color color;
	List<Card> colorInOrder;

	public Hand() {
		this.cards = new ArrayList<Card>();
	}
	
	public Hand(Card... cards) {
		this();
		for (Card card : cards) {
			this.cards.add(card);
		}
	}
	
	public Hand(List<Card> cards) {
	  this();
	  this.cards.addAll(cards);
	}

	public Hand(String... colorSuits) {
		this();
		int i = 0;
		for (String colorSuit : colorSuits) {
			cards.addAll(createCards(colorSuit, Color.list[i]));
			i++;
		}

	}
	
	public void add(Card c) {
		cards.add(c);
		orderedCards = null;
		if (c.getDenomination().equals(color)) {
			colorInOrder = null;
		}
		
	}

	private Collection<? extends Card> createCards(String colorSuit, Color color) {
		List<Card> results = new ArrayList<Card>();
		if (!"".equals(colorSuit.trim())) {

			String[] cardTokens = colorSuit.split(",");
			for (String cardToken : cardTokens) {
				results.add(new Card(cardToken, color));
			}
		}
		return results;
	}

	public List<Card> getColorHi2Low(Color color) {
		if (color.equals(this.color) && colorInOrder != null) {
			return colorInOrder;
		}
		List<Card> result = new ArrayList<Card>();
		for (Card card : cards) {
			if (card.getDenomination().equals(color)) {
				insertInOrder(result, card);

			}
		}
		this.color = color;
		colorInOrder = result;
		return result;
	}

	private void insertInOrder(List<Card> ordered, Card x) {
		if (ordered.isEmpty()) {
			ordered.add(x);
		} else {
			int i = 0;
			boolean inserted = false;
			for (Card card : ordered) {
				if (!card.hasGreaterValueThan(x)) {
					ordered.add(i, x);
					inserted = true;
					break;
				}
				i++;
			}
			if (!inserted) {
				ordered.add(x);
			}
		}

	}

	public int getColorLength(Color color) {
		return getColorHi2Low(color).size();
	}

	public List<Card> getCardsHighToLow() {
		if (orderedCards != null) {
			return orderedCards;
		}
		List<Card> orderedCards = new ArrayList<Card>();
		for (Color color : Color.list) {
		  orderedCards.addAll(getColorHi2Low(color));	
		}
		this.orderedCards = orderedCards;
		return orderedCards;
	}

	public int getLongestColorLength() {
		int result = 0;
		for (Color color : Color.list) {
			if (result < getColorLength(color)) {
				result = getColorLength(color);
			}
		}
		return result;
	}

	public boolean contains(Card card) {
		if (cards.size() == 0 && card == null) {
			return true;
		} else {
			return cards.contains(card);
		}
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}

	

}
