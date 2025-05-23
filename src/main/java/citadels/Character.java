package citadels;

import java.util.Scanner;
import java.util.ArrayList;

import org.json.simple.*;

public class Character
{
  String name;
  String description;
  int numBuild = 1;
  int numAction = 1;

  public void desc()
  {
    System.out.printf("%s: %s\n", name, description);
  }

  public String getName()
  {
    return name;
  }

  public void promptSkill(App app, Player player){}

  public int getNumBuild()
  {
    return numBuild;
  }

  public void decNumBuild()
  {
    numBuild--;
  }

  public void passiveSkill(App app, Player player){}

  public String actionDesc() {return "";}

  public boolean action(App app, String[] tokens, Player player) {return false;}

  public JSONObject toJSON()
  {
    JSONObject obj = new JSONObject();
    obj.put("name",name);
    obj.put("description",description);
    obj.put("numBuild",numBuild);
    obj.put("numAction",numAction);
    return obj;
  }
}