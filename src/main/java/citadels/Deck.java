package citadels;

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

public class Deck
{
  private ArrayList<Card> cards;

  public Deck()
  {
    cards = new ArrayList<>();
  }

  public void addCard(Card card)
  {
    cards.add(card);
  }

  public Card getCard(int pos)
  {
    if (pos < 0 || pos >= cards.size()) return null;
    return cards.get(pos);
  }

  public void removeCard(int pos)
  {
    if (pos < 0 || pos >= cards.size()) return;
    cards.remove(pos);
  }

  public void shuffle()
  {
    Collections.shuffle(cards, new Random(System.currentTimeMillis()));
  }

  public Card pop()
  {
    if (cards.size() == 0) return null;
    Card card = cards.get(0);
    cards.remove(0);
    return card;
  }

  public int size()
  {
    return cards.size();
  }

  public void disp()
  {
    for (Card c : cards){
      System.out.println(c.display());
    }
  }

  public void betterDisp()
  {
    for (Card c : cards){
      System.out.println(c.disp());
    }
  }
}