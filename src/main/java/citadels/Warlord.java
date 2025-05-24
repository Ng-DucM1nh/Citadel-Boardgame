package citadels;

import java.util.ArrayList;

import org.json.simple.*;

/**
 * The Warlord character class, a specific role in Citadels.
 * Implements unique behavior for warlord.
 */
public class Warlord extends Character
{
/**
 * Warlord method defines warlord's special skill.
 */
  public Warlord()
  {
    name = "Warlord";
    description = "Gains one gold for each red (military) district in their city. You can destroy one district of your choice by paying one fewer gold than its building cost. You cannot destroy a district in a city with 8 or more districts.";
  }

/**
 * Warlord method defines warlord's special skill.
 */
  public Warlord(JSONObject obj)
  {
    name = (String)obj.get("name");
    description = (String)obj.get("description");
    numBuild = ((Long)obj.get("numBuild")).intValue();
    numAction = ((Long)obj.get("numAction")).intValue();
  }

/**
 * passiveSkill method defines warlord's special skill.
 */
  public void passiveSkill(App app, Player player)
  {
    int gold = player.countPlacedCardColor("red");
    player.addGold(gold);
    System.out.printf("Player %d receives %d gold from their signature district color.\n", player.getId(), gold);
  }

/**
 * actionDesc method defines warlord's special skill.
 */
  public String actionDesc()
  {
    return "action destroy <playerId> <District_Name_Seperated_By_Underscore>\n";
  }

/**
 * action method defines warlord's special skill.
 */
  public boolean action(App app, String[] tokens, Player player)
  {
    if (tokens.length != 4) return false;
    if (tokens[1].equals("destroy") == false) return false;
    boolean human = (player.getId() == 1);
    if (player.isKilled()){
      if (human) System.out.println("You can't use the character's ability after being assassinated");
      return true;
    }
    if (numAction <= 0){
      if (human) System.out.println("You can no longer use the character's ability for this round.");
      return true;
    }
    int targetId = -1;
    try{
      targetId = Integer.parseInt(tokens[2]);
    }
    catch (Exception e){
      if (human) System.out.println("Invalid playerId");
      return true;
    }
    ArrayList<Player> players = app.getPlayers();
    if (targetId < 1 || targetId > players.size()){
      if (human) System.out.println("Invalid playerId");
      return true;
    }
    targetId--;
    Player target = players.get(targetId);
    if (target.getCharacter().getName().equals("Bishop") && !target.isKilled()){
      if (human) System.out.printf("The Bishop's buildings cannot be destroyed by the Warlord, unless they are killed by the Assassin\n");
      return true;
    }
    if (target.isCityCompleted(app)){
      if (human) System.out.println("You can't destroy a district in a completed city.");
      return true;
    }
    String districtName = "";
    String[] districtNameTokens = tokens[3].split("_");
    for (int i=0; i<districtNameTokens.length; i++){
      districtName += districtNameTokens[i];
      if (i < districtNameTokens.length-1) districtName += " ";
    }
    Card targetCard = target.findPlacedCardByName(districtName);
    if (targetCard == null){
      if (human) System.out.println("Invalid card");
      return true;
    }
    if (targetCard.getName().equals("Keep")){
      if (human) System.out.printf("The Keep cannot be destroyed by the Warlord.\n");
      return true;
    }
    int cost = targetCard.getDestroyCost();
    if (target.hasPlacedDistrict("Great Wall") && targetCard.getName().equals("Great Wall") == false){
      cost++;
      System.out.printf("Great Wall: The cost of destroying increases by 1 to %d.\n", cost);
    }
    if (player.getGold() < cost){
      if (human) System.out.printf("You don't have enough gold.\n", player.getId());
      return true;
    }
    target.removePlacedCard(app, targetCard.getName());
    player.addGold(-cost);
    numAction--;
    targetCard.destroy(app);
    System.out.printf("Player %d spent %d gold to destroy Player %d's %s.\n", player.getId(), cost, targetId+1, districtName);
    for (Player p : players){
      if (p.getId() != player.getId() && p.hasPlacedDistrict("Graveyard")){
        if (p.promptGraveyard(app, targetCard)) return true;
      }
    }
    Deck deck = app.getDeck();
    deck.addCard(targetCard);
    return true;
  }
}