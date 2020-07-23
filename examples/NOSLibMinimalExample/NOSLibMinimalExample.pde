import com.nosvisuals.engine.*;

NOSLib nos;
void setup() {
  size(1920, 1080, P3D);
  nos = new NOSLib(this);
  nos.printSketchInfo();
  background(0);

}

void draw() {
  surface.setTitle((int)frameRate + " fps");
}