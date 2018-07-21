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
    if(langMap.map[int(location.y)][int(location.x)] == 1) {
      island = 1;
    }
    else if(langMap.map[int(location.y)][int(location.x)] == 2) {
      island = 2;
    }
    else if(langMap.map[int(location.y)][int(location.x)] == 3) {
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

    // Give the agent a random ID
    id = int(random(float(MAX_INT)));
  }

  // Displays the agent as a circle with a darker shade of the island colour
  void display() {
    if(spawnIsland == 1) {
      fill(#7f3939);
    }
    else if(spawnIsland == 2){
      fill(#7f6000);
    }
    else if(spawnIsland == 3) {
      fill(#195953);
    }
    noStroke();
    ellipse((location.x+0.5)*langMap.gridX, (location.y+0.5)*langMap.gridY, langMap.gridX-7, langMap.gridY-7);
  }

  // returns the ID of the nearest Agent
  int nearestAgent() {
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
  // returns the ID of the nearest Agent
  int neighborAgent() {
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

  // Moves the agent in a random direction
  void move() {

    // Get the coordinates of the neighbour cells
    int[] upArr = {int(location.x), int(location.y - 1)};
    int[] downArr = {int(location.x), int(location.y + 1)};
    int[] rightArr = {int(location.x + 1), int(location.y)};
    int[] leftArr = {int(location.x - 1), int(location.y)};

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
    int direction = int(random(movableCells.size()));
    if(movableCells.size() > 0) {
      location.x = movableCells.get(direction)[0];
      location.y = movableCells.get(direction)[1];
    }

  }

  // Teleports the agent if he walks on a gate
  // gets the coordinates from the gatesXY methods (class Map)
  void gateTeleport() {
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
/*
  // Exchange words with the nearest Agent
  void exchangeWords() {
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
          if(dist <= 1.5) {
            // Random index
            int changeIndex = int(random(vocab.count-1));
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
  }*/
}