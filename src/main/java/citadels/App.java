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
  private int firstCompletePlayerId = -1;
  private int round;

  public Scanner getScanner()
  {
    return scanner;
  }

  public ArrayList<Character> getCharacters()
  {
    return characters;
  }

  public Deck getDeck()
  {
    return deck;
  }

  public ArrayList<Player> getPlayers()
  {
    return players;
  }

  public int getFirstCompletePlayerId()
  {
    return firstCompletePlayerId;
  }

  public int getRound()
  {
    return round;
  }

  public void getCardsData() throws FileNotFoundException
  {
    cardsFile = new File("src/main/resources/citadels/cards.tsv");
    Scanner scanner = new Scanner(cardsFile); // this can possibly throw FileNotFoundException
    deck = new Deck();
    scanner.nextLine();
    while (scanner.hasNextLine()){
      String s = scanner.nextLine();
      String[] tokens = s.split("\t");
      String name = tokens[0];
      int qty = Integer.parseInt(tokens[1]);
      String color= tokens[2];
      int cost = Integer.parseInt(tokens[3]);
      String desc = (tokens.length == 4 ? "A normal card with no ability" : tokens[4]);
      Card c = new Card(name, color, cost, cost, desc);
      while (qty-- > 0){
        deck.addCard(c);
      }
    }
  }

  public void assignCrown(int crownPlayerId)
  {
    if (crownPlayerId < 1 || crownPlayerId > players.size()){
      throw new RuntimeException("invalid crownPlayerId");
    }
    for (Player p : players){
      if (p.hasPlacedDistrict("Throne Room") == false) continue;
      if (crown.getOwner().getId() != crownPlayerId){
        p.addGold(1);
        System.out.printf("Throne Room: Player %d receives 1 gold.\n", p.getId());
      }
      break;
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
    // assignCrown(1); ///////////////////////////////////////////////////
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
      p.addGold(100);
    }
    players.get(1).addPlacedCard(new Card("Great Wall", "", 0, 0, ""));
    players.get(1).addPlacedCard(new Card("dummy", "", 1, 1, ""));
    humanPlayer.addPlacedCard(new Card("Observatory", "purple", 0, 0, ""));
    humanPlayer.addPlacedCard(new Card("Library", "purple", 0, 0, ""));
    System.out.printf("Starting Citadel with %d players...\n", numPlayers);
    System.out.println("You are player 1");
  }

  private int numDistrictGoal = 8;

  public int getNumDistrictGoal()
  {
    return numDistrictGoal;
  }

  public void setNumDistrictGoal(int num)
  {
    numDistrictGoal = num;
  }

  public boolean isGameOver()
  {
    for (Player p : players){
      if (p.isCityCompleted(this)) return true;
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
    humanPlayer.build(pos-1, round);
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

  public void actionCommand(String[] tokens)
  {
    if (tokens.length == 1){
      System.out.println(humanPlayer.getActionList());
      return;
    }
    humanPlayer.action(this, tokens);
  }

  public void prompt(boolean inTurn)
  {
    while (true){
      System.out.print(promptArrow);
      String command = scanner.nextLine();
      System.out.println("~~~"+command+"~~~");
      String[] tokens = command.split(" ");
      if (command.equals("t")){
        if (!inTurn) return;
      }
      if (command.equals("hand")){
        handCommand();
      }
      if (tokens[0].equals("gold")){
        goldCommand(tokens);
      }
      if (tokens[0].equals("build")){
        if (inTurn) buildCommand(tokens);
      }
      if (command.equals("end")){
        if (inTurn){
          System.out.println("You ended your turn.");
          return;
        }
      }
      if (tokens[0].equals("citadel") || tokens[0].equals("list") || tokens[0].equals("city")){
        citadelCommand(tokens);
      }
      if (command.equals("all")){
        allCommand();
      }
      if (tokens[0].equals("info")){
        infoCommand(tokens);
      }
      if (command.equals("help")){
        helpCommand();
      }
      if (tokens[0].equals("action")){
        actionCommand(tokens);
      }
    }
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
    prompt(false);
    // shuffle character cards and remove some cards
    // Collections.shuffle(characters, new Random(System.currentTimeMillis()));
    // Character facedownChar = characters.get(0);
    // characters.remove(0);
    // System.out.println("A mystery character was removed.");
    // int faceups = -1;
    // switch(players.size()){
    //   case 4:
    //     faceups = 2;
    //     break;
    //   case 5:
    //     faceups = 1;
    //     break;
    //   case 6:
    //     faceups = 0;
    //     break;
    //   case 7:
    //     faceups = 0;
    //     break;
    // }
    // for (int i=characters.size()-1; i>=0 && faceups > 0; i--){
    //   Character c = characters.get(i);
    //   if (c.getName() == "King") continue;
    //   System.out.printf("%s was removed.\n", c.getName());
    //   characters.remove(i);
    //   faceups--;
    // }
    Character facedownChar = null; ////////////////////////////////////////////////////
    for (Player player : players){
      characters = chooseCharacter(player, characters, facedownChar);
      prompt(false);
    }
    addCharacters();
    while (players.get(0).getId() != 1){
      Player p = players.get(0);
      players.remove(0);
      players.add(p);
    }
  }

  public Player findPlayerWithChar(String charName)
  {
    for (Player p : players){
      if (p.getCharacter().getName().equals(charName)) return p;
    }
    return null;
  }

  public void promptResource(Player player)
  {
    String res = "";
    while (true){
      System.out.print("Collect 2 gold or draw two cards and pick one [gold/cards].\n> ");
      res = scanner.nextLine();
      if (!res.equals("gold") && !res.equals("cards")){
        System.out.println("Invalid choice");
        continue;
      }
      if (res.equals("gold")){
        System.out.println("Player 1 received 2 gold.");
        player.addGold(2);
        return;
      }
      int numDraw = 2, numPick = 1;
      if (player.hasPlacedDistrict("Observatory")){
        numDraw = 3;
        System.out.printf("Observatory: Player %d may draw %d cards.\n", player.getId(), 3);
      }
      if (player.hasPlacedDistrict("Library")){
        numPick = numDraw;
        System.out.printf("Library: Player %d may keep %d cards.\n", player.getId(), numPick);
      }
      if (drawPick(numDraw, numPick, player)) break;
    }
  }

  public boolean drawPick(int numDraw, int numPick, Player player)
  {
    if (deck.size() == 0){
      System.out.println("deck empty");
      return false;
    }
    numDraw = Math.min(numDraw, deck.size());
    numPick = Math.min(numPick, numDraw);
    ArrayList<Card> choices = new ArrayList<>();
    for (int i=0; i<numDraw; i++){
      choices.add(deck.pop());
    }
    System.out.printf("Pick %d out of the following cards: 'collect card <pos1,pos2,...>'\n", numPick);
    for (int i=0; i<choices.size(); i++){
      Card card = choices.get(i);
      System.out.printf("%d. %s [%s], cost: %d\n", i+1, card.getName(), card.getColor(), card.getCost());
    }
    ArrayList<Integer> pos = new ArrayList<>();
    while (true){
      pos.clear();
      System.out.print(promptArrow);
      String cmd = scanner.nextLine();
      String[] tokens = cmd.split(" ");
      if (tokens.length != 3 || tokens[0].equals("collect") == false || tokens[1].equals("card") == false){
        System.out.println("Invalid choice");
        continue;
      }
      String[] spos = tokens[2].split(",");
      if (spos.length != numPick){
        System.out.println("Invalid choice");
        continue;
      }
      boolean valid = true;
      for (int i=0; i<numPick; i++){
        int npos = -1;
        try{
          npos = Integer.parseInt(spos[i]);
        }
        catch (Exception e){
          System.out.println("Invalid card position");
          valid = false;
          break;
        }
        if (npos < 1 || npos > numDraw){
          System.out.println("Invalid card position");
          valid = false;
          break;
        }
        npos--;
        pos.add(npos);
      }
      if (!valid) continue;
      break;
    }
    ArrayList<Card> picked = new ArrayList<>();
    Collections.sort(pos);
    for (int i=numPick-1; i>=0; i--){
      int p = pos.get(i);
      picked.add(choices.get(p));
      choices.remove(p);
    }
    System.out.printf("You chose cards:\n");
    for (Card c : picked){
      System.out.println(c.display());
      player.takeCard(c);
    }
    while (choices.size() > 0){
      deck.addCard(choices.get(0));
      choices.remove(0);
    }
    return true;
  }

  public void turn()
  {
    System.out.println("Character choosing is over, action round will now begin.");
    System.out.println("================================\nTURN PHASE\n================================");
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
        prompt(false);
        continue;
      }
      System.out.printf("Player %d is the %s\n", player.getId(), character.getName());
      if (player.isKilled()){
        System.out.printf("Player %d loses their turn because they were assassinated.\n", player.getId());
        prompt(false);
        continue;
      }
      if (player.isRobbed()){
        System.out.printf("The Thief steals %d gold from the %s.\n", player.getGold(), character.getName());
        int gold = player.getGold();
        Player robber = player.getRobber();
        robber.addGold(gold);
        player.addGold(-gold);
      }
      if (player.getId() != 1){
        prompt(false);
        continue;
      }
      System.out.println("Your turn.");
      if (player.hasPlacedDistrict("School Of Magic")){
        Card card = player.findPlacedCardByName("School Of Magic");
        String targetCol = "purple";
        if (character.getName().equals("Warlord")){
          targetCol = "red";
        }
        else if (character.getName().equals("Bishop")){
          targetCol = "blue";
        }
        else if (character.getName().equals("Merchant")){
          targetCol = "green";
        }
        else if (character.getName().equals("King")){
          targetCol = "yellow";
        }
        card.setColor(targetCol);
        System.out.printf("For the purposes of income, the School Of Magic's color is considered to be %s.\n", targetCol);
      }
      character.promptSkill(this);
      character.passiveSkill(this, player);
      promptResource(player);
      prompt(true);
      if (player.isCityCompleted(this)){
        firstCompletePlayerId = player.getId();
      }
      if (player.hasPlacedDistrict("Poor House") && player.getGold() == 0){
        player.addGold(1);
        System.out.printf("Poor House: Player %d received 1 gold.\n", player.getId());
      }
      if (player.hasPlacedDistrict("Park") && player.getNumCards() == 0){
        System.out.printf("Park: Player %d may draw 2 cards.\n", player.getId());
        drawPick(2, 2, player);
      }
    }
  }

  public void endGame()
  {
    int maxScore = -1, maxScoreCount = 0;
    int winnerId = -1;
    for (Player p : players){
      p.scoreCalculation(this);
      if (maxScore < p.getScore()){
        maxScore = p.getScore();
        maxScoreCount = 1;
        winnerId = p.getId();
      }
      else if (maxScore == p.getScore()){
        maxScoreCount++;
      }
    }
    if (maxScoreCount > 1){
      for (int i=0; i<8; i++){
        Player player = findPlayerWithChar(characters.get(i).getName());
        if (player == null) continue;
        if (player.getScore() != maxScore) continue;
        winnerId = player.getId();
        System.out.printf("Tie break: In the last round player %d has the highest rank character.\n", winnerId);
        break;
      }
    }
    System.out.printf("Congratulations, Player %d has won this game.\n", winnerId);
  }

	public App()
  {
    scanner = new Scanner(System.in);
    round = 0;
    try{
      getCardsData();
    }
    catch (FileNotFoundException e){
      System.out.println("CARDS FILE NOT FOUND");
      e.printStackTrace();
      return;
    }
    initialSetup();
    while(true){
      round++;
      characterSelection();
      turn();
      if (isGameOver()) break;
      System.out.println("Everyone is done, new round!");
    }
    endGame();
  }

  public static void main(String[] args)
  {
    App app = new App();
  }

}
