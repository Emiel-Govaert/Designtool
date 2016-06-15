import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import peasy.*; 
import toxi.geom.*; 
import controlP5.*; 
import processing.dxf.*; 
import nervoussystem.obj.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class EG_160614_Cellular_Automata_Designtool extends PApplet {



// Design tool for computer generated volumetric studies using rule-sets based on the principles of a cellular automaton. 
// Thesis code is written for a master thesis about the Applicability of Cellular Automata in the Architectural Design Process at Ghent University.
// Submitted in fulfilment of the requirements for the degree of "Master of Science in Engineering: Architecture"
// Promoted by prof. Ruben Verstraeten and Francis wyffels. Mentored by Sebastiaan Leenknegt and Tiemen Strobbe.
// Written by Emiel Govaert, published on 2016/06/01. Creative Commons license, some rights reserved.

// Terms of use: distributed under a CreativeCommons-Attribution-NonCommercial-ShareAlike 4.0 International licence (CC BY-NC-SA 4.0).
// This means you are free to share, adapt or build on this material, but these restrictions apply:
// Attribution \u2014 You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
// NonCommercial \u2014 You may not use the material for commercial purposes.
// ShareAlike \u2014 If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.
// No additional restrictions \u2014 You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.

// Special thanks to processing foundation, Ben Fry, Casey Reas and Daniel Shiffman and all open-source external libraries (PeasyCam, ToxiClibs, ControlP5, NervousSystem).
// Coded in Processing v3.1.1, using the PeasyCam v201, ToxiClibs v0020, ControlP5 v2.2.5 and NervousSystem v0.2.5 external libraries.


// ------------------------------------------------------------------------------ import external libraries
                          // 3D camera library (mouse interaction: left click = orbit, control left click = pan, right click = zoom, double click = reset to top view)
                      // vector library
                      // slider library
                 // DXF export library
              // OBJ export library

// ------------------------------------------------------------------------------ global start parameters
static int cols =   95;                  // number of columns in the grid
static int rows =  177;                  // number of rows in the grid
static int lvls =    3;                  // number of floors in the grid
static int sc =     20;                  // overall grid scale 3D model (used for navigation speed & cut-off distance)
// 95 177 3 20 nolli dokken

float startLife = 0.10f;                  // desired % alive (= start % alive)
float startSatisfaction = 0.50f;          // start score level that determines if cell stays alive

float weightNeighbourhood = 0.30f;        // weight neighbourhood (group)
float weightNeighbours    = 0.20f;        // weight neighbours (connection)
float weightStability     = 0.50f;        // weight stability
float weightGreen         = 0.40f;        // weight green
float weightLight         = 0.40f;        // weight light
float weightView          = 0.20f;        // weight view
float weightYYY           = 0.10f;        // weight YYY (random)
// 0.30 0.20 0.50 0.40 0.40 0.20 1.00 = example combination

// ------------------------------------------------------------------------------ static code
public void setup() {
  surface.setTitle("Cellular Automata Designtool"); // window mode title
  // size(1200, 900, P3D);               // window mode
                      // fullscreen mode
  frameRate(500);                        // set maximum visualization and calculation speed (default 60 CPM max)
  peasyCamera();                         // activate camera
  dataFonts();                           // load fonts
  defaultNolli();                        // load Nolli map
  sliders();                             // load sliders
  iniModules();                          // initiate the class modules
}

// ------------------------------------------------------------------------------ dynamic code
public void draw() {
  background(0);                         // make background colour black and clean previous screen
  if (interf)    scene();                // if the interface is on: draw floor/Nolli map
  if (!pause)    resetData();            // if not paused: reset text/data/colour values
  if (!pause)    calcModules();          // if not paused: run calculations for current generation
  if (visualize) dispModules();          // if the visualization is on: display all modules and the constraints
  if (!pause)    calcFutModules();       // if not paused: run calculations for next generation
  if (!pause)    calcData();             // if not paused: calculate text/data values
  if (interf)    dispData();             // if the interface is on: display text/data values
}
// \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7 CAMERA SETUP \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7

// ------------------------------------------------------------------------------ declare camera
PeasyCam cam; // initialize camera

// ------------------------------------------------------------------------------ load camera (in setup) (+start position)
public void peasyCamera() {
  cam = new PeasyCam(this, 0.50f * sc * (cols+10), 0.50f * sc * (rows+10), 0.50f * sc * (lvls+10), 1.00f * sc * (cols + rows)); // this, lookAt X, lookAt Y, lookAt Z, current zoom distance
  cam.setMinimumDistance(0.05f * sc * (cols+rows)); // min zoom distance
  cam.setMaximumDistance(4.00f * sc * (cols+rows)); // max zoom distance
  cam.setWheelScale(0.10f);                         // mouse wheel zoom sensitivity
  cam.rotateZ(radians(-45));                       // rotate camera around z-axis for 45\u00b0
  cam.rotateX(radians(-45));                       // rotate camera around x-axis for 45\u00b0
}
// \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7 POPULATION LEVEL CALCULATIONS & DATA VISUALIZATION \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7

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

float DA = 1.00f;                                                               // dead/alive multiplier (makes it harder for new cells to come alive
float Dtreshold = 0.50f;                                                        // effort for new cells to die
float Atreshold = 1.00f;                                                        // effort for new cells to get born

float scoreMasterTotal = 0.00f;                                                 // calculate the total master        score for all cells alive
float scoreNeighbourhoodTotal = 0.00f;                                          // calculate the total neighbourhood score for all cells alive
float scoreNeighboursTotal = 0.00f;                                             // calculate the total neighbours    score for all cells alive
float scoreStabilityTotal = 0.00f;                                              // calculate the total stability     score for all cells alive
float scoreGreenTotal = 0.00f;                                                  // calculate the total green         score for all cells alive
float scoreLightTotal = 0.00f;                                                  // calculate the total light           score for all cells alive
float scoreViewTotal = 0.00f;                                                   // calculate the total view           score for all cells alive
float scoreYYYTotal = 0.00f;                                                    // calculate the total YYY           score for all cells alive

float weightSum = weightNeighbourhood + weightNeighbours + weightStability + weightGreen + weightLight + weightView + weightYYY;

// ------------------------------------------------------------------------------ declare
PFont helLight;                                                                // regular font
PFont helBold;                                                                 // title font

// ------------------------------------------------------------------------------ load fonts (in setup)
public void dataFonts() {
  helLight = loadFont("HelveticaNeue-Light-12.vlw");                           // regular font
  helBold = loadFont("HelveticaNeue-Bold-12.vlw");                             // title font
}

// ------------------------------------------------------------------------------ reset data/text/color values (in draw)
public void resetData() {
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
    percBuildable = 1.00f;
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
public void calcData() {
  generation++;                                                                // count generation
  life = PApplet.parseFloat(numCellAlive) / numCell;                                 // count number of cells alive
  if (satisfaction > 0.10f) {                                                   // min satisfaction percentage (values too extreme, will lead to massive extintion)
    if (life <= 0.95f * startLife) satisfaction = satisfaction - 0.0005f;        // if our desired % alive is to low, lower the satisfaction threshold
    if (life > 0.95f * startLife && life <= 0.98f * startLife) satisfaction = satisfaction - 0.0002f; // if our desired % alive is a little bit to low, lower the satisfaction threshold a little bit
  } 
  if (satisfaction < 0.90f) {                                                   // max satisfaction percentage (values too extreme, will lead to massive extintion)
    if (life >= 1.05f * startLife) satisfaction = satisfaction + 0.0005f;        // if our desired % alive is to high, increase the satisfaction threshold
    if (life < 1.05f * startLife && life >= 1.02f * startLife) satisfaction = satisfaction + 0.0002f; // if our desired % alive is a little bit to high, increase the satisfaction threshold a little bit
  } 
  calcGraph();                                                                 // add the new population data to the graph and remove the oldest data
  weightSum = weightNeighbourhood + weightNeighbours + weightStability + weightGreen + weightLight + weightView + weightYYY;
  startLife = lifeAbs / numCell;
}

// ------------------------------------------------------------------------------ display data values (in draw)
public void dispData() {
  hint(DISABLE_DEPTH_TEST);                                                    // turn off depth test
  cam.beginHUD();                                                              // start heads-up-display system - text fixed to camera
  // ............................................................................ UI backgrounds
  strokeWeight(0.5f);                                                           // rectangle outline weight
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
  text(nfc((scoreMasterTotal/(0.01f*numCellAlive)), 2) + " %", width-20, 30+9*20); // mean master score
  fill(234, 74, 18);                                                           // orange text color
  textAlign(LEFT);                                                             // align text left
  text("Group score:", width-interfaceWidth, 30+10*20);                         // info mean score neighbourhood  
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreNeighbourhoodTotal/(0.01f*numCellAlive)), 2) + " %", width-20, 30+10*20); // mean neighbourhood score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+11*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightNeighbourhood*100)/weightSum), 2) + " %", width-20, 30+11*20); // normalised weight
  fill(234, 180, 18);                                                          // yellow-orange text color
  textAlign(LEFT);                                                             // align text left
  text("Connection score:", width-interfaceWidth, 30+12*20);                   // info mean score neighbours
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreNeighboursTotal/(0.01f*numCellAlive)), 2) + " %", width-20, 30+12*20); // mean neighbours score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+13*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightNeighbours*100)/weightSum), 2) + " %", width-20, 30+13*20); // normalised weight
  fill(18, 180, 234);                                                          // blue text color
  textAlign(LEFT);                                                             // align text left
  text("Stability score: ", width-interfaceWidth, 30+14*20);                   // info mean score stability
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreStabilityTotal/(0.01f*numCellAlive)), 2) + " %", width-20, 30+14*20); // mean stability score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+15*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightStability*100)/weightSum), 2) + " %", width-20, 30+15*20);  // normalised weight
  fill(18, 234, 74);                                                           // green text color
  textAlign(LEFT);                                                             // align text left
  text("Green score: ", width-interfaceWidth, 30+16*20);                       // info mean score green
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreGreenTotal/(0.01f*numCellAlive)), 2) + " %", width-20, 30+16*20); // mean green score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+17*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightGreen*100)/weightSum), 2) + " %", width-20, 30+17*20);      // normalised weight
  fill(234, 234, 18);                                                          // yellow text color
  textAlign(LEFT);                                                             // align text left
  text("Light score: ", width-interfaceWidth, 30+18*20);                       // info mean score light
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreLightTotal/(0.01f*numCellAlive)), 2) + " %", width-20, 30+18*20); // mean light score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+19*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightLight*100)/weightSum), 2) + " %", width-20, 30+19*20);      // normalised weight
  fill(74, 234, 234);                                                          // cyan text color
  textAlign(LEFT);                                                             // align text left
  text("View score: ", width-interfaceWidth, 30+20*20);                        // info mean score view
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreViewTotal/(0.01f*numCellAlive)), 2) + " %", width-20, 30+20*20); // mean view score
  textAlign(LEFT);                                                             // align text left
  text("Weight:", width-interfaceWidth, 30+21*20);                             // weight
  textAlign(RIGHT);                                                            // align text right
  text(nfc(((weightView*100)/weightSum), 2) + " %", width-20, 30+21*20);       // normalised weight
  fill(180, 180, 234);                                                         // purple text color
  textAlign(LEFT);                                                             // align text left
  text("Random score: ", width-interfaceWidth, 30+22*20);                      // info mean score YYY
  textAlign(RIGHT);                                                            // align text right
  text(nfc((scoreYYYTotal/(0.01f*numCellAlive)), 2) + " %", width-20, 30+22*20); // mean YYY score
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
  strokeWeight(1.5f);                                                           // stroke weight
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
  if (keyPressed && key == '\u00e9' || keyPressed && key == '2') line(16 * width/numOfKeys, 20, 17 * width/numOfKeys, 20);
  if (keyPressed && key == '"' || keyPressed && key == '3') line(17 * width/numOfKeys, 20, 18 * width/numOfKeys, 20);
  if (keyPressed && key == 39  || keyPressed && key == '4') line(18 * width/numOfKeys, 20, 19 * width/numOfKeys, 20);
  strokeWeight(0.5f);                                                           // stroke weight
  stroke(0, 0, 0);                                                             // black stroke color
  for (int i=width/numOfKeys; i<=width-width/numOfKeys; i+=width/numOfKeys) line(i, 0, i, 21); // topbar ticks
  cam.endHUD();                                                                // stop heads-up-display system - text fixed to camera
  hint(ENABLE_DEPTH_TEST);                                                     // turn depth test back on
}
// \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7 EXPORT FUNCTIONS \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7

