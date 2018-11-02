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
    this.gridX = height/mapWidth;
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
    color gateColor = color(200);

    color islandOne = color(#ff7373);
    color watermarkOne = color(#cc5c5c);

    color islandTwo = color(#ffc100);
    color watermarkTwo = color(#e5ad00);

    color islandThree = color(#40e0d0);
    color watermarkThree = color(#33b3a6);

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
            rect((j+0.5)*gridX, (k+0.5)*gridY, 0.2*gridX, gridY);
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
            rect((l+0.5)*gridX, (i+0.5)*gridY, gridX, 0.2*gridY);
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
            rect((j+0.5)*gridX, (m+0.5)*gridY, 0.2*gridX, gridY);
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
