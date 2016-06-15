// §§§§§§§§§§§§§§§§§ MODULES CLASS (inc. ALGORITMES) §§§§§§§§§§§§§§§§§

float countNeighbourhood = 0;                                    // count  neighbourhood  (for each cell)
float countNeighbours = 0;                                       // count  neighbours     (for each cell)
float countStability = 0;                                        // count  stability      (for each cell)
float countGreen = 0;                                            // count  green          (for each cell)
float countLight1 = 0;                                           // count  light          (for each cell)
float countLight2 = 0;                                           // count  light          (for each cell)
float countLight3 = 0;                                           // count  light          (for each cell)
float countLight4 = 0;                                           // count  light          (for each cell)
float countLight = 0;                                            // count  light          (for each cell)
float countView1 = 0;                                            // count  view           (for each cell)
float countView2 = 0;                                            // count  view           (for each cell)
float countView3 = 0;                                            // count  view           (for each cell)
float countView4 = 0;                                            // count  view           (for each cell)
float countView = 0;                                             // count  view           (for each cell)
float countYYY = 0;                                              // count  YYY            (for each cell)

// ------------------------------------------------------------------------------ declare
modules grid [][][] = new modules[cols+9][rows+9][lvls+9];       // class module makes a grid with the 3 start parameters 

// ------------------------------------------------------------------------------ class modules
class modules {
  // ------------------------------------------------------------------------------ build-up modules
  int type = 0;                                                  // type/state of the element: 0 = dead en 1 = alive
  int futType = 0;                                               // future type/state makes sure all elements are calculated at the same time: 0 = dead en 1 = alive

  float scoreNeighbourhood = 0.00;                               // score  neighbourhood  (for each cell)
  float scoreNeighbours = 0.00;                                  // score  neighbours     (for each cell)
  float scoreStability = 0.00;                                   // score  stability      (for each cell)
  float scoreGreen = 0.00;                                       // score  green          (for each cell)
  float scoreLight = 0.00;                                       // score  light          (for each cell)
  float scoreView = 0.00;                                        // score  view           (for each cell)
  float scoreYYY = 0.00;                                         // score  YYY            (for each cell)

  float scoreMaster = 0.00;                                      // calculate masterscore (for each cell)

  float scoreValue = 0.00;                                       // calculation value for the color

  // ------------------------------------------------------------------------------ build-up the location vector
  Vec3D loc;                                                     // point named loc (location)

  // ------------------------------------------------------------------------------ build-up modules
  int x;                                                         // place in the grid (column) (1st, 2nd, ...)
  int y;                                                         // place in the grid (row)    (1st, 2nd, ...)
  int z;                                                         // place in the grid (floor)  (1st, 2nd, ...)

  modules(Vec3D _loc, int _x, int _y, int _z) {                  // initiate all cells in the grid this 4 variables (location vector and a place in the grid)
    // link the modules properties with a global value
    loc = _loc;
    x = _x;
    y = _y;
    z = _z;

    randomSet();                                                 // randomly give all cells a dead/alive status with a % alive
  }

  // ------------------------------------------------------------------------------ set and reset variables every loop
  void setVar() {
    countNeighbourhood = 0;
    countNeighbours = 0;
    countStability = 0;
    countGreen = 0;
    countLight1 = 0;
    countLight2 = 0;
    countLight3 = 0;
    countLight4 = 0;
    countLight = 0;
    countView1 = 0;
    countView2 = 0;
    countView3 = 0;
    countView4 = 0;
    countView = 0;
    countYYY = 0;

    scoreNeighbourhood = 0;
    scoreNeighbours = 0;
    scoreStability = 0;
    scoreGreen = 0;
    scoreLight = 0;
    scoreView = 0;
    scoreYYY = 0;

    scoreMaster = 0;

    scoreValue = 0;

    if (type == 0) {
      DA = Dtreshold;
    } else {
      DA = Atreshold;
    }
  }

