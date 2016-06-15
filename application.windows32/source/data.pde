// §§§§§§§§§§§§§§§§§ POPULATION LEVEL CALCULATIONS & DATA VISUALIZATION §§§§§§§§§§§§§§§§§

// ------------------------------------------------------------------------------ data variables
static int interfaceWidth = 260;                                               // data/graph HUD width
static int textBlockHeight = 515;                                              // data HUD height
static int yGraphScale = 200;                                                  // graph HUD height

int generation = 0;                                                            // current population number
float buildable = 0;
float percBuildable = 1;                                                       // % of cells buildable due to Nolli map
float numCell = cols * rows * lvls * percBuildable;                            // total amount of cells
int numCellAlive = 0;                                                          // total amount of cells alive
float life = startLife;                                                        // percentage of cells alive
float lifeAbs = startLife * numCell;                                           // absolute number of unites desired

float satisfaction = startSatisfaction;                                        // level that determines if cell stays alive

float DA = 1.00;                                                               // dead/alive multiplier (makes it harder for new cells to come alive
float Dtreshold = 0.50;                                                        // effort for new cells to die
float Atreshold = 1.00;                                                        // effort for new cells to get born

float scoreMasterTotal = 0.00;                                                 // calculate the total master        score for all cells alive
float scoreNeighbourhoodTotal = 0.00;                                          // calculate the total neighbourhood score for all cells alive
float scoreNeighboursTotal = 0.00;                                             // calculate the total neighbours    score for all cells alive
float scoreStabilityTotal = 0.00;                                              // calculate the total stability     score for all cells alive
float scoreGreenTotal = 0.00;                                                  // calculate the total green         score for all cells alive
float scoreLightTotal = 0.00;                                                  // calculate the total light           score for all cells alive
float scoreViewTotal = 0.00;                                                   // calculate the total view           score for all cells alive
float scoreYYYTotal = 0.00;                                                    // calculate the total YYY           score for all cells alive

float weightSum = weightNeighbourhood + weightNeighbours + weightStability + weightGreen + weightLight + weightView + weightYYY;

// ------------------------------------------------------------------------------ declare
PFont helLight;                                                                // regular font
PFont helBold;                                                                 // title font

// ------------------------------------------------------------------------------ load fonts (in setup)
void dataFonts() {
  helLight = loadFont("HelveticaNeue-Light-12.vlw");                           // regular font
  helBold = loadFont("HelveticaNeue-Bold-12.vlw");                             // title font
}

