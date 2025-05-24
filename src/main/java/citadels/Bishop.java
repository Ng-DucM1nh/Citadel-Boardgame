package citadels;

import org.json.simple.*;

public class Bishop extends Character
{
  public Bishop()
  {
    name = "Bishop";
    description = "Gains one gold for each blue (religious) district in their city. Their buildings cannot be destroyed by the Warlord, unless they are killed by the Assassin.";
  }

  public Bishop(JSONObject obj)
  {
    name = (String)obj.get("name");
    description = (String)obj.get("description");
    numBuild = ((Long)obj.get("numBuild")).intValue();
    numAction = ((Long)obj.get("numAction")).intValue();
  }

  public void passiveSkill(App app, Player player)
  {
    int gold = player.countPlacedCardColor("blue");
    player.addGold(gold);
    System.out.printf("Player %d receives %d gold from their signature district color.\n", player.getId(), gold);
  }
}