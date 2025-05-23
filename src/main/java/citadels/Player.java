package citadels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Random;
import org.json.simple.*;

public class Player
{
  private Character character;
  private int gold;
  private int id;
  private ArrayList<Card> cards;
  private ArrayList<Card> placedCards;
  private boolean killed;
  private boolean robbed;
  private Player robber;
  private int score;

  public Player(int id)
  {
    character = null;
    gold = 0;
    this.id = id;
    cards = new ArrayList<>();
    placedCards = new ArrayList<>();
    killed = false;
    robbed = false;
    robber = null;
    score = 0;
  }

  public int getId()
  {
    return id;
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
    for (Card c : placedCards){
      if (c.getName().equals(cardName) == false) continue;
      return c;
    }
    return null;
  }

  public Card findPlacedCardByPos(int pos)
  {
    if (pos < 0 || pos >= placedCards.size()) return null;
    return placedCards.get(pos);
  }

  public void removePlacedCard(App app, String cardName)
  {
    if (hasPlacedDistrict(cardName) == false) return;
    for (int i=0; i<placedCards.size(); i++){
      if (placedCards.get(i).getName().equals(cardName) == false) continue;
      placedCards.get(i).freeMuseum(app);
      placedCards.remove(i);
      break;
    }
  }

  public void addPlacedCard(Card card)
  {
    placedCards.add(card);
  }

  public ArrayList<Card> getPlacedCards()
  {
    return placedCards;
  }

