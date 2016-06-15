

// Design tool for computer generated volumetric studies using rule-sets based on the principles of a cellular automaton. 
// Thesis code is written for a master thesis about the Applicability of Cellular Automata in the Architectural Design Process at Ghent University.
// Submitted in fulfilment of the requirements for the degree of "Master of Science in Engineering: Architecture"
// Promoted by prof. Ruben Verstraeten and Francis wyffels. Mentored by Sebastiaan Leenknegt and Tiemen Strobbe.
// Written by Emiel Govaert, published on 2016/06/01. Creative Commons license, some rights reserved.

// Terms of use: distributed under a CreativeCommons-Attribution-NonCommercial-ShareAlike 4.0 International licence (CC BY-NC-SA 4.0).
// This means you are free to share, adapt or build on this material, but these restrictions apply:
// Attribution — You must give appropriate credit, provide a link to the license, and indicate if changes were made. You may do so in any reasonable manner, but not in any way that suggests the licensor endorses you or your use.
// NonCommercial — You may not use the material for commercial purposes.
// ShareAlike — If you remix, transform, or build upon the material, you must distribute your contributions under the same license as the original.
// No additional restrictions — You may not apply legal terms or technological measures that legally restrict others from doing anything the license permits.

// Special thanks to processing foundation, Ben Fry, Casey Reas and Daniel Shiffman and all open-source external libraries (PeasyCam, ToxiClibs, ControlP5, NervousSystem).
// Coded in Processing v3.1.1, using the PeasyCam v201, ToxiClibs v0020, ControlP5 v2.2.5 and NervousSystem v0.2.5 external libraries.


// ------------------------------------------------------------------------------ import external libraries
import peasy.*;                          // 3D camera library (mouse interaction: left click = orbit, control left click = pan, right click = zoom, double click = reset to top view)
import toxi.geom.*;                      // vector library
import controlP5.*;                      // slider library
import processing.dxf.*;                 // DXF export library
import nervoussystem.obj.*;              // OBJ export library

// ------------------------------------------------------------------------------ global start parameters
static int cols =   95;                  // number of columns in the grid
static int rows =  177;                  // number of rows in the grid
static int lvls =    3;                  // number of floors in the grid
static int sc =     20;                  // overall grid scale 3D model (used for navigation speed & cut-off distance)
// 95 177 3 20 nolli dokken

float startLife = 0.10;                  // desired % alive (= start % alive)
float startSatisfaction = 0.50;          // start score level that determines if cell stays alive

float weightNeighbourhood = 0.30;        // weight neighbourhood (group)
float weightNeighbours    = 0.20;        // weight neighbours (connection)
float weightStability     = 0.50;        // weight stability
float weightGreen         = 0.40;        // weight green
float weightLight         = 0.40;        // weight light
float weightView          = 0.20;        // weight view
float weightYYY           = 0.10;        // weight YYY (random)
// 0.30 0.20 0.50 0.40 0.40 0.20 1.00 = example combination

// ------------------------------------------------------------------------------ static code
void setup() {
  surface.setTitle("Cellular Automata Designtool"); // window mode title
  // size(1200, 900, P3D);               // window mode
  fullScreen(P3D, 1);                    // fullscreen mode
  frameRate(500);                        // set maximum visualization and calculation speed (default 60 CPM max)
  peasyCamera();                         // activate camera
  dataFonts();                           // load fonts
  defaultNolli();                        // load Nolli map
  sliders();                             // load sliders
  iniModules();                          // initiate the class modules
}

// ------------------------------------------------------------------------------ dynamic code
void draw() {
  background(0);                         // make background colour black and clean previous screen
  if (interf)    scene();                // if the interface is on: draw floor/Nolli map
  if (!pause)    resetData();            // if not paused: reset text/data/colour values
  if (!pause)    calcModules();          // if not paused: run calculations for current generation
  if (visualize) dispModules();          // if the visualization is on: display all modules and the constraints
  if (!pause)    calcFutModules();       // if not paused: run calculations for next generation
  if (!pause)    calcData();             // if not paused: calculate text/data values
  if (interf)    dispData();             // if the interface is on: display text/data values
}