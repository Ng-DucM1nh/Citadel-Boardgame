package citadels;

public class Card
{
  private String name;
  private String color;
  private int cost;
  private String description;

  public Card(String name, String color, int cost)
  {
    this.name = name;
    this.color = color;
    this.cost = cost;
    description = "";
  }

  public Card(String name, String color, int cost, String description)
  {
    this.name = name;
    this.color = color;
    this.cost = cost;
    this.description = description;
  }

  public String disp()
  {
    return name+" "+color+String.valueOf(cost)+" "+description;
  }
}