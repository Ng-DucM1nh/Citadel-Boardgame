package citadels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Player
{
  private Character character;
  private int gold;
  private int id;
  private boolean crown;
  private ArrayList<Card> cards;
  private HashMap<Card,Integer> placedCards;
  private boolean killed;

  public Player(int id)
  {
    character = null;
    gold = 0;
    this.id = id;
    crown = false;
    cards = new ArrayList<>();
    placedCards = new HashMap<>();
    killed = false;
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

  public int getNumCards()
  {
    return cards.size();
  }

  public ArrayList<Card> getCards()
  {
    return cards;
  }

  public Card getCardAtPos(int pos)
  {
    if (pos < 0 || pos >= cards.size()) return null;
    return cards.get(pos);
  }

  public Character getCharacter()
  {
    return character;
  }

  public void setCharacter(Character character)
  {
    this.character = character;
  }

  public int getGold()
  {
    return gold;
  }

  public void addGold(int a)
  {
    gold += a;
  }

  public boolean isKilled()
  {
    return killed;
  }

  public void setKilled(boolean killed)
  {
    this.killed = killed;
  }

  public void build(int pos)
  {
    System.out.printf("**build called with pos = %d**\n", pos);
    if (pos < 0 || pos >= cards.size()){
      System.out.println("Invalid card position");
      return;
    }
    if (character.getNumBuild() <= 0){
      System.out.println("You can no longer build during this round.");
      return;
    }
    Card card = cards.get(pos);
    if (placedCards.containsKey(card)){
      System.out.println("This building is already built");
      return;
    }
    if (gold < card.getCost()){
      System.out.println("U too poor");
      return;
    }
    character.decNumBuild();
    gold -= card.getCost();
    placedCards.put(card, 1);
    cards.remove(pos);
    System.out.printf("Built %s\n", card.display());
  }

  public HashMap<Card,Integer> getPlacedCards()
  {
    return placedCards;
  }

  // public String promptSkill(Scanner scanner, ArrayList<Character> characters)
  // {
  //   if (character.getName().equals("Assassin") == false && character.getName().equals("Thief") == false) return;
  //   if (character.getName().equals("Assassin")){
  //     character.promptSkill(scanner, characters);
  //   }
  // }

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