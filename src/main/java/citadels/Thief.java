package citadels;

import java.util.ArrayList;
import java.util.Scanner;

public class Thief extends Character
{
  public Thief()
  {
    name = "Thief";
    description = "Select another character whom you wish to rob. When a player reveals that character to take his turn, you immediately take all of his gold. You cannot rob the Assassin or the killed character.";
  }

  public String promptSkill(Scanner scanner, ArrayList<Character> characters)
  {
    System.out.println("Who do you want to rob? Choose a character from 3-8:");
    int c = -1;
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
    c--;
    String name = characters.get(c).getName(); 
    System.out.printf("You chose to rob the %s\n", name);
    return name;
  }
}