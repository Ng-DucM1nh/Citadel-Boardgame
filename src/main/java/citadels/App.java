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
  private Deck deck;

  public void getCardsData() throws FileNotFoundException
  {
    cardsFile = new File("src/main/resources/citadels/cards.tsv");
    Scanner scanner = new Scanner(cardsFile); // this can possibly throw FileNotFoundException
    deck = new Deck();
    scanner.nextLine();
    // read and create normal district cards (there are 17 types) from file
    for (int i=0; i<17; i++){
      String s = scanner.nextLine();
      String[] tokens = s.split("[\\s]");
      int len = tokens.length;
      int cost = Integer.parseInt(tokens[len-1]);
      String color = tokens[len-2];
      int qty = Integer.parseInt(tokens[len-3]);
      String name = "";
      for (int j=0; j<len-3; j++){
        name += tokens[j];
        if (j < len-4) name += " ";
      }
      Card c = new Card(name, color, cost);
      for (int j=0; j<qty; j++){
        deck.addCard(c);
      }
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
    // rotate the crowned player to the first index in the list
    while (players.get(0).getId() != crownPlayerId){
      Player p = players.get(0);
      players.remove(0); players.add(p);
    }
    // shuffle deck
    System.out.println("Shuffling deck...");
    deck.shuffle();
    deck.disp();
    System.out.println("Adding characters...");
    // deal cards to players
    for (Player p : players){
      for (int i=0; i<4; i++){
        Card c = deck.pop();
        p.takeCard(c);
      }
    }
    for (Player p : players){
      System.out.print(p.disp()+": ");
      p.showCard();
    }
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
    initialSetup();
  }

  public static void main(String[] args)
  {
    App app = new App();
  }

}
