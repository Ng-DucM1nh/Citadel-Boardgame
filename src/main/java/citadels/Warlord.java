package citadels;

public class Warlord extends Character
{
  public Warlord()
  {
    name = "Warlord";
    description = "Gains one gold for each red (military) district in their city. You can destroy one district of your choice by paying one fewer gold than its building cost. You cannot destroy a district in a city with 8 or more districts.";
  }

  public void passiveSkill(App app, Player player)
  {
    int gold = player.countPlacedCardColor("red");
    player.addGold(gold);
    System.out.printf("Player %d receives %d gold from their signature district color.\n", player.getId(), gold);
  }
}