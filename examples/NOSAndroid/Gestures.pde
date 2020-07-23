import android.view.MotionEvent;
KetaiGesture gesture;

boolean mouseReleased = false;

void initializeGestures() {
  gesture = new KetaiGesture(this);
}

void onDoubleTap(float x, float y)
{
  //things.add(new Thing("DOUBLE", x, y));
}

void onTap(float x, float y)
{
  //things.add(new Thing("SINGLE", x, y));
}

void onLongPress(float x, float y)
{
  //things.add(new Thing("LONG", x, y));
}

//the coordinates of the start of the gesture, 
//     end of gesture and velocity in pixels/sec
void onFlick( float x, float y, float px, float py, float v)
{
  //things.add(new Thing("FLICK:"+v, x, y, px, py));
}

void onPinch(float x, float y, float d)
{
  if (colorRange.contains(x, y)) {
    colorRange.updateSize(x, y, d);
  }
  
  if (soundRange.contains(x, y)) {
    soundRange.updateSize(x, y, d);
  }

}

void onRotate(float x, float y, float ang)
{
  //Angle += ang;
}

//these still work if we forward MotionEvents below
void mouseDragged()
{
}

void mouseReleased(){
  mouseReleased = true;
}

//void mousePressed()
//{
//}


public boolean surfaceTouchEvent(MotionEvent event) {

  //call to keep mouseX, mouseY, etc updated
  super.surfaceTouchEvent(event);

  //forward event to class for processing
  return gesture.surfaceTouchEvent(event);
}