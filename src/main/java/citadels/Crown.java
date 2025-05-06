package citadels;

public class Crown
{
  private Player owner;

  public Crown()
  {
    owner = null;
  }

  public Crown(Player player)
  {
    owner = player;
  }

  public void setOwner(Player player)
  {
    owner = player;
  }

  public Player getOwner()
  {
    return owner;
  }
}