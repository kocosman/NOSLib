
String myIpAddress = "";
String incomingPortString = "";
String remoteIp = "";
int outgoingPort = 7878;

String incomingOscTag = "";
float incomingOscValue = -1;
float incomingOscValuePair = -1;
float[] incomingOscData;

boolean oscConnected = false;
boolean newMessage = false;
boolean newKeyAvailable = false;
OscP5 oscP5;
NetAddress myRemoteLocation;

String selectedTextfieldName = "";
String tempTextfieldString = "";

Textfield incomingPortText;
Textfield outgoingPortText;
Textfield remoteIpText;

void initializeSettingsGUI() {

  myIpAddress = KetaiNet.getIP();
  incomingPortString = str(oscConfig.getInt("OutgoingPort: "));
  remoteIp = oscConfig.getString("IP: ");
  outgoingPort = oscConfig.getInt("IncomingPort: ");

  initializeIncomingPortText();
  cp5.get(Textfield.class, "incomingPortText").hide();

  initializeOutgoingPortText();
  cp5.get(Textfield.class, "outgoingPortText").hide();

  initializeRemoteIPText();
  cp5.get(Textfield.class, "remoteIpText").hide();



  cp5.addButton("oscConnect")
    .setPosition(tabMargin, tabHeight*5)
    .setSize(tabWidth, tabHeight)
    .setCaptionLabel("Connect")
    ;

  cp5.addButton("oscSync")
    .setPosition(tabMargin*2+tabWidth*2, tabHeight*5)
    .setSize(tabWidth, tabHeight)
    .setCaptionLabel("SYNC")
    ;
}

void initializeIncomingPortText() {
  incomingPortText = cp5.addTextfield("incomingPortText")
    .setPosition(tabMargin*18, tabHeight*2 + tabMargin)
    .setSize(width/13, width/40)
    .setFocus(true)
    .setAutoClear(true)
    .setCaptionLabel("")
    ;
}

void initializeOutgoingPortText() {
  outgoingPortText = cp5.addTextfield("outgoingPortText")
    .setPosition(tabMargin*18, tabHeight*2 + tabMargin*9)
    .setSize(width/13, width/40)
    .setFocus(true)
    .setAutoClear(true)
    .setCaptionLabel("")
    ;
}

void initializeRemoteIPText() {
  remoteIpText = cp5.addTextfield("remoteIpText")
    .setPosition(tabMargin*14, tabHeight*2 + tabMargin*6)
    .setSize(width/6, width/40)
    .setFocus(true)
    .setAutoClear(true)
    .setCaptionLabel("")
    ;
}
void updateSettings() {
  if (newKeyAvailable) {
    newKeyAvailable = false;
  }
}

public void incomingPortText(String theText) {
  println("Incoming Port: " + theText);
  cp5.get(Textfield.class, "incomingPortText").hide();
  if (!theText.equals("")) {
    incomingPortString = theText;
  }
}

public void outgoingPortText(String theText) {
  println("Outgoing Port: " + theText);
  cp5.get(Textfield.class, "outgoingPortText").hide();
  if (!theText.equals("")) {
    outgoingPort = Integer.valueOf(theText);
  }
}

public void remoteIpText(String theText) {
  println("Remote IP: " + theText);
  cp5.get(Textfield.class, "remoteIpText").hide();
  if (!theText.equals("")) {
    remoteIp = theText;
  }
}

void showSettingsGUI(boolean _show) {
  if (_show) {
    cp5.getController("oscConnect").show();
    cp5.getController("oscSync").show();
  } else {
    cp5.getController("oscConnect").hide();
    cp5.getController("oscSync").hide();
    cp5.get(Textfield.class, "incomingPortText").hide();
    cp5.get(Textfield.class, "outgoingPortText").hide();
    cp5.get(Textfield.class, "remoteIpText").hide();
  }
}

public void oscSync() {
  if (oscConnected) {
    oscSendArray("/sync", new float[]{0});
  }
}

public void oscConnect() {
  println("Attempting to connect OSC");
  oscConnected = true;
  oscP5 = new OscP5(this, Integer.parseInt(incomingPortString));
  myRemoteLocation = new NetAddress(remoteIp, outgoingPort);
}

public void drawSettingsPanel() {
  fill(textColor);
  textSize(width/40);

  text("My IP: " + myIpAddress, tabMargin, tabHeight*2);
  text("Incoming Port: " + incomingPortString, tabMargin, tabHeight*2 + tabMargin*3);

  text("Remote IP: " + remoteIp, tabMargin, tabHeight*2 + tabMargin*8);
  text("Outgoing Port: " + outgoingPort, tabMargin, tabHeight*2 + tabMargin*11);


  text("Tag: " + incomingOscTag, tabMargin, tabHeight*6 + tabMargin*8);
  text("Value " + incomingOscValue, tabMargin, tabHeight*6 + tabMargin*11);

  strokeWeight(tabHeight);
  if (oscConnected) {
    stroke(mainYellow);
  } else {
    stroke(mainBlue);
  }
  point(tabWidth*1.5+tabMargin, tabHeight*5.5);
}

void oscSendArray(String _tag, float[] _data) {
  //if (oscConnected) {
  //  OscMessage myMessage = new OscMessage(_tag);
  //  for (int i = 0; i < _data.length; i++) {
  //    myMessage.add(_data[i]);
  //  }
  //  oscP5.send(myMessage, myRemoteLocation);
  //}
  oscSendArrayDuplicate(_tag, _data);
  oscSendArrayDuplicate(_tag, _data);
  oscSendArrayDuplicate(_tag, _data);
}

