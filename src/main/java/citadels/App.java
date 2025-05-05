package citadels;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

public class App {
	
	private File cardsFile;
  private ArrayList<Player> players;

  public void getCardsData()
  {
    try {
      cardsFile = new File(URLDecoder.decode(this.getClass().getResource("cards.tsv").getPath(), StandardCharsets.UTF_8.name()));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public void assignCrown(int crownPlayerId)
  {
    if (crownPlayerId < 0 || crownPlayerId >= players.size()){
      throw new RuntimeException("invalid crownPlayerId");
    }
    for (int i=0; i<players.size(); i++){
      Player p = players.get(i);
      if (p.getId() != crownPlayerId){
        p.hasCrown = false;
      }
      else{
        p.hasCrown = true;
      }
    }
  }

  public void initialSetup()
  {
    Scanner scanner = new Scanner(System.in);
    // prompt for the number of players
    System.out.print("Enter how many players [4-7]:\n> ");
    int numPlayers = scanner.nextInt();
    players = new ArrayList<>();
    for (int id=0; id<numPlayers; id++){
      Player p = new Player(id);
      players.add(p);
    }
    // randomly pick a player to assign the crown to
    // Random rand = new Random();
    // int crownPlayer = rand.nextInt(numPlayers);

  }

	public App()
  {
    initialSetup();
  }

  public static void main(String[] args)
  {
    App app = new App();
  }

}
