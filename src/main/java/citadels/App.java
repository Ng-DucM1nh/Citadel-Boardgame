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
  private Player humanPlayer;

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
    if (crownPlayerId < 1 || crownPlayerId > players.size()){
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

  public void addCharacters()
  {
    characters.clear();
    characters.add(new Assassin());
    characters.add(new Thief());
    characters.add(new Magician());
    characters.add(new King());
    characters.add(new Bishop());
    characters.add(new Merchant());
    characters.add(new Architect());
    characters.add(new Warlord());
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
      scanner.nextLine();
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
    for (int id=1; id<=numPlayers; id++){
      Player p = new Player(id);
      players.add(p);
    }
    humanPlayer = players.get(0);
    // randomly pick a player to assign the crown to
    Random rand = new Random(System.currentTimeMillis());
    int crownPlayerId = rand.nextInt(numPlayers) + 1;
    assignCrown(crownPlayerId);
    // shuffle deck
    System.out.println("Shuffling deck...");
    deck.shuffle();
    // add character cards
    System.out.println("Adding characters...");
    addCharacters();
    // deal cards to players
    System.out.println("Dealing cards...");
    for (Player p : players){
      for (int i=0; i<4; i++){
        Card c = deck.pop();
        p.takeCard(c);
      }
      p.addGold(2);
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
    if (player.getId() == 1){
      int l = 1, r = charList.size();
      System.out.printf("Choose a character from the list [%d..%d]:\n", l, r);
      for (int i=0; i<charList.size(); i++){
        System.out.printf("%d. %s\n", i+1, charList.get(i).getName());
      }
      while (true){
        System.out.print(promptArrow);
        int pos = -1;
        try{
          pos = scanner.nextInt();
          scanner.nextLine();
        }
        catch(Exception e){
          scanner.nextLine();
          System.out.println("Invalid character");
          continue;
        }
        if (pos < l || pos > r){
          System.out.println("Invalid character");
        }
        else{
          pos--;
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

  public void handCommand()
  {
    System.out.printf("You have %d gold. Cards in hand:\n", humanPlayer.getGold());
    ArrayList<Card> cards = humanPlayer.getCards();
    for (int i=0; i<cards.size(); i++){
      System.out.printf("%d. %s (%s), cost: %d\n", i+1, cards.get(i).getName(), cards.get(i).getColor(), cards.get(i).getCost());
    }
  }

  public void goldCommand(String[] tokens)
  {
    if (tokens.length > 2){
      System.out.println("Invalid parameter.");
      System.out.println("Expected \"gold [p]\".");
      return;
    }
    if (tokens.length == 1){
      System.out.printf("You have %d gold.\n", humanPlayer.getGold());
      return;
    }
    int playerId = -1;
    try{
      playerId = Integer.parseInt(tokens[1]);
    }
    catch (Exception e){
      System.out.println("Invalid player id.");
      return;
    }
    if (playerId < 1 || playerId > players.size()){
      System.out.println("Invalid player id.");
      return;
    }
    System.out.printf("Player %d has %d gold.\n", playerId, players.get(playerId-1).getGold());
  }

  public void buildCommand(String[] tokens)
  {
    if (tokens.length != 2){
      System.out.println("Invalid parameter.\nExpected \"build <H>\"");
      return;
    }
    int pos = -1;
    try{
      pos = Integer.parseInt(tokens[1]);
    }
    catch (Exception e){
      System.out.println("Invalid card position");
      return;
    }
    humanPlayer.build(pos-1);
  }

  public void citadelCommand(String[] tokens)
  {
    if (tokens.length > 2){
      System.out.println("Invalid parameter");
      System.out.println("Expected \"citadel|list|city [p]\"");
      return;
    }
    int playerId = -1;
    if (tokens.length == 1){
      playerId = 1;
    }
    else{
      try{
        playerId = Integer.parseInt(tokens[1]);
      }
      catch (Exception e){
        System.out.println("Invalid player id");
        return;
      }
    }
    if (playerId < 1 || playerId > players.size()){
      System.out.println("Invalid player id");
      return;
    }
    Player player = players.get(playerId-1);
    System.out.printf("Player %d has built:\n", playerId);
    HashMap<Card,Integer> placedCards = player.getPlacedCards();
    for (Card card : placedCards.keySet()){
      int num = placedCards.get(card);
      while (num-- > 0){
        System.out.printf("%s (%s), points: %d\n", card.getName(), card.getColor(), card.getPoint());
      }
    }
  }

  public void allCommand()
  {
    for (int i=0; i<players.size(); i++){
      Player player = players.get(i);
      System.out.printf("Player %d%s: cards=%d gold=%d city=", player.getId(), (player.getId() == 1 ? " (you)" : ""), player.getNumCards(), player.getGold());
      HashMap<Card,Integer> placedCards = player.getPlacedCards();
      for (Card card : placedCards.keySet()){
        System.out.print(card.display()+" ");
      }
      System.out.println();
    }
  }

  public void infoCommand(String[] tokens)
  {
    if (tokens.length != 2){
      System.out.println("Invalid parameter.");
      System.out.println("Expected \"info <H>\" or \"info <name>\"");
      return;
    }
    for (Character character : characters){
      if (character.getName().equals(tokens[1])){
        character.desc();
        return;
      }
    }
    int pos = -1;
    try{
      pos = Integer.parseInt(tokens[1]);
    }
    catch (Exception e){
      System.out.printf("No character named %s. Invalid card position %s\n", tokens[1], tokens[1]);
      return;
    }
    int numCards = humanPlayer.getNumCards();
    if (pos < 1 || pos > numCards){
      System.out.printf("No character named %s. Invalid card position %s\n", tokens[1], tokens[1]);
      return;
    }
    Card card = humanPlayer.getCardAtPos(pos-1);
    System.out.printf("%s: %s\n", card.getName(), card.getDesc());
  }

  public void helpCommand()
  {
    System.out.println("Available commands:\ninfo : show information about a character or building\nt : processes turns\n");
    System.out.println("all : shows all current game info\ncitadel/list/city : shows districts built by a player");
    System.out.println("hand : shows cards in hand\ngold [p] : shows gold of a player\n");
    System.out.println("build <place in hand> : Builds a building into your city");
    System.out.println("action : Gives info about your special action and how to perform it\nend : Ends your turn");
  }

  public boolean prompt(boolean inTurn)
  {
    System.out.print(promptArrow);
    String command = scanner.nextLine();
    System.out.println("~~~"+command+"~~~");
    String[] tokens = command.split(" ");
    if (command.equals("t")){
      return (inTurn ? false : true);
    }
    if (command.equals("hand")){
      handCommand();
      return false;
    }
    if (tokens[0].equals("gold")){
      goldCommand(tokens);
      return false;
    }
    if (tokens[0].equals("build")){
      if (inTurn) buildCommand(tokens);
      return false;
    }
    if (command.equals("end")){
      if (inTurn) System.out.println("You ended your turn.");
      return (inTurn ? true : false);
    }
    if (tokens[0].equals("citadel") || tokens[0].equals("list") || tokens[0].equals("city")){
      citadelCommand(tokens);
      return false;
    }
    if (command.equals("all")){
      allCommand();
      return false;
    }
    if (tokens[0].equals("info")){
      infoCommand(tokens);
      return false;
    }
    if (command.equals("help")){
      helpCommand();
      return false;
    }
    return false;
  }

  public void characterSelection()
  {
    // System.out.println("**characterSelection called**");
    // rotate the crowned player to the first index in the list
    while (!players.get(0).hasCrown()){
      Player p = players.get(0);
      players.remove(0);
      players.add(p);
    }
    System.out.printf("Player %d is the crowned player and goes first.\n", players.get(0).getId());
    System.out.println("Press t to process turns");
    System.out.println("================================\nSELECTION PHASE\n================================");
    while (true){
      if (prompt(false)) break;
    }
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
    for (Player player : players){
      characters = chooseCharacter(player, characters, facedownChar);
      while (true){
        if (prompt(false)) break;
      }
    }
    addCharacters();
    while (players.get(0).getId() != 1){
      Player p = players.get(0);
      players.remove(0);
      players.add(p);
    }
  }

  public void promptResource()
  {
    String res = "";
    while (true){
      System.out.print("Collect 2 gold or draw two cards and pick one [gold/cards].\n> ");
      res = scanner.nextLine();
      if (!res.equals("gold") && !res.equals("cards")){
        System.out.println("Invalid choice");
        continue;
      }
      break;
    }
    if (res.equals("gold")){
      System.out.println("Player 1 received 2 gold.");
      humanPlayer.addGold(2);
      return;
    }
    ArrayList<Card> choices = new ArrayList<>();
    choices.add(deck.pop()); choices.add(deck.pop());
    int pos = -1;
    while (true){
      System.out.print("Pick one of the following cards: 'collect card <option>'.\n");
      for (int i=0; i<2; i++){
        Card card = choices.get(i);
        System.out.printf("%d. %s [%s], cost: %d\n", i+1, card.getName(), card.getColor(), card.getCost());
      }
      System.out.print(promptArrow);
      res = scanner.nextLine();
      String[] tokens = res.split(" ");
      if (tokens.length != 3 || !tokens[0].equals("collect") || !tokens[1].equals("card")){
        System.out.println("Invalid choice.");
        continue;
      }
      try{
        pos = Integer.parseInt(tokens[2]);
      }
      catch (Exception e){
        System.out.println("Invalid choice.");
        continue;
      }
      if (pos < 1 || pos > 2){
        System.out.println("Invalid choice.");
        continue;
      }
      break;
    }
    pos--;
    System.out.printf("You chose card %s\n", choices.get(pos).display());
    humanPlayer.takeCard(choices.get(pos));
    choices.remove(pos);
    deck.addCard(choices.get(0));
    return;
  }

  public void turn()
  {
    System.out.println("Character choosing is over, action round will now begin.");
    System.out.println("================================\nTURN PHASE\n================================");
    String killedCharName = "";
    Player robber = null; String robbedCharName = "";
    for (int i=0; i<8; i++){
      Character character = characters.get(i);
      System.out.printf("%d. %s\n", i+1, character.getName());
      Player player = null;
      for (Player p : players){
        if (!p.getCharacter().getName().equals(character.getName())) continue;
        player = p;
        break;
      }
      boolean inTurn = false;
      if (player == null){
        System.out.printf("No one is the %s\n", character.getName());
        while (true) if (prompt(false)) break;
        continue;
      }
      System.out.printf("Player %d is the %s\n", player.getId(), character.getName());
      if (character.getName().equals(killedCharName)){
        System.out.printf("Player %d loses their turn because they were assassinated.\n", player.getId());
        while (true) if (prompt(false)) break;
        continue;
      }
      if (character.getName().equals(robbedCharName)){
        System.out.printf("The Thief steals %d gold from the %s.\n", player.getGold(), character.getName());
        int gold = player.getGold();
        robber.addGold(gold);
        player.addGold(-gold);
      }
      if (player.getId() != 1){
        while (true) if (prompt(false)) break;
        continue;
      }
      System.out.println("Your turn.");
      if (character.getName().equals("Assassin")){
        killedCharName = character.promptSkill(scanner, characters);
      }
      if (character.getName().equals("Thief")){
        robbedCharName = character.promptSkill(scanner, characters);
        robber = player;
      }
      player.passiveSkill();
      if (character.getName().equals("Architect")){
        for (int j=0; j<2; j++){
          Card card = deck.pop();  
          player.takeCard(card);
          System.out.printf("Player %d receives extra district card %s.\n", player.getId(), card.display());
        }
      }
      if (character.getName().equals("King")){
        assignCrown(player.getId());
        System.out.printf("Player %d receives the crown and will be the first to choose characters on the next round.\n", player.getId());
      }
      promptResource();
      while (true) if (prompt(true)) break;
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
      turn();
      System.out.println("Everyone is done, new round!");
    }
  }

  public static void main(String[] args)
  {
    App app = new App();
  }

}
