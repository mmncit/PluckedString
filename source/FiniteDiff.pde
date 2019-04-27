// Finite Difference Model of string (or any 1D wave)
import oscP5.*;
class FiniteDiff {

  int n_points;
  float string[], string_d[];
  float acc, diff_left, diff_right;

  FiniteDiff() {
    n_points = 200;
    string = new float[n_points];
    string_d = new float[n_points];
  }

  void setNumberOfPoints(int n_points) {
    this.n_points = n_points;
  }

  void pluck(int pos) {
    // create the plucked tringular shape
    float diff_up = 0.5/pos;
    float diff_down = 0.5/(n_points-pos-1);
    float val = 0.0;
    for (int x=0; x < pos; x++) {
      string[x] += val;
      val += diff_up;
    }
    val -= diff_down;
    for (int x=pos; x < n_points-1; x++) {
      string[x] += val;
      val -= diff_down;
    }
  }
  void reset() {
    for (int x = 0; x < n_points; x++) {
      string[x] = 0.0;
      string_d[x] = 0.0;
    }
  }
  void drawString(float damp, float yoffs, float xscale, SoundControl openSoundCtrl) {
    // calculate the central difference
    // ref: http://hplgit.github.io/INF5620/doc/notes/wave-sphinx/main_wave.html
    for (int x=1; x < n_points-1; x++) {
      diff_left = string[x] - string[x-1]; // change of slope from left
      diff_right = string[x+1] - string[x]; // change of slope from right
      acc = (diff_right - diff_left);
      string_d[x] += (acc / 2);
    }
    //  update whole string
    for (int x=0; x < n_points; x++) {
      string[x] = damp*string[x] + string_d[x];
    }
    // draw string and send OSC  
    stroke(0);
    float x1 = 0.0;
    float y1 = height/2;
    float y;
    OscMessage myMessage = new OscMessage("/scanSynth"); // create a new meesage
    for (int x=0; x < n_points; x++) {
      y = yoffs - yoffs*string[x];
      float xp = x*xscale;
      line(x1, y1, xp, y);
      myMessage.add(string[x]); /* add set of string x values to the osc message */
      x1 = xp;
      y1 = y;
    }
    openSoundCtrl.sendMessage(myMessage);
  }
}
