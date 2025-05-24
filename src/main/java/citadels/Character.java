package citadels;

import java.util.Scanner;
import java.util.ArrayList;

import org.json.simple.*;

/**
 * Represents a character card in the Citadels game.
 * Character cards define roles and abilities per round.
 */
public class Character
{
  String name;
  String description;
  int numBuild = 1;
  int numAction = 1;

/**
 * desc method for character-specific logic.
 */
  public void desc()
  {
    System.out.printf("%s: %s\n", name, description);
  }

  public String getName()
  {
    return name;
  }

/**
 * promptSkill method for character-specific logic.
 */
  public void promptSkill(App app, Player player){}

  public int getNumBuild()
  {
    return numBuild;
  }

/**
 * decNumBuild method for character-specific logic.
 */
  public void decNumBuild()
  {
    numBuild--;
  }

/**
 * passiveSkill method for character-specific logic.
 */
  public void passiveSkill(App app, Player player){}

/**
 * actionDesc method for character-specific logic.
 */
  public String actionDesc() {return "";}

/**
 * action method for character-specific logic.
 */
  public boolean action(App app, String[] tokens, Player player) {return false;}

/**
 * toJSON method for character-specific logic.
 */
  public JSONObject toJSON()
  {
    JSONObject obj = new JSONObject();
    obj.put("name",name);
    obj.put("description",description);
    obj.put("numBuild",numBuild);
    obj.put("numAction",numAction);
    return obj;
  }

  public static Character getCharFromJsonObj(JSONObject charJson)
  {
    String name = (String)charJson.get("name");
    if (name.equals("Assassin")){
      return new Assassin(charJson);
    }
    if (name.equals("Thief")){
      return new Thief(charJson);
    }
    if (name.equals("Magician")){
      return new Magician(charJson);
    }
    if (name.equals("King")){
      return new King(charJson);
    }
    if (name.equals("Bishop")){
      return new Bishop(charJson);
    }
    if (name.equals("Merchant")){
      return new Merchant(charJson);
    }
    if (name.equals("Architect")){
      return new Architect(charJson);
    }
    if (name.equals("Warlord")){
      return new Warlord(charJson);
    }
    return null;
  }
}