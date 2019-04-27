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
float damping = 0.0;

void setup() {

  size(1536, 864);
  sc = new Screen();
  fourierAdditiveSynth = new AdditiveSynth();
  finitediffmod = new FiniteDiff();
  openSoundCtrl = new SoundControl();

  openSoundCtrl.setupOSC();
  openSoundCtrl.oscOrder(sc.getNumPoints());
  time = 0.0;
  harmonic_number = 0;
  showfourierSynth = false;
}
void draw() {
  background(255); // set the background as white
  sc.displayInstruction(showfourierSynth); // display instruction for 2 sound synth model
  time += 0.02; // update time
  
  if (showfourierSynth) {
    sc.setDisplayText("Sinusidal Additive Synthesis");
    fourierAdditiveSynth.setHarmoics(harmonic_number, 1.0/ harmonic_number); // set current harmonics 1/ harmonic number
    fourierAdditiveSynth.displayString(0.009 + damping, sc.getNumPoints(), sc.getNumX(), sc.getOffsetY(), sc.getNumY(), time);
    openSoundCtrl.updateHarmsOSC(fourierAdditiveSynth.getHarmonics());
  } else {
    sc.setDisplayText("Finite Difference Model");

    finitediffmod.drawString(0.999 + damping, sc.getOffsetY(), sc.getNumX(), openSoundCtrl);
  }
}
void keyPressed() {
  if (keyCode == UP) {
    fourierAdditiveSynth.resetHarmonics();
    harmonic_number += 1;
  } else if (keyCode == DOWN) {
    fourierAdditiveSynth.resetHarmonics();
    harmonic_number -= 1;
  } else if (key == '+') {
    harmonic_number += 1;
  } else if (key == 'd') {
    damping += 0.001; // don't use that! buggy
  } else if (key == 'c') {
    clear();
  } else if (key == 'f') {
    if (showfourierSynth == true)  showfourierSynth = false;
    else showfourierSynth = true;
  }
}
void mousePressed() {
  if(!showfourierSynth){
  int  clickPos = width / mouseX;
  if (clickPos >= 2) finitediffmod.pluck((int)sc.getNumPoints()/clickPos);
  }
}
void clear() {
  fourierAdditiveSynth.resetHarmonics();
  damping = 0.0;
  time = 0.0;
  harmonic_number = 0;
  finitediffmod.reset();
}
