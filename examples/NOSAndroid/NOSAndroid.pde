import controlP5.*;
import oscP5.*;
import netP5.*;
import ketai.net.*;
import ketai.ui.*;
ControlP5 cp5;

RadioButton tabSelector;

String[] tabNames = {"Thumbnails", "Visuals", "Color", "Camera", "Sound", "Presets", "Settings"};

int[] tabValues = {0, 0, 0, 0, 0, 0};
int selectedTab = -1;
int selectedTabPre = -1;
int tabAmount;
int tabWidth;
int tabHeight;
int tabMargin;

// 2560 x 1600 Pixel
PFont pFont;
ControlFont font;

float screenAspect;

String selectedKnobName = "";

//-----VISUALS
ArrayList<VisualEngineRemote> visuals;

void setup() {
  size(displayWidth, displayHeight, P3D);
  //size(1280, 720, P3D);
  orientation(LANDSCAPE);    
  frameRate(60);

  screenAspect = (float)width/(float)height;
  println("Screen Aspect: " + screenAspect);

  initializeColors();
  tabAmount = tabNames.length;
  tabMargin = width/100;
  tabWidth = (width-tabMargin*(tabAmount+1))/tabAmount;
  tabHeight = height/10;

  cp5 = new ControlP5(this);
  cp5.setColorForeground(dimYellow);
  cp5.setColorActive(mainYellow);
  cp5.setColorBackground(mainBlue);
  cp5.setColorCaptionLabel(textColor);
  cp5.setColorValueLabel(valueLabel);

  //printArray(PFont.list());

  pFont = createFont("SansSerif", 64);
  font = new ControlFont(pFont, width/80);
  cp5.setFont(font);
  cp5.setColorValueLabel(valueLabel);

  visuals = new ArrayList<VisualEngineRemote>();  
  loadConfig();

  println("NOS has " + visuals.size() + " visuals");
  for (int i = 0; i < visuals.size(); i++) {
    println(visuals.get(i).name);
  }

  tabSelector = cp5.addRadioButton("tabs")
    .setPosition(tabMargin, tabMargin)
    .setSize(tabWidth, tabHeight)
    .setItemsPerRow(7)
    .setSpacingColumn(tabMargin)
    .addItem("Thumbnails", 0)
    .addItem("VSP", 1)
    .addItem("Color", 2)
    .addItem("Camera", 3)
    .addItem("Sound", 4)
    .addItem("Presets", 5)
    .addItem("Settings", 6)
    .setNoneSelectedAllowed(false)
    ;  

  tabSelector.activate(0);
  for (Toggle t : tabSelector.getItems()) {
    t.getCaptionLabel().align(CENTER, CENTER);
  }

  initializeGestures();
  initializePresetsGUI();
  initializeSoundGUI();
  initializeCam();
  initializeSettingsGUI();
  initializeThumbnails();
  initializeVSP();
  initializeColorGUI();
  background(0);
}


