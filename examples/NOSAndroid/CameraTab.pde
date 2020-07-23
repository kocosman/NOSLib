
float rotX, rotY, rotZ;
float rotXPre, rotYPre, rotZPre;
boolean stopRotX, stopRotY, stopRotZ;
boolean stopRotXPre, stopRotYPre, stopRotZPre;

void initializeCam() {
  cp5.addSlider("rotX")
    .plugTo(rotX)
    .setPosition(tabWidth*3+tabMargin*4, tabHeight*2)
    .setSize(tabWidth/2, tabHeight*5+tabMargin*4)
    .setCaptionLabel("Rot X")
    .setRange(-0.05f, 0.05f);
  cp5.getController("rotX").getValueLabel().align(ControlP5.LEFT, ControlP5.TOP).setPaddingX(0);

  cp5.addSlider("rotY")
    .plugTo(rotY)
    .setPosition(tabWidth*4+tabMargin*5, tabHeight*2)
    .setSize(tabWidth/2, tabHeight*5+tabMargin*4)    
    .setCaptionLabel("Rot Y")
    .setRange(-0.05f, 0.05f);
  cp5.getController("rotY").getValueLabel().align(ControlP5.LEFT, ControlP5.TOP).setPaddingX(0);

  cp5.addSlider("rotZ")
    .plugTo(rotZ)
    .setPosition(tabWidth*5+tabMargin*6, tabHeight*2)
    .setSize(tabWidth/2, tabHeight*5+tabMargin*4)    
    .setCaptionLabel("Rot Z")
    .setRange(-0.05f, 0.05f);
  cp5.getController("rotZ").getValueLabel().align(ControlP5.LEFT, ControlP5.TOP).setPaddingX(0);

  cp5.addToggle("stopRotX")
    .plugTo(stopRotX)
    .setPosition(tabWidth*3+tabMargin*4, tabHeight*8+tabMargin)
    .setSize(tabWidth/2, tabHeight)
    .setCaptionLabel("STOP X");
  cp5.getController("stopRotX").getCaptionLabel().align(CENTER, CENTER);

  cp5.addToggle("stopRotY")
    .plugTo(stopRotY)
    .setPosition(tabWidth*4+tabMargin*5, tabHeight*8+tabMargin)
    .setSize(tabWidth/2, tabHeight)
    .setCaptionLabel("STOP Y");
  cp5.getController("stopRotY").getCaptionLabel().align(CENTER, CENTER);

  cp5.addToggle("stopRotZ")
    .plugTo(stopRotZ)
    .setPosition(tabWidth*5+tabMargin*6, tabHeight*8+tabMargin)
    .setSize(tabWidth/2, tabHeight)
    .setCaptionLabel("STOP Z");
  cp5.getController("stopRotZ").getCaptionLabel().align(CENTER, CENTER);



  cp5.addButton("randomAngle")
    .setPosition(tabMargin*2 + tabWidth, tabHeight*2)
    .setSize(tabWidth, tabHeight)
    .setCaptionLabel("Random");

  cp5.addButton("resetAngle")
    .setPosition(tabMargin, tabHeight*2)
    .setSize(tabWidth, tabHeight)
    .setCaptionLabel("Reset");

  cp5.addButton("plusRoll")
    .setPosition(tabMargin, tabHeight*4)
    .setSize(tabWidth, tabHeight)
    .setCaptionLabel("Roll +");

  cp5.addButton("minusRoll")
    .setPosition(tabMargin*2 + tabWidth, tabHeight*4)
    .setSize(tabWidth, tabHeight)
    .setCaptionLabel("Roll -");

  cp5.addButton("plusPitch")
    .setPosition(tabMargin, tabHeight*5+tabMargin*2)
    .setSize(tabWidth, tabHeight)
    .setCaptionLabel("Pitch +");

  cp5.addButton("minusPitch")
    .setPosition(tabMargin*2 + tabWidth, tabHeight*5+tabMargin*2)
    .setSize(tabWidth, tabHeight)
    .setCaptionLabel("Pitch -");

  cp5.addButton("plusYaw")
    .setPosition(tabMargin, tabHeight*6+tabMargin*4)
    .setSize(tabWidth, tabHeight)
    .setCaptionLabel("Yaw +");

  cp5.addButton("minusYaw")
    .setPosition(tabMargin*2 + tabWidth, tabHeight*6+tabMargin*4)
    .setSize(tabWidth, tabHeight)
    .setCaptionLabel("Yaw -");
}