void oscSendArrayDuplicate(String _tag, float[] _data) {
  if (oscConnected) {
    OscMessage myMessage = new OscMessage(_tag);
    for (int i = 0; i < _data.length; i++) {
      myMessage.add(_data[i]);
    }
    oscP5.send(myMessage, myRemoteLocation);
  }
}


void oscEvent(OscMessage theOscMessage) {
  newMessage = true;

  incomingOscTag = theOscMessage.addrPattern();
  incomingOscData = new float[theOscMessage.typetag().length()];
  for (int i = 0; i < incomingOscData.length; i++) {
    incomingOscData[i] = theOscMessage.get(i).floatValue();
  }

  if (oscConnected) {
    if (newMessage) {
      getOscData();
    }
  }
}


void getOscData() {
  if (incomingOscTag.equals("/flush")) {
    cp5.getController("blackAlpha").setValue(incomingOscData[0]);
    cp5.getController("trailAlpha").setValue(incomingOscData[1]);
    cp5.getController("rotX").setValue(incomingOscData[2]);
    cp5.getController("rotY").setValue(incomingOscData[3]);
    cp5.getController("rotZ").setValue(incomingOscData[4]);
    cp5.getController("stopRotX").setValue(incomingOscData[5]);
    cp5.getController("stopRotY").setValue(incomingOscData[6]);
    cp5.getController("stopRotZ").setValue(incomingOscData[7]);
    colorRange.updateOffsetAndSizeNormalized(incomingOscData[8], incomingOscData[9]);
    cp5.getController("strokeSaturation").setValue(incomingOscData[10]);
    cp5.getController("strokeAlpha").setValue(incomingOscData[11]);
    cp5.getController("fillSaturation").setValue(incomingOscData[12]);
    cp5.getController("fillAlpha").setValue(incomingOscData[13]);  
    savePreset.setValue(incomingOscData[14]);

    if ((int)incomingOscData[15] == -1) {
      presets.deactivateAll();
    } else {
      presets.activate((int)incomingOscData[15]);
    }

    soundRange.updateOffsetAndSizeNormalized(incomingOscData[16], incomingOscData[17]);
    soundGain.setValue(incomingOscData[18]);
    soundDecay.setValue(incomingOscData[19]);

    selectedThumbnailIndex = (int)incomingOscData[20];
    showVSPGUI(false);
    showVSPGUI(true);
    if (selectedTab != 1) {
      showVSPGUI(false);
    }
  } else if (incomingOscTag.equals("/soundGain")) {
    soundGain.setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/soundDecay")) {
    soundDecay.setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/soundFocus")) {
    soundRange.updateOffsetAndSizeNormalized(incomingOscData[1], incomingOscData[0]);
  } else if (incomingOscTag.equals("/blackAlpha")) {
    cp5.getController("blackAlpha").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/trailAlpha")) {
    cp5.getController("trailAlpha").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/colorFocus")) {
    colorRange.updateOffsetAndSizeNormalized(incomingOscData[1], incomingOscData[0]);
  } else if (incomingOscTag.equals("/strokeSaturation")) {
    cp5.getController("strokeSaturation").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/strokeAlpha")) {
    cp5.getController("strokeAlpha").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/fillSaturation")) {
    cp5.getController("fillSaturation").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/fillAlpha")) {
    cp5.getController("fillAlpha").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/stopRotX")) {
    cp5.getController("stopRotX").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/stopRotY")) {
    cp5.getController("stopRotY").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/stopRotZ")) {
    cp5.getController("stopRotZ").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/rotX")) {
    cp5.getController("rotX").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/rotY")) {
    cp5.getController("rotY").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/rotZ")) {
    cp5.getController("rotZ").setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/thumbnailIndex")) {
    selectedThumbnailIndex = (int)incomingOscData[0];
    showVSPGUI(false);
    showVSPGUI(true);
    if (selectedTab != 1) {
      showVSPGUI(false);
    }
  } else if (incomingOscTag.equals("/savePreset")) {
    savePreset.setValue(incomingOscData[0]);
  } else if (incomingOscTag.equals("/preset")) {
    if ((int)incomingOscData[0] == -1) {
      presets.deactivateAll();
    } else {
      presets.activate((int)incomingOscData[0]);
    }
  } else if (incomingOscTag.indexOf(visuals.get(selectedThumbnailIndex).name) != -1) {
    VisualEngineRemote veTemp = visuals.get(selectedThumbnailIndex);
    for (int i = 0; i < veTemp.vep.size(); i++) {
      VisualEngineParameterRemote vepTemp = veTemp.vep.get(i);
      if (incomingOscTag.equals('/'+vepTemp.name)) {
        if (vepTemp.type.equals("radioButton")) {
          if (incomingOscData[0] == -1) {
            vepTemp.deactivateAll();
          } else {
            vepTemp.controllers.get((int)incomingOscData[0]).setValue(1.);
            vepTemp.deactivateOthers((int)incomingOscData[0]);
          }
        } else {
          vepTemp.controllers.get(0).setValue(incomingOscData[0]);
        }
      }
    }
    veTemp.update();
  }
}