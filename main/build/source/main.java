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
Vocabulary voc;
Word trying;

public void setup() {
  
  langMap = new Map();
  population = new Population();
  voc = new Vocabulary('l');
  trying = new Word("aeiou", 'l');
  for(int i = 0; i < voc.count; i++) {
    Word w = voc.vocabulary.get(i);
    print(w.letters,"\n");
  }
  randomSeed(1);
}

public void draw() {
  frameRate(2.5f);
  background(250);
  langMap.display();
  population.update();
  print(trying.letters, "\n");
  trying.mutateVowel();
}

/* TODO 1) FIX EXCHANGEWORDS METHOD AND ALL THE MUTATION METHODS

        2) solve all the word mutations with StringLists instead of
           character arrays

        3) doubleConsonant is very complex. Maybe think of an easier way
           to implement it

        4) you cannot jump from j to o -> from a to \u00e4 or to o

        5) change the vowels of diphtongs

        6) method which checks if the word has changed so it can be
           inserted in the etym array (hard) -> maybe create class-wide
            variables with random words so they can be checked inside the
             class

        7) create a method to mutate the begin or the end of a word if
           it is often used with another word with a vowel on the beginning
            or the end
             -> maybe use another class to create sentences

        8) maybe give certain letters a higher probability to be picked
           in the different languages

        9) maybe include mountain regions which have a slower walking rate?
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
/* The agent class includes the methods to move, teleport
   and exchange vocabulary with other agents */

class Agent {
  PVector location;       // Current position of the agent
  int island;             // Current island
  int spawnIsland;        // Island on which the agent spawned
  int id;                 // ID of the agent
  int currentX;           // Current Square in x-direction
  int currentY;           // Current Square in y-direction
  int spawnX;             // Spawning Square in x-direction
  int spawnY;             // Spawning Square in y-direction
  Vocabulary vocab;       // Vocabulary

  // TODO create a vocabulary and insert it into every agent

  Agent(int xcoord, int ycoord, Vocabulary lingua) {

    vocab = lingua;

    location = new PVector(xcoord, ycoord);

    // Get the coordinates of the spawn
    spawnX = xcoord;
    spawnY = ycoord;
    // Decide on which island the agent currently is
    if(langMap.map[PApplet.parseInt(location.y)][PApplet.parseInt(location.x)] == 1) {
      island = 1;
    }
    else if(langMap.map[PApplet.parseInt(location.y)][PApplet.parseInt(location.x)] == 2) {
      island = 2;
    }
    else if(langMap.map[PApplet.parseInt(location.y)][PApplet.parseInt(location.x)] == 3) {
      island = 3;
    }

    // Decide on which island the agent spawned
    if(langMap.map[spawnY][spawnX] == 1) {
      spawnIsland = 1;
    }
    else if(langMap.map[spawnY][spawnX] == 2) {
      spawnIsland = 2;
    }
    else if(langMap.map[spawnY][spawnX] == 3) {
      spawnIsland = 3;
    }

    id = PApplet.parseInt(random(PApplet.parseFloat(MAX_INT)));
  }

  // Displays the agent
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

  // returns the ID of the nearest Agent
  // -> maybe needed for the exchange of words later
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

  // Moves the agent in a random direction
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
      movableCells.add(upArr); //<>// //<>//
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

    // Choose a random direction
    int direction = PApplet.parseInt(random(movableCells.size()));
    if(movableCells.size() > 0) {
      location.x = movableCells.get(direction)[0];
      location.y = movableCells.get(direction)[1];
    }

  }

  // Teleports the agent if he walks on a gate
  // gets the coordinates from the gatesXY methods (class Map)
  public void gateTeleport() {
    // Teleport from A to B or from B to A
    if(location.x == langMap.gates("AB")[0][0] && location.y == langMap.gates("AB")[0][1]) {
      location.x = langMap.gates("AB")[1][0];
      location.y = langMap.gates("AB")[1][1];
    }
    else if(location.x == langMap.gates("AB")[1][0] && location.y == langMap.gates("AB")[1][1]) {
      location.x = langMap.gates("AB")[0][0];
      location.y = langMap.gates("AB")[0][1];
    }

    // Teleport from A to C or from C to A
    else if(location.x == langMap.gates("AC")[0][0] && location.y == langMap.gates("AC")[0][1]) {
      location.x = langMap.gates("AC")[1][0];
      location.y = langMap.gates("AC")[1][1];
    }
    else if(location.x == langMap.gates("AC")[1][0] && location.y == langMap.gates("AC")[1][1]) {
      location.x = langMap.gates("AC")[0][0];
      location.y = langMap.gates("AC")[0][1];
    }

    // Teleport from B to C or from C to B
    else if(location.x == langMap.gates("BC")[0][0] && location.y == langMap.gates("BC")[0][1]) {
      location.x = langMap.gates("BC")[1][0];
      location.y = langMap.gates("BC")[1][1];
    }
    else if(location.x == langMap.gates("BC")[1][0] && location.y == langMap.gates("BC")[1][1]) {
      location.x = langMap.gates("BC")[0][0];
      location.y = langMap.gates("BC")[0][1];
    }
  }

  // Exchange words with the nearest Agent
  public void exchangeWords() {
    Word exchangeWord1 = new Word("", voc.vocCls);
    Word exchangeWord2 = new Word("", voc.vocCls);
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
            int changeIndex = PApplet.parseInt(random(vocab.count-1));
            // Take a word from the nearest agent
            exchangeWord1 = comm.vocab.vocabulary.get(changeIndex);
            comm.vocab.vocabulary.remove(changeIndex);

            // Take a word from the agent
            exchangeWord2 = vocab.vocabulary.get(changeIndex);
            vocab.vocabulary.remove(changeIndex);

            // Mutate the word
            exchangeWord1.mutateVowel();
            exchangeWord2.mutateVowel();

            exchangeWord1.mutateConsonant();
            exchangeWord2.mutateConsonant();

            exchangeWord1.doubleConsonant();
            exchangeWord2.doubleConsonant();

            // Exchange the words
            comm.vocab.vocabulary.add(exchangeWord1);
            vocab.vocabulary.add(exchangeWord2);
          }
        }
      }
    }
    //print("\"" + exchangeWord1.letters + "\" was exchanged for \"" + exchangeWord2.letters + "\"\n");
  }
}
/* The map class defines the Map of the simulation and includes the methods to
get coordinates of certain points (gates, islands). It also includes the whole
coloring and modeling of the world. */