// ------------------------------------------------------------------------------ export variables
String filename="EG_Cellular_Automata_Designtool";
PrintWriter txtfile;
Table csvfile;
Table rhinocsvfile;

// ------------------------------------------------------------------------------ create a txt file with info about the current generation
public void txt() {
  txtfile= createWriter("export/" + year() + nf(month(), 2) + nf(day(), 2) + "_" + str(generation) + "/" + filename + "_" + year() + nf(month(), 2) + nf(day(), 2) + "-"  + nf(hour(), 2) + nf(minute(), 2) + nf(second(), 2) + "_" + str(generation) + ".txt"); // create file
  txtfile.println("Filename:                   " + filename + "_" + year() + nf(month(), 2) + nf(day(), 2) + "-"  + nf(hour(), 2) + nf(minute(), 2) + nf(second(), 2) + "_" + str(generation) + ".txt"); // write a new line with data to the file
  txtfile.println("Created on:                 " + year() + "/" + month() + "/" + day() + " at " + hour() + ":" + minute() + ":" + second());
  txtfile.println(); // break
  txtfile.println("Generation:                 " + nfc(generation, 0));
  txtfile.println(); // break
  txtfile.println("# of rows:                  " + nfc(rows, 0));
  txtfile.println("# of columns:               " + nfc(cols, 0));
  txtfile.println("# of levels:                " + nfc(lvls, 0));
  txtfile.println(); // break
  txtfile.println("Desired # of cells alive:   " + nfc((numCell*startLife), 0) + "/" + nfc(numCell, 0));
  txtfile.println("Current # of cells alive:   " + nfc(numCellAlive, 0) + "/" + nfc(numCell, 0));
  txtfile.println(); // break
  txtfile.println("Desired % cells alive:      " + nfc((startLife*100), 1) + "  %");
  txtfile.println("Current % cells alive:      " + nfc((life*100), 1) + "  %");
  txtfile.println(); // break
  txtfile.println("Satisfaction threshold:     " + nfc((satisfaction*100), 1) + "  %");
  txtfile.println(); // break
  txtfile.println("Mean master score:          " + nfc((scoreMasterTotal       /(0.01f*numCellAlive)), 2) + " %");
  txtfile.println("Mean neighbourhood score:   " + nfc((scoreNeighbourhoodTotal/(0.01f*numCellAlive)), 2) + " %");
  txtfile.println("Mean neighbours score:      " + nfc((scoreNeighboursTotal   /(0.01f*numCellAlive)), 2) + " %");
  txtfile.println("Mean stability score:       " + nfc((scoreStabilityTotal    /(0.01f*numCellAlive)), 2) + " %");
  txtfile.println("Mean green score:           " + nfc((scoreGreenTotal        /(0.01f*numCellAlive)), 2) + " %");
  txtfile.println("Mean light score:           " + nfc((scoreLightTotal        /(0.01f*numCellAlive)), 2) + " %");
  txtfile.println("Mean view score:            " + nfc((scoreViewTotal         /(0.01f*numCellAlive)), 2) + " %");
  txtfile.println("Mean YYY score:             " + nfc((scoreYYYTotal          /(0.01f*numCellAlive)), 2) + " %");
  txtfile.flush(); // write the remaining data to the file
  txtfile.close(); // finishes the file
}