// ------------------------------------------------------------------------------ reset data/text/color values (in draw)
void resetData() {
  buildable = 0;
  newNolli.loadPixels();
  if (useNolli) {
    for (int i = 5; i < cols+5; i++) {
      for (int j = 5; j < rows+5; j++) {
        if (brightness (newNolli.pixels[ (i-5) + (j-5) * cols ]) < brightnessThreshold) {
          buildable ++;
        }
      }
    }
    percBuildable = buildable / (cols * rows);
  } else {
    percBuildable = 1.00;
  }
  numCell = cols * rows * lvls * percBuildable;
  numCellAlive = 0;                                                            // reset number of cells alive (for every frame)
  scoreMasterTotal = 0;                                                        // reset the total master        score
  scoreNeighbourhoodTotal = 0;                                                 // reset the total neighbourhood score
  scoreNeighboursTotal = 0;                                                    // reset the total neighbours    score
  scoreStabilityTotal = 0;                                                     // reset the total stability     score
  scoreGreenTotal = 0;                                                         // reset the total green         score
  scoreLightTotal = 0;                                                         // reset the total light         score
  scoreViewTotal = 0;                                                          // reset the total view          score
  scoreYYYTotal = 0;                                                           // reset the total YYY           score

  if (scoreColor == 0) { // RGB set 0%, RGB set 50%, RGB set 100%, COLOR OFF (white)
    colors[0] = 255;
    colors[1] = 255;
    colors[2] = 255;
    colors[3] = 255;
    colors[4] = 255;
    colors[5] = 255;
    colors[6] = 255;
    colors[7] = 255;
    colors[8] = 255;
  }
  if (scoreColor == 1) { // RGB set 0%, RGB set 50%, RGB set 100%, COLOR MASTER (red-yellow-green)
    colors[0] = 255;
    colors[1] =   0;
    colors[2] =   0;
    colors[3] = 255;
    colors[4] = 255;
    colors[5] =   0;
    colors[6] =   0;
    colors[7] = 255;
    colors[8] =   0;
  }
  if (scoreColor == 2) { // RGB set 0%, RGB set 50%, RGB set 100%, COLOR MASTER (red-orange-green)
    colors[0] = 255;
    colors[1] =   0;
    colors[2] =   0;
    colors[3] = 195;
    colors[4] = 130;
    colors[5] =   0;
    colors[6] =   0;
    colors[7] = 255;
    colors[8] =   0;
  }
  if (scoreColor == 3) { // RGB set 0%, RGB set 50%, RGB set 100%, COLOR MASTER (grey-white-red)
    colors[0] = 128;
    colors[1] = 128;
    colors[2] = 128;
    colors[3] = 255;
    colors[4] = 255;
    colors[5] = 255;
    colors[6] = 255;
    colors[7] =   0;
    colors[8] =   0;
  }
  if (scoreColor == 4) { // RGB set 0%, RGB set 50%, RGB set 100%, COLOR MOORE (grey-white-redorange)
    colors[0] = 128;
    colors[1] = 128;
    colors[2] = 128;
    colors[3] = 255;
    colors[4] = 255;
    colors[5] = 255;
    colors[6] = 234;
    colors[7] =  74;
    colors[8] =  18;
  }
  if (scoreColor == 5) { // RGB set 0%, RGB set 50%, RGB set 100%, COLOR VON NEUMANN (grey-white-orange)
    colors[0] = 128;
    colors[1] = 128;
    colors[2] = 128;
    colors[3] = 255;
    colors[4] = 255;
    colors[5] = 255;
    colors[6] = 234;
    colors[7] = 180;
    colors[8] =  18;
  }
  if (scoreColor == 6) { // RGB set 0%, RGB set 50%, RGB set 100%, COLOR STABILIY (grey-white-blue)
    colors[0] = 128;
    colors[1] = 128;
    colors[2] = 128;
    colors[3] = 255;
    colors[4] = 255;
    colors[5] = 255;
    colors[6] =  18;
    colors[7] = 180;
    colors[8] = 234;
  }
  if (scoreColor == 7) { // RGB set 0%, RGB set 50%, RGB set 100%, COLOR GREEN (grey-white-dark_green)
    colors[0] = 128;
    colors[1] = 128;
    colors[2] = 128;
    colors[3] = 255;
    colors[4] = 255;
    colors[5] = 255;
    colors[6] =  18;
    colors[7] = 234;
    colors[8] =  74;
  }
  if (scoreColor == 8) { // RGB set 0%, RGB set 50%, RGB set 100% COLOR LIGHT (grey-white-yellow)
    colors[0] = 128;
    colors[1] = 128;
    colors[2] = 128;
    colors[3] = 255;
    colors[4] = 255;
    colors[5] = 255;
    colors[6] = 234;
    colors[7] = 234;
    colors[8] =  18;
  }
  if (scoreColor == 9) { // RGB set 0%, RGB set 50%, RGB set 100% COLOR VIEW (grey-white-yellow)
    colors[0] = 128;
    colors[1] = 128;
    colors[2] = 128;
    colors[3] = 255;
    colors[4] = 255;
    colors[5] = 255;
    colors[6] =  74;
    colors[7] = 234;
    colors[8] = 234;
  }
}

