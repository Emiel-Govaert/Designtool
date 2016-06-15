// §§§§§§§§§§§§§§§§§ USER SLIDER INTERACTIONS §§§§§§§§§§§§§§§§§

// ------------------------------------------------------------------------------ declare
ControlP5 cp5; // sliders

// ------------------------------------------------------------------------------ load sliders (in setup)
void sliders() {
  cp5 = new ControlP5(this);
  ButtonBar topBar = cp5.addButtonBar("topBar")
    .setPosition(0, 0)
    .setSize(width, 20)
    .setColorForeground(200) 
    .addItems(split("exit, reset, pause, visualize, constraints, box, scorecolor, load nolli, use nolli, show nolli, interface, export screen, export txt/data, export dwg, export obj, top view, isometric view, front view, edge view", ", "));
  cp5.addSlider("lifeAbs")    
    .setRange(0, numCell/3)
    .setDecimalPrecision(0)
    .setNumberOfTickMarks(int((numCell/3)+1))
    .showTickMarks(false) 
    .setPosition(width - interfaceWidth + 50, 9 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightNeighbourhood")
    .setRange(0.00, 1.00)
    .setPosition(width - interfaceWidth + 50, 12 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightNeighbours")   
    .setRange(0.00, 1.00)
    .setPosition(width - interfaceWidth + 50, 14 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightStability")    
    .setRange(0.00, 1.00)
    .setPosition(width - interfaceWidth + 50, 16 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightGreen")   
    .setRange(0.00, 1.00)
    .setPosition(width - interfaceWidth + 50, 18 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightLight")   
    .setRange(0.00, 1.00)
    .setPosition(width - interfaceWidth + 50, 20 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightView")   
    .setRange(0.00, 1.00)
    .setPosition(width - interfaceWidth + 50, 22 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("weightYYY")   
    .setRange(0.00, 1.00)
    .setPosition(width - interfaceWidth + 50, 24 * 20)
    .setSize(interfaceWidth - 130, 12)
    .setLabel("");
  cp5.addSlider("Dtreshold")   
    .setRange(0.00, 1.00)
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
        cam.setDistance(1.00 * sc * (cols + rows));      // reset camera zoom
        cam.lookAt(0.50 * sc * (cols+10), 0.50 * sc * (rows+10), 0.50 * sc * (lvls+10)); // reset camera centre
      }
      if (bar.hover() == 16) { // ISOMETRIC VIEW
        cam.setRotations(0, 0, 0);                       // reset camera rotations
        cam.setDistance(1.00 * sc * (cols + rows));      // reset camera zoom
        cam.lookAt(0.50 * sc * (cols+10), 0.50 * sc * (rows+10), 0.50 * sc * (lvls+10)); // reset camera centre
        cam.rotateZ(radians(-45));                       // rotate camera around z-axis for 45°
        cam.rotateX(radians(-45));                       // rotate camera around x-axis for 45°
      }
      if (bar.hover() == 17) { // FRONT VIEW
        cam.setRotations(0, 0, 0);                       // reset camera rotations
        cam.setDistance(1.00 * sc * (cols + rows));      // reset camera zoom
        cam.lookAt(0.50 * sc * (cols+10), 0.50 * sc * (rows+10), 0.50 * sc * (lvls+10)); // reset camera centre
        cam.rotateX(radians(-90));                       // rotate camera around x-axis for 90°
      }
      if (bar.hover() == 18) { // EDGE VIEW
        cam.setRotations(0, 0, 0);                       // reset camera rotations
        cam.setDistance(1.05 * sc * (cols + rows));      // reset camera zoom
        cam.lookAt(0.50 * sc * (cols+10), 0.50 * sc * (rows+10), 0.50 * sc * (lvls+10)); // reset camera centre
        cam.rotateX(radians(-90));                       // rotate camera around x-axis for 90°
        cam.rotateY(radians(-45));                       // rotate camera around y-axis for 45°
      }
    }
  }
  );
}