// ------------------------------------------------------------------------------ create a csv (comma-separated values) file with info about the current generation
public void csv() {
  csvfile = new Table();
  csvfile.addColumn("Column (" + cols + ")");
  csvfile.addColumn("Row (" + rows + ")");
  csvfile.addColumn("Level(" + lvls + ")");
  csvfile.addColumn("Dead(0) or alive(1)");
  csvfile.addColumn("Master score (in %)");
  csvfile.addColumn("Neighbourhood score (in %)");
  csvfile.addColumn("Neighbours score (in %)");
  csvfile.addColumn("Stability score (in %)");
  csvfile.addColumn("Green score (in %)");
  csvfile.addColumn("Light score (in %)");
  csvfile.addColumn("View score (in %)");
  csvfile.addColumn("YYY score (in %)");
  for (int i = 5; i < cols+5; i++) {
    for (int j = 5; j < rows+5; j++) {
      for (int k = 5; k < lvls+5; k++) {
        TableRow newRow = csvfile.addRow();
        newRow.setInt("Column (" + cols + ")", (i-4));
        newRow.setInt("Row (" + rows + ")", (j-4));
        newRow.setInt("Level(" + lvls + ")", (k-4));
        newRow.setInt("Dead(0) or alive(1)", grid[i][j][k].type);
        newRow.setFloat("Master score (in %)", grid[i][j][k].scoreMaster*100);
        newRow.setFloat("Neighbourhood score (in %)", grid[i][j][k].scoreNeighbourhood*100);
        newRow.setFloat("Neighbours score (in %)", grid[i][j][k].scoreNeighbours*100);
        newRow.setFloat("Stability score (in %)", grid[i][j][k].scoreStability*100);
        newRow.setFloat("Green score (in %)", grid[i][j][k].scoreGreen*100);
        newRow.setFloat("Light score (in %)", grid[i][j][k].scoreLight*100);
        newRow.setFloat("View score (in %)", grid[i][j][k].scoreView*100);
        newRow.setFloat("YYY score (in %)", grid[i][j][k].scoreYYY*100);
      }
    }
  }
  saveTable(csvfile, "export/" + year() + nf(month(), 2) + nf(day(), 2) + "_" + str(generation) + "/" + filename + "_" + year() + nf(month(), 2) + nf(day(), 2) + "-"  + nf(hour(), 2) + nf(minute(), 2) + nf(second(), 2) + "_" + str(generation) + ".csv");
}

// ------------------------------------------------------------------------------ create a csv (comma-separated values) file with info about the current generation
public void rhinocsv() {
  rhinocsvfile = new Table();
  rhinocsvfile.addColumn("Column (" + cols + ")");
  rhinocsvfile.addColumn("Row (" + rows + ")");
  rhinocsvfile.addColumn("Level(" + lvls + ")");
  rhinocsvfile.addColumn("Dead(0) or alive(1)");
  for (int i = 5; i < cols+5; i++) {
    for (int j = 5; j < rows+5; j++) {
      for (int k = 5; k < lvls+5; k++) {
        TableRow newRow = rhinocsvfile.addRow();
        newRow.setInt("Column (" + cols + ")", (i-5));
        newRow.setInt("Row (" + rows + ")", (j-5));
        newRow.setInt("Level(" + lvls + ")", (k-5));
        newRow.setInt("Dead(0) or alive(1)", grid[i][j][k].type);
      }
    }
  }
  saveTable(rhinocsvfile, "export/" + year() + nf(month(), 2) + nf(day(), 2) + "_" + str(generation) + "/rhinocsv.csv");
}

// ------------------------------------------------------------------------------ create a dxf file from the current generation
public void dxf() {
  beginRaw(DXF, "export/" + year() + nf(month(), 2) + nf(day(), 2) + "_" + str(generation) + "/" + filename + "_" + year() + nf(month(), 2) + nf(day(), 2) + "-"  + nf(hour(), 2) + nf(minute(), 2) + nf(second(), 2) + "_" + str(generation) + ".dxf");
  for (int i = 5; i < cols+5; i++) {
    for (int j = 5; j < rows+5; j++) {
      for (int k = 5; k < lvls+5; k++) {
        grid[i][j][k].displayBox(); // display all elements that are alive
      }
    }
  }
  endRaw();
}

