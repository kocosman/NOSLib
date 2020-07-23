
public class Rectangle {
  float x;
  float y;
  float width;
  float height;
  boolean strokeEnable = true;
  color strokeColor = color(255);
  boolean fillEnable = true;
  color fillColor = color(255, 0, 255, 127);
  PImage backgroundImage;
  boolean imageEnable = false;

  Rectangle(float _x, float _y, float _width, float _height) {
    x = _x;
    y = _y;
    width = _width;
    height = _height;
  }

  public boolean contains(float _x, float _y) {
    if ((_x >= x) && (_y >= y) && (_x <= x+width) && (_y <= y+height)) {
      return true;
    } else {
      return false;
    }
  }

  public void render() {
    if (imageEnable) {
      image(backgroundImage, x, y, width, height);
    } else {
      rectMode(CORNER);
      strokeWeight(width/200);
      if (strokeEnable) {
        stroke(strokeColor);
      } else {
        noStroke();
      }
      if (fillEnable) {
        fill(fillColor);
      } else {
        noFill();
      }
      rect(x, y, width, height);
    }
  }
}

public class AndroidRange {
  Rectangle controllerRect;
  Rectangle sliderRect;
  boolean controllerStrokeEnable = false;
  boolean controllerFillEnable = true;
  boolean sliderStrokeEnable = false;
  boolean sliderFillEnable = true;

  color controllerStrokeColor = color(255, 100);
  color controllerFillColor = color(255, 100);
  color sliderStrokeColor = color(255, 100);
  color sliderFillColor = color(255, 100);

  color sliderFillColorActive = color(255, 255);

  float rangeOffset;
  float rangeSize;

  float sliderOffsetPre;
  float sliderWidthPre;
  boolean isChanged = false;

  AndroidRange(float _x, float _y, float _w, float _h) {
    controllerRect = new Rectangle(_x, _y, _w, _h);
    sliderRect = new Rectangle(_x+_w*0.1, _y, _w-_w*0.2, _h);
    calculateOffsetAndSize();
  }

  void updateOffsetAndSizeNormalized(float _offset, float _size) {
    sliderRect.x = map(_offset, 0.f, 1.f, controllerRect.x, controllerRect.x+controllerRect.width-sliderRect.width); 
    sliderRect.width = map(_size, 0.f, 1.f, 0., controllerRect.width);

    sliderRect.x = constrain(sliderRect.x, controllerRect.x, controllerRect.x+controllerRect.width-sliderRect.width);
    sliderRect.width = constrain(sliderRect.width, 5, controllerRect.width);
  }

  void calculateOffsetAndSize() {
    rangeOffset = map(sliderRect.x, controllerRect.x, controllerRect.x+controllerRect.width, 0.f, 1.f);
    rangeSize = map(sliderRect.width, 0, controllerRect.width, 0.f, 1.f);
  }

  boolean contains(float _x, float _y) {
    if (controllerRect.contains(_x, _y)) {
      return true;
    } else {
      return false;
    }
  }

  void updateColors() {
    controllerRect.strokeEnable = controllerStrokeEnable;
    sliderRect.strokeEnable = sliderStrokeEnable;
    controllerRect.strokeColor = controllerStrokeColor;
    sliderRect.strokeColor = sliderStrokeColor;
    controllerRect.fillEnable = controllerFillEnable;
    controllerRect.fillColor = controllerFillColor;
    sliderRect.fillEnable = sliderFillEnable;
  }

  void updateOffset(float _x, float _y, float _d) {
    if (sliderRect.contains(_x, _y)) {
      sliderRect.fillColor = sliderFillColorActive;
      sliderRect.x = constrain(sliderRect.x+_d, controllerRect.x, controllerRect.x+controllerRect.width-sliderRect.width);
    } else {
      sliderRect.fillColor = sliderFillColor;
    }
  }

  void updateSize(float _x, float _y, float _d) {
    sliderRect.width = constrain(sliderRect.width+_d*2, 5, controllerRect.width);
    //sliderRect.x = sliderRect.x-_d;
    sliderRect.x = constrain(sliderRect.x-_d, controllerRect.x, controllerRect.x+controllerRect.width-sliderRect.width);

    if (sliderRect.contains(_x, _y)) {
      sliderRect.fillColor = sliderFillColorActive;
      //sliderRect.width = constrain(sliderRect.width+_d*2, 5, controllerRect.width);
      //sliderRect.x = sliderRect.x-_d;
    } else {
      sliderRect.fillColor = sliderFillColor;
    }
  }


  void update() {
    updateColors();
    float deltaMouseX = mouseX-pmouseX;
    updateOffset(mouseX, mouseY, deltaMouseX);
    calculateOffsetAndSize();

    if (sliderRect.x != sliderOffsetPre) {
      isChanged = true;
    } 

    if (sliderRect.width != sliderWidthPre) {
      isChanged = true;
    } 

    if ((sliderRect.x == sliderOffsetPre)&&(sliderRect.width == sliderWidthPre)) {
      isChanged = false;
    }

    sliderWidthPre = sliderRect.width;
    sliderOffsetPre = sliderRect.x;
  }

  void render() {
    update();
    controllerRect.render();
    sliderRect.render();
  }
}