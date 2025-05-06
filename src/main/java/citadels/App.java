package citadels;

import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

public class App {
	
	private File cardsFile;
  private ArrayList<Player> players;
  private Crown crown;

  public void getCardsData() throws FileNotFoundException
  {
    cardsFile = new File("src/main/resources/citadels/cards.tsv");
    Scanner scanner = new Scanner(cardsFile); // this can possibly throw FileNotFoundException
    String name; int qty; String color; int cost; String text;
    scanner.nextLine();
    name = scanner.next();
    qty = scanner.nextInt();
    color = scanner.next();
    cost = scanner.nextInt();
    text = scanner.next();
    System.out.printf("%s %d %s %d %s", name, qty, color, cost, text);
  }

  public void assignCrown(int crownPlayerId)
  {
    if (crownPlayerId < 0 || crownPlayerId >= players.size()){
      throw new RuntimeException("invalid crownPlayerId");
    }
    for (int i=0; i<players.size(); i++){
      Player p = players.get(i);
      if (p.getId() != crownPlayerId){
        p.setCrown(false);
      }
      else{
        p.setCrown(true);
        crown.setOwner(p);
      }
    }
  }

  public void initialSetup()
  {
    players = new ArrayList<>();
    crown = new Crown();
    // prompt for the number of players
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter how many players [4-7]:\n> ");
    int numPlayers = scanner.nextInt();
    // add players object to list
    for (int id=0; id<numPlayers; id++){
      Player p = new Player(id);
      players.add(p);
    }
    // randomly pick a player to assign the crown to
    Random rand = new Random(System.currentTimeMillis());
    int crownPlayerId = rand.nextInt(numPlayers);
    assignCrown(crownPlayerId);
    System.out.println(crown.getOwner().getId());
  }

	public App()
  {
    try{
      getCardsData();
    }
    catch (FileNotFoundException e){
      System.out.println("CARDS FILE NOT FOUND");
      e.printStackTrace();
      return;
    }
    // initialSetup();
  }

  public static void main(String[] args)
  {
    App app = new App();
  }

}
