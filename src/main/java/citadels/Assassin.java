package citadels;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

import org.json.simple.*;

/**
 * The Assassin character class, a specific role in Citadels.
 * Implements unique behavior for assassin.
 */
public class Assassin extends Character
{
/**
 * Assassin method defines assassin's special skill.
 */
  public Assassin()
  {
    // Set Assassin's name and description
    name = "Assassin";
    description = "Select another character whom you wish to kill. The killed character loses their turn.";
  }

/**
 * Assassin method defines assassin's special skill.
 */
  public Assassin(JSONObject obj)
  {
    // Initialize Assassin from JSON object
    name = (String)obj.get("name");
    description = (String)obj.get("description");
    numBuild = ((Long)obj.get("numBuild")).intValue();
    numAction = ((Long)obj.get("numAction")).intValue();
  }

/**
 * promptSkill method defines assassin's special skill.
 */
  public void promptSkill(App app, Player player)
  {
    // Prompt player to choose a character to kill
    ArrayList<Character> characters = app.getCharacters();
    int c = -1;
    if (player.getId() == 1){
      Scanner scanner = app.getScanner();
      System.out.print("Who do you want to kill? Choose a character from 2-8:\n> ");
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
        if (c < 2 || c > 8){
          System.out.println("Invalid character");
          continue;
        }
        break;
      }
    }
    else{
      // Bot chooses randomly
      Random rand = new Random(System.currentTimeMillis());
      c = rand.nextInt(7) + 2;
    }
    c--;
    String name = characters.get(c).getName();
    kill(app, name);
    System.out.printf("Player %d chose to kill the %s\n", player.getId(), name);
  }

/**
 * kill method defines assassin's special skill.
 */
  public void kill(App app, String name)
  {
    // Set killed flag for chosen character
    Player killedPlayer = app.findPlayerWithChar(name);
    if (killedPlayer == null) return;
    killedPlayer.setKilled(true);
  }
}