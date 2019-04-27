class Screen {

  int n_points = 200;
  float xscale, yoffs, yscale;

  Screen() {
    xscale = width/(n_points * 1.0);
    yoffs = height/2;
    yscale = 0.5*yoffs;
  }

  float getNumX() {
    return xscale;
  }
  float getNumY() {
    return yscale;
  };
  float getOffsetY() {
    return yoffs;
  }
  int getNumPoints() {
    return n_points;
  }

  void setDisplayText(String msg) {
    fill(random(100, 255), 0, 255);
    textFont(createFont("Arial", 12), 32);
    text(msg, width * .08, height * 0.11);
  }
  void displayInstruction(boolean showfourierSynth){
    fill(0);
    textFont(createFont("Arial", 12), 20);
    text("Instructions:", width * .08, height * 0.2);
    if (showfourierSynth){
    text("Press up/down arrow to increase/decrease harmonics", width * .08, height * 0.23);
    text("Press '+' to add harmonics", width * .08, height * 0.26);
    } else{
    text("Left-mouse click to pluck", width * .08, height * 0.25);
    }
    text("Prese 'c' to reset screen", width * .08, height * 0.29);
    text("Prese 'f' to switch models", width * .08, height * 0.32);
  }
}
