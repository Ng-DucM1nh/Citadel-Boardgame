package citadels;

public class Card
{
  private String name;
  private String color;
  private int cost;
  private int destroyCost;
  private int point;
  private String description;

  public Card(String name, String color, int cost, int point, String description)
  {
    this.name = name;
    this.color = color;
    this.cost = cost;
    this.destroyCost = cost-1;
    this.point = point;
    this.description = description;
  }

  public String getName()
  {
    return name;
  }

  public String getColor()
  {
    return color;
  }

  public int getCost()
  {
    return cost;
  }

  public int getDestroyCost()
  {
    return destroyCost;
  }

  public int getPoint()
  {
    return point;
  }

  public String getDesc()
  {
    return description;
  }

  public String display()
  {
    return name+" ["+color+String.valueOf(cost)+"]";
  }

  public String actionDesc() {return "";}

  public String disp()
  {
    return name+" "+color+String.valueOf(cost)+" "+description;
  }
}