  // ------------------------------------------------------------------------------ evaluate/calculate Neighbourhood score for all elements
  void evalNeighbourhood() {
    // --- COUNT ---
    // if our ... neighbour is alive, add 1 to our countNeighbourhood, do this for all 26 Neighbourhood
    // point itself has coordinates grid[x][y][z]
    // same level
    if (grid[x-1][y-1][z]  .type == 1) countNeighbourhood ++;    // cell left    up      same level
    if (grid[x]  [y-1][z]  .type == 1) countNeighbourhood ++;    // cell central up      same level
    if (grid[x+1][y-1][z]  .type == 1) countNeighbourhood ++;    // cell right   up      same level
    if (grid[x-1][y]  [z]  .type == 1) countNeighbourhood ++;    // cell left    central same level
    if (grid[x+1][y]  [z]  .type == 1) countNeighbourhood ++;    // cell right   central same level
    if (grid[x-1][y+1][z]  .type == 1) countNeighbourhood ++;    // cell left    down    same level
    if (grid[x]  [y+1][z]  .type == 1) countNeighbourhood ++;    // cell central down    same level
    if (grid[x+1][y+1][z]  .type == 1) countNeighbourhood ++;    // cell right   down    same level
    // level higher
    if (grid[x-1][y-1][z+1].type == 1) countNeighbourhood ++;    // cell left    up      level higher
    if (grid[x]  [y-1][z+1].type == 1) countNeighbourhood ++;    // cell central up      level higher
    if (grid[x+1][y-1][z+1].type == 1) countNeighbourhood ++;    // cell right   up      level higher
    if (grid[x-1][y]  [z+1].type == 1) countNeighbourhood ++;    // cell left    central level higher
    if (grid[x]  [y]  [z+1].type == 1) countNeighbourhood ++;    // cell central central level higher
    if (grid[x+1][y+1][z+1].type == 1) countNeighbourhood ++;    // cell right   central level higher
    if (grid[x-1][y+1][z+1].type == 1) countNeighbourhood ++;    // cell left    down    level higher
    if (grid[x]  [y+1][z+1].type == 1) countNeighbourhood ++;    // cell central down    level higher
    if (grid[x+1][y+1][z+1].type == 1) countNeighbourhood ++;    // cell right   down    level higher
    // level lower
    if (grid[x-1][y-1][z-1].type == 1) countNeighbourhood ++;    // cell left    up      level lower
    if (grid[x]  [y-1][z-1].type == 1) countNeighbourhood ++;    // cell central up      level lower
    if (grid[x+1][y-1][z-1].type == 1) countNeighbourhood ++;    // cell right   up      level lower
    if (grid[x-1][y]  [z-1].type == 1) countNeighbourhood ++;    // cell left    central level lower
    if (grid[x]  [y]  [z-1].type == 1) countNeighbourhood ++;    // cell central central level lower
    if (grid[x+1][y]  [z-1].type == 1) countNeighbourhood ++;    // cell right   central level lower
    if (grid[x-1][y+1][z-1].type == 1) countNeighbourhood ++;    // cell left    down    level lower
    if (grid[x]  [y+1][z-1].type == 1) countNeighbourhood ++;    // cell central down    level lower
    if (grid[x+1][y+1][z-1].type == 1) countNeighbourhood ++;    // cell right   down    level lower
    // --- RULES ---
    // rules give a score to the number of neighbours
    if (countNeighbourhood < 3)  scoreNeighbourhood = 0.00;      // loneliness (die)
    if (countNeighbourhood == 3) scoreNeighbourhood = 0.33 * DA; // loneliness (die)
    if (countNeighbourhood == 4) scoreNeighbourhood = 0.67 * DA; // ideal state (stay alive)
    if (countNeighbourhood == 5) scoreNeighbourhood = 1.00 * DA; // ideal state (stay alive)
    if (countNeighbourhood == 6) scoreNeighbourhood = 1.00;      // ideal state (stay alive)
    if (countNeighbourhood == 7) scoreNeighbourhood = 0.67 * DA; // ideal state (stay alive)
    if (countNeighbourhood == 8) scoreNeighbourhood = 0.33 * DA; // overpopulation (die)
    if (countNeighbourhood > 8)  scoreNeighbourhood = 0.00;      // overpopulation (die)
  }