void draw() {  
  background(mainBackground);
  selectedTab = (int)tabSelector.getValue();

  textSize(width/100);
  fill(textColor);
  text((int)frameRate, tabMargin, tabHeight+tabMargin*2);

  if (selectedTab == 0) {    // Thumbnails
    if (selectedTab != selectedTabPre) {
      showPresetGUI(false);
      showSoundGUI(false);
      showSettingsGUI(false);
      showThumbnailGUI(true);
      showVSPGUI(false);
      showCameraGUI(false);
      showColorGUI(false);
    }
    drawThumbnails();
  } else if (selectedTab == 1) {    // Visuals
    if (selectedTab != selectedTabPre) {
      showPresetGUI(false);
      showSoundGUI(false);
      showSettingsGUI(false);
      showThumbnailGUI(false);
      showVSPGUI(true);
      showCameraGUI(false);
      showColorGUI(false);
    }
    updateVSP();
  } else if (selectedTab == 2) {    // Color
    if (selectedTab != selectedTabPre) {
      showPresetGUI(false);
      showSoundGUI(false);
      showSettingsGUI(false);
      showThumbnailGUI(false);
      showVSPGUI(false);
      showCameraGUI(false);
      showColorGUI(true);
    }
    updateColor();
  } else if (selectedTab == 3) {    // Camera
    if (selectedTab != selectedTabPre) {
      showPresetGUI(false);
      showSoundGUI(false);
      showSettingsGUI(false);
      showThumbnailGUI(false);
      showVSPGUI(false);
      showCameraGUI(true);
      showColorGUI(false);
    }
    updateCam();
  } else if (selectedTab == 4) {    // Sound
    if (selectedTab != selectedTabPre) {
      showPresetGUI(false);
      showSoundGUI(true);
      showSettingsGUI(false);
      showThumbnailGUI(false);
      showVSPGUI(false);
      showCameraGUI(false);
      showColorGUI(false);
    }
    updateSound();
  } else if (selectedTab == 5) {    // Presets
    if (selectedTab != selectedTabPre) {
      showPresetGUI(true);
      showSoundGUI(false);
      showSettingsGUI(false);
      showThumbnailGUI(false);
      showVSPGUI(false);
      showCameraGUI(false);
      showColorGUI(false);
    }
    updatePresetsGUI();
  } else if (selectedTab == 6) {    // Settings
    if (selectedTab != selectedTabPre) {
      showPresetGUI(false);
      showSoundGUI(false);
      showSettingsGUI(true);
      showThumbnailGUI(false);
      showVSPGUI(false);
      showCameraGUI(false);
      showColorGUI(false);
    }
    drawSettingsPanel();
  } 


  if (selectedTab == 1) {
    for (VisualEngineParameterRemote vep : visuals.get(selectedThumbnailIndex).vep) {
      if (vep.type.equals("knob")) {
        controlP5.Controller vepTempKnob = cp5.getController(vep.name);
        if (mousePressed) {
          Rectangle rTemp = new Rectangle(vepTempKnob.getPosition()[0], vepTempKnob.getPosition()[1], vepTempKnob.getWidth(), vepTempKnob.getHeight());
          if (rTemp.contains(mouseX, mouseY)) {
            println("zxc: " + vepTempKnob.getCaptionLabel().getText());
            fill(255, 100);
            rect(rTemp.x, rTemp.y, rTemp.width, rTemp.height);
            if (selectedKnobName.equals("")) {
              selectedKnobName = vep.name;
            }
          }
        } else if (mouseReleased) {
          selectedKnobName = "";
        }

        if (!selectedKnobName.equals("")) {
          controlP5.Controller selectedKnob = cp5.getController(selectedKnobName);
          float userValue = map((mouseX-pmouseX), 0, tabWidth*2, 0, selectedKnob.getMax()-selectedKnob.getMin());
          selectedKnob.setValue(selectedKnob.getValue()+userValue);
        }
      }
    }
  }


  if (newMessage) {
    newMessage = false;
  }
  selectedTabPre = selectedTab;
  mouseReleased = false;
}

