/* The population class includes methods to control the behaviour of all agents
currently on the map */

class Population {
  int count;                                                        // Number of agents in the population
  ArrayList<Agent> pop;                                             // ArrayList with all the agents in it
  int[][] agentMap = new int[langMap.mapWidth][langMap.mapWidth];   // Map with all agents

  //Create a grid with all the positions of agents so it can be considered in the move-method
  Population() {
    count = 15;
    pop = new ArrayList<Agent>();
  }

  // Reset the agentMap to contain only 0s
  void resetAgentMap() {
    for(int i = 0; i < langMap.mapWidth; i++) {
      for(int j = 0; j < langMap.mapWidth; j++) {
        agentMap[i][j] = 0;
      }
    }
  }

  // Initialize the agents randomly on the islands and update their movement
  void update() {
    // If there is no population yet, create one
    if(pop.size() == 0) {
      int i = 0;
      while(i < count) {
        // Only create an agent when the spawning location is on an island (or gate)
        // Might be efficient, as this algorithm does not scan the whole map
        Agent agent = new Agent(int(random(langMap.mapWidth)),int(random(langMap.mapWidth)), voc);
        if(langMap.map[int(agent.location.y)][int(agent.location.x)] != 0 && agent.location.x > 0 && agent.location.y > 0) {
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
          agentMap[int(b.location.y)][int(b.location.x)] = 1;
        }
        //a.exchangeWords();
        a.gateTeleport();
        a.move();
        a.display();
      }
    }
  }

  // Show the ID of the living agents
  void showID() {
    for(int i = 0; i < count; i++) {
      Agent a = pop.get(i);
      print(a.id, "\n");
    }
  }
}