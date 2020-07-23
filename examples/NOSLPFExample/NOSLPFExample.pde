import com.nosvisuals.engine.*;

NOSLib nos;
LPFArray dataLPF;

float[] data;

void setup() {
  size(1920, 1080, OPENGL);
  nos = new NOSLib(this);
  nos.printSketchInfo();
  background(0);

  dataLPF = new LPFArray(width);
  data = new float[width];
  for (int i = 0; i < width; i++) {
    data[i] = random(100)+height/2;
  }
}

void draw() {
  surface.setTitle((int)frameRate + " fps");
  background(0);

  for (int i = 0; i < width; i++) {
    data[i] = random(100)+height/2;
  }

  dataLPF.lpfArray(data);
  dataLPF.decay = map(mouseX, 0, width, 0, 1);

  for (int i = 0; i < width-1; i++) {
    strokeWeight(1);
    stroke(255, 0, 0);
    line(i, data[i], i+1, data[i+1]);

    stroke(0, 255, 0);
    line(i, dataLPF.lpfOut[i], i+1, dataLPF.lpfOut[i+1]);
  }
}

void mousePressed() {
  nos.test();
}