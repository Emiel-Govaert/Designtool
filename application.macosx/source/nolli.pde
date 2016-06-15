// §§§§§§§§§§§§§§§§§ NOLLI EXCLUSSION MAP & SCENE §§§§§§§§§§§§§§§§§

PImage nolli;
PImage newNolli;

void defaultNolli() {
  nolli = loadImage("data/nolli-dokken.png");
  newNolli = loadImage("data/nolli-dokken.png");
  newNolli.resize(cols, rows);
  newNolli.filter(THRESHOLD, 0.5);
}

void loadNolli() {
  selectInput("Select your Nolli jpg map:", "fileSelected");
}

void fileSelected(File selection) {
  if (selection == null) {
    defaultNolli();
    println("Image loading cancel, restoring default image.");
  } else {
    nolli = loadImage(selection.getAbsolutePath());
    newNolli = loadImage(selection.getAbsolutePath());
    newNolli.resize(cols, rows);
    newNolli.filter(THRESHOLD, 0.5);
    println("Image loaded from " + selection.getAbsolutePath());
  }
}

// ------------------------------------------------------------------------------ make floor planes (in draw)
void scene() {
  stroke(0);
  strokeWeight(1);
  pushMatrix();
  translate(9*sc/2, 9*sc/2, 9*sc/2);
  if (useNolli == true) {
    if (showNolli == 0) {
      fill(#003CBE, 70);
      rect(0, 0, sc * (cols), sc * (rows));
    }
    if (showNolli == 1) image(nolli, 0, 0, sc * (cols), sc * (rows));
    if (showNolli == 2) image(newNolli, 0, 0, sc * (cols), sc * (rows));
  } else {
    fill(#003CBE, 70);
    rect(0, 0, sc * (cols), sc * (rows));
  }
  popMatrix();
}