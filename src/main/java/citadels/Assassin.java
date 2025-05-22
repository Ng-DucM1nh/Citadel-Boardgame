package citadels;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

public class Assassin extends Character
{
  public Assassin()
  {
    name = "Assassin";
    description = "Select another character whom you wish to kill. The killed character loses their turn.";
  }

  public void promptSkill(App app, Player player)
  {
    ArrayList<Character> characters = app.getCharacters();
    int c = -1;
    if (player.getId() == 1){
      Scanner scanner = app.getScanner();
      System.out.println("Who do you want to kill? Choose a character from 2-8:");
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
      Random rand = new Random(System.currentTimeMillis());
      c = rand.nextInt(7) + 2;
    }
    c--;
    String name = characters.get(c).getName();
    kill(app, name);
    System.out.printf("Player %d chose to kill the %s\n", player.getId(), name);
  }

  public void kill(App app, String name)
  {
    Player killedPlayer = app.findPlayerWithChar(name);
    if (killedPlayer == null) return;
    killedPlayer.setKilled(true);
  }
}