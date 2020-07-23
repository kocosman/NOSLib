
class VisTemp3D extends VisualEngine {

  int xAmount;
  int yAmount;
  float rot;
  LPF rotLPF;
  boolean fill;
  boolean strokeC;
  boolean strokeW;

  int rectSize;

  color strokeColor = color(255);

  public VisTemp3D(PApplet myApplet, String name) {
    super(myApplet, name);
  }

  public void init() {
    rot = 0;
    rotLPF = new LPF(0.0001);
  }

  public void initGUI() {
    addParameter("xAmount", "knob", 0, 0, 10, "xASD");
    addParameter("yAmount", "knob", 1, 0, 10, "yQWE");

    addParameter("rot", "fader", 0, 0, TWO_PI, "ROTATION");

    addParameter("fill", "toggle", 0, "FILL");
    addParameter("strokeC", "bang", 1, "color");
    addParameter("strokeW", "button", 2, "thick");

    addParameter("rectSize", "radioButton", 3, 3, "size");
  }


  public void drawArray(float[] in) {
    for (int i = 1; i < in.length; i++) {
      //stroke(255);
      //strokeWeight(1);
      //line(i-1, soundInput[i-1]*100, i, soundInput[i]*100);
      line(i-1, in[i-1]*100, i, in[i]*100);
    }
  }

  public void update() {
    /*
     soundInput      
     soundLevel      
     soundLevelLPF     
     soundSpectrum     
     soundSpectrumLPF   
     soundFocusSpectrum   
     soundFocusSpectrumLPF 
     soundFocusLevel   
     soundFocusLevelLPF   
     */
    stroke(255, 0, 0);
    strokeWeight(1);
    drawArray(soundFocusSpectrum);
    
    //println(colorPalette.length);
    for (int i = 0; i < colorPalette.length; i++) {
      pushMatrix();
      translate(i*30, 0, 0);
      stroke(255, guiStrokeAlpha);
      fill(colorPalette[i], guiFillAlpha);
      rect(0, 0, 20, 2000);
      popMatrix();
    }

    for (int i = 0; i < xAmount; i++) {
      for (int j = 0; j < yAmount; j++) {
        if (fill) {
          fill(150);
        } else {
          noFill();
        }

        if (strokeC) {
          strokeColor = color(random(255), random(255), random(255));
        }

        stroke(strokeColor);
        if (strokeW) {
          strokeWeight(10);
        } else {
          strokeWeight(2);
        }

        pushMatrix();
        translate(i*100, j*50);
        rotateX(rotLPF.lpf(rot));
        rect(0, 0, rectSize*10, rectSize*10);
        popMatrix();
      }
    }
  }

  public void start() {
  }

  public void exit() {
  }
}