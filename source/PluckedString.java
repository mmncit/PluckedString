import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.ArrayList; 
import java.util.List; 
import oscP5.*; 
import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class PluckedString extends PApplet {

/* Vibration and Sound rendering of plucked string
 *  Md Mamunur Rashid
 */
Screen sc;
AdditiveSynth fourierAdditiveSynth;
FiniteDiff finitediffmod;
SoundControl openSoundCtrl;
float time;
int harmonic_number;
boolean showfourierSynth;
float damping = 0.0f;

public void setup() {

  
  sc = new Screen();
  fourierAdditiveSynth = new AdditiveSynth();
  finitediffmod = new FiniteDiff();
  openSoundCtrl = new SoundControl();

  openSoundCtrl.setupOSC();
  openSoundCtrl.oscOrder(sc.getNumPoints());
  time = 0.0f;
  harmonic_number = 0;
  showfourierSynth = false;
}
public void draw() {
  background(255); // set the background as white
  sc.displayInstruction(showfourierSynth); // display instruction for 2 sound synth model
  time += 0.02f; // update time
  
  if (showfourierSynth) {
    sc.setDisplayText("Sinusidal Additive Synthesis");
    fourierAdditiveSynth.setHarmoics(harmonic_number, 1.0f/ harmonic_number); // set current harmonics 1/ harmonic number
    fourierAdditiveSynth.displayString(0.009f + damping, sc.getNumPoints(), sc.getNumX(), sc.getOffsetY(), sc.getNumY(), time);
    openSoundCtrl.updateHarmsOSC(fourierAdditiveSynth.getHarmonics());
  } else {
    sc.setDisplayText("Finite Difference Model");

    finitediffmod.drawString(0.999f + damping, sc.getOffsetY(), sc.getNumX(), openSoundCtrl);
  }
}
public void keyPressed() {
  if (keyCode == UP) {
    fourierAdditiveSynth.resetHarmonics();
    harmonic_number += 1;
  } else if (keyCode == DOWN) {
    fourierAdditiveSynth.resetHarmonics();
    harmonic_number -= 1;
  } else if (key == '+') {
    harmonic_number += 1;
  } else if (key == 'd') {
    damping += 0.001f; // don't use that! buggy
  } else if (key == 'c') {
    clear();
  } else if (key == 'f') {
    if (showfourierSynth == true)  showfourierSynth = false;
    else showfourierSynth = true;
  }
}
public void mousePressed() {
  if(!showfourierSynth){
  int  clickPos = width / mouseX;
  if (clickPos >= 2) finitediffmod.pluck((int)sc.getNumPoints()/clickPos);
  }
}
public void clear() {
  fourierAdditiveSynth.resetHarmonics();
  damping = 0.0f;
  time = 0.0f;
  harmonic_number = 0;
  finitediffmod.reset();
}



// Sinusoidal Additive Synthesis of harmonic sound/ Fourier Synthesis
class AdditiveSynth {
  float base_frequency; // base frequency
  int n_harmonics; // number of harmonics
  List<Float> harmonics = new ArrayList<Float>(); // set of harmonics

  AdditiveSynth() {
    base_frequency = 1.0f;
    n_harmonics = 20;

    // Initialize the harmonics
    for (int i = 0; i < n_harmonics; i++) harmonics.add(0.0f);
  }
  // Draw the string
  public void displayString(float damping, int n_points, float xscale, float yoffs, float yscale, float time) {
    float x1 = 0.0f; // x-coordinate of the first point of the line
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
  public void setHarmoics(int loc, float value) {
    harmonics.set(loc, value);
  }
  public void resetHarmonics() {
    for (int i = 0; i < harmonics.size(); i++) {
      harmonics.set(i, 0.0f);
    }
  }
  public List<Float> getHarmonics() {
    return harmonics;
  }
  public int getNumHarmonics() {
    return n_harmonics;
  }
}
// Finite Difference Model of string (or any 1D wave)

class FiniteDiff {

  int n_points;
  float string[], string_d[];
  float acc, diff_left, diff_right;

  FiniteDiff() {
    n_points = 200;
    string = new float[n_points];
    string_d = new float[n_points];
  }

  public void setNumberOfPoints(int n_points) {
    this.n_points = n_points;
  }

  public void pluck(int pos) {
    // create the plucked tringular shape
    float diff_up = 0.5f/pos;
    float diff_down = 0.5f/(n_points-pos-1);
    float val = 0.0f;
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
  public void reset() {
    for (int x = 0; x < n_points; x++) {
      string[x] = 0.0f;
      string_d[x] = 0.0f;
    }
  }
  public void drawString(float damp, float yoffs, float xscale, SoundControl openSoundCtrl) {
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
    float x1 = 0.0f;
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

class Screen {

  int n_points = 200;
  float xscale, yoffs, yscale;

  Screen() {
    xscale = width/(n_points * 1.0f);
    yoffs = height/2;
    yscale = 0.5f*yoffs;
  }

  public float getNumX() {
    return xscale;
  }
  public float getNumY() {
    return yscale;
  };
  public float getOffsetY() {
    return yoffs;
  }
  public int getNumPoints() {
    return n_points;
  }

  public void setDisplayText(String msg) {
    fill(random(100, 255), 0, 255);
    textFont(createFont("Arial", 12), 32);
    text(msg, width * .08f, height * 0.11f);
  }
  public void displayInstruction(boolean showfourierSynth){
    fill(0);
    textFont(createFont("Arial", 12), 20);
    text("Instructions:", width * .08f, height * 0.2f);
    if (showfourierSynth){
    text("Press up/down arrow to increase/decrease harmonics", width * .08f, height * 0.23f);
    text("Press '+' to add harmonics", width * .08f, height * 0.26f);
    } else{
    text("Left-mouse click to pluck", width * .08f, height * 0.25f);
    }
    text("Prese 'c' to reset screen", width * .08f, height * 0.29f);
    text("Prese 'f' to switch models", width * .08f, height * 0.32f);
  }
}


 
// ref: http://www.sojamo.de/libraries/oscP5
class SoundControl {
  OscP5 oscP5;
  NetAddress myRemoteLocation;


  public void setupOSC() {
    /* start oscP5, listening for incoming messages at port 12000 */
    oscP5 = new OscP5(this, 12000);
    /* myRemoteLocation is a NetAddress. a NetAddress takes 2 parameters,
     * an ip address and a port number. myRemoteLocation is used as parameter in
     * oscP5.send() when sending osc packets to another computer, device, 
     * application. usage see below. for testing purposes the listening port
     * and the port of the remote location address are the same, hence you will
     * send messages back to this sketch.
     */
    myRemoteLocation = new NetAddress("127.0.0.1", 12000);
  }

  public void oscOrder(int order) {
    OscMessage myMessage = new OscMessage("/Order");
    myMessage.add(order); /* add an int to the osc message */

    oscP5.send(myMessage, myRemoteLocation);
  }

  public void dispose() {
    OscMessage myMessage = new OscMessage("/Order");
    myMessage.add(0); /* add an int to the osc message */

    oscP5.send(myMessage, myRemoteLocation);
  }

  public void updateHarmsOSC(List<Float> harmonics) {
    OscMessage myMessage = new OscMessage("/Harms");
    for (int i = 1; i < harmonics.size(); i++) {
      myMessage.add((float)harmonics.get(i)); /* add float to the osc message */
    }
    oscP5.send(myMessage, myRemoteLocation);
  }
  public void sendMessage(OscMessage myMessage) {
    oscP5.send(myMessage, myRemoteLocation);
  }
}

  public void settings() {  size(1536, 864); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PluckedString" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
