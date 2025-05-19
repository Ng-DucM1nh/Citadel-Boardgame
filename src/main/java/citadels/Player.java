package citadels;

import java.util.ArrayList;

public class Player
{
  private Character character;
  private int gold;
  private int id;
  private boolean crown;
  private ArrayList<Card> cards;
  private ArrayList<Card> placedCards;

  public Player(int id)
  {
    character = null;
    gold = 0;
    this.id = id;
    crown = false;
    cards = new ArrayList<>();
    placedCards = new ArrayList<>();
  }

  public int getId()
  {
    return id;
  }

  public boolean hasCrown()
  {
    return crown;
  }

  public void setCrown(boolean crown)
  {
    this.crown = crown;
  }

  public void takeCard(Card card)
  {
    if (card == null) return;
    cards.add(card);
  }

  public int getNumPlacedCards()
  {
    return placedCards.size();
  }

  public void setCharacter(Character character)
  {
    this.character = character;
  }

  public String disp()
  {
    return "player"+String.valueOf(id);
  }
  public void showCard()
  {
    for (Card c : cards){
      System.out.println(c.disp());
    }
  }
}