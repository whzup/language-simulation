import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class main extends PApplet {

Map langMap;
Population population;
Vocabulary lingua;
Vocabulary dialect;

// All vowels that can be used for words in mutation order
static final StringList vowels = new StringList(
  "a", "\u00e4", "e", "i", "j", "o", "\u00f6", "u", "\u00fc", "y"
);

// All consonants that can be used for words in mutation order
static final StringList consonants = new StringList(
  "b", "p", "d", "t", "g", "h", "k", "c", "q",
  "m", "n", "l", "r", "s", "z", "f", "v", "w", "x"
);

// All diphtongs that can be used for mutation
static final StringList diphs = new StringList(
  "aa", "ai", "aj", "ao", "au", "ay", "ea", "ee", "ei", "ej", "eo", "ey",
  "ie", "ii", "ij", "iy", "ja", "j\u00e4", "je", "ji", "jo", "ju", "jy", "oa",
  "oi", "oj", "oo", "ou", "oy", "ua", "ue", "ui", "uj", "uo", "uu", "uy",
  "\u00e4i", "\u00e4j", "\u00e4y", "\u00f6i", "\u00f6j", "\u00f6y", "\u00fci", "\u00fcj","\u00fcy"
);

// Consonant shifts loosely based on Grimm's law
static final StringList shift1 = new StringList(
  "bh", "b", "p", "pf"
);

static final StringList shift2 = new StringList(
  "dh", "d", "t", "th"
);

static final StringList shift3 = new StringList(
  "gh", "g", "k", "x"
);

static final StringList shift4 = new StringList(
  "gwh", "gw", "kw", "xw"
);

static final StringList shift5 = new StringList(
  "th", "pf", "w", "h"
);

public void setup() {
  
  langMap = new Map();
  population = new Population(15);
  lingua = new Vocabulary('l');
  dialect = new Vocabulary('i');

  // print the words in the lingua franca vocabulary
  for(int i = 0; i < lingua.wordCount; i++) {
    Word w = lingua.vocabulary.get(i);
    print(w.letters,"\n");
  }
}

public void draw() {
  frameRate(2.5f);
  background(250);
  langMap.display();
  population.update();
}

/* TODO - Make the agent search more efficient. Maybe just check the neighbor
          cells for every agent instead of checking the whole map for every agent

        - Make the doubleConsonant method more efficient. It always checks every
          letter even if it doesn't act.

        - Make the map displaying more efficient.

        - solve all the word mutations with StringLists instead of
           character arrays

        - doubleConsonant is very complex. Maybe think of an easier way
           to implement it

        - you cannot jump from j to o -> from a to \u00e4 or to o

        - change the vowels of diphtongs

        - method which checks if the word has changed so it can be
           inserted in the etym array (hard) -> maybe create class-wide
            variables with random words so they can be checked inside the
             class

        - create a method to mutate the begin or the end of a word if
           it is often used with another word with a vowel on the beginning
            or the end
             -> maybe use another class to create sentences

        - maybe give certain letters a higher probability to be picked
           in the different languages

        - maybe include mountain regions which have a slower walking rate?
*/

/*
TODO Every island should have its own vocabulary, but there should also be a
lingua franca. The words of the lingua franca should have a chance to be included
into the vocabulary of an island but the other way should also be possible.
-> Maybe develop a model?
-> If an agent talks with an agent from his own island they should use the
   same vocabulary
*/

/*
TODO talking:
(1) get the ID of the nearest agent                                 done
(2) get the agent by ID                                             done
(3) exchange random (Zipf) words from his vocabulary                Zipf misses
(4) give the words the possibility to mutate (vowels, consonants)   done
(5) if the word mutates -> put it in the etym of itself
*/
/**
  * @author Aaron
  */

class Agent {
  public PVector location;       // Current position of the agent
  public int island;             // Current island
  public int spawnIsland;        // Island on which the agent spawned
  public int id;                 // ID of the agent
  public int currentX;           // Current Square in x-direction
  public int currentY;           // Current Square in y-direction
  public Vocabulary lingua;      // Vocabulary for the lingua franca
  public Vocabulary dialect;     // Vocabulary for the island dialect

  /** Agent class constructor
    * <p>
    * The agent class includes the methods to move, teleport
    * and exchange linguaulary with other agents.
    * <p>
    * @param xcoord The x-coordinate of the agent's spawn
    * @param ycoord The y-coordinate of the agent's spawn
    * @param lingua The vocabulary of the lingua franca
    * @param dialect The vocabulary of the island dialect
    */
  public Agent(int xcoord, int ycoord, Vocabulary lingua, Vocabulary dialect) {

    this.lingua = lingua;
    this.dialect = dialect;

    this.location = new PVector(xcoord, ycoord);

    // Decide on which island the agent currently is
    if(langMap.map[PApplet.parseInt(location.y)][PApplet.parseInt(location.x)] == 1) {
      this.island = 1;
    }
    else if(langMap.map[PApplet.parseInt(location.y)][PApplet.parseInt(location.x)] == 2) {
      this.island = 2;
    }
    else if(langMap.map[PApplet.parseInt(location.y)][PApplet.parseInt(location.x)] == 3) {
      this.island = 3;
    }

    // Decide on which island the agent spawned
    if(langMap.map[ycoord][xcoord] == 1) {
      this.spawnIsland = 1;
    }
    else if(langMap.map[ycoord][xcoord] == 2) {
      this.spawnIsland = 2;
    }
    else if(langMap.map[ycoord][xcoord] == 3) {
      this.spawnIsland = 3;
    }

    // Give the agent a random ID
    this.id = PApplet.parseInt(random(PApplet.parseFloat(MAX_INT)));
  }

  /** Displays the agent
    * <p>
    * The agent is displayed as a circle with a darker shade of the island colour
    */
  public void display() {
    if(spawnIsland == 1) {
      fill(0xff7f3939);
    }
    else if(spawnIsland == 2){
      fill(0xff7f6000);
    }
    else if(spawnIsland == 3) {
      fill(0xff195953);
    }
    noStroke();
    ellipse((location.x+0.5f)*langMap.gridX, (location.y+0.5f)*langMap.gridY, langMap.gridX-7, langMap.gridY-7);
  }

  /** Returns the ID of the nearest Agent
    * <p>
    * Searches the whole population for the nearest neighbour
    * and returns its ID
    * <p>
    * @return the ID of the nearest neighbour
    */
  public int nearestAgent() {
    float min = langMap.mapWidth;
    for(Agent b : population.pop) {
      float dist = sqrt((location.x - b.location.x)*(location.x - b.location.x)
                   + (location.y - b.location.y)*(location.y - b.location.y));
      if(dist < min && dist != 0) {
        min = dist;
        id = b.id;
      }
    }
    return id;
  }
/*
  // returns the ID of an agent which is 1 cell away -> used for talking
  public int neighborAgent() {
    // Get the coordinates of the neighbour cells
    int[] upArr = {int(location.x), int(location.y - 1)};
    int[] upRightArr = {int(location.x + 1), int(location.y - 1)};
    int[] upLeftArr = {int(location.x - 1), int(location.y - 1)};
    int[] downArr = {int(location.x), int(location.y + 1)};
    int[] downRightArr = {int(location.x + 1), int(location.y + 1)};
    int[] downLeftArr = {int(location.x - 1), int(location.y + 1)};
    int[] rightArr = {int(location.x + 1), int(location.y)};
    int[] leftArr = {int(location.x - 1), int(location.y)};

    // Get the values of the neighbour cells
    int up = population.agentMap.map[upArr[1]][upArr[0]];
    int upRight = population.agentMap.map[upRightArr[1]][upRightArr[0]];
    int upLeft = population.agentMap.map[upLeftArr[1]][upLeftArr[0]];
    int down = population.agentMap.map[downArr[1]][downArr[0]];
    int downRight = population.agentMap.map[downRightArr[1]][downRightArr[0]];
    int downLeft = population.agentMap.map[downLeftArr[1]][downLeftArr[0]];
    int right = population.agentMap.map[rightArr[1]][rightArr[0]];
    int left = population.agentMap.map[leftArr[1]][leftArr[0]];

    ArrayList<int[]> reachableAgents = new ArrayList<int[]>();

    if()
  }*/

  /** Moves the agent in a random direction
    */
  public void move() {

    // Get the coordinates of the neighbour cells
    int[] upArr = {PApplet.parseInt(location.x), PApplet.parseInt(location.y - 1)};
    int[] downArr = {PApplet.parseInt(location.x), PApplet.parseInt(location.y + 1)};
    int[] rightArr = {PApplet.parseInt(location.x + 1), PApplet.parseInt(location.y)};
    int[] leftArr = {PApplet.parseInt(location.x - 1), PApplet.parseInt(location.y)};

    // Get the values of the neighbour cells
    int up = langMap.map[upArr[1]][upArr[0]];
    int down = langMap.map[downArr[1]][downArr[0]];
    int right = langMap.map[rightArr[1]][rightArr[0]];
    int left = langMap.map[leftArr[1]][leftArr[0]];

    // Check if there are agents on the neighbour cells
    int upAgent = population.agentMap[upArr[1]][upArr[0]];
    int downAgent = population.agentMap[downArr[1]][downArr[0]];
    int rightAgent = population.agentMap[rightArr[1]][rightArr[0]];
    int leftAgent = population.agentMap[leftArr[1]][leftArr[0]];

    // ArrayList to store all neighbour cells on which the agent can move
    ArrayList<int[]> movableCells = new ArrayList<int[]>();

    // Check if the neighbour cells are still on an island,
    // if so move them to moveableCells
    if(up > 0 && upAgent != 1) {
      movableCells.add(upArr);
    }
    if(down > 0 && downAgent != 1) {
      movableCells.add(downArr);
    }
    if(right > 0 && rightAgent != 1) {
      movableCells.add(rightArr);
    }
    if(left > 0 && leftAgent != 1) {
      movableCells.add(leftArr);
    }

    // Choose a random direction from movableCells
    int direction = PApplet.parseInt(random(movableCells.size()));
    if(movableCells.size() > 0) {
      location.x = movableCells.get(direction)[0];
      location.y = movableCells.get(direction)[1];
    }

  }

  /** Teleports the agent
    * <p>
    * The agent is teleported if he walks on a gate.
    * This method gets the coordinates of the gatesXY
    * from the gatesXY methods (class Map)
    */
  public void gateTeleport() {
    // Teleport from A to B
    if(location.x == langMap.gates("AB")[0][0] && location.y == langMap.gates("AB")[0][1]) {
      location.x = langMap.gates("AB")[1][0];
      location.y = langMap.gates("AB")[1][1];
    }
    // or from B to A
    else if(location.x == langMap.gates("AB")[1][0] && location.y == langMap.gates("AB")[1][1]) {
      location.x = langMap.gates("AB")[0][0];
      location.y = langMap.gates("AB")[0][1];
    }

    // Teleport from A to C
    else if(location.x == langMap.gates("AC")[0][0] && location.y == langMap.gates("AC")[0][1]) {
      location.x = langMap.gates("AC")[1][0];
      location.y = langMap.gates("AC")[1][1];
    }
    // or from C to A
    else if(location.x == langMap.gates("AC")[1][0] && location.y == langMap.gates("AC")[1][1]) {
      location.x = langMap.gates("AC")[0][0];
      location.y = langMap.gates("AC")[0][1];
    }

    // Teleport from B to C
    else if(location.x == langMap.gates("BC")[0][0] && location.y == langMap.gates("BC")[0][1]) {
      location.x = langMap.gates("BC")[1][0];
      location.y = langMap.gates("BC")[1][1];
    }
    // or from C to B
    else if(location.x == langMap.gates("BC")[1][0] && location.y == langMap.gates("BC")[1][1]) {
      location.x = langMap.gates("BC")[0][0];
      location.y = langMap.gates("BC")[0][1];
    }
  }

  // Exchange words with the nearest Agent -> huge performance loss
  public void exchangeWords() {
    double dist;
    for(int j = 0; j < 5;j++) {
      for(int i = 0; i < population.count; i++) {
        Agent comm = population.pop.get(i);
        if(comm.id == nearestAgent()) {
          dist = sqrt((comm.location.x - location.x) * (comm.location.x - location.x)
                      + (comm.location.y - location.y) * (comm.location.y - location.y));
          // Only exchange words if the agent is near enough
          if(dist <= 1.5f) {
            // Random index
            int changeIndex = PApplet.parseInt(random(lingua.wordCount-1));
            // Take a word from the nearest agent
            Word exchangeWord1 = comm.lingua.vocabulary.get(changeIndex);
            comm.lingua.vocabulary.remove(changeIndex);

            // Take a word from the agent
            Word exchangeWord2 = lingua.vocabulary.get(changeIndex);
            lingua.vocabulary.remove(changeIndex);

            // Mutate the word
            exchangeWord1.mutateVowel();
            exchangeWord2.mutateVowel();

            exchangeWord1.mutateConsonant();
            exchangeWord2.mutateConsonant();

            exchangeWord1.doubleConsonant();
            exchangeWord2.doubleConsonant();

            // Exchange the words
            comm.lingua.vocabulary.add(exchangeWord1);
            lingua.vocabulary.add(exchangeWord2);
            print("\"" + exchangeWord1.letters + "\" was exchanged for \"" + exchangeWord2.letters + "\"\n");
          }
        }
      }
    }
  }
}
/**
  * @author Aaron
  */

class Map {
  public int[][] map = { {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,1,1,1,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,1,1,1,0,0,0,0,1,1,5,0,0,0,0,0,0,0,0,0,0,0,5,2,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0},
                  {0,0,1,1,1,1,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,0,0,0,0,0,0,0,0},
                  {0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,0,0,2,2,2,0,0,0,0,0,0,0},
                  {0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,2,2,2,2,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,1,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,2,2,2,0,0,0,2,2,2,2,2,0,0,0,0,0},
                  {0,0,0,0,0,0,1,1,1,1,1,1,0,0,1,0,0,0,0,0,0,0,0,2,2,2,0,0,0,2,2,2,2,2,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,1,1,1,1,0,1,1,1,0,0,0,0,0,0,2,2,2,0,0,0,2,2,2,2,2,2,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,0,0,0,0,0,0,0,2,2,2,0,0,2,2,2,2,2,2,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,2,2,0,0,0,2,2,2,2,2,2,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,2,2,2,0,0,2,2,2,2,2,2,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,2,2,2,2,0,2,2,2,2,2,2,2,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,2,0,2,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,2,2,2,2,2,0,0,2,0,2,2,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,3,3,3,3,3,3,3,0,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,4,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,3,3,3,3,3,3,3,0,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,3,3,3,3,3,0,0,0,3,3,3,3,3,3,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,0,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                  {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};    // The blueprint for the map
  public int mapWidth;                                                                                   // Size of the blueprint
  public int gridX, gridY;                                                                               // Gridsize

  /**Map class constructor
    * <p>
    * The map class defines the Map of the simulation and includes the methods to
    * get coordinates of certain points (gates, islands). It also includes the whole
    * coloring and modeling of the world.
    */
  public Map() {
    this.mapWidth = 40;
    this.gridX = width/mapWidth;
    this.gridY = height/mapWidth;
  }

  /** Returns the area of the island
    * <p>
    * Adds up all the squares as unit squares
    * and returns the number of squares, i.e.
    * the area of the island.
    * <p>
    * @param island The number of the island
    * @return Area of the island
    */
  public int area(int island) {
    int area = 0;
    // Add up the cells with the island number
    for(int i = 0; i < mapWidth; i++) {
      for(int j = 0; j < mapWidth; j++) {
        if(map[i][j] == island) {
          area += 1;
        }
      }
    }
    return area;
  }

  /** Returns the coordinates of the gates
    * <p>
    * @param gate A String specifying which gates should be considered
    *               "AB" : The gate connecting island A to B
    *               "AC" : The gate connecting island A to C
    *               "BC" : The gate connecting island B to C
    * @return A two-dimensional array with the coordinates of the specified gate
    */
  public int[][] gates(String gate) {
    int gateNumber = 0;
    if(gate == "AB") {
      gateNumber = 6;
    }
    else if(gate == "AC") {
      gateNumber = 5;
    }
    else if(gate == "BC") {
      gateNumber = 4;
    }
    int indexOne = 0;
    int indexTwo = 0;
    int[][] indices = new int[2][2];
    for(int i = 0; i < mapWidth; i++) {
      for(int j = 0; j < mapWidth; j++) {
        if(map[i][j] == gateNumber) {
          indices[indexOne][indexTwo] = j;
          indexTwo += 1;
          indices[indexOne][indexTwo] = i;
          indexTwo = 0;
          indexOne += 1;
        }
      }
    }
    return indices;
  }

  /** Method to display the map
    * <p>
    * Displays the map as a two dimensional array with the gates, the gate
    * connections and the watermarks for the islands.
    */
  public void display() {

    // Color declaration
    int gateColor = color(200);

    int islandOne = color(0xffff7373);
    int watermarkOne = color(0xffcc5c5c);

    int islandTwo = color(0xffffc100);
    int watermarkTwo = color(0xffe5ad00);

    int islandThree = color(0xff40e0d0);
    int watermarkThree = color(0xff33b3a6);

    for(int i = 0; i < mapWidth; i++) {
      for(int j = 0; j < mapWidth; j++) {

        // Check which island or gate it is and color it
        // Island One
        if(map[i][j] == 1) {
          fill(islandOne);
          stroke(islandOne);
          rect(j*gridX, i*gridY, gridX, gridY);
        }

        // Island Two
        else if(map[i][j] == 2) {
          fill(islandTwo);
          stroke(islandTwo);
          rect(j*gridX, i*gridY, gridX, gridY);
        }

        // Island Three
        else if(map[i][j] == 3) {
          fill(islandThree);
          stroke(islandThree);
          rect(j*gridX, i*gridY, gridX, gridY);
        }

        // GateBC
        else if(map[i][j] == 4) {
          fill(gateColor);
          stroke(gateColor);
          rect(j*gridX, i*gridY, gridX, gridY);
          int k = i;

          // Draw a line of rectangles to the next gate
          do {
            rectMode(CENTER);
            rect((j+0.5f)*gridX, (k+0.5f)*gridY, 0.2f*gridX, gridY);
            k += 1;
            rectMode(CORNER);
          } while (map[k][j] == 0);

        }

        // GateAB
        else if(map[i][j] == 5) {
          fill(gateColor);
          stroke(gateColor);
          rect(j*gridX, i*gridY, gridX, gridY);
          int l = j;

          // Draw a line of rectangles to the next gate
          do {
            rectMode(CENTER);
            rect((l+0.5f)*gridX, (i+0.5f)*gridY, gridX, 0.2f*gridY);
            l += 1;
            rectMode(CORNER);
          } while (map[i][l] == 0);
        }

        // GateAC
        else if(map[i][j] == 6) {
          fill(gateColor);
          stroke(gateColor);
          rect(j*gridX, i*gridY, gridX, gridY);
          int m = i;

          // Draw a line of rectangles to the next gate
          do {
            rectMode(CENTER);
            rect((j+0.5f)*gridX, (m+0.5f)*gridY, 0.2f*gridX, gridY);
            m += 1;
            rectMode(CORNER);
          } while (map[m][j] == 0);
        }
      }
    }

    // Watermarking of the islands
    PFont Font1 = createFont("Arial Bold", 100);
    textFont(Font1);

    // Give the Island one a watermark
    fill(watermarkOne);
    text("A",210,320);

    // Give the Island two a watermark
    fill(watermarkTwo);
    text("B",710,425);

    // Give the Island three a watermark
    fill(watermarkThree);
    text("C",420,800);
  }
}
/**
  * @author Aaron
  */
class Population {
  public int count;                                                        // Number of agents in the population
  public ArrayList<Agent> pop;                                             // ArrayList with all the agents in it
  public int[][] agentMap = new int[langMap.mapWidth][langMap.mapWidth];   // Map with all agents


  /** Population class constructor
    * <p>
    * The population class includes methods to control the behaviour of all agents
    * currently on the map. It creates a binary grid with all the positions of agents
    * so it can be considered in the move-method.
    * <p>
    * @param count Number of agents in the population
    */
  public Population(int count) {
    this.count = count;
    this.pop = new ArrayList<Agent>();
  }

  /** Resets the agentMap
    * <p>
    * After resetting the whole map is filled with 0's
    */
  public void resetAgentMap() {
    for(int i = 0; i < langMap.mapWidth; i++) {
      for(int j = 0; j < langMap.mapWidth; j++) {
        agentMap[i][j] = 0;
      }
    }
  }

  /** Initialize and move the agents
    * <p>
    * The agents are initialized randomly on the islands and moved
    * using their move method.
    */
  public void update() {
    // If there is no population yet, create one
    if(pop.size() == 0) {
      int i = 0;
      while(i < count) {
        // Only create an agent when the spawning location is on an island (or gate)
        // Might be efficient, as this algorithm does not scan the whole map
        Agent agent = new Agent(PApplet.parseInt(random(langMap.mapWidth)),PApplet.parseInt(random(langMap.mapWidth)), lingua, dialect);
        if(langMap.map[PApplet.parseInt(agent.location.y)][PApplet.parseInt(agent.location.x)] != 0 && agent.location.x > 0 && agent.location.y > 0) {
          agent.display();
          pop.add(agent);
          i++;
        }
      }
    }

    // Else let the population move
    else {
      for(Agent a: pop) {
        // Reset the agentMap and fill it
        resetAgentMap();
        for(Agent b: pop) {
          agentMap[PApplet.parseInt(b.location.y)][PApplet.parseInt(b.location.x)] = 1;
        }
        a.exchangeWords();
        a.gateTeleport();
        a.move();
        a.display();
      }
    }
  }

  /** Show the IDs
    * <p>
    * This helper method prints the IDs of all the agents on the map.
    */
  private void showID() {
    for(int i = 0; i < count; i++) {
      Agent a = pop.get(i);
      print(a.id, "\n");
    }
  }
}
/**
  * @author Aaron
  */

class Vocabulary {
  public int wordCount;
  public ArrayList<Word> vocabulary = new ArrayList<Word>();

  // Classification of the vocabulary (Lingua or island)
  public char vocCls;

  /** Vocabulary class constructor
    * <p>
    * The vocabulary class includes the methods to create a vocabulary
    * with a given length and with the appropriate distributions.
    * <p>
    * @param classification The classification of the vocabulary
    *                       "i" : island vocabulary
    *                       "l" : lingua franca vocabulary
    */
  public Vocabulary(char classification) {
    this.wordCount = 10;
    this.vocCls = classification;
    createVocabulary();
  }

  /** Helper method to calculate poisson distributed random numbers
    * <p>
    * Calculates a poisson distributed random number with lambda = 4.8.
    * This method is used to calculate the word length.
    * The algorithm was adapted from Donald E. Knuth's work.
    * <p>
    * @return A poisson distributed random number with a lambda of 4.8
    */
  private int poissonDist() {
    double lambda = 4.8f;
    double p = 1.0f;
    double l = Math.exp(-lambda);
    int k = 0;

    do {
      k += 1;
      double random = random(1);
      p *= random;
    } while(p > l);

    return k-1;
  }

  /** Calculate a Zipf distributed probability
    * <p>
    * @param rank Rank on a scale of word usage
    * @return The probability that this word occurs
    */
  private float zipfDist(int rank) {
    float sum = 0;
    for(int i = 0; i < wordCount; i++) {
      sum += 1/PApplet.parseFloat(i);
    }
    return (1/PApplet.parseFloat(rank)) / sum;
  }

  /** Returns a new syllable with either 2 or 3 letters
    * <p>
    * The syllables are randomly generate with different letter orders.
    * The letter orders are permutations of vocal and consonant combinations.
    * <p>
    * @param sylLength Length of the syllable
    * @return A syllable of the specified length
    */
  public String createSyllable(int sylLength) {
    String syll = new String();

    // If the length is 2 the syllable contains just a consonant and a vowel (C V)
    if(sylLength == 2) {
      int consonant = PApplet.parseInt(random(consonants.size()));
      int vowel = PApplet.parseInt(random(vowels.size()));

      syll += consonants.get(consonant);
      syll += vowels.get(vowel);
    }

    // If the length is 3 create a random order of vowels and consonants
    else if(sylLength == 3) {
      int ran = PApplet.parseInt(random(6));
      int vowel1 = PApplet.parseInt(random(vowels.size()));
      int vowel2 = PApplet.parseInt(random(vowels.size()));
      int consonant1 = PApplet.parseInt(random(consonants.size()));
      int consonant2 = PApplet.parseInt(random(consonants.size()));

      // V C V
      if(ran == 0) {
        syll += vowels.get(vowel1);
        syll += consonants.get(consonant1);
        syll += vowels.get(vowel2);
      }

      // C V C
      else if(ran == 1) {
        syll += consonants.get(consonant1);
        syll += vowels.get(vowel1);
        syll += consonants.get(consonant2);
      }

      // C C V
      else if(ran == 2) {
        syll += consonants.get(consonant1);
        syll += consonants.get(consonant2);
        syll += vowels.get(vowel1);
      }

      // V C C
      else if(ran == 3) {
        syll += vowels.get(vowel1);
        syll += consonants.get(consonant1);
        syll += consonants.get(consonant2);
      }

      // V V C
      else if(ran == 4) {
        syll += vowels.get(vowel1);
        syll += vowels.get(vowel2);
        syll += consonants.get(consonant1);
      }

      //C V V
      else if(ran == 5) {
        syll += consonants.get(consonant1);
        syll += vowels.get(vowel1);
        syll += vowels.get(vowel2);
      }
    }
    return syll;
  }

  /** Returns a new word
    * <p>
    * The word is created by adding randomly generated syllables
    * <p>
    * @param wordLength Length of the word
    * @return A word generated with random syllables
    */
  public String createWord(int wordLength) {
    int sylNumber = wordLength / 2;
    String newSyllable = new String();
    String newWord = new String();
    // If the word length is even then only create syllables of length 2
    if(wordLength % 2 == 0) {
      for(int i = 0; i < sylNumber; i++) {
        newSyllable = createSyllable(2);
        newWord += newSyllable;
      }
    }
    // If the word length is odd then create syllables of length 2 and one with length 3
    else {
      newSyllable += createSyllable(3);
      newWord += newSyllable;
      for(int j = 0; j < (sylNumber-1); j++) {
        newSyllable = createSyllable(2);
        newWord += newSyllable;
      }
    }
    return newWord;
  }

  /** Create a new vocabulary
    * <p>
    * Initializes the Vocabulary class with words.
    */
  public void createVocabulary() {
    for (int i = 0; i < wordCount; i++) {
      Word newWord = new Word(createWord(poissonDist()), vocCls);
      vocabulary.add(newWord);
    }
  }

}
/**
  * @author Aaron
  */

class Word {
  public int length;     // Length of the word
  // int island;     // Island to whose vocabulary the word belongs
  // int parent;     // ID of the agent that created the word USEFUL?
  public String letters; // Letters which the word contains
  public String[] etym;  // Origin of the word; from which word it was derived
  public char vocClass;  // Classification of the word in the different vocabularies
                         // i = island; l = lingua

  /** Word class constructor
    * <p>
    * The word class includes all methods to modify words. It also
    * contains the tools to retrace the origin of words.
    * <p>
    * @param word String that represents the word
    * @param classification The classification of the vocabulary
    *                       "i" : island vocabulary
    *                       "l" : lingua franca vocabulary
    */
  public Word(String word, char classification) {
    this.letters = word;
    this.length = word.length();
    this.vocClass = classification;
  }

  /** Mutate the vowels in the word.
    * <p>
    * Mutates every vowel in a word with the chance of 0.2% (2/3 from the dir variable
    * times 3/100 of the if statement)
    */
  public void mutateVowel() {
    int max_random = 100;
    for(int i = 0; i < vowels.size(); i++) {
      int newIndex;
      String vowel = vowels.get(i);
      if(letters.contains(vowel)) {
        float prob = random(max_random);
        if(prob < 3) {
          // The vowels can shift either forwards or backwards in the array
          int dir = PApplet.parseInt(random(3)) - 1;
          if(i == vowels.size() - 1 && dir > 0) {
            newIndex = 0;
          }
          else if(i == 0 && dir < 0) {
            newIndex = vowels.size() - 1;
          }
          else{
            newIndex = i + dir;
          }
          String newVowel = vowels.get(newIndex);
          letters = letters.replaceFirst(vowel, newVowel);
          break;
        }
      }
    }
  }

  /** Mutate the consonants of the word.
    * <p>
    * Mutates consonants or consonant groups according to the shiftX-lists in
    * the this class with a probability of 0.03%
    */
  public void mutateConsonant() {
    int max_random = 100;
    float prob = random(max_random);
    for(int j = 0; j < 4; j++) {
      if(prob < 3) {
        int newIndex = (j+1) % 4;
        int switchVar = PApplet.parseInt(random(6));
        // Replace a random consonant in the string
        switch (switchVar) {
          case 1: if(letters.contains(shift1.get(j))) {
                    letters = letters.replace(shift1.get(j), shift1.get(newIndex));
                  }
                  break;
          case 2: if(letters.contains(shift2.get(j))) {
                    letters = letters.replace(shift2.get(j), shift2.get(newIndex));
                  }
                  break;
          case 3: if(letters.contains(shift3.get(j))) {
                    letters = letters.replace(shift3.get(j), shift3.get(newIndex));
                  }
                  break;
          case 4: if(letters.contains(shift4.get(j))) {
                    letters = letters.replace(shift4.get(j), shift4.get(newIndex));
                  }
                  break;
          case 5: if(letters.contains(shift5.get(j))) {
                    letters = letters.replace(shift5.get(j), shift5.get(newIndex));
                  }
                  break;
        }
      }
    }
  }

  /** Double the consonant in a word.
    * <p>
    * Give certain consonants the possibility to mutate to
    * a double consonant
    */
  public void doubleConsonant() {
    char[] letterArray = letters.toCharArray();
    char[] newCharArr = new char[length+1];
    char doubleLetter = ' ';
    int max_random = 1000000;
    int index = 0;
    float prob = max_random;
    for(int i = 1; i < length; i++) {
      prob = random(max_random);
      // D's can only be doubled if the preceding letter is
      // an 'e' or an '\u00e4'
      if(letterArray[i] == 'd' && (letterArray[i-1] == 'e' || letterArray[i-1] == '\u00e4')) {
        doubleLetter = 'd';
        index = i;
        if(prob < 0.5f * max_random) {
          break;
        }
      }
      // T's can only be doubled if the preceding letter is
      // an 'i' or a 'j'
      if(letterArray[i] == 't' && (letterArray[i-1] == 'i' || letterArray[i-1] == 'j')) {
        doubleLetter = 't';
        index = i;
        if(prob < 0.5f * max_random) {
          break;
        }
      }
      // L's can only be doubled if the preceding letter is
      // an 'o' or a 'u'
      if(letterArray[i] == 'l' && (letterArray[i-1] == 'o' || letterArray[i-1] == 'u')) {
        doubleLetter = 'l';
        index = i;
        if(prob < 0.5f * max_random) {
          break;
        }
      }
    }
    // Chance to double: 3 ppm
    if (prob < 3) {
      for(int j = 0; j < index; j++) {
        newCharArr[j] = letterArray[j];
      }
      newCharArr[index+1] = doubleLetter;
      for(int k = length+1; k > index+1; k--) {
        newCharArr[k] = letterArray[k];
      }
      letters = new String(newCharArr);
    }
  }

}
  public void settings() {  size(1000, 1000); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
