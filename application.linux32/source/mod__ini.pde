// §§§§§§§§§§§§§§§§§ MODULES INITIATION §§§§§§§§§§§§§§§§§

// ------------------------------------------------------------------------------ initiate the class modules (a nested for-loop goes through all modules) (in setup)
void iniModules() {
  for (int i = 0; i < cols+9; i++) {
    for (int j = 0; j < rows+9; j++) {
      for (int k = 0; k < lvls+9; k++) {
        Vec3D ptLoc = new Vec3D(i * sc, j * sc, k * sc); // build-up the ptLoc vector (in the location of the point)
        grid[i][j][k] = new modules(ptLoc, i, j, k);     // build-up modules for the whole grid (has a vector ptLoc and a location in the grid)
      }
    }
  }
}