// ------------------------------------------------------------------------------ create a dxf file from the current generation
public void obj() {
  beginRecord("nervoussystem.obj.OBJExport", "export/" + year() + nf(month(), 2) + nf(day(), 2) + "_" + str(generation) + "/" + filename + "_" + year() + nf(month(), 2) + nf(day(), 2) + "-"  + nf(hour(), 2) + nf(minute(), 2) + nf(second(), 2) + "_" + str(generation) + ".obj");
  for (int i = 5; i < cols+5; i++) {
    for (int j = 5; j < rows+5; j++) {
      for (int k = 5; k < lvls+5; k++) {
        grid[i][j][k].displayBox(); // display all elements that are alive
      }
    }
  }
  endRecord();
}

// ------------------------------------------------------------------------------ create a screenshot from the current generation
public void screenShot() {
  saveFrame("export/" + year() + nf(month(), 2) + nf(day(), 2) + "_" + str(generation) + "/" + filename + "_" + year() + nf(month(), 2) + nf(day(), 2) + "-"  + nf(hour(), 2) + nf(minute(), 2) + nf(second(), 2) + "_" + str(generation) + ".jpg");
}
// \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7 LIVE DATA GRAPH \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7

// ------------------------------------------------------------------------------ graph variables
ArrayList<Float> graphAlive         = new ArrayList();
ArrayList<Float> graphMaster        = new ArrayList();
ArrayList<Float> graphNeighbourhood = new ArrayList();
ArrayList<Float> graphNeighbours    = new ArrayList();
ArrayList<Float> graphStability     = new ArrayList();
ArrayList<Float> graphGreen         = new ArrayList();
ArrayList<Float> graphLight         = new ArrayList();
ArrayList<Float> graphView          = new ArrayList();
ArrayList<Float> graphYYY           = new ArrayList();
ArrayList<Float> graphSatisfaction  = new ArrayList();

public void calcGraph() {
  graphAlive         .add(yGraphScale*                                  life);
  graphMaster        .add(yGraphScale*(scoreMasterTotal        /numCellAlive));
  graphNeighbourhood .add(yGraphScale*(scoreNeighbourhoodTotal /numCellAlive));
  graphNeighbours    .add(yGraphScale*(scoreNeighboursTotal    /numCellAlive));
  graphStability     .add(yGraphScale*(scoreStabilityTotal     /numCellAlive));
  graphGreen         .add(yGraphScale*(scoreGreenTotal         /numCellAlive));
  graphLight         .add(yGraphScale*(scoreLightTotal         /numCellAlive));
  graphView          .add(yGraphScale*(scoreViewTotal          /numCellAlive));
  graphYYY           .add(yGraphScale*(scoreYYYTotal           /numCellAlive));
  graphSatisfaction  .add(yGraphScale*                           satisfaction);
  for (int i = graphMaster.size() - 1; i >= 0; i--) {
    if (graphMaster.size() > interfaceWidth - 20) { // if the list gets longer than the xscale, delete the first element
      graphAlive         .remove(0);
      graphMaster        .remove(0);
      graphNeighbourhood .remove(0);
      graphNeighbours    .remove(0);
      graphStability     .remove(0);
      graphGreen         .remove(0);
      graphLight         .remove(0);
      graphView          .remove(0);
      graphYYY           .remove(0);
      graphSatisfaction  .remove(0);
    }
  }
}

public void dispGraph() {
  pushMatrix();
  translate(width - interfaceWidth, height - 20);                             // origin of graph
  scale(1, -1);                                                               // flip around y-axis
  fill(0, 200);                                                               // rectangle color
  rect(-10, -10, interfaceWidth, yGraphScale + 20);                           // top left
  strokeWeight(1);                                                            // stroke weight
  for (int i=1; i<graphMaster.size(); i++) {                                  // for all elements in the arraylist (from element 1 till n)
    stroke(234, 234, 234);                                                    // white stroke color
    line(i-1, graphAlive        .get(i-1), i, graphAlive        .get(i));
    stroke(234, 18, 74);                                                      // red stroke color
    line(i-1, graphMaster       .get(i-1), i, graphMaster       .get(i));
    stroke(234, 74, 18);                                                      // orange stroke color
    line(i-1, graphNeighbourhood.get(i-1), i, graphNeighbourhood.get(i));
    stroke(234, 180, 18);                                                     // yellow-orange stroke color
    line(i-1, graphNeighbours   .get(i-1), i, graphNeighbours   .get(i));
    stroke(18, 180, 234);                                                     // blue stroke color
    line(i-1, graphStability    .get(i-1), i, graphStability    .get(i));
    stroke(18, 234, 74);                                                      // green stroke color
    line(i-1, graphGreen        .get(i-1), i, graphGreen        .get(i));
    stroke(234, 234, 18);                                                     // yellow stroke color
    line(i-1, graphLight        .get(i-1), i, graphLight        .get(i));
    stroke(74, 234, 234);                                                     // cyan stroke color
    line(i-1, graphView         .get(i-1), i, graphView         .get(i));
    stroke(180, 180, 234);                                                    // purple stroke color
    line(i-1, graphYYY          .get(i-1), i, graphYYY          .get(i));
    stroke(180, 180, 180);                                                    // grey stroke color
    line(i-1, graphSatisfaction .get(i-1), i, graphSatisfaction .get(i));
  } 
  stroke(255);                                                                // white axis color
  line(0, 0, interfaceWidth - 20, 0);                                         // x-axis line
  line(0, 0, 0, yGraphScale);                                                 // y-axis line
  for (int i=0; i<=interfaceWidth - 20; i+=10) line(i, 0, i, -4);             // x-axis ticks
  for (int i=0; i<=10; i++) line(0, i*yGraphScale/10, -4, i*yGraphScale/10);  // y-axis ticks
  popMatrix();
}
// \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7 USER KEY INTERACTIONS \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7

// ------------------------------------------------------------------------------ key-functions variables
boolean reset        = false;         // press r to reset the system to the starting conditions
boolean pause        = false;         // press p to pause/start the calculations
boolean visualize    = true;          // press v to display the current population
boolean box          = false;         // press b to switch between box and point visualization
boolean constr       = false;         // press c to show the constraints
int     scoreColor   = 1;             // press s to change scores coloring type
boolean interf       = true;          // press i to hide the interface
boolean exportscreen = false;         // press e to make a screenshot
boolean exporttxt    = false;         // press t to export the current scene in TXT/CSV and make a screenshot
boolean exportdwg    = false;         // press d to export the current scene in DXF and make a screenshot
boolean exportobj    = false;         // press o to export the current scene in DXF and make a screenshot
int     showNolli    = 1;             // press n to show Nolli map
boolean useNolli     = true;          // press u to use Nolli map

