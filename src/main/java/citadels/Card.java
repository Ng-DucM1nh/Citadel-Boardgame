package citadels;

import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.*;

/**
 * Base class representing a generic card in the Citadels game.
 */
public class Card
{
  private String name;
  private String color;
  private int cost;
  private int destroyCost;
  private int point;
  private String description;
  private int roundBuilt;
  private boolean used;
  ArrayList<Card> museum;

/**
 * Card method for card-specific behavior.
 */
  public Card(String name, String color, int cost, int point, String description)
  {
    this.name = name;
    this.color = color;
    this.cost = cost;
    this.destroyCost = cost-1;
    this.point = point;
    this.description = description;
    this.roundBuilt = -1;
    used = false;
    museum = new ArrayList<>();

    if (name.equals("Dragon Gate") || name.equals("University")){
      this.point = 8;
    }
  }

/**
 * Card method for card-specific behavior.
 */
  public Card(JSONObject obj)
  {
    name = (String)obj.get("name");
    color = (String)obj.get("color");
    cost = ((Long)obj.get("cost")).intValue();
    destroyCost = ((Long)obj.get("destroyCost")).intValue();
    point = ((Long)obj.get("point")).intValue();
    description = (String)obj.get("description");
    roundBuilt = ((Long)obj.get("roundBuilt")).intValue();
    used = (boolean)obj.get("used");
    JSONArray museumJson = (JSONArray)obj.get("museum");
    museum = new ArrayList<>();
    for (int i=0; i<museumJson.size(); i++){
      museum.add(new Card((JSONObject)museumJson.get(i)));
    }
  }

  public String getName()
  {
    return name;
  }

  public String getColor()
  {
    return color;
  }

  public void setColor(String color)
  {
    this.color = color;
  }

  public int getCost()
  {
    return cost;
  }

  public int getDestroyCost()
  {
    return destroyCost;
  }

  public int getPoint()
  {
    return point;
  }

  public String getDesc()
  {
    return description;
  }

  public int getRoundBuilt()
  {
    return roundBuilt;
  }

  public void setRoundBuilt(int round)
  {
    roundBuilt = round;
  }

  public void setUsed(boolean used)
  {
    this.used = used;
  }