void mousePressed() {
  println(mouseX + " - " + mouseY);
  if (selectedTab == 6) { // Settings
    Rectangle incomingPortRect = new Rectangle((int)cp5.get(Textfield.class, "incomingPortText").getPosition()[0], (int)cp5.get(Textfield.class, "incomingPortText").getPosition()[1], (int)cp5.get(Textfield.class, "incomingPortText").getWidth(), (int)cp5.get(Textfield.class, "incomingPortText").getHeight());
    Rectangle outgoingPortRect = new Rectangle((int)cp5.get(Textfield.class, "outgoingPortText").getPosition()[0], (int)cp5.get(Textfield.class, "outgoingPortText").getPosition()[1], (int)cp5.get(Textfield.class, "outgoingPortText").getWidth(), (int)cp5.get(Textfield.class, "outgoingPortText").getHeight());
    Rectangle remoteIpRect = new Rectangle((int)cp5.get(Textfield.class, "remoteIpText").getPosition()[0], (int)cp5.get(Textfield.class, "remoteIpText").getPosition()[1], (int)cp5.get(Textfield.class, "remoteIpText").getWidth(), (int)cp5.get(Textfield.class, "remoteIpText").getHeight());

    if (incomingPortRect.contains(mouseX, mouseY)) {
      initializeIncomingPortText();
      incomingPortText.show();
      incomingPortText.setText("");
      incomingPortText.setFocus(true);
      KetaiKeyboard.show(this);
      selectedTextfieldName = "incomingPortText";
      tempTextfieldString = "";
      outgoingPortText.hide();
      remoteIpText.hide();
    } else if (outgoingPortRect.contains(mouseX, mouseY)) {
      initializeOutgoingPortText();
      outgoingPortText.show();
      outgoingPortText.setText("");
      outgoingPortText.setFocus(true);
      KetaiKeyboard.show(this);
      selectedTextfieldName = "outgoingPortText";
      tempTextfieldString = "";
      incomingPortText.hide();
      remoteIpText.hide();
    } else if (remoteIpRect.contains(mouseX, mouseY)) {
      initializeRemoteIPText();
      remoteIpText.show();
      remoteIpText.setText("");
      remoteIpText.setFocus(true);
      KetaiKeyboard.show(this);
      selectedTextfieldName = "remoteIpText";
      tempTextfieldString = "";
      incomingPortText.hide();
      outgoingPortText.hide();
    } else {
      if (selectedTextfieldName.equals("remoteIpText")) {
        //tempTextfieldString = cp5.get(Textfield.class, selectedTextfieldName).getText();
        remoteIpText(tempTextfieldString);
        tempTextfieldString = "";
      } else if (selectedTextfieldName.equals("outgoingPortText")) {
        //tempTextfieldString = cp5.get(Textfield.class, selectedTextfieldName).getText();
        outgoingPortText(tempTextfieldString);
        tempTextfieldString = "";
      } else if (selectedTextfieldName.equals("incomingPortText")) {
        //tempTextfieldString = cp5.get(Textfield.class, selectedTextfieldName).getText();
        incomingPortText(tempTextfieldString);
        tempTextfieldString = "";
      }
      cp5.get(Textfield.class, "incomingPortText").hide();
      cp5.get(Textfield.class, "outgoingPortText").hide();
      cp5.get(Textfield.class, "remoteIpText").hide();

      tempTextfieldString = "";
      selectedTextfieldName = "";
      KetaiKeyboard.hide(this);
    }
  }
}

void keyPressed() {
  newKeyAvailable = true;
  if (selectedTab == 6) { // Settings
    if (key == '0') {
      println("incoming key 0");
      tempTextfieldString = tempTextfieldString+'0';
    } else if (key == '1') {
      println("incoming key 1");
      tempTextfieldString = tempTextfieldString+'1';
    } else if (key == '2') {
      println("incoming key 2");
      tempTextfieldString = tempTextfieldString+'2';
    } else if (key == '3') {
      println("incoming key 3");
      tempTextfieldString = tempTextfieldString+'3';
    } else if (key == '4') {
      println("incoming key 4");
      tempTextfieldString = tempTextfieldString+'4';
    } else if (key == '5') {
      println("incoming key 5");
      tempTextfieldString = tempTextfieldString+'5';
    } else if (key == '6') {
      println("incoming key 6");
      tempTextfieldString = tempTextfieldString+'6';
    } else if (key == '7') {
      println("incoming key 7");
      tempTextfieldString = tempTextfieldString+'7';
    } else if (key == '8') {
      println("incoming key 8");
      tempTextfieldString = tempTextfieldString+'8';
    } else if (key == '9') {
      println("incoming key 9");
      tempTextfieldString = tempTextfieldString+'9';
    } else if (key == '.') {
      println("incoming key DOT");
      tempTextfieldString = tempTextfieldString+'.';
    } else if (keyCode == 66) {
      println("incoming key ENTER");
    } else if (keyCode == 67) {
      println("incoming key BACKSPACE");
      try {
        tempTextfieldString = tempTextfieldString.substring(0, tempTextfieldString.length()-1);
        if (selectedTextfieldName.equals("incomingPortText")) {
          initializeIncomingPortText();
        } else if (selectedTextfieldName.equals("outgoingPortText")) {
          initializeOutgoingPortText();
        } else if (selectedTextfieldName.equals("remoteIpText")) {
          initializeRemoteIPText();
        }
      } 
      catch (Exception e) {
        println("BACKSPACE Exception");
      }
    }
    cp5.get(Textfield.class, selectedTextfieldName).setText(tempTextfieldString);
  }
}