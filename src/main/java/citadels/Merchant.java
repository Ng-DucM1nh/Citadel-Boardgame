package citadels;

import org.json.simple.*;

/**
 * The Merchant character class, a specific role in Citadels.
 * Implements unique behavior for merchant.
 */
public class Merchant extends Character
{
/**
 * Merchant method defines merchant's special skill.
 */
  public Merchant()
  {
    // Set Merchant's name and description
    name = "Merchant";
    description = "Gains one gold for each green (trade) district in their city. Gains one extra gold.";
  }

/**
 * Merchant method defines merchant's special skill.
 */
  public Merchant(JSONObject obj)
  {
    // Initialize Merchant from JSON object
    name = (String)obj.get("name");
    description = (String)obj.get("description");
    numBuild = ((Long)obj.get("numBuild")).intValue();
    numAction = ((Long)obj.get("numAction")).intValue();
  }

/**
 * passiveSkill method defines merchant's special skill.
 */
  public void passiveSkill(App app, Player player)
  {
    // Merchant receives 1 extra gold and gold for each green district
    System.out.printf("Player %d receives 1 extra gold.\n", player.getId());
    player.addGold(1);
    int gold = player.countPlacedCardColor("green");
    player.addGold(gold);
    System.out.printf("Player %d receives %d gold from their signature district color.\n", player.getId(), gold);
  }
}