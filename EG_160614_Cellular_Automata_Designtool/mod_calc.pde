// §§§§§§§§§§§§§§§§§ MODULES LEVEL CALCULATIONS §§§§§§§§§§§§§§§§§ //<>//

int brightnessThreshold = 128;

// ------------------------------------------------------------------------------ run calculations for current generation (in draw)
void calcModules() {
  for (int i = 5; i < cols+5; i++) {
    for (int j = 5; j < rows+5; j++) {
      for (int k = 5; k < lvls+5; k++) {
        newNolli.loadPixels();
        if (useNolli) {
          if (brightness (newNolli.pixels[ (i-5) + (j-5) * cols ]) < brightnessThreshold) {
            //if (brightness (newNolli.pixels[ (i-5) + (j-5) * cols ]) < brightnessThreshold || k != 5) {
            //if (i != 20 && i != 21 && j != 20 && j != 21 || k != 5) {
            grid[i][j][k].setVar();            // set and reset variables every loop
            grid[i][j][k].evalNeighbourhood(); // evaluate/calculate Neighbourhood score for all cells
            grid[i][j][k].evalNeighbours();    // evaluate/calculate num of Neighbours score for all cells
            grid[i][j][k].evalStability();     // evaluate/calculate stability score for all cells
            grid[i][j][k].evalGreen();         // evaluate/calculate green score for all cells
            grid[i][j][k].evalLight();         // evaluate/calculate light score for all cells
            grid[i][j][k].evalView();          // evaluate/calculate view score for all cells
            grid[i][j][k].evalYYY();           // evaluate/calculate YYY score for all cells
            grid[i][j][k].evalMaster();        // evaluate/calculate master score for all cells
          } else {
            grid[i][j][k].scoreMaster = 0;
          }
        } else {
          grid[i][j][k].setVar();            // set and reset variables every loop
          grid[i][j][k].evalNeighbourhood(); // evaluate/calculate Neighbourhood score for all cells
          grid[i][j][k].evalNeighbours();    // evaluate/calculate num of Neighbours score for all cells
          grid[i][j][k].evalStability();     // evaluate/calculate stability score for all cells
          grid[i][j][k].evalGreen();         // evaluate/calculate green score for all cells
          grid[i][j][k].evalLight();         // evaluate/calculate light score for all cells
          grid[i][j][k].evalView();          // evaluate/calculate view score for all cells
          grid[i][j][k].evalYYY();           // evaluate/calculate YYY score for all cells
          grid[i][j][k].evalMaster();        // evaluate/calculate master score for all cells
        }
      }
    }
  }
}

// ------------------------------------------------------------------------------ display all modules and the boundary (in draw)
void dispModules() {
  for (int i = 0; i < cols+9; i++) {
    for (int j = 0; j < rows+9; j++) {
      for (int k = 0; k < lvls+9; k++) {
        if (visualize) grid[i][j][k].displayColor(); // calculate color for all cells
        if (visualize && box) grid[i][j][k].displayBox(); // dispay all elements that are alive as a box
        if (visualize && !box) grid[i][j][k].displayPoint(); // dispay all elements that are alive as a point
        if (visualize && constr && interf) grid[i][j][k].displayConstraints(); // color boundary cells red
      }
    }
  }
}

// ------------------------------------------------------------------------------ run calculations for next generation + data from current gen (in draw)
void calcFutModules() {
  for (int i = 5; i < cols+5; i++) {
    for (int j = 5; j < rows+5; j++) {
      for (int k = 5; k < lvls+5; k++) {
        grid[i][j][k].calcData();          // calculate data of current population for HUD text
        grid[i][j][k].calcFutType();       // calculate future-type/status (dead-alive)
        grid[i][j][k].updateType();        // update all elements with their new dead/alive status
      }
    }
  }
}