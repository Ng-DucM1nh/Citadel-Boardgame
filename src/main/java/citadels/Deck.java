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

  public void shuffle()
  {
    Collections.shuffle(cards, new Random(System.currentTimeMillis()));
  }

  public void disp()
  {
    for (Card c : cards){
      System.out.println(c.disp());
    }
  }
}