

class VisualEngineRemote {

  String name;
  PImage thumbnail;
  int index;

  ArrayList<VisualEngineParameterRemote> vep;

  VisualEngineRemote(String _name, int _index) {
    name = _name;
    _index = index;
    thumbnail = loadImage(name+".jpg");
    vep = new ArrayList<VisualEngineParameterRemote>();
  }

  void addParameter(String _name, String _type, int _index, float _min, float _max, String _label) {
    vep.add(new VisualEngineParameterRemote( _name, _type, _index, _min, _max, _label));
  }


  void update() {
    for (int i = 0; i < vep.size(); i++) {
      vep.get(i).updateValue();
    }
  }

  void show(boolean _show) {
    if (_show) {
      for (int i = 0; i < vep.size(); i++) {
        VisualEngineParameterRemote v = vep.get(i);
        v.show(true);
      }
    } else {
      for (int i = 0; i < vep.size(); i++) {
        VisualEngineParameterRemote v = vep.get(i);
        v.show(false);
      }
    }
  }
}


class VisualEngineParameterRemote {

  String name;
  String type;
  int index;
  float min;
  float max;
  String label;

  float value;
  float valuePre;

  float[] radioValues;
  float[] radioValuesPre;


  boolean changed = false;

  boolean mPressed = false;
  boolean mPressedPre = false;

  ArrayList<controlP5.Controller> controllers;

  VisualEngineParameterRemote(String _name, String _type, int _index, float _min, float _max, String _label) {
    name = _name;
    type = _type;
    index = _index;
    min = _min;
    max = _max;
    label = _label;

    controllers = new ArrayList<controlP5.Controller>(); 

    if (type.equals("knob")) {
      cp5.addKnob(name)
        .setPosition(index*(width/8)+tabMargin*2, tabHeight*2)   
        .setRadius(tabHeight-tabMargin*2)
        .setRange(min, max)
        .setViewStyle(Knob.ARC)
        .setCaptionLabel(label)
        .lock();
        
      controllers.add(cp5.getController(name));
    } else if (type.equals("fader")) {
      cp5.addSlider(name)
        .setPosition(index*(width/8)+tabMargin*6, tabHeight*4)   
        .setSize((int)(tabHeight*0.6), tabWidth*2)
        .setRange(min, max)
        .setCaptionLabel(label);

        cp5.getController(name).getValueLabel().align(ControlP5.LEFT, ControlP5.TOP).setPaddingX(0);

      controllers.add(cp5.getController(name));
    } else if (type.equals("toggle") || type.equals("bang") || type.equals("button")) {
      cp5.addToggle(name)
        .setPosition(int(index/3)*(width/8)+tabMargin, (tabHeight*4)+(index%3)*(tabHeight*2))   
        .setSize(tabHeight/2, tabWidth/3)
        .setCaptionLabel(label);        
      controllers.add(cp5.getController(name));
    } else if (type.equals("radioButton")) {
      for (int i = 0; i < max; i++) {
        cp5.addToggle(name+i)
          .setPosition(int((index+i)/3)*(width/8)+tabMargin, (tabHeight*4)+((index+i)%3)*(tabHeight*2))   
          .setSize(tabHeight/2, tabWidth/3)
          .setCaptionLabel(label+i);        
        controllers.add(cp5.getController(name+i));
      }
      radioValues = new float[(int)max];
      radioValuesPre = new float[(int)max];
      for (int i = 0; i < max; i++) {
        radioValues[i] = 0;
        radioValuesPre[i] = 0;
      }
    }
  }

  void updateValue() {
    valuePre = value;

    if (type.equals("bang") && (value != 0.)) {
      controllers.get(0).setValue(0);
    } else if (type.equals("button")) {
      mPressedPre = mPressed;
      mPressed = controllers.get(0).isMousePressed(); 

      if (mPressed && !mPressedPre) {
        controllers.get(0).setValue(1.);
      } else if (!mPressed && mPressedPre) {
        controllers.get(0).setValue(0.);
      }
    }


    if (!type.equals("radioButton")) {
      value = controllers.get(0).getValue();
    } else {
      int isZero = 0;
      for (int i = 0; i < controllers.size(); i++) {
        radioValuesPre[i] = radioValues[i];
        radioValues[i] = controllers.get(i).getValue();
        if (radioValues[i]>radioValuesPre[i]) {
          deactivateOthers(i);
          value = i;
        }
        isZero += radioValues[i];
      }
      if (isZero == 0) {
        value = -1;
      }
    }

    if (value == valuePre) {
      changed = false;
    } else {
      changed = true;
      //println(name + " - " + value);
      if (oscConnected && !newMessage) {
        oscSendArray("/"+name, new float[]{value});
      }
    }
  }


  void deactivateOthers(int _radioIndex) {
    for (int i = 0; i < controllers.size(); i++) {
      if (i != _radioIndex) {
        controlP5.Controller c = controllers.get(i);
        c.setValue(0);
        radioValues[i] = 0;
      }
    }
  }


  void deactivateAll() {
    for (int i = 0; i < controllers.size(); i++) {
        controlP5.Controller c = controllers.get(i);
        c.setValue(0);
        radioValues[i] = 0;
      }
  }


  void show(boolean _show) {
    if (_show) {
      for (controlP5.Controller c : controllers) {
        c.show();
      }
    } else {
      for (controlP5.Controller c : controllers) {
        c.hide();
      }
    }
  }
}