  // ------------------------------------------------------------------------------ evaluate/calculate the number of Neighbours score for all elements
  void evalNeighbours() {
    // --- COUNT ---
    if (grid[x-1][y]  [z]  .type == 1) countNeighbours ++;       // cell left    same level
    if (grid[x+1][y]  [z]  .type == 1) countNeighbours ++;       // cell right   same level
    if (grid[x]  [y-1][z]  .type == 1) countNeighbours ++;       // cell up      same level
    if (grid[x]  [y+1][z]  .type == 1) countNeighbours ++;       // cell down    same level
    if (grid[x]  [y]  [z+1].type == 1) countNeighbours ++;       // cell central level higher
    if (grid[x]  [y]  [z-1].type == 1) countNeighbours ++;       // cell central level lower
    // --- RULES ---
    if (countNeighbours == 0) scoreNeighbours = 0.00;            // loneliness (die)
    if (countNeighbours == 1) scoreNeighbours = 0.50 * DA;       // ideal state (stay alive)
    if (countNeighbours == 2) scoreNeighbours = 0.80 * DA;       // ideal state (stay alive)
    if (countNeighbours == 3) scoreNeighbours = 1.00;            // ideal state (stay alive)
    if (countNeighbours == 4) scoreNeighbours = 0.40 * DA;       // overpopulation (die)
    if (countNeighbours == 5) scoreNeighbours = 0.10 * DA;       // overpopulation (die)
    if (countNeighbours == 6) scoreNeighbours = 0.00;            // overpopulation (die)
  }

  // ------------------------------------------------------------------------------ evaluate/calculate stability score for all element
  void evalStability() {
    // --- COUNT ---
    if (grid[x]  [y-1][z-1].type == 1 && grid[x]  [y-1][z]  .type == 1) countStability ++;   // cell central up      level lower+same
    if (grid[x]  [y+1][z-1].type == 1 && grid[x]  [y+1][z]  .type == 1) countStability ++;   // cell central down    level lower+same
    if (grid[x-1][y]  [z-1].type == 1 && grid[x-1][y]  [z]  .type == 1) countStability ++;   // cell left    central level lower+same
    if (grid[x+1][y]  [z-1].type == 1 && grid[x+1][y]  [z]  .type == 1) countStability ++;   // cell right   central level lower+same
    // --- RULES ---
    if (z == 5) {                                               // rules for ground level
      scoreStability = 1.00 * DA;                               // is stable because it is placed directly on the ground
    } else {                                                    // rules for all other levels 
      if (grid[x][y][z-1].type == 1) {                          // if cell central level lower is alive it has direct support
        scoreStability = grid[x][y][z-1].scoreStability * DA;   // is stable because it right above an other cell, get the score of that cell
      } else {                                                  // if the cell has no direct support underneath it
        if (countStability == 0) scoreStability = 0.00;         // can carry no loads sideways
        if (countStability == 1) scoreStability = 0.40 * DA;    // can carry small loads sideways
        if (countStability == 2) scoreStability = 0.60 * DA;    // can carry some loads sideways
        if (countStability == 3) scoreStability = 0.70 * DA;    // can carry loads sideways
        if (countStability == 4) scoreStability = 0.80 * DA;    // can carry big loads sideways
      }
    }
  }

  // ------------------------------------------------------------------------------ evaluate/calculate green score for all element
  void evalGreen() {
    // --- COUNT ---
    if (z == 5) { // count for ground level
      if (                                 grid[x]  [y-1][z]  .type == 0) countGreen ++;   // cell central up      same level
      if (                                 grid[x]  [y+1][z]  .type == 0) countGreen ++;   // cell central down    same level
      if (                                 grid[x-1][y]  [z]  .type == 0) countGreen ++;   // cell left    central same level
      if (                                 grid[x+1][y]  [z]  .type == 0) countGreen ++;   // cell right   central same level
      if (                                 grid[x]  [y]  [z+1].type == 0) countGreen ++;   // cell central central level higher
    } else { // count for all other levels
      if (grid[x]  [y-1][z-1].type == 1 && grid[x]  [y-1][z]  .type == 0) countGreen ++;   // cell central up      level lower+same
      if (grid[x]  [y+1][z-1].type == 1 && grid[x]  [y+1][z]  .type == 0) countGreen ++;   // cell central down    level lower+same
      if (grid[x-1][y]  [z-1].type == 1 && grid[x-1][y]  [z]  .type == 0) countGreen ++;   // cell left    central level lower+same
      if (grid[x+1][y]  [z-1].type == 1 && grid[x+1][y]  [z]  .type == 0) countGreen ++;   // cell right   central level lower+same
      if (                                 grid[x]  [y]  [z+1].type == 0) countGreen ++;   // cell central central level higher
    }
    // --- RULES ---
    if (countGreen == 0) scoreGreen = 0.00;                   // no acces to green
    if (countGreen == 1) scoreGreen = 0.50 * DA;              // little acces to green
    if (countGreen == 2) scoreGreen = 0.70 * DA;              // normal acces to green
    if (countGreen == 3) scoreGreen = 0.90 * DA;              // lots of acces to green
    if (countGreen == 4) scoreGreen = 1.00 * DA;              // lots of acces to green
    if (countGreen == 5) scoreGreen = 1.00;                   // maximum acces to green
  }

