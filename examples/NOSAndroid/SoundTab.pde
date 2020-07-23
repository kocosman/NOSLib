
Slider soundGain, soundDecay;

AndroidRange soundRange;

float sGain = 0; 
float sGainPre = 0; 
float sDecay = 0; 
float sDecayPre = 0;

void initializeSoundGUI() {
  soundGain = cp5.addSlider("soundGain")
    .setPosition(tabMargin, height-tabMargin*3-tabHeight*3)
    .setSize(width-tabMargin*2-tabWidth, tabHeight)
    .setRange(0., 5.)
    .setCaptionLabel("Gain")
    ;

  soundDecay = cp5.addSlider("soundDecay")
    .setPosition(tabMargin, height-tabMargin-tabHeight*2)
    .setSize(width-tabMargin*2-tabWidth, tabHeight)
    .setRange(0., 0.5)
    .setCaptionLabel("Decay")
    ;

  soundRange = new AndroidRange(tabMargin, tabMargin*4+tabHeight, width-tabMargin*2, height-tabHeight*4-tabMargin*9);  
  soundRange.controllerFillColor = mainBlue;
  soundRange.sliderFillColor = dimYellow;
  soundRange.sliderFillColorActive = mainYellow;
  
}

void updateSound() {
  soundRange.render();

  if (soundRange.isChanged) {
    if (oscConnected && !newMessage) {
      oscSendArray("/soundFocus", new float[]{soundRange.rangeSize, soundRange.rangeOffset});
    }
  }

  sGain = soundGain.getValue();
  sDecay = soundDecay.getValue();

  if ((sGain != sGainPre) && (!newMessage)) {
    if (oscConnected) {
      oscSendArray("/soundGain", new float[]{sGain});
    }
  }

  if ((sDecay != sDecayPre) && (!newMessage)) {
    if (oscConnected) {
      oscSendArray("/soundDecay", new float[]{sDecay});
    }
  }

  sGainPre = sGain;
  sDecayPre = sDecay;
}

void showSoundGUI(boolean _show) {
  if (_show) {
    soundGain.show();
    soundDecay.show();
  } else {
    soundGain.hide();
    soundDecay.hide();
  }
}