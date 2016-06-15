// §§§§§§§§§§§§§§§§§ LIVE DATA GRAPH §§§§§§§§§§§§§§§§§

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

void calcGraph() {
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

void dispGraph() {
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