  // ------------------------------------------------------------------------------ evaluate/calculate light score for all element
  void evalLight() {
    // --- COUNT ---
    if (grid[x-1][y]  [z]  .type == 1 || grid[x-1][y]  [z+1].type == 1) { 
      countLight1 = 0;        // if the space is O cells
    } else {
      if (grid[x-2][y]  [z+1].type == 1 || grid[x-2][y]  [z+2].type == 1) {
        countLight1 = 1;      // if the space is 1 cell
      } else {
        if (grid[x-3][y]  [z+2].type == 1 || grid[x-3][y]  [z+3].type == 1) {
          countLight1 = 2;    // if the space is 2 cells
        } else {
          if (grid[x-4][y]  [z+3].type == 1 || grid[x-4][y]  [z+4].type == 1) {
            countLight1 = 3;  // if the space is 3 cells
          } else {
            countLight1 = 4;  // if the space is 4 cells or more
          }
        }
      }
    }
    if (grid[x]  [y-1][z]  .type == 1 || grid[x]  [y-1][z+1].type == 1) { 
      countLight2 = 0;        // if the space is O cells
    } else {
      if (grid[x]  [y-2][z+1].type == 1 || grid[x]  [y-2][z+2].type == 1) {
        countLight2 = 1;      // if the space is 1 cell
      } else {
        if (grid[x]  [y-3][z+2].type == 1 || grid[x]  [y-3][z+3].type == 1) {
          countLight2 = 2;    // if the space is 2 cells
        } else {
          if (grid[x]  [y-4][z+3].type == 1 || grid[x]  [y-4][z+4].type == 1) {
            countLight2 = 3;  // if the space is 3 cells
          } else {
            countLight2 = 4;  // if the space is 4 cells or more
          }
        }
      }
    }
    if (grid[x+1][y]  [z]  .type == 1 || grid[x+1][y]  [z+1].type == 1) { 
      countLight3 = 0;        // if the space is O cells
    } else {
      if (grid[x+2][y]  [z+1].type == 1 || grid[x+2][y]  [z+2].type == 1) {
        countLight3 = 1;      // if the space is 1 cell
      } else {
        if (grid[x+3][y]  [z+2].type == 1 || grid[x+3][y]  [z+3].type == 1) {
          countLight3 = 2;    // if the space is 2 cells
        } else {
          if (grid[x+4][y]  [z+3].type == 1 || grid[x+4][y]  [z+4].type == 1) {
            countLight3 = 3;  // if the space is 3 cells
          } else {
            countLight3 = 4;  // if the space is 4 cells or more
          }
        }
      }
    }
    if (grid[x]  [y+1][z]  .type == 1 || grid[x]  [y+1][z+1].type == 1) { 
      countLight4 = 0;        // if the space is O cells
    } else {
      if (grid[x]  [y+2][z+1].type == 1 || grid[x]  [y+2][z+2].type == 1) {
        countLight4 = 1;      // if the space is 1 cell
      } else {
        if (grid[x]  [y+3][z+2].type == 1 || grid[x]  [y+3][z+3].type == 1) {
          countLight4 = 2;    // if the space is 2 cells
        } else {
          if (grid[x]  [y+4][z+3].type == 1 || grid[x]  [y+4][z+4].type == 1) {
            countLight4 = 3;  // if the space is 3 cells
          } else {
            countLight4 = 4;  // if the space is 4 cells or more
          }
        }
      }
    }
    countLight = countLight1 + countLight2 + countLight3 + countLight4;
    // --- RULES ---
    if (countLight <= 3)  scoreLight = 0.00;      // no acces to light
    if (countLight == 4)  scoreLight = 0.30 * DA; // minimum acces to light
    if (countLight == 5)  scoreLight = 0.40 * DA; // minimum acces to light
    if (countLight == 6)  scoreLight = 0.50 * DA; // little acces to light
    if (countLight == 7)  scoreLight = 0.65 * DA; // little acces to light
    if (countLight == 8)  scoreLight = 0.80 * DA; // normal acces to light
    if (countLight == 9)  scoreLight = 0.90 * DA; // normal acces to light
    if (countLight >= 10) scoreLight = 1.00;      // maximum acces to light
  }