int[]   colors       = new int[9];    // RGB set 1, RGB set 2, RGB set 3

// ------------------------------------------------------------------------------ key-functions
public void keyPressed() {
  if (key == 'x') {
    exit();                           // change pause status
  }
  if (key=='r') {
    reset = !reset;                   // change reset status
    pause = false;                    // un-pause if paused
    generation = 0;                   // reset generation countNeighbourhood
    satisfaction = startSatisfaction; // reset the satisfaction value
    for (int i = 0; i < cols+9; i++) {
      for (int j = 0; j < rows+9; j++) {
        for (int k = 0; k < lvls+9; k++) {
          grid[i][j][k].randomSet();  // reset type
        }
      }
    }
  }
  if (key == 'p') {
    pause = !pause;                   // change pause status
  }
  if (key == 'v') {
    visualize = !visualize;           // change visualize status
  }
  if (key == 'c') {
    constr = !constr;                 // change constraints visibility status
  }
  if (key == 'b') {
    box = !box;                       // change box/point status
  }
  if (key == 's') {
    scoreColor ++;                    // change scores coloring type
    if (scoreColor > 9) scoreColor = 0;
  }
  if (key == 'l') {
    loadNolli();                      // load Nolli map
  }
  if (key == 'u') {
    useNolli = !useNolli;             // change useNolli status
  }
  if (key == 'n') {
    showNolli ++;                     // change showNolli status
    if (showNolli > 2) showNolli = 0;
  }
  if (key == 'i') {
    interf = !interf;                 // change interface visibility status
  }
  if (key == 'e') {
    exportscreen = !exportscreen;     // change exportScreen status
    visualize = true;                 // turn on visualization
    box = true;                       // turn on box view
    screenShot();                     // export a screenshot
    println("Screenshot generated");  // write in the console that file is successfully made
    exportscreen = false;             // reset exportScreen
  }
  if (key == 't') {
    exporttxt = !exporttxt;           // change exportTXT status
    pause = true;                     // pause calculations
    visualize = true;                 // turn on visualization
    box = true;                       // turn on box view
    screenShot();                     // export a screenshot
    txt();                            // export a txt data file
    csv();                            // export a csv data file
    rhinocsv();                       // export a csv data file for importing in Rhino
    println("TXT-file/CSV-file/screenshot generated"); // write in the console that file is successfully made
    exporttxt = false;                // reset exportTXT
  }
  if (key == 'd') {
    exportdwg = !exportdwg;           // change exportDXG status
    pause = true;                     // pause calculations
    visualize = true;                 // turn on visualization
    box = true;                       // turn on box view
    screenShot();                     // export a screenshot
    dxf();                            // export a dxf file
    println("DXF-file/screenshot generated"); // write in the console that file is successfully made
    exportdwg = false;                // reset exportDXG
  }
  if (key == 'o') {
    exportobj = !exportobj;           // change exportDXG status
    pause = true;                     // pause calculations
    visualize = true;                 // turn on visualization
    box = true;                       // turn on box view
    screenShot();                     // export a screenshot
    obj();                            // export a dxf file
    println("OBJ-file/screenshot generated"); // write in the console that file is successfully made
    exportobj = false;                // reset exportDXG
  }
  if (key == '&' || key == '1') { // TOP VIEW
    cam.setRotations(0, 0, 0); // reset camera rotations
    cam.setDistance(1.00f * sc * (cols + rows)); // reset camera zoom
    cam.lookAt(0.50f * sc * (cols+10), 0.50f * sc * (rows+10), 0.50f * sc * (lvls+10)); // reset camera centre
  }
  if (key == '\u00e9' || key == '2') { // ISOMETRIC VIEW
    cam.setRotations(0, 0, 0); // reset camera rotations
    cam.setDistance(1.00f * sc * (cols + rows)); // reset camera zoom
    cam.lookAt(0.50f * sc * (cols+10), 0.50f * sc * (rows+10), 0.50f * sc * (lvls+10)); // reset camera centre
    cam.rotateZ(radians(-45));                       // rotate camera around z-axis for 45\u00b0
    cam.rotateX(radians(-45));                       // rotate camera around x-axis for 45\u00b0
  }
  if (key == '"' || key == '3') { // FRONT VIEW
    cam.setRotations(0, 0, 0); // reset camera rotations
    cam.setDistance(1.00f * sc * (cols + rows)); // reset camera zoom
    cam.lookAt(0.50f * sc * (cols+10), 0.50f * sc * (rows+10), 0.50f * sc * (lvls+10)); // reset camera centre
    cam.rotateX(radians(-90));                       // rotate camera around x-axis for 90\u00b0
  }
  if (key == 39 || key == '4') { // EDGE VIEW
    cam.setRotations(0, 0, 0); // reset camera rotations
    cam.setDistance(1.05f * sc * (cols + rows)); // reset camera zoom
    cam.lookAt(0.50f * sc * (cols+10), 0.50f * sc * (rows+10), 0.50f * sc * (lvls+10)); // reset camera centre
    cam.rotateX(radians(-90));                       // rotate camera around x-axis for 90\u00b0
    cam.rotateY(radians(-45));                       // rotate camera around y-axis for 45\u00b0
  }
}
// \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7 MODULES INITIATION \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7

// ------------------------------------------------------------------------------ initiate the class modules (a nested for-loop goes through all modules) (in setup)
public void iniModules() {
  for (int i = 0; i < cols+9; i++) {
    for (int j = 0; j < rows+9; j++) {
      for (int k = 0; k < lvls+9; k++) {
        Vec3D ptLoc = new Vec3D(i * sc, j * sc, k * sc); // build-up the ptLoc vector (in the location of the point)
        grid[i][j][k] = new modules(ptLoc, i, j, k);     // build-up modules for the whole grid (has a vector ptLoc and a location in the grid)
      }
    }
  }
}
// \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7 MODULES LEVEL CALCULATIONS \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7

int brightnessThreshold = 128;

