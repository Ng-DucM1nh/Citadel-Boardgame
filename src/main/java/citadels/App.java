package citadels;

import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

import org.json.simple.*;

/**
 * Main application logic for running the Citadels game.
 * Handles game initialization, turn processing, and saving/loading.
 */
public class App {
  static final String promptArrow = "> ";

	private File cardsFile;
  private ArrayList<Player> players;
  private int crownOwnerId;
  private Deck deck;
  private ArrayList<Character> characters;
  private Scanner scanner;
  private int firstCompletePlayerId = -1;
  private int round = 1;
  private boolean debugMode = false;
  private String phase = "";
  private int numDistrictGoal = 8;
  private boolean loadCalled = false;
  private int currentTurnPlayerId = -1;
  private Character facedownChar;

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

/**
 * toggleDebug method for game application logic.
 */
  public void toggleDebug()
  {
    // Toggle debug mode on/off
    debugMode = !debugMode;
  }

  public void getCardsData() throws FileNotFoundException
  {
    // Load card data from TSV file
    cardsFile = new File("src/main/resources/citadels/cards.tsv");
    Scanner fileScanner = new Scanner(cardsFile); // this can possibly throw FileNotFoundException
    deck = new Deck();
    fileScanner.nextLine(); // Skip header
    while (fileScanner.hasNextLine()){
      String s = fileScanner.nextLine();
      String[] tokens = s.split("\t");
      String name = tokens[0];
      int qty = Integer.parseInt(tokens[1]);
      String color= tokens[2];
      int cost = Integer.parseInt(tokens[3]);
      String desc = (tokens.length == 4 ? "A normal card with no ability" : tokens[4]);
      // Add each card to the deck according to its quantity
      while (qty-- > 0){
        deck.addCard(new Card(name, color, cost, cost, desc));
      }
    }
    fileScanner.close();
  }

  public void assignCrown(int crownPlayerId)
  {
    // Validate crownPlayerId
    if (crownPlayerId < 1 || crownPlayerId > players.size()){
      throw new RuntimeException("invalid crownPlayerId");
    }
    // Check for Throne Room bonus
    for (Player p : players){
      if (p.hasPlacedDistrict("Throne Room") == false) continue;
      if (crownOwnerId != crownPlayerId){
        p.addGold(1);
        System.out.printf("Throne Room: Player %d receives 1 gold.\n", p.getId());
      }
      break;
    }
    crownOwnerId = crownPlayerId;
  }

  public void addCharacters()
  {
    // Reset and add all character cards
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
    // Initialize player list and game state
    players = new ArrayList<>();
    crownOwnerId = -1;
    characters = new ArrayList<>();
    // Prompt for number of players
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
    // Add Player objects to list
    for (int id=1; id<=numPlayers; id++){
      Player p = new Player(id);
      players.add(p);
    }
    // Randomly assign crown
    Random rand = new Random(System.currentTimeMillis());
    int crownPlayerId = rand.nextInt(numPlayers) + 1;
    assignCrown(crownPlayerId);
    // Shuffle deck
    System.out.println("Shuffling deck...");
    deck.shuffle();
    // Add character cards
    System.out.println("Adding characters...");
    addCharacters();
    // Deal starting gold and cards to players
    System.out.println("Dealing cards...");
    for (Player p : players){
      p.addGold(2);
      for (int i=0; i<4; i++){
        Card c = deck.pop();
        p.takeCard(c);
      }
    }
    System.out.printf("Starting Citadel with %d players...\n", numPlayers);
    System.out.println("You are player 1");
    phase = "charSelect";
  }

  public int getNumDistrictGoal()
  {
    return numDistrictGoal;
  }