// ------------------------------------------------------------------------------ calculate new data values (in draw)
void calcData() {
  generation++;                                                                // count generation
  life = float(numCellAlive) / numCell;                                 // count number of cells alive
  if (satisfaction > 0.10) {                                                   // min satisfaction percentage (values too extreme, will lead to massive extintion)
    if (life <= 0.95 * startLife) satisfaction = satisfaction - 0.0005;        // if our desired % alive is to low, lower the satisfaction threshold
    if (life > 0.95 * startLife && life <= 0.98 * startLife) satisfaction = satisfaction - 0.0002; // if our desired % alive is a little bit to low, lower the satisfaction threshold a little bit
  } 
  if (satisfaction < 0.90) {                                                   // max satisfaction percentage (values too extreme, will lead to massive extintion)
    if (life >= 1.05 * startLife) satisfaction = satisfaction + 0.0005;        // if our desired % alive is to high, increase the satisfaction threshold
    if (life < 1.05 * startLife && life >= 1.02 * startLife) satisfaction = satisfaction + 0.0002; // if our desired % alive is a little bit to high, increase the satisfaction threshold a little bit
  } 
  calcGraph();                                                                 // add the new population data to the graph and remove the oldest data
  weightSum = weightNeighbourhood + weightNeighbours + weightStability + weightGreen + weightLight + weightView + weightYYY;
  startLife = lifeAbs / numCell;
}

