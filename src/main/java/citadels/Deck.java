package citadels;

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

import org.json.simple.*;

/**
 * Represents the deck of district cards used in the Citadels game.
 * Responsible for shuffling and managing draw operations.
 */
public class Deck
{
  private ArrayList<Card> cards;

/**
 * Deck method for managing the deck operations.
 */
  public Deck()
  {
    // Initialize empty deck
    cards = new ArrayList<>();
  }

/**
 * Deck method for managing the deck operations.
 */
  public Deck(JSONArray arr)
  {
    // Initialize deck from JSON array
    cards = new ArrayList<>();
    for (int i=0; i<arr.size(); i++){
      cards.add(new Card((JSONObject)arr.get(i)));
    }
  }

/**
 * addCard method for managing the deck operations.
 */
  public void addCard(Card card)
  {
    // Add card to deck
    cards.add(card);
  }

  public Card getCard(int pos)
  {
    // Get card at position, or null if out of bounds
    if (pos < 0 || pos >= cards.size()) return null;
    return cards.get(pos);
  }

/**
 * removeCard method for managing the deck operations.
 */
  public void removeCard(int pos)
  {
    // Remove card at position if valid
    if (pos < 0 || pos >= cards.size()) return;
    cards.remove(pos);
  }

/**
 * shuffle method for managing the deck operations.
 */
  public void shuffle()
  {
    // Shuffle the deck
    Collections.shuffle(cards, new Random(System.currentTimeMillis()));
  }

/**
 * pop method for managing the deck operations.
 */
  public Card pop()
  {
    // Remove and return the top card, or null if empty
    if (cards.size() == 0) return null;
    Card card = cards.get(0);
    cards.remove(0);
    return card;
  }

/**
 * size method for managing the deck operations.
 */
  public int size()
  {
    // Return number of cards in deck
    return cards.size();
  }

/**
 * toJSON method for managing the deck operations.
 */
  public JSONArray toJSON()
  {
    // Convert deck to JSON array
    JSONArray arr = new JSONArray();
    for (Card c : cards){
      arr.add(c.toJSON());
    }
    return arr;
  }

/**
 * disp method for managing the deck operations.
 */
  public void disp()
  {
    for (Card c : cards){
      System.out.println(c.display());
    }
  }

/**
 * betterDisp method for managing the deck operations.
 */
  public void betterDisp()
  {
    for (Card c : cards){
      System.out.println(c.disp());
    }
  }
}