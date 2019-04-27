import java.util.ArrayList;
import java.util.List;

// Sinusoidal Additive Synthesis of harmonic sound/ Fourier Synthesis
class AdditiveSynth {
  float base_frequency; // base frequency
  int n_harmonics; // number of harmonics
  List<Float> harmonics = new ArrayList<Float>(); // set of harmonics

  AdditiveSynth() {
    base_frequency = 1.0;
    n_harmonics = 20;

    // Initialize the harmonics
    for (int i = 0; i < n_harmonics; i++) harmonics.add(0.0);
  }
  // Draw the string
  void displayString(float damping, int n_points, float xscale, float yoffs, float yscale, float time) {
    float x1 = 0.0; // x-coordinate of the first point of the line
    float y1 = height/2; // y-coordinate of the first point of the line
    stroke(0); // set the color of stroke as black
    for (int x = 1; x <= n_points; x++) {
      float xp = x*xscale;
      float y=yoffs;
      for (int i = 1; i < n_harmonics; i++) {
        float damp = exp( - damping * time * i); // for appy1ing damping effect
        // ref: modal anay1sis of tight string : Daniel S. Stutts
        y += damp * harmonics.get(i) * yscale * sin(x * i * base_frequency * PI / n_points) * cos( i * time * PI); // eqn is simplified
      }
      line(x1, y1, xp, y);
      x1 = xp;
      y1 = y;
    }
  }
  // set the value of a particular harmonic into harmonic set
  void setHarmoics(int loc, float value) {
    harmonics.set(loc, value);
  }
  void resetHarmonics() {
    for (int i = 0; i < harmonics.size(); i++) {
      harmonics.set(i, 0.0);
    }
  }
  List<Float> getHarmonics() {
    return harmonics;
  }
  int getNumHarmonics() {
    return n_harmonics;
  }
}