  // ------------------------------------------------------------------------------ evaluate/calculate view score for all element
  void evalView() {
    // --- COUNT ---
    if (grid[x-1][y][z].type == 1) { 
      countView1 = 0;        // if the space is O cells
    } else {
      if (grid[x-2][y][z].type == 1) {
        countView1 = 1;      // if the space is 1 cell
      } else {
        if (grid[x-3][y][z].type == 1) {
          countView1 = 2;    // if the space is 2 cells
        } else {
          if (grid[x-4][y][z].type == 1) {
            countView1 = 3;  // if the space is 3 cells
          } else {
            countView1 = 4;  // if the space is 4 cells or more
          }
        }
      }
    }
    if (grid[x][y-1][z].type == 1) { 
      countView2 = 0;        // if the space is O cells
    } else {
      if (grid[x][y-2][z].type == 1) {
        countView2 = 1;      // if the space is 1 cell
      } else {
        if (grid[x][y-3][z].type == 1) {
          countView2 = 2;    // if the space is 2 cells
        } else {
          if (grid[x][y-4][z].type == 1) {
            countView2 = 3;  // if the space is 3 cells
          } else {
            countView2 = 4;  // if the space is 4 cells or more
          }
        }
      }
    }
    if (grid[x+1][y][z].type == 1) { 
      countView3 = 0;        // if the space is O cells
    } else {
      if (grid[x+2][y][z].type == 1) {
        countView3 = 1;      // if the space is 1 cell
      } else {
        if (grid[x+3][y][z].type == 1) {
          countView3 = 2;    // if the space is 2 cells
        } else {
          if (grid[x+4][y][z].type == 1) {
            countView3 = 3;  // if the space is 3 cells
          } else {
            countView3 = 4;  // if the space is 4 cells or more
          }
        }
      }
    }
    if (grid[x][y+1][z].type == 1) { 
      countView4 = 0;        // if the space is O cells
    } else {
      if (grid[x][y+2][z].type == 1) {
        countView4 = 1;      // if the space is 1 cell
      } else {
        if (grid[x][y+3][z].type == 1) {
          countView4 = 2;    // if the space is 2 cells
        } else {
          if (grid[x][y+4][z].type == 1) {
            countView4 = 3;  // if the space is 3 cells
          } else {
            countView4 = 4;  // if the space is 4 cells or more
          }
        }
      }
    }
    countView = countView1 + countView2 + countView3 + countView4;
    // --- RULES ---
    if (countView <= 3)  scoreView = 0.00;      // no      acces to a view
    if (countView == 4)  scoreView = 0.30 * DA; // minimum acces to a view
    if (countView == 5)  scoreView = 0.40 * DA; // minimum acces to a view
    if (countView == 6)  scoreView = 0.50 * DA; // little  acces to a view
    if (countView == 7)  scoreView = 0.65 * DA; // little  acces to a view
    if (countView == 8)  scoreView = 0.80 * DA; // normal  acces to a view
    if (countView == 9)  scoreView = 0.90 * DA; // normal  acces to a view
    if (countView >= 10) scoreView = 1.00;      // maximum acces to a view
  }

  // ------------------------------------------------------------------------------ evaluate/calculate YYY score for all element
  void evalYYY() {
    scoreYYY = random(0.00, 1.00);
  }

  // ------------------------------------------------------------------------------ evaluate/calculate master score
  void evalMaster() {
    scoreMaster = (scoreNeighbourhood * weightNeighbourhood + scoreNeighbours * weightNeighbours + scoreStability * weightStability + scoreGreen * weightGreen + scoreLight * weightLight + scoreView * weightView + scoreYYY * weightYYY)/(weightSum); // calculate a main score for every cell
  }

  // ------------------------------------------------------------------------------ display all elements that are alive as a box
  void displayColor() {
    if (scoreColor == 0) scoreValue = 1.00;
    if (scoreColor == 1) scoreValue = scoreMaster;
    if (scoreColor == 2) scoreValue = scoreMaster;
    if (scoreColor == 3) scoreValue = scoreMaster;
    if (scoreColor == 4) scoreValue = scoreNeighbourhood;
    if (scoreColor == 5) scoreValue = scoreNeighbours;
    if (scoreColor == 6) scoreValue = scoreStability;
    if (scoreColor == 7) scoreValue = scoreGreen;
    if (scoreColor == 8) scoreValue = scoreLight;
    if (scoreColor == 9) scoreValue = scoreView;
  }

