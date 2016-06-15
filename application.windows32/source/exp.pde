// §§§§§§§§§§§§§§§§§ EXPORT FUNCTIONS §§§§§§§§§§§§§§§§§

// ------------------------------------------------------------------------------ export variables
String filename="EG_Cellular_Automata_Designtool";
PrintWriter txtfile;
Table csvfile;
Table rhinocsvfile;

// ------------------------------------------------------------------------------ create a txt file with info about the current generation
void txt() {
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
  txtfile.println("Mean master score:          " + nfc((scoreMasterTotal       /(0.01*numCellAlive)), 2) + " %");
  txtfile.println("Mean neighbourhood score:   " + nfc((scoreNeighbourhoodTotal/(0.01*numCellAlive)), 2) + " %");
  txtfile.println("Mean neighbours score:      " + nfc((scoreNeighboursTotal   /(0.01*numCellAlive)), 2) + " %");
  txtfile.println("Mean stability score:       " + nfc((scoreStabilityTotal    /(0.01*numCellAlive)), 2) + " %");
  txtfile.println("Mean green score:           " + nfc((scoreGreenTotal        /(0.01*numCellAlive)), 2) + " %");
  txtfile.println("Mean light score:           " + nfc((scoreLightTotal        /(0.01*numCellAlive)), 2) + " %");
  txtfile.println("Mean view score:            " + nfc((scoreViewTotal         /(0.01*numCellAlive)), 2) + " %");
  txtfile.println("Mean YYY score:             " + nfc((scoreYYYTotal          /(0.01*numCellAlive)), 2) + " %");
  txtfile.flush(); // write the remaining data to the file
  txtfile.close(); // finishes the file
}

// ------------------------------------------------------------------------------ create a csv (comma-separated values) file with info about the current generation
void csv() {
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
void rhinocsv() {
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
void dxf() {
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
void obj() {
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
void screenShot() {
  saveFrame("export/" + year() + nf(month(), 2) + nf(day(), 2) + "_" + str(generation) + "/" + filename + "_" + year() + nf(month(), 2) + nf(day(), 2) + "-"  + nf(hour(), 2) + nf(minute(), 2) + nf(second(), 2) + "_" + str(generation) + ".jpg");
}