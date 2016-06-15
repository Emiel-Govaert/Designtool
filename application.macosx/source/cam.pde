// §§§§§§§§§§§§§§§§§§ CAMERA SETUP §§§§§§§§§§§§§§§§§

// ------------------------------------------------------------------------------ declare camera
PeasyCam cam; // initialize camera

// ------------------------------------------------------------------------------ load camera (in setup) (+start position)
void peasyCamera() {
  cam = new PeasyCam(this, 0.50 * sc * (cols+10), 0.50 * sc * (rows+10), 0.50 * sc * (lvls+10), 1.00 * sc * (cols + rows)); // this, lookAt X, lookAt Y, lookAt Z, current zoom distance
  cam.setMinimumDistance(0.05 * sc * (cols+rows)); // min zoom distance
  cam.setMaximumDistance(4.00 * sc * (cols+rows)); // max zoom distance
  cam.setWheelScale(0.10);                         // mouse wheel zoom sensitivity
  cam.rotateZ(radians(-45));                       // rotate camera around z-axis for 45°
  cam.rotateX(radians(-45));                       // rotate camera around x-axis for 45°
}