void randomAngle() {
  println("Random Angle");
  if (oscConnected) {
    oscSendArray("/randomAngle", new float[]{1.});
  }
}

void resetAngle() {
  println("Reset Angle");
  if (oscConnected) {
    oscSendArray("/resetAngle", new float[]{1.});
  }
}

void plusRoll() {
  println("plusRoll");
  if (oscConnected) {
    oscSendArray("/plusRoll", new float[]{1.});
  }
}
void minusRoll() {
  println("minusRoll");
  if (oscConnected) {
    oscSendArray("/minusRoll", new float[]{1.});
  }
}
void plusYaw() {
  println("plusYaw");
  if (oscConnected) {
    oscSendArray("/plusYaw", new float[]{1.});
  }
}
void minusYaw() {
  println("minusYaw");
  if (oscConnected) {
    oscSendArray("/minusYaw", new float[]{1.});
  }
}
void plusPitch() {
  println("plusPitch");
  if (oscConnected) {
    oscSendArray("/plusPitch", new float[]{1.});
  }
}
void minusPitch() {
  println("minusPitch");
  if (oscConnected) {
    oscSendArray("/minusPitch", new float[]{1.});
  }
}

void updateCam() {
  if (rotX != rotXPre) {
    println("rotX changed");
    if (oscConnected && !newMessage) {
      oscSendArray("/rotX", new float[]{rotX});
    }
  }

  if (rotY != rotYPre) {
    println("rotY changed");
    if (oscConnected && !newMessage) {
      oscSendArray("/rotY", new float[]{rotY});
    }
  }

  if (rotZ != rotZPre) {
    println("rotZ changed");
    if (oscConnected && !newMessage) {
      oscSendArray("/rotZ", new float[]{rotZ});
    }
  }

  if (stopRotX != stopRotXPre) {
    println("StopRotX changed");
    if (oscConnected && !newMessage) {
      if (stopRotX) {
        oscSendArray("/stopRotX", new float[]{1.});
      } else {
        oscSendArray("/stopRotX", new float[]{0.});
      }
    }
  }

  if (stopRotY != stopRotYPre) {
    println("StopRotY changed");
    if (oscConnected && !newMessage) {
      if (stopRotY) {
        oscSendArray("/stopRotY", new float[]{1.});
      } else {
        oscSendArray("/stopRotY", new float[]{0.});
      }
    }
  }

  if (stopRotZ != stopRotZPre) {
    println("StopRotZ changed");
    if (oscConnected && !newMessage) {
      if (stopRotZ) {
        oscSendArray("/stopRotZ", new float[]{1.});
      } else {
        oscSendArray("/stopRotZ", new float[]{0.});
      }
    }
  }


  stopRotXPre = stopRotX; 
  stopRotYPre = stopRotY; 
  stopRotZPre = stopRotZ;

  rotXPre = rotX;
  rotYPre = rotY;
  rotZPre = rotZ;
}

void showCameraGUI(boolean _show) {
  if (_show) {
    cp5.getController("stopRotX").show();
    cp5.getController("stopRotY").show();
    cp5.getController("stopRotZ").show();
    cp5.getController("randomAngle").show();
    cp5.getController("resetAngle").show();
    cp5.getController("plusRoll").show();
    cp5.getController("minusRoll").show();
    cp5.getController("plusPitch").show();
    cp5.getController("minusPitch").show();
    cp5.getController("plusYaw").show();
    cp5.getController("minusYaw").show();
    cp5.getController("rotX").show();
    cp5.getController("rotY").show();
    cp5.getController("rotZ").show();
  } else {

    cp5.getController("stopRotX").hide();
    cp5.getController("stopRotY").hide();
    cp5.getController("stopRotZ").hide();
    cp5.getController("randomAngle").hide();
    cp5.getController("resetAngle").hide();
    cp5.getController("plusRoll").hide();
    cp5.getController("minusRoll").hide();
    cp5.getController("plusPitch").hide();
    cp5.getController("minusPitch").hide();
    cp5.getController("plusYaw").hide();
    cp5.getController("minusYaw").hide();
    cp5.getController("rotX").hide();
    cp5.getController("rotY").hide();
    cp5.getController("rotZ").hide();
  }
}