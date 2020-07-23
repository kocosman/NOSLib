///*
//import ddf.minim.Minim;
// import ddf.minim.AudioInput;
 //*/

import com.nosvisuals.engine.SoundAnalyzer;

SoundAnalyzer s;

void setup() {
  size(1000, 600);
  s = new SoundAnalyzer(this);
  //s.analyze();
}

void draw() {
  background(0);
  //s.setGain(map(mouseX, 0, width, 0., 100.));
  //s.setDecay(map(mouseY, 0, height, 0., 1.));
  s.analyze();

stroke(255);
fill(255);

s.drawSoundInput(0, 0, width, height/3);
s.drawFFTRaw(0, height/3, width, height/3);
s.drawFFTSmooth(0, 2*height/3, width, height/3);

strokeWeight(3);
stroke(255,0,0);
line(0,s.soundLevel*5,width,s.soundLevel*5);
stroke(255,255,0);
line(0,s.soundLevelLPF*5,width,s.soundLevelLPF*5);

}