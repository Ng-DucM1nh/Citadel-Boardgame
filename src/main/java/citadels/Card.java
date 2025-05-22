package citadels;

import java.util.ArrayList;
import java.util.HashMap;

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

    if (name.equals("Dragon Gate") || name.equals("University")){
      this.point = 8;
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

  public String display()
  {
    return name+" ["+color+String.valueOf(cost)+"]";
  }

  public String actionDesc()
  {
    if (name.equals("Laboratory")){
      return "action Laboratory <pos1>";
    }
    if (name.equals("Smithy")){
      return "Once during your turn, you may pay two gold to draw 3 district cards.";
    }
    if (name.equals("Armory")){
      return "During your turn, you may destroy the Armory in order to destory any other district card of your choice in another player's city";
    }
    if (name.equals("Museum")){
      return "On your turn, you may place on district card from your hand face down under the Museum.  At the end of the game, you score one extra point for every card under the Museum";
    }
    return "";
  }

  public boolean action(App app, String[] tokens, Player player)
  {
    if (tokens[1].equals("Laboratory")){
      actionLaboratory(app, tokens, player);
      return true;
    }
    if (tokens[1].equals("Smithy")){
      actionSmithy(app, tokens, player);
      return true;
    }
    return false;
  }

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

  public void actionSmithy(App app, String[] tokens, Player player)
  {
    if (tokens.length != 2){
      System.out.println("Invalid arguments");
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
  }

  public void destroy(App app)
  {
    if (name.equals("Bell Tower")){
      app.setNumDistrictGoal(8);
    }
  }

  public String disp()
  {
    return name+" "+color+" "+String.valueOf(cost)+" "+String.valueOf(point);
  }
}