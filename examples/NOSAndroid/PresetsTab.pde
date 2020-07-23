
RadioButton presets;
Toggle savePreset;

int presetValue = -1;
int presetValuePre = -1;
int saveValue = -1;
int saveValuePre = -1;

void initializePresetsGUI() {
  presets = cp5.addRadioButton("presets")
    .setPosition(tabMargin, tabHeight*4)
    .setSize(tabWidth, tabHeight)
    .setItemsPerRow(4)
    .setSpacingColumn(tabMargin*2)
    .setSpacingRow(tabMargin*2)
    .addItem("preset1", 0)
    .addItem("preset2", 1)
    .addItem("preset3", 2)
    .addItem("preset4", 3)
    .addItem("preset5", 4)
    .addItem("preset6", 5)
    .addItem("preset7", 6)
    .addItem("preset8", 7)
    ;

  presets.setValue(-1);

  for (Toggle t : presets.getItems()) {
    t.getCaptionLabel().align(CENTER, CENTER);
  }

  savePreset = cp5.addToggle("savePreset")
    .setPosition(tabMargin, tabHeight*2)
    .setSize(tabWidth, tabHeight)
    .setCaptionLabel("SAVE")
    ;

  savePreset.getCaptionLabel().align(CENTER, CENTER);
}

void updatePresetsGUI() {
  presetValue = (int)presets.getValue();
  saveValue = (int)savePreset.getValue();
  if (presetValue != presetValuePre) {
    println(presetValue);
    if (oscConnected && !newMessage) {      
      oscSendArray("/preset", new float[]{presetValue});
    }
  }

  if (saveValue != saveValuePre) {
    println("Save preset " + saveValue);
    if (oscConnected && !newMessage) {
      oscSendArray("/savePreset", new float[]{saveValue});
    }
  }


  presetValuePre = presetValue;
  saveValuePre = saveValue;
}


void showPresetGUI(boolean _show) {
  if (_show) {
    presets.show();
    savePreset.show();
  } else {
    presets.hide();
    savePreset.hide();
  }
}