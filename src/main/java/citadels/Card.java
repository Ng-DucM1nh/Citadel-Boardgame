package citadels;

public class Card
{
  private String name;
  private String color;
  private int cost;
  private int point;
  private String description;

  public Card(String name, String color, int cost)
  {
    this.name = name;
    this.color = color;
    this.cost = cost;
    this.point = cost;
    description = "";
  }

  public Card(String name, String color, int cost, int point, String description)
  {
    this.name = name;
    this.color = color;
    this.cost = cost;
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

  public int getPoint()
  {
    return point;
  }

  public String disp()
  {
    return name+" "+color+String.valueOf(cost)+" "+description;
  }
}