  public boolean hasPlacedDistrict(String districtName)
  {
    for (Card c : placedCards){
      if (c.getName().equals(districtName)) return true;
    }
    return false;
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

  public void removeCardAtPos(int pos)
  {
    if (pos < 0 || pos >= cards.size()) return;
    cards.remove(pos);
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

  public void resetPlayer()
  {
    killed = false;
    robbed = false; robber = null;
  }

  public void showCity()
  {
    for (Card card : placedCards){
      System.out.printf("%s (%s), points: %d\n", card.getName(), card.getColor(), card.getPoint());
    }
  }

  public void build(App app, int pos)
  {
    boolean human = (id == 1);
    if (isKilled()){
      if (human) System.out.printf("You can't build district cards after being assassinated\n");
      return;
    }
    if (pos < 0 || pos >= cards.size()){
      if (human) System.out.println("Invalid card position");
      return;
    }
    if (character.getNumBuild() <= 0){
      if (human) System.out.println("You can no longer build during this round.");
      return;
    }
    Card card = cards.get(pos);
    if (hasPlacedDistrict("Quarry")){
      boolean duplicate = false;
      HashMap<String,Integer> cardCount = new HashMap<>();
      for (Card c : placedCards){
        String name = c.getName();
        if (!cardCount.containsKey(name)){
          cardCount.put(name, 1);
        }
        else{
          cardCount.put(name, cardCount.get(name)+1);
        }
        duplicate |= (cardCount.get(name) > 1);
      }
      if (duplicate == true){
        if (human) System.out.printf("This district is already built and player %d already have one duplicate district\n", id);
        return;
      }
      else if (hasPlacedDistrict(card.getName())){
        System.out.printf("Quarry: player %d may have one duplicate district\n", id);
      }
    }
    else if (hasPlacedDistrict(card.getName())){
      if (human) System.out.println("This building is already built");
      return;
    }
    int cost = card.getCost();
    if (hasPlacedDistrict("Factory") && card.getColor().equals("purple")){
      cost--;
      System.out.printf("Factory: cost of building reduced by 1 to %d.\n", cost);
    }
    if (gold < cost){
      if (human) System.out.println("You don't have enough gold.");
      return;
    }
    character.decNumBuild();
    gold -= cost;
    placedCards.add(card);
    cards.remove(pos);
    card.setRoundBuilt(app.getRound());
    System.out.printf("Player %d built %s\n", id, card.display());
    if (card.getName().equals("Bell Tower")){
      app.setNumDistrictGoal(7);
    }
    if (card.getName().equals("Lighthouse")){
      lighthouse(app);
    }
  }

  public void lighthouse(App app)
  {
    Deck deck = app.getDeck();
    if (deck.size() == 0) return;
    int pos = -1;
    System.out.printf("Lighthouse: Player %d may choose one card from deck [%d..%d]\n", id, 1, deck.size());
    if (id != 1){
      Random rand = new Random(System.currentTimeMillis());
      pos = rand.nextInt(deck.size());
    }
    else{
      Scanner scanner = app.getScanner();
      System.out.printf("Cards in deck:\n");
      for (int i=0; i<deck.size(); i++){
        Card c = deck.getCard(i);
        System.out.printf("%d %s\n", i+1, c.display());
      }
      while (true){
        System.out.print("> ");
        String res = scanner.nextLine();
        try{
          pos = Integer.parseInt(res);
        }
        catch (Exception e){
          System.out.println("Invalid card position");
          continue;
        }
        if (pos < 1 || pos > deck.size()){
          System.out.println("Invalid card position");
          continue;
        }
        break;
      }
      pos--;
    }
    takeCard(deck.getCard(pos));
    if (id == 1){
      System.out.printf("Player %d chose card %s\n", id, deck.getCard(pos).display());
    }
    else{
      System.out.printf("Player %d picked a card\n", id);
    }
    deck.removeCard(pos);
    deck.shuffle();
  }

  public boolean isCityCompleted(App app)
  {
    int numDistrictGoal = app.getNumDistrictGoal();
    return (placedCards.size() >= numDistrictGoal);
  }

  public int countPlacedCardColor(String color)
  {
    int cnt = 0;
    for (Card c : placedCards){
      if (c.getColor().equals(color)) cnt++;
    }
    return cnt;
  }

  public void resetCard()
  {
    for (Card c : placedCards){
      c.setUsed(false);
    }
  }

  public String getActionList()
  {
    String charAction = "character's action:\n" + character.actionDesc();
    String cardAction = "special cards' action:\n";
    for (Card c : placedCards){
      String s = c.actionDesc();
      if (s.equals("")) continue;
      cardAction += s + "\n";
    }
    return charAction+"\n"+cardAction;
  }

  public void action(App app, String[] tokens)
  {
    if (character.action(app, tokens, this)) return;
    for (Card c : placedCards){
      if (c.action(app, tokens, this)) return;
    }
    System.out.println("Invalid action.");
  }

  public boolean promptGraveyard(App app, Card destroyedCard)
  {
    if (getGold() < 1) return false;
    if (id != 1) return false;
    Scanner scanner = app.getScanner();
    System.out.printf("Player %d may pay 1 gold to take the card [y/n]: %s.\n", id, destroyedCard.display());
    String res = "";
    while (true){
      res = scanner.nextLine();
      if (res.equals("y") == false && res.equals("n") == false){
        System.out.println("Invalid choice");
        continue;
      }
      break;
    }
    if (res.equals("y")){
      takeCard(destroyedCard);
      return true;
    }
    return false;
  }

  public void scoreCalculation(App app)
  {
    System.out.printf("Player %d's score :\n", id);
    int score = 0;
    HashMap<String,Integer> colorCount = new HashMap<>();
    for (Card c : placedCards){
      score += c.getPoint();
      System.out.printf("Scored %d points with %s district card.\n", c.getPoint(), c.display());
      String col = c.getColor();
      if (colorCount.containsKey(col) == false){
        colorCount.put(col, 1);
      }
      else{
        colorCount.put(col, colorCount.get(col)+1);
      }
    }
    if (hasPlacedDistrict("Haunted City") && findPlacedCardByName("Haunted City").getRoundBuilt() != app.getRound()){
      Card card = findPlacedCardByName("Haunted City");
      String targetCol = "purple";
      if (colorCount.get("purple") >= 2 && colorCount.size() == 4){
        String[] colors = {"red", "blue", "green", "yellow"};
        for (String c : colors){
          if (colorCount.containsKey(c)) continue;
          targetCol = c;
          colorCount.put("purple", colorCount.get("purple")-1);
          colorCount.put(targetCol, 1);
          break;
        }
      }
      card.setColor(targetCol);
      System.out.printf("For the purposes of victory points, the Haunted City's color is considered to be %s.\n", targetCol);
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
    if (hasPlacedDistrict("Museum")){
      Card card = findPlacedCardByName("Museum");
      int add = card.getMuseumSize();
      score += add;
      System.out.printf("Museum: scored %d points for each card under Museum\n", add);
    }
    System.out.printf("Total score: %d.\n", score);
    this.score = score;
  }

  public JSONObject toJSON()
  {
    JSONObject obj = new JSONObject();
    obj.put("character",character.toJSON());
    obj.put("gold",gold);
    obj.put("id",id);
    obj.put("killed",killed);
    obj.put("robbed",robbed);
    if (robber == null){
      obj.put("robber",null);
    }
    else{
      obj.put("robber",robber.toJSON());
    }
    obj.put("score",score);
    JSONArray cardsJson = new JSONArray();
    for (Card c : cards){
      cardsJson.add(c.toJSON());
    }
    obj.put("cards", cardsJson);
    JSONArray placedCardsJson = new JSONArray();
    for (Card c : placedCards){
      placedCardsJson.add(c.toJSON());
    }
    obj.put("placedCards", placedCardsJson);
    return obj;
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