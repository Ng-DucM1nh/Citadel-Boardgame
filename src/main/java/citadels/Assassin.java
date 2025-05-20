package citadels;

import java.util.ArrayList;
import java.util.Scanner;

public class Assassin extends Character
{
  public Assassin()
  {
    name = "Assassin";
    description = "Select another character whom you wish to kill. The killed character loses their turn.";
  }

  public String promptSkill(Scanner scanner, ArrayList<Character> characters)
  {
    System.out.println("Who do you want to kill? Choose a character from 2-8:");
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
      if (c < 2 || c > 8){
        System.out.println("Invalid character");
        continue;
      }
      break;
    }
    c--;
    String name = characters.get(c).getName(); 
    System.out.printf("You chose to kill the %s\n", name);
    return name;
  }
}