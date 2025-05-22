package citadels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.HashSet;

public class Player
{
  private Character character;
  private int gold;
  private int id;
  private boolean crown;
  private ArrayList<Card> cards;
  private HashMap<Card,Integer> placedCards;
  private boolean killed;
  private boolean robbed;
  private Player robber;
  private int score;

  public Player(int id)
  {
    character = null;
    gold = 0;
    this.id = id;
    crown = false;
    cards = new ArrayList<>();
    placedCards = new HashMap<>();
    killed = false;
    robbed = false;
    robber = null;
    score = 0;
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

  public Card findPlacedCardByName(String cardName)
  {
    for (Card c : placedCards.keySet()){
      if (c.getName().equals(cardName) == false) continue;
      return c;
    }
    return null;
  }

  public int getNumCards()
  {
    return cards.size();
  }

  public ArrayList<Card> getCards()
  {
    return cards;
  }

  public void setCards(ArrayList<Card> cards)
  {
    this.cards = cards;
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

  public boolean isRobbed()
  {
    return robbed;
  }

  public Player getRobber()
  {
    return robber;
  }

  public void setRobber(Player robber)
  {
    this.robber = robber;
    this.robbed = true;
  }

  public int getScore()
  {
    return score;
  }

  public void setScore(int score)
  {
    this.score = score;
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
    int cost = card.getCost();
    if (hasPlacedDistrict("Factory") && card.getColor().equals("purple")){
      cost--;
      System.out.printf("Factory: cost of building reduced by 1 to %d.\n", cost);
    }
    if (gold < cost){
      System.out.println("You don't have enough gold.");
      return;
    }
    character.decNumBuild();
    gold -= cost;
    placedCards.put(card, 1);
    cards.remove(pos);
    System.out.printf("Built %s\n", card.display());
  }

  public void removePlacedCard(Card card)
  {
    if (placedCards.containsKey(card) == false) return;
    int cnt = placedCards.get(card);
    placedCards.remove(card);
    if (--cnt > 0){
      placedCards.put(card, cnt);
    }
  }

  public void addPlacedCard(Card card)
  {
    placedCards.put(card, 1);
  }

  public HashMap<Card,Integer> getPlacedCards()
  {
    return placedCards;
  }

  public boolean hasPlacedDistrict(String districtName)
  {
    for (Card c : placedCards.keySet()){
      if (c.getName().equals(districtName)) return true;
    }
    return false;
  }

  public boolean isCityCompleted(App app)
  {
    int numDistrictGoal = app.getNumDistrictGoal();
    return (placedCards.size() >= numDistrictGoal);
  }

  public int countPlacedCardColor(String color)
  {
    int cnt = 0;
    for (Card c : placedCards.keySet()){
      if (c.getColor().equals(color)) cnt++;
    }
    return cnt;
  }

  public String getActionList()
  {
    String charAction = "character's action:\n" + character.actionDesc();
    String cardAction = "special cards' action:\n";
    return charAction+"\n"+cardAction;
  }

  public void action(App app, String[] tokens)
  {
    if (character.action(app, tokens, this)) return;
    System.out.println("Invalid action.");
  }

  public void scoreCalculation(App app)
  {
    System.out.printf("Player %d's score :\n", id);
    int score = 0;
    HashMap<String,Integer> colorCount = new HashMap<>();
    for (Card c : placedCards.keySet()){
      score += c.getPoint();
      System.out.printf("Scored %d points with %s district card.\n", c.getPoint(), c.getName());
      String col = c.getColor();
      if (colorCount.containsKey(col) == false){
        colorCount.put(col, 1);
      }
      else{
        colorCount.put(col, colorCount.get(col)+1);
      }
    }
    if (colorCount.size() == 5){
      score += 3;
      System.out.printf("Scored 3 points for having at least one district of each type.\n");
    }
    if (isCityCompleted(app)){
      if (id == app.getFirstCompletePlayerId()){
        score += 4;
        System.out.printf("Scored 4 points for being the first to complete their city.\n");
      }
      else{
        score += 2;
        System.out.printf("Scored 2 points for completing their city.\n");
      }
    }
    if (hasPlacedDistrict("Imperial Treasury")){
      score += getGold();
      System.out.printf("Imperial Treasury: scored %d points for having %d golds.\n", getGold(), getGold());
    }
    if (hasPlacedDistrict("Map Room")){
      score += getNumCards();
      System.out.printf("Map Room: scored %d points for having %d cards.\n", getNumCards(), getNumCards());
    }
    if (hasPlacedDistrict("Wishing Well")){
      int add = (colorCount.containsKey("purple") ? colorCount.get("purple")-1 : 0);
      score += add;
      System.out.printf("Wishing Well: scored %d points for each other purple cards.\n", add);
    }
    System.out.printf("Total score: %d.\n", score);
    this.score = score;
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