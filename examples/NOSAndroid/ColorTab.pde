
float strokeSaturation, strokeAlpha, strokeSaturationPre, strokeAlphaPre;
float fillSaturation, fillAlpha, fillSaturationPre, fillAlphaPre;

AndroidRange colorRange;

void initializeColorGUI() {
  cp5.addSlider("strokeSaturation")
    .plugTo(strokeSaturation)
    .setPosition(tabMargin, height-tabMargin*7-tabHeight*5)
    .setSize(width-tabMargin*2-tabWidth, tabHeight)
    .setRange(0., 255.)
    .setCaptionLabel("Stroke Sat")
    ;

  cp5.addSlider("strokeAlpha")
    .plugTo(strokeAlpha)
    .setPosition(tabMargin, height-tabMargin*5-tabHeight*4)
    .setSize(width-tabMargin*2-tabWidth, tabHeight)
    .setRange(0., 255.)
    .setCaptionLabel("Stroke Alpha")
    ;

  cp5.addSlider("fillSaturation")
    .plugTo(fillSaturation)
    .setPosition(tabMargin, height-tabMargin*3-tabHeight*3)
    .setSize(width-tabMargin*2-tabWidth, tabHeight)
    .setRange(0., 255.)
    .setCaptionLabel("Fill Sat")
    ;

  cp5.addSlider("fillAlpha")
    .plugTo(fillAlpha)
    .setPosition(tabMargin, height-tabMargin-tabHeight*2)
    .setSize(width-tabMargin*2-tabWidth, tabHeight)
    .setRange(0., 255.)    
    .setCaptionLabel("Fill Alpha")
    ;

  colorRange = new AndroidRange(tabMargin, tabMargin*4+tabHeight, width-tabMargin*2, height-tabHeight*6-tabMargin*13);  
  colorRange.controllerFillColor = mainBlue;
  colorRange.sliderFillColor = color(255, 50);
  colorRange.sliderFillColorActive = color(255, 150);

  colorRange.controllerRect.backgroundImage = loadImage(colorPaletteConfig.getString("colorPaletteFileName"));
  colorRange.controllerRect.imageEnable = true;
  colorRange.sliderStrokeEnable = true;
}

void updateColor() {
  colorRange.render();

  if (colorRange.isChanged) {
    if (oscConnected && !newMessage) {
      oscSendArray("/colorFocus", new float[]{colorRange.rangeSize, colorRange.rangeOffset});
    }
  }

  if (strokeSaturation != strokeSaturationPre) {
    println("strokeSat changed");
    if (oscConnected) {
      oscSendArray("/strokeSaturation", new float[]{strokeSaturation});
    }
  }

  if (strokeAlpha != strokeAlphaPre) {
    println("strokeAlpha changed");
    if (oscConnected) {
      oscSendArray("/strokeAlpha", new float[]{strokeAlpha});
    }
  }

  if (fillSaturation != fillSaturationPre) {
    println("fillSat changed");
    if (oscConnected) {
      oscSendArray("/fillSaturation", new float[]{fillSaturation});
    }
  }

  if (fillAlpha != fillAlphaPre) {
    println("fillAlpha changed");
    if (oscConnected) {
      oscSendArray("/fillAlpha", new float[]{fillAlpha});
    }
  }


  strokeSaturationPre = strokeSaturation;
  strokeAlphaPre = strokeAlpha;
  fillSaturationPre = fillSaturation;
  fillAlphaPre = fillAlpha;
}

void showColorGUI(boolean _show) {
  if (_show) {
    cp5.getController("strokeSaturation").show();
    cp5.getController("strokeAlpha").show();
    cp5.getController("fillSaturation").show();
    cp5.getController("fillAlpha").show();
  } else {
    cp5.getController("strokeSaturation").hide();
    cp5.getController("strokeAlpha").hide();
    cp5.getController("fillSaturation").hide();
    cp5.getController("fillAlpha").hide();
  }
}