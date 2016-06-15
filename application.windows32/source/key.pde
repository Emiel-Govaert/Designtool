// §§§§§§§§§§§§§§§§§ USER KEY INTERACTIONS §§§§§§§§§§§§§§§§§

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
void keyPressed() {
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
    cam.setDistance(1.00 * sc * (cols + rows)); // reset camera zoom
    cam.lookAt(0.50 * sc * (cols+10), 0.50 * sc * (rows+10), 0.50 * sc * (lvls+10)); // reset camera centre
  }
  if (key == 'é' || key == '2') { // ISOMETRIC VIEW
    cam.setRotations(0, 0, 0); // reset camera rotations
    cam.setDistance(1.00 * sc * (cols + rows)); // reset camera zoom
    cam.lookAt(0.50 * sc * (cols+10), 0.50 * sc * (rows+10), 0.50 * sc * (lvls+10)); // reset camera centre
    cam.rotateZ(radians(-45));                       // rotate camera around z-axis for 45°
    cam.rotateX(radians(-45));                       // rotate camera around x-axis for 45°
  }
  if (key == '"' || key == '3') { // FRONT VIEW
    cam.setRotations(0, 0, 0); // reset camera rotations
    cam.setDistance(1.00 * sc * (cols + rows)); // reset camera zoom
    cam.lookAt(0.50 * sc * (cols+10), 0.50 * sc * (rows+10), 0.50 * sc * (lvls+10)); // reset camera centre
    cam.rotateX(radians(-90));                       // rotate camera around x-axis for 90°
  }
  if (key == 39 || key == '4') { // EDGE VIEW
    cam.setRotations(0, 0, 0); // reset camera rotations
    cam.setDistance(1.05 * sc * (cols + rows)); // reset camera zoom
    cam.lookAt(0.50 * sc * (cols+10), 0.50 * sc * (rows+10), 0.50 * sc * (lvls+10)); // reset camera centre
    cam.rotateX(radians(-90));                       // rotate camera around x-axis for 90°
    cam.rotateY(radians(-45));                       // rotate camera around y-axis for 45°
  }
}