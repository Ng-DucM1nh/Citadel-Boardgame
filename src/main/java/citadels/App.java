package citadels;

import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

public class App {
  static final String promptArrow = "> ";

	private File cardsFile;
  private ArrayList<Player> players;
  private Crown crown;
  private Deck deck;
  private ArrayList<Character> characters;
  private Scanner scanner;

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
    characters = new ArrayList<>();
    // prompt for the number of players
    
    System.out.print("Enter how many players [4-7]:\n> ");
    int numPlayers = -1;
    try{
      numPlayers = scanner.nextInt();
    }
    catch (Exception e){
      System.out.println("Invalid number of players");
      System.exit(1);
    }
    if (numPlayers < 4 || numPlayers > 7){
      System.out.println("Invalid number of players");
      System.exit(1);
    }
    // add players object to list
    for (int id=0; id<numPlayers; id++){
      Player p = new Player(id);
      players.add(p);
    }
    // randomly pick a player to assign the crown to
    Random rand = new Random(System.currentTimeMillis());
    int crownPlayerId = rand.nextInt(numPlayers);
    assignCrown(crownPlayerId);
    // shuffle deck
    System.out.println("Shuffling deck...");
    deck.shuffle();
    // add character cards
    System.out.println("Adding characters...");
    characters.add(new Assassin());
    characters.add(new Thief());
    characters.add(new Magician());
    characters.add(new King());
    characters.add(new Bishop());
    characters.add(new Merchant());
    characters.add(new Architect());
    characters.add(new Warlord());
    // deal cards to players
    System.out.println("Dealing cards...");
    for (Player p : players){
      for (int i=0; i<4; i++){
        Card c = deck.pop();
        p.takeCard(c);
      }
    }
    System.out.printf("Starting Citadel with %d players...\n", numPlayers);
    System.out.println("You are player 1");
  }

  private int numDistrictGoal = 8;

  public void setNumDistrictGoal(int num)
  {
    numDistrictGoal = num;
  }

  public boolean isGameOver()
  {
    for (Player p : players){
      if (p.getNumPlacedCards() >= numDistrictGoal) return true;
    }
    return false;
  }
  
  public ArrayList<Character> chooseCharacter(Player player, ArrayList<Character> charList, Character facedownChar)
  {
    if (players.size() == 7 && charList.size() == 1) charList.add(facedownChar);
    if (player.getId() == 0){
      int l = 0, r = charList.size()-1;
      System.out.printf("Choose a character from the list [%d..%d]:\n", l, r);
      for (int i=0; i<charList.size(); i++){
        System.out.printf("%d. %s\n", i, charList.get(i).getName());
      }
      while (true){
        System.out.print(promptArrow);
        int pos = -1;
        try{
          pos = scanner.nextInt();
        }
        catch(Exception e){
          scanner.nextLine();
          System.out.println("Invalid character");
          continue;
        }
        scanner.nextLine();
        if (pos < l || pos > r){
          System.out.println("Invalid character");
        }
        else{
          player.setCharacter(charList.get(pos));
          charList.remove(pos);
          break;
        }
      }
    }
    else{
      // bot
      player.setCharacter(charList.get(0));
      charList.remove(0);
    }
    System.out.printf("Player %d chose a character.\n", player.getId());
    return charList;
  }

  public boolean executeCommand(String command)
  {
    System.out.println("~~~"+command+"~~~");
    if (command.equals("t")){
      return true;
    }
    return false;
  }

  public void characterSelection()
  {
    // rotate the crowned player to the first index in the list
    while (!players.get(0).hasCrown()){
      Player p = players.get(0);
      players.remove(0);
      players.add(p);
    }
    // System.out.println("player after rotation");
    // for (Player p : players){
    //   System.out.println(p.disp());
    // }
    // shuffle character cards and remove some cards
    Collections.shuffle(characters, new Random(System.currentTimeMillis()));
    Character facedownChar = characters.get(0);
    characters.remove(0);
    System.out.println("A mystery character was removed.");
    int faceups = -1;
    switch(players.size()){
      case 4:
        faceups = 2;
        break;
      case 5:
        faceups = 1;
        break;
      case 6:
        faceups = 0;
        break;
      case 7:
        faceups = 0;
        break;
    }
    for (int i=characters.size()-1; i>=0 && faceups > 0; i--){
      Character c = characters.get(i);
      if (c.getName() == "King") continue;
      System.out.printf("%s was removed.\n", c.getName());
      characters.remove(i);
      faceups--;
    }
    System.out.println("remaining chars");
    for (Character c : characters){
      c.desc();
    }
    // characters = chooseCharacter(players.get(0), characters, facedownChar);
    for (Player player : players){
      characters = chooseCharacter(player, characters, facedownChar);
      while (true){
        System.out.print(promptArrow);
        String cmd = scanner.nextLine();
        if (executeCommand(cmd)) break;
      }
    }
  }

	public App()
  {
    scanner = new Scanner(System.in);
    try{
      getCardsData();
    }
    catch (FileNotFoundException e){
      System.out.println("CARDS FILE NOT FOUND");
      e.printStackTrace();
      return;
    }
    initialSetup();
    while (!isGameOver()){
      characterSelection();
      break;
    }
  }

  public static void main(String[] args)
  {
    App app = new App();
  }

}