class Map {
  int[][] map = { {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
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
  int mapWidth;                                                                                          // Size of the blueprint
  int gridX, gridY;                                                                                      // Gridsize

  Map () {
    mapWidth = 40;
    gridX = width/mapWidth;
    gridY = height/mapWidth;
  }

  // Returns the area of the island
  public int area(int island) {
    int area = 0;
    for(int i = 0; i < mapWidth; i++) {
      for(int j = 0; j < mapWidth; j++) {
        if(map[i][j] == island) {
          area += 1;
        }
      }
    }
    return area;
  }

  // Returns a two-dimensional Array with the coordinates of the gate AB
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

  // Method to display the map as a two dimensional array
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
/* The population class includes methods to control the behaviour of all agents
currently on the map */

class Population {
  int count;                                                        // Number of agents in the population
  ArrayList<Agent> pop;                                             // ArrayList with all the agents in it
  int[][] agentMap = new int[langMap.mapWidth][langMap.mapWidth];   // Map with all agents

  //Create a grid with all the positions of agents so it can be considered in the move-method
  Population() {
    count = 20;
    pop = new ArrayList<Agent>();
  }

  // Reset the agentMap to contain only 0s
  public void resetAgentMap() {
    for(int i = 0; i < langMap.mapWidth; i++) {
      for(int j = 0; j < langMap.mapWidth; j++) {
        agentMap[i][j] = 0;
      }
    }
  }

  // Initialize the agents randomly on the islands and update their movement
  public void update() {
    // If there is no population yet, create one
    if(pop.size() == 0) {
      int i = 0;
      while(i < count) {
        // Only create an agent when the spawning location is on an island (or gate)
        // Might be efficient, as this algorithm does not scan the whole map
        Agent agent = new Agent(PApplet.parseInt(random(langMap.mapWidth)),PApplet.parseInt(random(langMap.mapWidth)), voc);
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

  // Show the ID of the living agents
  public void showID() {
    for(int i = 0; i < count; i++) {
      Agent a = pop.get(i);
      print(a.id, "\n");
    }
  }
}
/* The vocabulary class includes the methods to create a vocabulary
   with a given length and with the appropriate distributions */

class Vocabulary {
  int count;
  ArrayList<Word> vocabulary = new ArrayList<Word>();

  // All vowels that can be used for words in mutation order
  StringList vowels = new StringList(
    "a", "\u00e4", "e", "i", "j", "o", "\u00f6", "u", "\u00fc", "y"
  );

  // All consonants that can be used for words in mutation order
  StringList consonants = new StringList(
    "b", "p", "d", "t", "g", "h", "k", "c", "q",
    "m", "n", "l", "r", "s", "z", "f", "v", "w", "x"
  );

    // Classification of the vocabulary (Lingua or island)
  char vocCls;

  Vocabulary(char classification) {
    count = 2;
    vocCls = classification;
    createVocabulary();
  }

  // Method to calculate poisson distributed
  // numbers with lambda = 4.8
  public int poissonDist() {
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

  // Method to calculate the Zipf distributed probability
  public float zipfDist(int rank) {
    float sum = 0;
    for(int i = 0; i < count; i++) {
      sum += 1/PApplet.parseFloat(i);
    }
    return (1/PApplet.parseFloat(rank)) / sum;
  }

  // Creates a new syllable with either 2 or 3 letters
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

  // Create a new word with random syllables
  public String createWord(int wordLength) {
    int sylNumber = wordLength / 2;
    String newSyllable = new String();
    String newWord = new String();
    if(wordLength % 2 == 0) {
      for(int i = 0; i < sylNumber; i++) {
        newSyllable = createSyllable(2);
        newWord += newSyllable;
      }
    }
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

  // Create a new vocabulary
  public void createVocabulary() {
    for (int i = 0; i < count; i++) {
      Word newWord = new Word(createWord(poissonDist()), vocCls);
      vocabulary.add(newWord);
    }
  }

}
/* The word class includes all methods to modify words. It also
contains the tools to retrace the origin of words */

class Word {
  int length;     // Length of the word
  // int island;     // Island to whose vocabulary the word belongs
  // int parent;     // ID of the agent that created the word USEFUL?
  String letters; // Letters which the word contains
  String[] etym;  // Origin of the word; from which word it was derived
  char vocClass;  // Classification of the word in the different vocabularies
                  // i = island; l = lingua

  // All diphtongs that can be used for mutation
  StringList diphs = new StringList(
    "aa", "ai", "aj", "ao", "au", "ay", "ea", "ee", "ei", "ej", "eo", "ey",
    "ie", "ii", "ij", "iy", "ja", "j\u00e4", "je", "ji", "jo", "ju", "jy", "oa",
    "oi", "oj", "oo", "ou", "oy", "ua", "ue", "ui", "uj", "uo", "uu", "uy",
    "\u00e4i", "\u00e4j", "\u00e4y", "\u00f6i", "\u00f6j", "\u00f6y", "\u00fci", "\u00fcj","\u00fcy"
  );

  // Consonant shifts loosely based on Grimm's law
  StringList shift1 = new StringList(
    "bh", "b", "p", "pf"
  );

  StringList shift2 = new StringList(
    "dh", "d", "t", "th"
  );

  StringList shift3 = new StringList(
    "gh", "g", "k", "x"
  );

  StringList shift4 = new StringList(
    "gwh", "gw", "kw", "xw"
  );

  StringList shift5 = new StringList(
    "th", "pf", "kw", "gh"
  );

  Word(String word, char classification) {
    letters = word;
    length = word.length();
    vocClass = classification;
  }


/*  void mutateVowel() {
    int count = 0;
    char[] letterArray = letters.toCharArray();
    for(int i = 0; i < length && count < 1; i++) {
      for(int j = 0; j < voc.vowels.length; j++) {
        float prob = random(1000);
        // add 1,0, or -1
        int dir = int(random(3))-1;
        if(prob < 3) {
          if(letterArray[i] == voc.vowels[j] && j != 0 && j != voc.vowels.length-1) {
            letterArray[i] = voc.vowels[j+dir];
          }
          // At the first element of the vowels array the index has to be
          // adapted
          else if(letterArray[i] == voc.vowels[j] && j == 0) {
            if(dir >= 0) {
              letterArray[i] = voc.vowels[j+1];
            }
            else {
              letterArray[i]  = voc.vowels[voc.vowels.length-1];
            }
          }
          // Start from the first element of the vowels array
          // if it is at the end
          else if(letterArray[i] == voc.vowels[j] && j == voc.vowels.length-1) {
            letterArray[i] = voc.vowels[0];
          }
        }
      }
    }
    letters = new String(letterArray);
  }*/

  // Mutates every vowel in a word with the chance of 0.2% (2/30 from the dir variable
  // times 3/100 of the if statement)
  public void mutateVowel() {
    int max_random = 10;
    for(int i = 0; i < voc.vowels.size(); i++) {
      float prob = random(max_random);
      if(letters.contains(voc.vowels.get(i)) && prob < 3) {
        int dir = PApplet.parseInt(random(3))-1;
        int newIndex;
        if(dir < 0 && i == 0) {
          newIndex = voc.vowels.size()-1;
        } else {
          newIndex = (i+dir) % voc.vowels.size();
        }
        letters.replace(voc.vowels.get(i), voc.vowels.get(newIndex));
      }
    }
  }

  // Mutates consonants or consonant groups according to the shiftX-lists
  // in the vocabulary class with a probability of 0.03%
  public void mutateConsonant() {
    String newLetters = new String(letters);
    int max_random = 3;
    float prob = random(max_random);
    for(int j = 0; j < 4; j++) {
      if(prob < 3) {
        // replacements for shift1
        if(newLetters.contains(shift1.get(j))) {
          newLetters.replace(shift1.get(j), shift1.get((j+1) % 4));
        }
        // replacements for shift2
        else if(newLetters.contains(shift2.get(j))) {
          newLetters.replace(shift2.get(j), shift2.get((j+1) % 4));
        }
        // replacements for shift3
        else if(newLetters.contains(shift3.get(j))) {
          newLetters.replace(shift3.get(j), shift3.get((j+1) % 4));
        }
        // replacements for shift4
        else if(newLetters.contains(shift4.get(j))) {
          newLetters.replace(shift4.get(j), shift4.get((j+1) % 4));
        }
        // replacements for shift5
        else if(newLetters.contains(shift1.get(j))) {
          newLetters.replace(shift5.get(j), shift5.get((j+1) % 4));
        }
      }
    }
    letters = newLetters;
  }

  // Give certain consonants the possibility to mutate to
  // a double consonant
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