  public int getMuseumSize()
  {
    return museum.size();
  }

/**
 * freeMuseum method for card-specific behavior.
 */
  public void freeMuseum(App app)
  {
    Deck deck = app.getDeck();
    for (Card c : museum){
      deck.addCard(c);
    }
  }

/**
 * display method for card-specific behavior.
 */
  public String display()
  {
    String inf = name+" ["+color+String.valueOf(cost)+"]";
    if (name.equals("Museum")){
      inf += " {";
      for (Card c : museum){
        inf += c.display() + " ";
      }
      inf += "}";
    }
    return inf;
  }

/**
 * actionDesc method for card-specific behavior.
 */
  public String actionDesc()
  {
    if (name.equals("Laboratory")){
      return "action Laboratory <pos1>";
    }
    if (name.equals("Smithy")){
      return "action Smithy";
    }
    if (name.equals("Armory")){
      return "action Armory <playerId> <District_Name_Seperated_By_Underscore>";
    }
    if (name.equals("Museum")){
      return "action Museum <pos>";
    }
    return "";
  }

/**
 * action method for card-specific behavior.
 */
  public boolean action(App app, String[] tokens, Player player)
  {
    if (tokens[1].equals(name) && name.equals("Laboratory")){
      actionLaboratory(app, tokens, player);
      return true;
    }
    if (tokens[1].equals(name) && name.equals("Smithy")){
      actionSmithy(app, tokens, player);
      return true;
    }
    if (tokens[1].equals(name) && name.equals("Armory")){
      actionArmory(app, tokens, player);
      return true;
    }
    if (tokens[1].equals(name) && name.equals("Museum")){
      actionMuseum(app, tokens, player);
      return true;
    }
    return false;
  }

/**
 * actionLaboratory method for card-specific behavior.
 */
  public void actionLaboratory(App app, String[] tokens, Player player)
  {
    if (tokens.length != 3){
      System.out.println("Invalid arguments");
      return;
    }
    if (used){
      System.out.println("You can only use this card's ability once per turn");
      return;
    }
    int pos = -1;
    try{
      pos = Integer.parseInt(tokens[2]);
    }
    catch (Exception e){
      System.out.println("Invalid card position");
      return;
    }
    if (pos < 1 || pos > player.getNumCards()){
      System.out.println("Invalid card position");
      return;
    }
    pos--;
    ArrayList<Card> cards = player.getCards();
    Card card = cards.get(pos);
    cards.remove(pos);
    player.setCards(cards);
    player.addGold(1);
    setUsed(true);
    System.out.printf("Player %d discard card %s for 1 gold\n", player.getId(), card.display());
  }

/**
 * actionSmithy method for card-specific behavior.
 */
  public void actionSmithy(App app, String[] tokens, Player player)
  {
    if (tokens.length != 2){
      System.out.println("Invalid arguments");
      return;
    }
    if (used){
      System.out.println("You can only use this card's ability once per turn");
      return;
    }
    if (player.getGold() < 2){
      System.out.println("You don't have enough gold");
      return;
    }
    Deck deck = app.getDeck();
    if (deck.size() == 0){
      System.out.println("deck empty");
      return;
    }
    player.addGold(-2);
    app.drawPick(3, 3, player);
    setUsed(true);
  }

/**
 * actionArmory method for card-specific behavior.
 */
  public void actionArmory(App app, String[] tokens, Player player)
  {
    if (tokens.length != 4){
      System.out.println("Invalid arguments");
      return;
    }
    ArrayList<Player> players = app.getPlayers();
    int playerId = -1;
    try{
      playerId = Integer.parseInt(tokens[2]);
    }
    catch (Exception e){
      System.out.println("Invalid player id");
      return;
    }
    if (playerId < 1 || playerId > players.size()){
      System.out.println("Invalid player id");
      return;
    }
    playerId--;
    String districtName = "";
    String[] districtNameTokens = tokens[3].split("_");
    for (int i=0; i<districtNameTokens.length; i++){
      districtName += districtNameTokens[i];
      if (i < districtNameTokens.length-1) districtName += " ";
    }
    Player target = players.get(playerId);
    if (!target.hasPlacedDistrict(districtName)){
      System.out.println("Invalid district name");
      return;
    }
    Card targetCard = target.findPlacedCardByName(districtName);
    player.removePlacedCard(app, name);
    target.removePlacedCard(app, targetCard.getName());
    System.out.printf("Player %d destroy their Armory to destroy player %d's %s\n", player.getId(), target.getId(), targetCard.display());
  }

/**
 * actionMuseum method for card-specific behavior.
 */
  public void actionMuseum(App app, String[] tokens, Player player)
  {
    if (tokens.length != 3){
      System.out.println("Invalid arguments");
      return;
    }
    if (used){
      System.out.println("You can only use this card's ability once per turn");
      return;
    }
    int pos = -1;
    try{
      pos = Integer.parseInt(tokens[2]);
    }
    catch (Exception e){
      System.out.println("Invalid card position");
      return;
    }
    if (pos < 1 || pos > player.getNumCards()){
      System.out.println("Invalid card position");
      return;
    }
    pos--;
    Card card = player.getCardAtPos(pos);
    museum.add(card);
    player.removeCardAtPos(pos);
    setUsed(true);
    System.out.printf("Player %d puts card %s under their Museum\n", player.getId(), card.display());
  }

/**
 * destroy method for card-specific behavior.
 */
  public void destroy(App app)
  {
    if (name.equals("Bell Tower")){
      app.setNumDistrictGoal(8);
    }
  }

/**
 * disp method for card-specific behavior.
 */
  public String disp()
  {
    return name+" "+color+" "+String.valueOf(cost)+" "+String.valueOf(point)+" "+description;
  }

/**
 * toJSON method for card-specific behavior.
 */
  public JSONObject toJSON()
  {
    JSONObject obj = new JSONObject();
    obj.put("name", name);
    obj.put("color",color);
    obj.put("cost",cost);
    obj.put("destroyCost",destroyCost);
    obj.put("point",point);
    obj.put("description",description);
    obj.put("roundBuilt",roundBuilt);
    obj.put("used",used);
    JSONArray museumjson = new JSONArray();
    for (Card c : this.museum){
      museumjson.add(c.toJSON());
    }
    obj.put("museum", museumjson);
    return obj;
  }
}