package citadels;

public class Player
{
  private Object character;
  private int gold;
  private int id;
  private boolean crown;

  public Player(int id)
  {
    character = null;
    gold = 0;
    this.id = id;
    crown = false;
  }

  public int getId()
  {
    return id;
  }

  public boolean hasCrown()
  {
    return crown;
  }

  public void setCrown(boolean crown)
  {
    this.crown = crown;
  }

  public String disp()
  {
    return "player"+String.valueOf(id);
  }
}