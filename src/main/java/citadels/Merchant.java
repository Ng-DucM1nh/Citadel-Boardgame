package citadels;

import org.json.simple.*;

public class Merchant extends Character
{
  public Merchant()
  {
    name = "Merchant";
    description = "Gains one gold for each green (trade) district in their city. Gains one extra gold.";
  }

  public Merchant(JSONObject obj)
  {
    name = (String)obj.get("name");
    description = (String)obj.get("description");
    numBuild = ((Long)obj.get("numBuild")).intValue();
    numAction = ((Long)obj.get("numAction")).intValue();
  }

  public void passiveSkill(App app, Player player)
  {
    System.out.printf("Player %d receives 1 extra gold.\n", player.getId());
    player.addGold(1);
    int gold = player.countPlacedCardColor("green");
    player.addGold(gold);
    System.out.printf("Player %d receives %d gold from their signature district color.\n", player.getId(), gold);
  }
}