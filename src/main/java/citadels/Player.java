package citadels;

public class Player
{
  Object character;
  int gold;
  int id;
  boolean hasCrown;

  public Player(int id)
  {
    character = null;
    gold = 0;
    this.id = id;
    hasCrown = false;
  }

  public int getId()
  {
    return id;
  }
}