  public void setNumDistrictGoal(int num)
  {
    numDistrictGoal = num;
    System.out.printf("A completed city must have at least %d districts.\n", numDistrictGoal);
  }

/**
 * isGameOver method for game application logic.
 */
  public boolean isGameOver()
  {
    int districtGoal = 8;
    // Check if any player has Bell Tower, which reduces the goal
    for (Player p : players){
      if (p.hasPlacedDistrict("Bell Tower")) districtGoal = 7;
    }
    setNumDistrictGoal(districtGoal);
    // Check if any player has completed their city
    for (Player p : players){
      if (p.isCityCompleted(this)) return true;
    }
    return false;
  }
  
/**
 * chooseCharacter method for game application logic.
 */
  public ArrayList<Character> chooseCharacter(Player player, ArrayList<Character> charList, Character facedownChar)
  {
    // Special rule for 7 players: add facedown character back if only one left
    if (players.size() == 7 && charList.size() == 1) charList.add(facedownChar);
    if (player.getId() == 1){
      // Human player chooses character
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
      // Bot chooses randomly
      Random rand = new Random(System.currentTimeMillis());
      int id = rand.nextInt(charList.size());
      player.setCharacter(charList.get(id));
      charList.remove(id);
    }
    System.out.printf("Player %d chose a character.\n", player.getId());
    return charList;
  }

/**
 * handCommand method for game application logic.
 */
  public void handCommand()
  {
    // Show cards in hand and gold for player 1
    System.out.printf("You have %d gold. Cards in hand:\n", players.get(0).getGold());
    ArrayList<Card> cards = players.get(0).getCards();
    for (int i=0; i<cards.size(); i++){
      System.out.printf("%d. %s (%s), cost: %d\n", i+1, cards.get(i).getName(), cards.get(i).getColor(), cards.get(i).getCost());
    }
  }

/**
 * goldCommand method for game application logic.
 */
  public void goldCommand(String[] tokens)
  {
    // Show gold for player 1 or a specific player
    if (tokens.length > 2){
      System.out.println("Invalid parameter.");
      System.out.println("Expected \"gold [p]\".");
      return;
    }
    if (tokens.length == 1){
      System.out.printf("You have %d gold.\n", players.get(0).getGold());
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

/**
 * buildCommand method for game application logic.
 */
  public void buildCommand(String[] tokens, Player player)
  {
    // Build a district from hand
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
    player.build(this, pos-1);
  }

/**
 * citadelCommand method for game application logic.
 */
  public void citadelCommand(String[] tokens)
  {
    // Show built districts for a player
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
    player.showCity();
  }

/**
 * allCommand method for game application logic.
 */
  public void allCommand()
  {
    // Show summary info for all players
    for (int i=0; i<players.size(); i++){
      Player player = players.get(i);
      System.out.printf("Player %d%s: cards=%d gold=%d city=", player.getId(), (player.getId() == 1 ? " (you)" : ""), player.getNumCards(), player.getGold());
      ArrayList<Card> placedCards = player.getPlacedCards();
      for (Card card : placedCards){
        System.out.print(card.display()+" ");
      }
      System.out.println();
    }
  }

/**
 * infoCommand method for game application logic.
 */
  public void infoCommand(String[] tokens)
  {
    // Show info about a character or card
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
    int numCards = players.get(0).getNumCards();
    if (pos < 1 || pos > numCards){
      System.out.printf("No character named %s. Invalid card position %s\n", tokens[1], tokens[1]);
      return;
    }
    Card card = players.get(0).getCardAtPos(pos-1);
    System.out.printf("%s: %s\n", card.getName(), card.getDesc());
  }

/**
 * helpCommand method for game application logic.
 */
  public void helpCommand()
  {
    // Print help for available commands
    System.out.println("Available commands:\ninfo : show information about a character or building\nt : processes turns\n");
    System.out.println("all : shows all current game info\ncitadel/list/city : shows districts built by a player");
    System.out.println("hand : shows cards in hand\ngold [p] : shows gold of a player\n");
    System.out.println("build <place in hand> : Builds a building into your city");
    System.out.println("action : Gives info about your special action and how to perform it\nend : Ends your turn");
  }

/**
 * actionCommand method for game application logic.
 */
  public void actionCommand(String[] tokens, Player player)
  {
    // Handle special action command for player
    if (tokens.length == 1){
      System.out.println(players.get(0).getActionList());
      return;
    }
    player.action(this, tokens);
  }

/**
 * botBuild method for game application logic.
 */
  public void botBuild(Player player)
  {
    // Bot attempts to build as many districts as allowed
    if (player.getNumCards() == 0) return;
    Random rand = new Random(System.currentTimeMillis());
    int numTry = player.getNumCards();
    while (player.getCharacter().getNumBuild() > 0 && numTry-- > 0){
      int pos = rand.nextInt(player.getNumCards())+1;
      buildCommand(new String[] {"build", String.valueOf(pos)}, player);
    }
  }

/**
 * botAction method for game application logic.
 */
  public void botAction(Player player)
  {
    // Bot performs special actions for Magician or Warlord
    String charName = player.getCharacter().getName();
    if (charName.equals("Magician") == false && charName.equals("Warlord") == false) return;
    Random rand = new Random(System.currentTimeMillis());
    if (charName.equals("Magician")){
      int c = rand.nextInt(2);
      if (c == 0) return;
      c = rand.nextInt(2);
      if (c == 0){
        // swap hands
        int id = -1;
        while (id == -1 || id == player.getId()){
          id = rand.nextInt(players.size()) + 1;
        }
        actionCommand(new String[] {"action", "swap", String.valueOf(id)}, player);
      }
      else{
        // redraw cards
        int numRedraw = rand.nextInt(player.getNumCards())+1;
        ArrayList<Integer> pos = new ArrayList<>();
        for (int i=0; i<player.getNumCards(); i++){
          pos.add(i+1);
        }
        Collections.shuffle(pos);
        String spos = "";
        for (int i=0; i<numRedraw; i++){
          spos += String.valueOf(pos.get(i));
          if (i < numRedraw-1) spos+=",";
        }
        actionCommand(new String[] {"action", "redraw", spos}, player);
      }
      return;
    }
    if (charName.equals("Warlord")){
      int c = rand.nextInt(2);
      if (c == 0) return;
      int id = -1;
      while (id == -1 || id == player.getId()){
        id = rand.nextInt(players.size())+1;
      }
      Player target = players.get(id-1);
      if (target.getNumPlacedCards() == 0) return;
      int pos = rand.nextInt(target.getNumPlacedCards());
      String cardName = target.findPlacedCardByPos(pos).getName().replace(' ','_');
      actionCommand(new String[] {"action", "destroy", String.valueOf(id), cardName}, player);
    }
  }

/**
 * saveCommand method for game application logic.
 */
  public void saveCommand(String[] tokens)
  {
    // Save game state to file
    if (tokens.length != 2){
      System.out.println("Invalid arguments");
      return;
    }
    save(tokens[1]);
  }

/**
 * loadCommand method for game application logic.
 */
  public void loadCommand(String[] tokens)
  {
    // Load game state from file
    if (tokens.length != 2){
      System.out.println("Invalid arguments");
      return;
    }
    loadCalled = true;
    load(tokens[1]);
  }

/**
 * prompt method for game application logic.
 */
  public void prompt(Player player)
  {
    // Main command prompt for player or bot
    boolean inTurn = (player != null && player.getId() == 1);
    if (player != null && !inTurn){
      botAction(player);
      botBuild(player);
    }
    while (true){
      System.out.print(promptArrow);
      String command = scanner.nextLine();
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
        if (inTurn) buildCommand(tokens, player);
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
        actionCommand(tokens, player);
      }
      if (command.equals("debug")){
        System.out.printf("Debug mode toggled\n");
        toggleDebug();
      }
      if (tokens[0].equals("save")){
        saveCommand(tokens);
      }
      if (tokens[0].equals("load")){
        loadCommand(tokens);
        if (loadCalled) return;
      }
    }
  }

/**
 * characterSelection method for game application logic.
 */
  public void characterSelection()
  {
    // Rotate crowned player to first index
    while (players.get(0).getId() != crownOwnerId){
      Player p = players.get(0);
      players.remove(0);
      players.add(p);
    }
    System.out.printf("Player %d is the crowned player and goes first.\n", players.get(0).getId());
    System.out.println("Press t to process turns");
    System.out.println("================================\nSELECTION PHASE\n================================");
    prompt(null);
    if (loadCalled) return;
    // Shuffle character cards and remove some for selection phase
    if (currentTurnPlayerId == -1){
      Collections.shuffle(characters, new Random(System.currentTimeMillis()));
      facedownChar = characters.get(0);
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

    }
    for (Player player : players){
      if (currentTurnPlayerId != -1){
        System.out.printf("cur turn player id%d\n", currentTurnPlayerId);
        if (player.getId() != currentTurnPlayerId) continue;
      }
      else{
        currentTurnPlayerId = player.getId();
      }
      characters = chooseCharacter(player, characters, facedownChar);
      prompt(null);
      if (loadCalled) return;
      currentTurnPlayerId = -1;
    }
    addCharacters();
    // Rotate player 1 back to first index
    while (players.get(0).getId() != 1){
      Player p = players.get(0);
      players.remove(0);
      players.add(p);
    }
    phase = "turn";
  }

  public Player findPlayerWithChar(String charName)
  {
    // Find player with a specific character
    for (Player p : players){
      if (p.getCharacter().getName().equals(charName)) return p;
    }
    return null;
  }

  public void promptResource(Player player)
  {
    // Prompt player to collect gold or draw cards
    boolean human = (player.getId() == 1);
    String res = "";
    int numDraw = 2, numPick = 1;
    // Check for Observatory and Library bonuses
    if (player.hasPlacedDistrict("Observatory")){
      numDraw = 3;
      System.out.printf("Observatory: Player %d may draw %d cards and keep 1.\n", player.getId(), 3);
    }
    if (player.hasPlacedDistrict("Library")){
      numPick = numDraw;
      System.out.printf("Library: Player %d may keep %d cards.\n", player.getId(), numPick);
    }
    if (!human){
      // Bot chooses randomly
      Random rand = new Random(System.currentTimeMillis());
      while (true){
        int choice = rand.nextInt(2);
        if (choice == 0){
          player.addGold(2);
          System.out.printf("Player %d collected 2 gold\n", player.getId());
          break;
        }
        else{
          if (drawPick(numDraw, numPick, player)) break;
        }
      }
      return;
    }
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
      
      if (drawPick(numDraw, numPick, player)) break;
    }
  }

/**
 * drawPick method for game application logic.
 */
  public boolean drawPick(int numDraw, int numPick, Player player)
  {
    // Handle drawing and picking cards for player
    boolean human = (player.getId() == 1);
    if (deck.size() == 0){
      if (human) System.out.println("deck empty");
      return false;
    }
    if (!human){
      // Bot picks cards automatically
      numPick = Math.min(numPick, numDraw);
      numPick = Math.min(numPick, deck.size());
      for (int i=0; i<numPick; i++){
        player.takeCard(deck.pop());
      }
      System.out.printf("Player %d drew %d cards\n", player.getId(), numPick);
      return true;
    }
    System.out.printf("%d remaining cards in deck\n", deck.size());
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
    if (numPick == numDraw){
      // If picking all drawn cards
      for (int i=0; i<numPick; i++){
        pos.add(i);
      }
    }
    else{
      // Prompt user to pick specific cards
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

/**
 * turn method for game application logic.
 */
  public void turn()
  {
    // Main turn phase: process each character in order
    System.out.println("Character choosing is over, action round will now begin.");
    System.out.println("================================\nTURN PHASE\n================================");
    for (int i=0; i<8; i++){
      Character character = characters.get(i);
      System.out.printf("%d. %s\n", i+1, character.getName());
      Player player = null;
      // Find player with this character
      for (Player p : players){
        if (!p.getCharacter().getName().equals(character.getName())) continue;
        player = p;
        break;
      }
      System.out.printf("currentTurnPlayerId %d\n", currentTurnPlayerId);
      if (currentTurnPlayerId != -1){
        if (player == null || player.getId() != currentTurnPlayerId) continue;
      }
      else if (player != null){
        currentTurnPlayerId = player.getId();
      }
      boolean inTurn = false;
      if (player == null){
        // No one has this character
        System.out.printf("No one is the %s\n", character.getName());
        prompt(null);
        if (loadCalled) return;
        currentTurnPlayerId = -1;
        continue;
      }
      System.out.printf("Player %d is the %s\n", player.getId(), character.getName());
      if (player.isKilled()){
        // Handle assassination
        if (player.hasPlacedDistrict("Hospital") == false){
          System.out.printf("Player %d loses their turn because they were assassinated.\n", player.getId());
          prompt(null);
          if (loadCalled) return;
          currentTurnPlayerId = -1;
          continue;
        }
        else{
          System.out.printf("Hospital: Player %d may take an action but may not build a district card or use character's power\n", player.getId());
        }
      }
      if (player.isRobbed() && !player.isKilled()){
        // Handle robbery
        System.out.printf("The Thief steals %d gold from the %s.\n", player.getGold(), character.getName());
        int gold = player.getGold();
        Player robber = player.getRobber();
        robber.addGold(gold);
        player.addGold(-gold);
      }
      boolean myTurn = (player.getId() == 1);
      if (myTurn) System.out.println("Your turn.");
      if (player.hasPlacedDistrict("School Of Magic")){
        // School of Magic color adapts for income
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
      if (!player.isKilled()) character.promptSkill(this, player);
      if (!player.isKilled()) character.passiveSkill(this, player);
      if (!myTurn && debugMode){
        // Show bot hand in debug mode
        System.out.printf("Debug: Player %d's hand: ", player.getId());
        for (int j=0; j<player.getNumCards(); j++){
          System.out.printf("%s ", player.getCardAtPos(j).display());
        }
        System.out.println();
      }
      promptResource(player);
      prompt(player);
      if (loadCalled){
        return;
      }
      if (player.isCityCompleted(this)){
        if (firstCompletePlayerId == -1) firstCompletePlayerId = player.getId();
      }
      if (player.hasPlacedDistrict("Poor House") && player.getGold() == 0){
        // Poor House bonus
        player.addGold(1);
        System.out.printf("Poor House: Player %d received 1 gold.\n", player.getId());
      }
      if (player.hasPlacedDistrict("Park") && player.getNumCards() == 0){
        // Park bonus
        System.out.printf("Park: Player %d may draw 2 cards.\n", player.getId());
        drawPick(2, 2, player);
      }
      currentTurnPlayerId = -1;
    }
    phase = "charSelect";
  }

/**
 * endGame method for game application logic.
 */
  public void endGame()
  {
    // Calculate scores and determine winner
    int maxScore = -1, maxScoreCount = 0;
    int winnerId = -1;
    for (Player p : players){
      p.scoreCalculation(this);
      System.out.println();
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
      // Tie-breaker: highest rank character in last round
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

/**
 * save method for game application logic.
 */
  public void save(String fileName)
  {
    // Save game state to JSON file
    fileName += ".json";
    try{
      File file = new File(fileName);
      FileWriter writer = new FileWriter(fileName);
      writer.write(this.toJSON().toString());
      writer.close();
    }
    catch (Exception e){
      System.out.printf("Error saving to %s.json\n", fileName);
      e.printStackTrace();
      return;
    }
    System.out.printf("Saved game state to %s\n", fileName);
  }

/**
 * toJSON method for game application logic.
 */
  public JSONObject toJSON()
  {
    // Convert game state to JSON object
    JSONObject obj = new JSONObject();
    obj.put("crownOwnerId", crownOwnerId);
    obj.put("deck", deck.toJSON());
    obj.put("firstCompletePlayerId", firstCompletePlayerId );
    obj.put("round", round);
    obj.put("debugMode", debugMode );
    obj.put("phase", phase);
    obj.put("numDistrictGoal", numDistrictGoal);
    obj.put("currentTurnPlayerId", currentTurnPlayerId);
    obj.put("facedownChar", facedownChar.toJSON());
    JSONArray charactersJson = new JSONArray();
    for (Character c : characters){
      charactersJson.add(c.toJSON());
    }
    obj.put("characters", charactersJson);
    JSONArray playersJson = new JSONArray();
    for (Player p : players){
      playersJson.add(p.toJSON());
    }
    obj.put("players", playersJson);
    return obj;
  }

/**
 * load method for game application logic.
 */
  public void load(String fileName)
  {
    // Load game state from JSON file
    fileName += ".json";
    String jsonCode = "";
    try{
      File file = new File(fileName);
      Scanner fileScanner = new Scanner(file);
      jsonCode = fileScanner.nextLine();
      fileScanner.close();
    }
    catch (Exception e){
      System.out.printf("Some error occurred when loading from %s.json\n", fileName);
      e.printStackTrace();
      return;
    }

    JSONObject obj = (JSONObject) JSONValue.parse(jsonCode);
    crownOwnerId = ((Long)obj.get("crownOwnerId")).intValue();
    numDistrictGoal = ((Long)obj.get("numDistrictGoal")).intValue();
    currentTurnPlayerId = ((Long)obj.get("currentTurnPlayerId")).intValue();
    deck = new Deck((JSONArray)obj.get("deck"));
    firstCompletePlayerId = ((Long)obj.get("firstCompletePlayerId")).intValue();
    round = ((Long)obj.get("round")).intValue();
    debugMode = (boolean)obj.get("debugMode");
    phase = (String)obj.get("phase");
    characters = new ArrayList<>();
    JSONArray charactersJson = (JSONArray)obj.get("characters");
    for (int i=0; i<charactersJson.size(); i++){
      JSONObject charJson = (JSONObject)charactersJson.get(i);
      String name = (String)charJson.get("name");
      Character character = Character.getCharFromJsonObj(charJson);
      characters.add(character);
    }
    System.out.printf("characters dau r %d\n", characters.size());
    for (Character c : characters){
      if (c == null){
        System.out.println("null char");
      }
      else{
        System.out.println(c.getName());
      }
    }
    players = new ArrayList<>();
    JSONArray playersJson = (JSONArray)obj.get("players");
    for (int i=0; i<playersJson.size(); i++){
      players.add(new Player((JSONObject)playersJson.get(i)));
    }
    facedownChar = Character.getCharFromJsonObj((JSONObject)obj.get("facedownChar"));
    System.out.printf("Load from %s successfully\n", fileName);
  }

/**
 * App method for game application logic.
 */
	public App()
  {
    // Main application constructor: setup and run game loop
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
    while(true){
      // Reset all players for new round
      for (Player p : players){
        p.resetCard();
        p.resetPlayer();
      }
      if (phase.equals("charSelect")) characterSelection();
      if (loadCalled){
        loadCalled = false;
        continue;
      }
      if (phase.equals("turn")) turn();
      if (loadCalled){
        loadCalled = false;
        continue;
      }
      if (isGameOver()) break;
      System.out.println("Everyone is done, new round!");
      round++;
    }
    endGame();
  }

/**
 * main method for game application logic.
 */
  public static void main(String[] args)
  {
    // Entry point: start the game
    App app = new App();
  }

}