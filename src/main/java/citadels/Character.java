package citadels;

import java.util.Scanner;
import java.util.ArrayList;

public class Character
{
  String name;
  String description;

  public void desc()
  {
    System.out.printf("%s: %s\n", name, description);
  }

  public String getName()
  {
    return name;
  }

  public String promptSkill(Scanner scanner, ArrayList<Character> characters)
  {
    return "";
  }
}