// ------------------------------------------------------------------------------ display data values (in draw)
void dispData() {
  hint(DISABLE_DEPTH_TEST);                                                    // turn off depth test
  cam.beginHUD();                                                              // start heads-up-display system - text fixed to camera
  // ............................................................................ UI backgrounds
  strokeWeight(0.5);                                                           // rectangle outline weight
  stroke(255, 200);                                                            // rectangle outline color
  fill(0, 200);                                                                // rectangle fill color (transparent black)
  rect(width - interfaceWidth - 10, 30, interfaceWidth, textBlockHeight);      // right top
  fill(0, 255);                                                                // rectangle fill color (transparent white)
  if (scoreColor > 0 && scoreColor < 4) rect(width - interfaceWidth - 10, 197, interfaceWidth, 20); // scoreColor overlay
  if (scoreColor > 3) rect(width - interfaceWidth - 10, 57 + 40 * scoreColor, interfaceWidth, 2*20); // scoreColor overlay
  // ............................................................................ data right-up on the screen
  fill(255, 255, 255);                                                         // white text color
  textFont(helBold, 12);                                                       // title font
  textAlign(LEFT);                                                             // align text left
  text("Population data:", width-interfaceWidth, 30+1*20);                     // info text population data
  textFont(helLight, 12);                                                      // data font
  textAlign(LEFT);                                                             // align text left
  text("Generation:", width-interfaceWidth, 30+2*20);                          // info text generation
  textAlign(RIGHT);                                                            // align text right
  text(nfc(generation, 0), width-20, 30+2*20);                                 // current generation number
  textAlign(LEFT);                                                             // align text left
  text("# of calcualtions per second:", width-interfaceWidth, 30+3*20);        // info text population data
  textAlign(RIGHT);                                                            // align text right
  text(nfc(frameRate, 1), width-20, 30+3*20);                                  // number of calculations every second
  textAlign(LEFT);                                                             // align text left
  text("Rows x columns x levels:", width-interfaceWidth, 30+4*20);             // info grid
  textAlign(RIGHT);                                                            // align text right
  text(nfc(rows, 0) + " x " + nfc(cols, 0) + " x " + nfc(lvls, 0), width-20, 30+4*20); // number of cells in the grid
  textAlign(LEFT);                                                             // align text left
  text("Nolli buildable %:", width-interfaceWidth, 30+5*20);                   // % buildable due to Nolli map
  textAlign(RIGHT);                                                            // align text right
  text(nfc((percBuildable*100), 2) + " %", width-20, 30+5*20);                 // % buildable
  textAlign(LEFT);                                                             // align text left
  text("Total # of cells alive:", width-interfaceWidth, 30+6*20);              // info cells alive in absolute numbers
  textAlign(RIGHT);                                                            // align text right
  text(nfc(numCellAlive, 0) + " / " + nfc(numCell, 0), width-20, 30+6*20);     // number of cells alive in absolute numbers
  textAlign(LEFT);                                                             // align text left
  text("Cells alive:", width-interfaceWidth, 30+7*20);                         // info cells alive in percentage
  textAlign(RIGHT);                                                            // align text right
  text(nfc((life*100), 2) + " %", width-20, 30+7*20);                          // number of cells alive in percentage
  textAlign(LEFT);                                                             // align text left
  text("Desired:", width-interfaceWidth, 30+8*20);                             // info cells alive in percentage
  textAlign(RIGHT);                                                            // align text right
  text(nfc((startLife*100), 2) + " %", width-20, 30+8*20);                     // number of cells alive in percentage
  fill(234, 18, 74);                                                           // red text color
  textAlign(LEFT);                                                             // align text left
  text("Master score:", width-interfaceWidth, 30+9*20);                        // info mean score master
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreMasterTotal/(0.01*numCellAlive)), 2) + " %", width-20, 30+9*20); // mean master score
  fill(234, 74, 18);                                                           // orange text color
  textAlign(LEFT);                                                             // align text left
  text("Group score:", width-interfaceWidth, 30+10*20);                         // info mean score neighbourhood  
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreNeighbourhoodTotal/(0.01*numCellAlive)), 2) + " %", width-20, 30+10*20); // mean neighbourhood score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+11*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightNeighbourhood*100)/weightSum), 2) + " %", width-20, 30+11*20); // normalised weight
  fill(234, 180, 18);                                                          // yellow-orange text color
  textAlign(LEFT);                                                             // align text left
  text("Connection score:", width-interfaceWidth, 30+12*20);                   // info mean score neighbours
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreNeighboursTotal/(0.01*numCellAlive)), 2) + " %", width-20, 30+12*20); // mean neighbours score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+13*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightNeighbours*100)/weightSum), 2) + " %", width-20, 30+13*20); // normalised weight
  fill(18, 180, 234);                                                          // blue text color
  textAlign(LEFT);                                                             // align text left
  text("Stability score: ", width-interfaceWidth, 30+14*20);                   // info mean score stability
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreStabilityTotal/(0.01*numCellAlive)), 2) + " %", width-20, 30+14*20); // mean stability score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+15*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightStability*100)/weightSum), 2) + " %", width-20, 30+15*20);  // normalised weight
  fill(18, 234, 74);                                                           // green text color
  textAlign(LEFT);                                                             // align text left
  text("Green score: ", width-interfaceWidth, 30+16*20);                       // info mean score green
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreGreenTotal/(0.01*numCellAlive)), 2) + " %", width-20, 30+16*20); // mean green score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+17*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightGreen*100)/weightSum), 2) + " %", width-20, 30+17*20);      // normalised weight
  fill(234, 234, 18);                                                          // yellow text color
  textAlign(LEFT);                                                             // align text left
  text("Light score: ", width-interfaceWidth, 30+18*20);                       // info mean score light
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreLightTotal/(0.01*numCellAlive)), 2) + " %", width-20, 30+18*20); // mean light score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+19*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightLight*100)/weightSum), 2) + " %", width-20, 30+19*20);      // normalised weight
  fill(74, 234, 234);                                                          // cyan text color
  textAlign(LEFT);                                                             // align text left
  text("View score: ", width-interfaceWidth, 30+20*20);                        // info mean score view
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreViewTotal/(0.01*numCellAlive)), 2) + " %", width-20, 30+20*20); // mean view score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+21*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightView*100)/weightSum), 2) + " %", width-20, 30+21*20);       // normalised weight
  fill(180, 180, 234);                                                         // purple text color
  textAlign(LEFT);                                                             // align text left
  text("Random score: ", width-interfaceWidth, 30+22*20);                      // info mean score YYY
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreYYYTotal/(0.01*numCellAlive)), 2) + " %", width-20, 30+22*20); // mean YYY score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+23*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightYYY*100)/weightSum), 2) + " %", width-20, 30+23*20);        // normalised weight
  fill(180, 180, 180);                                                         // grey text color
  textAlign(LEFT);                                                             // align text left
  text("Satisfaction threshold: ", width-interfaceWidth, 30+24*20);            // info satisfaction threshold
  textAlign(RIGHT);                                                            // align text right
  text(nfc((satisfaction*100), 2) + " %", width-20, 30+24*20);                 // current satisfaction threshold
  fill(255);                                                                   // continue with white text
  textAlign(LEFT);                                                             // align text left
  text("Dead threshold: ", width-interfaceWidth, 30+25*20);                    // info satisfaction threshold
  textAlign(LEFT);                                                             // align text left
  //  text("Alive threshold: ", width-interfaceWidth, 30+25*20);               // info satisfaction threshold
  cp5.draw();                                                                  // draw sliders
  // ............................................................................ data right-down on the screen
  dispGraph();                                                                 // draw graphs
  // ............................................................................ text centered-top on the screen
  strokeWeight(1.5);                                                           // stroke weight
  stroke(255, 0, 0);                                                           // red stroke color
  line(0, 20, width, 20);                                                      // underline the topbar with a line
  stroke(0, 255, 0);                                                           // green stroke color
  int numOfKeys = 19;                                                          // number of key-functions (divider for underlining line segment)
  if (keyPressed && key == 'x')                             line(0  * width/numOfKeys, 20, 1  * width/numOfKeys, 20);
  if (reset || keyPressed && key == 'r')                    line(1  * width/numOfKeys, 20, 2  * width/numOfKeys, 20);
  if (pause)                                                line(2  * width/numOfKeys, 20, 3  * width/numOfKeys, 20);
  if (visualize)                                            line(3  * width/numOfKeys, 20, 4  * width/numOfKeys, 20);
  if (constr)                                               line(4  * width/numOfKeys, 20, 5  * width/numOfKeys, 20);
  if (box)                                                  line(5  * width/numOfKeys, 20, 6  * width/numOfKeys, 20);
  if (scoreColor != 0)                                      line(6  * width/numOfKeys, 20, 7  * width/numOfKeys, 20);
  if (keyPressed && key == 'l')                             line(7  * width/numOfKeys, 20, 8  * width/numOfKeys, 20);
  if (useNolli)                                             line(8  * width/numOfKeys, 20, 9  * width/numOfKeys, 20);
  if (showNolli != 0)                                       line(9  * width/numOfKeys, 20, 10 * width/numOfKeys, 20);
  if (interf)                                               line(10 * width/numOfKeys, 20, 11 * width/numOfKeys, 20);
  if (exportscreen || keyPressed && key == 'e')             line(11 * width/numOfKeys, 20, 12 * width/numOfKeys, 20); 
  if (exporttxt    || keyPressed && key == 't')             line(12 * width/numOfKeys, 20, 13 * width/numOfKeys, 20);
  if (exportdwg    || keyPressed && key == 'd')             line(13 * width/numOfKeys, 20, 14 * width/numOfKeys, 20);
  if (exportobj    || keyPressed && key == 'o')             line(14 * width/numOfKeys, 20, 15 * width/numOfKeys, 20);
  if (keyPressed && key == '&' || keyPressed && key == '1') line(15 * width/numOfKeys, 20, 16 * width/numOfKeys, 20);
  if (keyPressed && key == 'é' || keyPressed && key == '2') line(16 * width/numOfKeys, 20, 17 * width/numOfKeys, 20);
  if (keyPressed && key == '"' || keyPressed && key == '3') line(17 * width/numOfKeys, 20, 18 * width/numOfKeys, 20);
  if (keyPressed && key == 39  || keyPressed && key == '4') line(18 * width/numOfKeys, 20, 19 * width/numOfKeys, 20);
  strokeWeight(0.5);                                                           // stroke weight
  stroke(0, 0, 0);                                                             // black stroke color
  for (int i=width/numOfKeys; i<=width-width/numOfKeys; i+=width/numOfKeys) line(i, 0, i, 21); // topbar ticks
  cam.endHUD();                                                                // stop heads-up-display system - text fixed to camera
  hint(ENABLE_DEPTH_TEST);                                                     // turn depth test back on
}