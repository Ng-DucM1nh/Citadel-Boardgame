package citadels;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

import org.json.simple.*;

/**
 * The Thief character class, a specific role in Citadels.
 * Implements unique behavior for thief.
 */
public class Thief extends Character
{
/**
 * Thief method defines thief's special skill.
 */
  public Thief()
  {
    name = "Thief";
    description = "Select another character whom you wish to rob. When a player reveals that character to take his turn, you immediately take all of his gold. You cannot rob the Assassin or the killed character.";
  }

/**
 * Thief method defines thief's special skill.
 */
  public Thief(JSONObject obj)
  {
    name = (String)obj.get("name");
    description = (String)obj.get("description");
    numBuild = ((Long)obj.get("numBuild")).intValue();
    numAction = ((Long)obj.get("numAction")).intValue();
  }

/**
 * promptSkill method defines thief's special skill.
 */
  public void promptSkill(App app, Player player)
  {
    ArrayList<Character> characters = app.getCharacters();
    int c = -1;
    if (player.getId() == 1){
      Scanner scanner = app.getScanner();
      System.out.print("Who do you want to rob? Choose a character from 3-8:\n> ");
      c = -1;
      while (true){
        try{
          c = scanner.nextInt();
          scanner.nextLine();
        }
        catch (Exception e){
          scanner.nextLine();
          System.out.println("Invalid character");
          continue;
        }
        if (c < 3 || c > 8){
          System.out.println("Invalid character");
          continue;
        }
        break;
      }
    }
    else{
      Random rand = new Random(System.currentTimeMillis());
      c = rand.nextInt(6) + 3;
    }
    c--;
    String name = characters.get(c).getName(); 
    rob(app, name, player);
    System.out.printf("Player %d chose to rob the %s\n", player.getId(), name);
  }

/**
 * rob method defines thief's special skill.
 */
  public void rob(App app, String name, Player robber)
  {
    Player robbedPlayer = app.findPlayerWithChar(name);
    if (robbedPlayer == null) return;
    robbedPlayer.setRobber(robber);
  }
}