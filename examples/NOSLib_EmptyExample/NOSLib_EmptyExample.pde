import com.nosvisuals.engine.*;

NOSLib nos;
void settings() {
  try {
    fullScreen(OPENGL, 2);
    //size(1920, 1080, P3D);
    String osName = System.getProperty("os.name").toLowerCase();
    println(osName);
    if (osName.indexOf("mac")!=-1) {
      PJOGL.profile = 1;
    }
  }
  catch(Exception e) {
    println("Settings error: " + e);
  }
  //surface.setLocation(100, 100);
}
void setup() {

  nos = new NOSLib(this);
  nos.guiFrame.previewEnable = !false;
  nos.printDebug = true;
  nos.printMidi = false;
  nos.printOsc = !false;
  nos.printValueChange = !false;

  nos.addVisualEngine(new VisTemp3D(this, "VisTemp3D"));

  frameRate(60);
  smooth();
}

void draw() {
  try {
    nos.draw();
  } 
  catch(Exception e) {
    println(e);
  }

  surface.setTitle((int)frameRate + " fps");
  nos.guiFrame.canvasFPS = (int)frameRate;
} 