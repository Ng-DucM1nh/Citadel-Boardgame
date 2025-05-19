package citadels;

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
}