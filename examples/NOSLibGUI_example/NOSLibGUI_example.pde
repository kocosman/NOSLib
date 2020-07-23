import com.nosvisuals.engine.*;

ControlFrame cf;

void settings() {
  size(400, 400, P3D);
}

void setup() {
  cf = new ControlFrame(this);
  //frameRate(999);
}

void draw() {
  surface.setTitle(int(frameRate) + "fps");
}