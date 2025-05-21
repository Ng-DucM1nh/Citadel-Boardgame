package citadels;

import java.util.ArrayList;

public class Warlord extends Character
{
  private int numAction = 1; 

  public Warlord()
  {
    name = "Warlord";
    description = "Gains one gold for each red (military) district in their city. You can destroy one district of your choice by paying one fewer gold than its building cost. You cannot destroy a district in a city with 8 or more districts.";
  }

  public void passiveSkill(App app, Player player)
  {
    int gold = player.countPlacedCardColor("red");
    player.addGold(gold);
    System.out.printf("Player %d receives %d gold from their signature district color.\n", player.getId(), gold);
  }

  public String actionDesc()
  {
    return "action destroy <playerId> <nameOfDistrict>\n";
  }

  public boolean action(App app, String[] tokens, Player player)
  {
    if (tokens.length != 4) return false;
    if (tokens[1].equals("destroy") == false) return false;
    if (numAction <= 0){
      System.out.println("You can no longer use the character's ability for this round.");
      return true;
    }
    int targetId = -1;
    try{
      targetId = Integer.parseInt(tokens[2]);
    }
    catch (Exception e){
      System.out.println("Invalid playerId");
      return true;
    }
    String districtName = tokens[3];
    ArrayList<Player> players = app.getPlayers();
    if (targetId < 1 || targetId > players.size()){
      System.out.println("Invalid playerId");
      return true;
    }
    targetId--;
    Player target = players.get(targetId);
    if (target.isCityCompleted(app)){
      //////////// not tested yet
      System.out.println("You can't destroy a district in a completed city.");
      return true;
    }
    Card targetCard = target.findPlacedCardByName(districtName);
    if (targetCard == null){
      System.out.println("Invalid card");
      return true;
    }
    int cost = targetCard.getDestroyCost();
    if (player.getGold() < cost){
      System.out.printf("You don't have enough gold.\n", player.getId());
      return true;
    }
    target.removePlacedCard(targetCard);
    player.addGold(-cost);
    Deck deck = app.getDeck();
    deck.addCard(targetCard);
    System.out.printf("Player %d spent %d gold to destroy Player %d's %s.\n", player.getId(), cost, targetId, districtName);
    numAction--;
    return true;
  }
}