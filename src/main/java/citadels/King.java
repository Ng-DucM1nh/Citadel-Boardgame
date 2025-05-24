package citadels;

import org.json.simple.*;

public class King extends Character
{
  public King()
  {
    name = "King";
    description = "Gains one gold for each yellow (noble) district in their city. They receive the crown token and will be the first to choose characters on the next round.";
  }

  public King(JSONObject obj)
  {
    name = (String)obj.get("name");
    description = (String)obj.get("description");
    numBuild = ((Long)obj.get("numBuild")).intValue();
    numAction = ((Long)obj.get("numAction")).intValue();
  }

  public void passiveSkill(App app, Player player)
  {
    int gold = player.countPlacedCardColor("yellow");
    player.addGold(gold);
    System.out.printf("Player %d receives %d gold from their signature district color.\n", player.getId(), gold);
    app.assignCrown(player.getId());
    System.out.printf("Player %d receives the crown and will be the first to choose characters on the next round.\n", player.getId());
  }
}