package citadels;

import java.util.ArrayList;
import java.util.Collections;

public class Magician extends Character
{
  private int numAction = 1;

  public Magician()
  {
    name = "Magician";
    description = "Can either exchange their hand with another player's, or discard any number of district cards face down to the bottom of the deck and draw an equal number of cards from the district deck (can only do this once per turn).";
  }

  public String actionDesc()
  {
    return "action swap <player number>\naction redraw <id1,id2,...>\n";
  }

  public boolean action(App app, String[] tokens, Player player)
  {
    if (tokens.length != 3) return false;
    if (tokens[1].equals("swap") == false && tokens[1].equals("redraw") == false) return false;
    if (player.isKilled()){
      System.out.println("You can't use the character's ability after being assassinated");
      return true;
    }
    if (numAction <= 0){
      System.out.println("You can no longer use the character's ability for this round.");
      return true;
    }
    ArrayList<Player> players = app.getPlayers();
    if (tokens[1].equals("swap")){
      int id = -1;
      try{
        id = Integer.parseInt(tokens[2]);
      }
      catch (Exception e){
        System.out.println("Invalid player number");
        return true;
      }
      if (id < 1 || id > players.size()){
        System.out.println("Invalid player number");
        return true;
      }
      id--;
      Player swappedPlayer = app.getPlayers().get(id);
      ArrayList<Card> tmp = player.getCards();
      player.setCards(swappedPlayer.getCards());
      swappedPlayer.setCards(tmp);
      System.out.printf("Player %d swapped hands with Player %d.\n", player.getId(), swappedPlayer.getId());
      numAction--;
      return true;
    }
    else{
      String[] spos = tokens[2].split(",");
      ArrayList<Card> cards = player.getCards();
      ArrayList<Integer> pos = new ArrayList<>();
      for (String p : spos){
        int npos = -1;
        try{
          npos = Integer.parseInt(p);
        }
        catch (Exception e){
          System.out.println("Invalid card position");
          return true;
        }
        if (npos < 1 || npos > cards.size()){
          System.out.println("Invalid card position");
          return true;
        }
        pos.add(npos-1);
      }
      Collections.sort(pos);
      Collections.reverse(pos);
      Deck deck = app.getDeck();
      int num = pos.size();
      for (int p : pos){
        deck.addCard(cards.get(p));
        cards.remove(p);
      }
      for (int i=0; i<num; i++){
        cards.add(deck.pop());
      }
      player.setCards(cards);
      System.out.printf("Player %d redraw %d cards.\n", player.getId(), num);
      numAction--;
      return true;
    }
  }
}