  // ------------------------------------------------------------------------------ display all elements that are alive as a box
  void displayBox() {
    if (type == 1) {                    // if the point is alive: draw a box or a point
      stroke(0);                        // black outline
      strokeWeight(1);                  // make outline thin
      if (scoreValue <  0.50) fill(colors[0] * (1 - 2 * scoreValue) + colors[3] * (     2 * scoreValue), colors[1] * (1 - 2 * scoreValue) + colors[4] * (     2 * scoreValue), colors[2] * (1 - 2 * scoreValue) + colors[5] * (     2 * scoreValue)); // color from score
      if (scoreValue >= 0.50) fill(colors[3] * (2 - 2 * scoreValue) + colors[6] * (-1 + 2 * scoreValue), colors[4] * (2 - 2 * scoreValue) + colors[7] * (-1 + 2 * scoreValue), colors[5] * (2 - 2 * scoreValue) + colors[8] * (-1 + 2 * scoreValue)); // color from score
      pushMatrix();
      translate(loc.x, loc.y, loc.z);
      box(sc);                          // draw a box
      popMatrix();
    }
  }

  // ------------------------------------------------------------------------------ display all elements that are alive as a point
  void displayPoint() {
    if (type == 1) {                    // if the point is alive: draw a box or a point
      if (scoreValue <  0.50) stroke(colors[0] * (1 - 2 * scoreValue) + colors[3] * (     2 * scoreValue), colors[1] * (1 - 2 * scoreValue) + colors[4] * (     2 * scoreValue), colors[2] * (1 - 2 * scoreValue) + colors[5] * (     2 * scoreValue)); // color from score
      if (scoreValue >= 0.50) stroke(colors[3] * (2 - 2 * scoreValue) + colors[6] * (-1 + 2 * scoreValue), colors[4] * (2 - 2 * scoreValue) + colors[7] * (-1 + 2 * scoreValue), colors[5] * (2 - 2 * scoreValue) + colors[8] * (-1 + 2 * scoreValue)); // color from score
      strokeWeight(2.5);                // make point bigger
      point(loc.x, loc.y, loc.z);       // draw a point
    }
  }

  // ------------------------------------------------------------------------------ display constraints
  void displayConstraints() {
    if (x <= 4 || y <= 4 || z <= 4 || x >= cols+5 || y >= rows+5 || z >= lvls+5) {
      Vec3D boundary = grid[x][y][z].loc;
      stroke(#003CBE);                  // blue
      strokeWeight(2);
      point(boundary.x, boundary.y, boundary.z);
    }
  }

  // ------------------------------------------------------------------------------ calculate data of current population for HUD text
  void calcData() {
    if (type == 1) {
      numCellAlive ++;
      scoreNeighbourhoodTotal = scoreNeighbourhoodTotal + grid[x][y][z].scoreNeighbourhood;
      scoreNeighboursTotal    = scoreNeighboursTotal    + grid[x][y][z].scoreNeighbours;
      scoreStabilityTotal     = scoreStabilityTotal     + grid[x][y][z].scoreStability;
      scoreGreenTotal         = scoreGreenTotal         + grid[x][y][z].scoreGreen;
      scoreLightTotal         = scoreLightTotal         + grid[x][y][z].scoreLight;
      scoreViewTotal          = scoreViewTotal          + grid[x][y][z].scoreView;
      scoreYYYTotal           = scoreYYYTotal           + grid[x][y][z].scoreYYY;
      scoreMasterTotal        = scoreMasterTotal        + grid[x][y][z].scoreMaster;
    }
  }

  // ------------------------------------------------------------------------------ calculate future-type/status (dead-alive)
  void calcFutType() {
    if (scoreMaster >= satisfaction) {
      futType = 1;                      // if the main score is equal or bigger than the threshold: cells is/becomes alive
    } else {
      futType = 0;                      // if the main score is smaller than the threshold: cells is/becomes dead
    }
  }

  // ------------------------------------------------------------------------------ update all elements with their new dead/alive status
  void updateType() {
    type = futType;                     // update all elements with their new dead/alive status
  }

  // ------------------------------------------------------------------------------ (re)set function (activated when R is pressed and at the beginning of the script)
  void randomSet() {                    // randomly give all cells a dead/alive status with life% alive
    if (x <= 4 || y <= 4 || z <= 4 || x >= cols+5 || y >= rows+5 || z >= lvls+5) {
      type = 0;
    } else {
      float rndRest = random(100);      // all points get a random value between 0 and 100
      if (rndRest < (startLife*100)) {  // if random value is bigger than 50
        type = 1;                       // point is alive
      } else {
        type = 0;                       // point is dead
      }
    }
  }
}