// ------------------------------------------------------------------------------ run calculations for current generation (in draw)
public void calcModules() {
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
public void dispModules() {
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
public void calcFutModules() {
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
// \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7 MODULES CLASS (inc. ALGORITMES) \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7

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

  float scoreNeighbourhood = 0.00f;                               // score  neighbourhood  (for each cell)
  float scoreNeighbours = 0.00f;                                  // score  neighbours     (for each cell)
  float scoreStability = 0.00f;                                   // score  stability      (for each cell)
  float scoreGreen = 0.00f;                                       // score  green          (for each cell)
  float scoreLight = 0.00f;                                       // score  light          (for each cell)
  float scoreView = 0.00f;                                        // score  view           (for each cell)
  float scoreYYY = 0.00f;                                         // score  YYY            (for each cell)

  float scoreMaster = 0.00f;                                      // calculate masterscore (for each cell)

  float scoreValue = 0.00f;                                       // calculation value for the color

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
  public void setVar() {
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
  public void evalNeighbourhood() {
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
    if (countNeighbourhood < 3)  scoreNeighbourhood = 0.00f;      // loneliness (die)
    if (countNeighbourhood == 3) scoreNeighbourhood = 0.33f * DA; // loneliness (die)
    if (countNeighbourhood == 4) scoreNeighbourhood = 0.67f * DA; // ideal state (stay alive)
    if (countNeighbourhood == 5) scoreNeighbourhood = 1.00f * DA; // ideal state (stay alive)
    if (countNeighbourhood == 6) scoreNeighbourhood = 1.00f;      // ideal state (stay alive)
    if (countNeighbourhood == 7) scoreNeighbourhood = 0.67f * DA; // ideal state (stay alive)
    if (countNeighbourhood == 8) scoreNeighbourhood = 0.33f * DA; // overpopulation (die)
    if (countNeighbourhood > 8)  scoreNeighbourhood = 0.00f;      // overpopulation (die)
  }

  // ------------------------------------------------------------------------------ evaluate/calculate the number of Neighbours score for all elements
  public void evalNeighbours() {
    // --- COUNT ---
    if (grid[x-1][y]  [z]  .type == 1) countNeighbours ++;       // cell left    same level
    if (grid[x+1][y]  [z]  .type == 1) countNeighbours ++;       // cell right   same level
    if (grid[x]  [y-1][z]  .type == 1) countNeighbours ++;       // cell up      same level
    if (grid[x]  [y+1][z]  .type == 1) countNeighbours ++;       // cell down    same level
    if (grid[x]  [y]  [z+1].type == 1) countNeighbours ++;       // cell central level higher
    if (grid[x]  [y]  [z-1].type == 1) countNeighbours ++;       // cell central level lower
    // --- RULES ---
    if (countNeighbours == 0) scoreNeighbours = 0.00f;            // loneliness (die)
    if (countNeighbours == 1) scoreNeighbours = 0.50f * DA;       // ideal state (stay alive)
    if (countNeighbours == 2) scoreNeighbours = 0.80f * DA;       // ideal state (stay alive)
    if (countNeighbours == 3) scoreNeighbours = 1.00f;            // ideal state (stay alive)
    if (countNeighbours == 4) scoreNeighbours = 0.40f * DA;       // overpopulation (die)
    if (countNeighbours == 5) scoreNeighbours = 0.10f * DA;       // overpopulation (die)
    if (countNeighbours == 6) scoreNeighbours = 0.00f;            // overpopulation (die)
  }

  // ------------------------------------------------------------------------------ evaluate/calculate stability score for all element
  public void evalStability() {
    // --- COUNT ---
    if (grid[x]  [y-1][z-1].type == 1 && grid[x]  [y-1][z]  .type == 1) countStability ++;   // cell central up      level lower+same
    if (grid[x]  [y+1][z-1].type == 1 && grid[x]  [y+1][z]  .type == 1) countStability ++;   // cell central down    level lower+same
    if (grid[x-1][y]  [z-1].type == 1 && grid[x-1][y]  [z]  .type == 1) countStability ++;   // cell left    central level lower+same
    if (grid[x+1][y]  [z-1].type == 1 && grid[x+1][y]  [z]  .type == 1) countStability ++;   // cell right   central level lower+same
    // --- RULES ---
    if (z == 5) {                                               // rules for ground level
      scoreStability = 1.00f * DA;                               // is stable because it is placed directly on the ground
    } else {                                                    // rules for all other levels 
      if (grid[x][y][z-1].type == 1) {                          // if cell central level lower is alive it has direct support
        scoreStability = grid[x][y][z-1].scoreStability * DA;   // is stable because it right above an other cell, get the score of that cell
      } else {                                                  // if the cell has no direct support underneath it
        if (countStability == 0) scoreStability = 0.00f;         // can carry no loads sideways
        if (countStability == 1) scoreStability = 0.40f * DA;    // can carry small loads sideways
        if (countStability == 2) scoreStability = 0.60f * DA;    // can carry some loads sideways
        if (countStability == 3) scoreStability = 0.70f * DA;    // can carry loads sideways
        if (countStability == 4) scoreStability = 0.80f * DA;    // can carry big loads sideways
      }
    }
  }

  // ------------------------------------------------------------------------------ evaluate/calculate green score for all element
  public void evalGreen() {
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
    if (countGreen == 0) scoreGreen = 0.00f;                   // no acces to green
    if (countGreen == 1) scoreGreen = 0.50f * DA;              // little acces to green
    if (countGreen == 2) scoreGreen = 0.70f * DA;              // normal acces to green
    if (countGreen == 3) scoreGreen = 0.90f * DA;              // lots of acces to green
    if (countGreen == 4) scoreGreen = 1.00f * DA;              // lots of acces to green
    if (countGreen == 5) scoreGreen = 1.00f;                   // maximum acces to green
  }

  // ------------------------------------------------------------------------------ evaluate/calculate light score for all element
  public void evalLight() {
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
    if (countLight <= 3)  scoreLight = 0.00f;      // no acces to light
    if (countLight == 4)  scoreLight = 0.30f * DA; // minimum acces to light
    if (countLight == 5)  scoreLight = 0.40f * DA; // minimum acces to light
    if (countLight == 6)  scoreLight = 0.50f * DA; // little acces to light
    if (countLight == 7)  scoreLight = 0.65f * DA; // little acces to light
    if (countLight == 8)  scoreLight = 0.80f * DA; // normal acces to light
    if (countLight == 9)  scoreLight = 0.90f * DA; // normal acces to light
    if (countLight >= 10) scoreLight = 1.00f;      // maximum acces to light
  }

  // ------------------------------------------------------------------------------ evaluate/calculate view score for all element
  public void evalView() {
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
    if (countView <= 3)  scoreView = 0.00f;      // no      acces to a view
    if (countView == 4)  scoreView = 0.30f * DA; // minimum acces to a view
    if (countView == 5)  scoreView = 0.40f * DA; // minimum acces to a view
    if (countView == 6)  scoreView = 0.50f * DA; // little  acces to a view
    if (countView == 7)  scoreView = 0.65f * DA; // little  acces to a view
    if (countView == 8)  scoreView = 0.80f * DA; // normal  acces to a view
    if (countView == 9)  scoreView = 0.90f * DA; // normal  acces to a view
    if (countView >= 10) scoreView = 1.00f;      // maximum acces to a view
  }

  // ------------------------------------------------------------------------------ evaluate/calculate YYY score for all element
  public void evalYYY() {
    scoreYYY = random(0.00f, 1.00f);
  }

  // ------------------------------------------------------------------------------ evaluate/calculate master score
  public void evalMaster() {
    scoreMaster = (scoreNeighbourhood * weightNeighbourhood + scoreNeighbours * weightNeighbours + scoreStability * weightStability + scoreGreen * weightGreen + scoreLight * weightLight + scoreView * weightView + scoreYYY * weightYYY)/(weightSum); // calculate a main score for every cell
  }

  // ------------------------------------------------------------------------------ display all elements that are alive as a box
  public void displayColor() {
    if (scoreColor == 0) scoreValue = 1.00f;
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
  public void displayBox() {
    if (type == 1) {                    // if the point is alive: draw a box or a point
      stroke(0);                        // black outline
      strokeWeight(1);                  // make outline thin
      if (scoreValue <  0.50f) fill(colors[0] * (1 - 2 * scoreValue) + colors[3] * (     2 * scoreValue), colors[1] * (1 - 2 * scoreValue) + colors[4] * (     2 * scoreValue), colors[2] * (1 - 2 * scoreValue) + colors[5] * (     2 * scoreValue)); // color from score
      if (scoreValue >= 0.50f) fill(colors[3] * (2 - 2 * scoreValue) + colors[6] * (-1 + 2 * scoreValue), colors[4] * (2 - 2 * scoreValue) + colors[7] * (-1 + 2 * scoreValue), colors[5] * (2 - 2 * scoreValue) + colors[8] * (-1 + 2 * scoreValue)); // color from score
      pushMatrix();
      translate(loc.x, loc.y, loc.z);
      box(sc);                          // draw a box
      popMatrix();
    }
  }

  // ------------------------------------------------------------------------------ display all elements that are alive as a point
  public void displayPoint() {
    if (type == 1) {                    // if the point is alive: draw a box or a point
      if (scoreValue <  0.50f) stroke(colors[0] * (1 - 2 * scoreValue) + colors[3] * (     2 * scoreValue), colors[1] * (1 - 2 * scoreValue) + colors[4] * (     2 * scoreValue), colors[2] * (1 - 2 * scoreValue) + colors[5] * (     2 * scoreValue)); // color from score
      if (scoreValue >= 0.50f) stroke(colors[3] * (2 - 2 * scoreValue) + colors[6] * (-1 + 2 * scoreValue), colors[4] * (2 - 2 * scoreValue) + colors[7] * (-1 + 2 * scoreValue), colors[5] * (2 - 2 * scoreValue) + colors[8] * (-1 + 2 * scoreValue)); // color from score
      strokeWeight(2.5f);                // make point bigger
      point(loc.x, loc.y, loc.z);       // draw a point
    }
  }

  // ------------------------------------------------------------------------------ display constraints
  public void displayConstraints() {
    if (x <= 4 || y <= 4 || z <= 4 || x >= cols+5 || y >= rows+5 || z >= lvls+5) {
      Vec3D boundary = grid[x][y][z].loc;
      stroke(0xff003CBE);                  // blue
      strokeWeight(2);
      point(boundary.x, boundary.y, boundary.z);
    }
  }

  // ------------------------------------------------------------------------------ calculate data of current population for HUD text
  public void calcData() {
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
  public void calcFutType() {
    if (scoreMaster >= satisfaction) {
      futType = 1;                      // if the main score is equal or bigger than the threshold: cells is/becomes alive
    } else {
      futType = 0;                      // if the main score is smaller than the threshold: cells is/becomes dead
    }
  }

  // ------------------------------------------------------------------------------ update all elements with their new dead/alive status
  public void updateType() {
    type = futType;                     // update all elements with their new dead/alive status
  }

  // ------------------------------------------------------------------------------ (re)set function (activated when R is pressed and at the beginning of the script)
  public void randomSet() {                    // randomly give all cells a dead/alive status with life% alive
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
// \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7 NOLLI EXCLUSSION MAP & SCENE \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7

PImage nolli;
PImage newNolli;

public void defaultNolli() {
  nolli = loadImage("data/nolli-dokken.png");
  newNolli = loadImage("data/nolli-dokken.png");
  newNolli.resize(cols, rows);
  newNolli.filter(THRESHOLD, 0.5f);
}

public void loadNolli() {
  selectInput("Select your Nolli jpg map:", "fileSelected");
}

public void fileSelected(File selection) {
  if (selection == null) {
    defaultNolli();
    println("Image loading cancel, restoring default image.");
  } else {
    nolli = loadImage(selection.getAbsolutePath());
    newNolli = loadImage(selection.getAbsolutePath());
    newNolli.resize(cols, rows);
    newNolli.filter(THRESHOLD, 0.5f);
    println("Image loaded from " + selection.getAbsolutePath());
  }
}

// ------------------------------------------------------------------------------ make floor planes (in draw)
public void scene() {
  stroke(0);
  strokeWeight(1);
  pushMatrix();
  translate(9*sc/2, 9*sc/2, 9*sc/2);
  if (useNolli == true) {
    if (showNolli == 0) {
      fill(0xff003CBE, 70);
      rect(0, 0, sc * (cols), sc * (rows));
    }
    if (showNolli == 1) image(nolli, 0, 0, sc * (cols), sc * (rows));
    if (showNolli == 2) image(newNolli, 0, 0, sc * (cols), sc * (rows));
  } else {
    fill(0xff003CBE, 70);
    rect(0, 0, sc * (cols), sc * (rows));
  }
  popMatrix();
}
// \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7 USER SLIDER INTERACTIONS \u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7\u00a7

// ------------------------------------------------------------------------------ declare
ControlP5 cp5; // sliders

// ------------------------------------------------------------------------------ load sliders (in setup)
public void sliders() {
  cp5 = new ControlP5(this);
  ButtonBar topBar = cp5.addButtonBar("topBar")
    .setPosition(0, 0)
    .setSize(width, 20)
    .setColorForeground(200) 
    .addItems(split("exit, reset, pause, visualize, constraints, box, scorecolor, load nolli, use nolli, show nolli, interface, export screen, export txt/data, export dwg, export obj, top view, isometric view, front view, edge view", ", "));
  cp5.addSlider("lifeAbs")    
    .setRange(0, numCell/3)
    .setDecimalPrecision(0)
    .setNumberOfTickMarks(PApplet.parseInt((numCell/3)+1))
    .showTickMarks(false) 
    .setPosition(width - interfaceWidth + 50, 9 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightNeighbourhood")
    .setRange(0.00f, 1.00f)
    .setPosition(width - interfaceWidth + 50, 12 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightNeighbours")   
    .setRange(0.00f, 1.00f)
    .setPosition(width - interfaceWidth + 50, 14 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightStability")    
    .setRange(0.00f, 1.00f)
    .setPosition(width - interfaceWidth + 50, 16 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightGreen")   
    .setRange(0.00f, 1.00f)
    .setPosition(width - interfaceWidth + 50, 18 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightLight")   
    .setRange(0.00f, 1.00f)
    .setPosition(width - interfaceWidth + 50, 20 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightView")   
    .setRange(0.00f, 1.00f)
    .setPosition(width - interfaceWidth + 50, 22 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightYYY")   
    .setRange(0.00f, 1.00f)
    .setPosition(width - interfaceWidth + 50, 24 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("Dtreshold")   
    .setRange(0.00f, 1.00f)
    .setPosition(width - interfaceWidth + 90, 26 * 20)
    .setSize(interfaceWidth - 110, 12)
    .setLabel("");
  cp5.setAutoDraw(false);


  topBar.onClick(new CallbackListener() {
    public void controlEvent(CallbackEvent ev) {
      ButtonBar bar = (ButtonBar)ev.getController();
      if (bar.hover() == 0) {
        exit();                           // change pause status
      }
      if (bar.hover() == 1) {
        reset = !reset;                   // change reset status
        pause = false;                    // un-pause if paused
        generation = 0;                   // reset generation countNeighbourhood
        satisfaction = startSatisfaction; // reset the satisfaction value
        for (int i = 0; i < cols+9; i++) {
          for (int j = 0; j < rows+9; j++) {
            for (int k = 0; k < lvls+9; k++) {
              grid[i][j][k].randomSet();  // reset type
            }
          }
        }
      }
      if (bar.hover() == 2) {
        pause = !pause;                   // change pause status
      }
      if (bar.hover() == 3) {
        visualize = !visualize;           // change visualize status
      }
      if (bar.hover() == 4) {
        constr = !constr;                 // change constraints visibility status
      }
      if (bar.hover() == 5) {
        box = !box;                       // change box/point status
      }
      if (bar.hover() == 6) {
        scoreColor ++;                    // change scores coloring type
        if (scoreColor > 8) scoreColor = 0;
      }
      if (bar.hover() == 7) {
        loadNolli();                      // load Nolli map
      }
      if (bar.hover() == 8) {
        useNolli = !useNolli;             // change useNolli status
      }
      if (bar.hover() == 9) {
        showNolli ++;                     // change showNolli status
        if (showNolli > 2) showNolli = 0;
      }
      if (bar.hover() == 10) {
        interf = !interf;                 // change interface visibility status
      }
      if (bar.hover() == 11) {
        exportscreen = !exportscreen;     // change exportScreen status
        visualize = true;                 // turn on visualization
        box = true;                       // turn on box view
        screenShot();                     // export a screenshot
        println("Screenshot generated");  // write in the console that file is successfully made
        exportscreen = false;             // reset exportScreen
      }
      if (bar.hover() == 12) {
        exporttxt = !exporttxt;           // change exportTXT status
        pause = true;                     // pause calculations
        visualize = true;                 // turn on visualization
        box = true;                       // turn on box view
        screenShot();                     // export a screenshot
        txt();                            // export a txt data file
        csv();                            // export a csv data file
        rhinocsv();                       // export a csv data file for importing in Rhino
        println("TXT-file/CSV-file/screenshot generated"); // write in the console that file is successfully made
        exporttxt = false;                // reset exportTXT
      }
      if (bar.hover() == 13) {
        exportdwg = !exportdwg;           // change exportDXG status
        pause = true;                     // pause calculations
        visualize = true;                 // turn on visualization
        box = true;                       // turn on box view
        screenShot();                     // export a screenshot
        dxf();                            // export a dxf file
        println("DXF-file/screenshot generated"); // write in the console that file is successfully made
        exportdwg = false;                // reset exportDWG
      }
      if (bar.hover() == 14) {
        exportobj = !exportobj;           // change exportDXG status
        pause = true;                     // pause calculations
        visualize = true;                 // turn on visualization
        box = true;                       // turn on box view
        screenShot();                     // export a screenshot
        obj();                            // export a dxf file
        println("OBJ-file/screenshot generated"); // write in the console that file is successfully made
        exportobj = false;                // reset exportDXG
      }
      if (bar.hover() == 15) { // TOP VIEW
        cam.setRotations(0, 0, 0);                       // reset camera rotations
        cam.setDistance(1.00f * sc * (cols + rows));      // reset camera zoom
        cam.lookAt(0.50f * sc * (cols+10), 0.50f * sc * (rows+10), 0.50f * sc * (lvls+10)); // reset camera centre
      }
      if (bar.hover() == 16) { // ISOMETRIC VIEW
        cam.setRotations(0, 0, 0);                       // reset camera rotations
        cam.setDistance(1.00f * sc * (cols + rows));      // reset camera zoom
        cam.lookAt(0.50f * sc * (cols+10), 0.50f * sc * (rows+10), 0.50f * sc * (lvls+10)); // reset camera centre
        cam.rotateZ(radians(-45));                       // rotate camera around z-axis for 45\u00b0
        cam.rotateX(radians(-45));                       // rotate camera around x-axis for 45\u00b0
      }
      if (bar.hover() == 17) { // FRONT VIEW
        cam.setRotations(0, 0, 0);                       // reset camera rotations
        cam.setDistance(1.00f * sc * (cols + rows));      // reset camera zoom
        cam.lookAt(0.50f * sc * (cols+10), 0.50f * sc * (rows+10), 0.50f * sc * (lvls+10)); // reset camera centre
        cam.rotateX(radians(-90));                       // rotate camera around x-axis for 90\u00b0
      }
      if (bar.hover() == 18) { // EDGE VIEW
        cam.setRotations(0, 0, 0);                       // reset camera rotations
        cam.setDistance(1.05f * sc * (cols + rows));      // reset camera zoom
        cam.lookAt(0.50f * sc * (cols+10), 0.50f * sc * (rows+10), 0.50f * sc * (lvls+10)); // reset camera centre
        cam.rotateX(radians(-90));                       // rotate camera around x-axis for 90\u00b0
        cam.rotateY(radians(-45));                       // rotate camera around y-axis for 45\u00b0
      }
    }
  }
  );
}
  public void settings() {  fullScreen(P3D, 1); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "EG_160614_Cellular_Automata_Designtool" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
