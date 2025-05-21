package citadels;

public class Architect extends Character
{
  public Architect()
  {
    name = "Architect";
    description = "Gains two extra district cards. Can build up to 3 districts per turn.";
    numBuild = 3;
  }

  public void passiveSkill(App app, Player player)
  {
    Deck deck = app.getDeck();
    for (int j=0; j<2; j++){
      Card card = deck.pop();  
      player.takeCard(card);
      System.out.printf("Player %d receives extra district card %s.\n", player.getId(), card.display());
    }
  }
}