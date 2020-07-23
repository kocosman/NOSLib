
int selectedThumbnailIndex = 0;
int thumbnailPageIndex = 0;
int thumbnailAmount = 8;
int thumbnailXAmount = 4;
int thumbnailYAmount = 2;

float thumbnailWidth;
float thumbnailHeight;
float thumbnailAspect;
ArrayList<PImage> visualThumbnails;
PImage thumbnailTemp;

float blackAlpha;
float trailAlpha;
float blackAlphaPre;
float trailAlphaPre;

void initializeThumbnails() {

  visualThumbnails = new ArrayList<PImage>();

  thumbnailAspect = 104./185.;
  thumbnailWidth = (width - (tabMargin*6) - tabHeight)/thumbnailXAmount;
  thumbnailHeight = thumbnailWidth*thumbnailAspect;

  for (int i = 0; i < visuals.size(); i++) {
    thumbnailTemp = new PImage((int)thumbnailWidth, (int)thumbnailHeight);
    thumbnailTemp.copy(visuals.get(i).thumbnail, 0, 0, visuals.get(i).thumbnail.width, visuals.get(i).thumbnail.height, 0, 0, (int)thumbnailWidth, (int)thumbnailHeight);
    visualThumbnails.add(thumbnailTemp);
  }

  thumbnailTemp = createImage((int)thumbnailWidth, (int)thumbnailHeight, RGB);

  for (int i = 0; i < thumbnailAmount; i++) {
    cp5.addButton("ve"+i)
      .setPosition(tabMargin + ((tabMargin+thumbnailWidth)*(i%thumbnailXAmount)), tabHeight*2 + ((tabMargin*2+tabWidth)*(i/thumbnailXAmount)))
      .setSize(30, 30)
      ;
    cp5.getController("ve"+i).hide();
  }

  selectedThumbnailIndex = 0;
  cp5.addButton("pageUp")
    .setPosition(width-tabMargin-tabHeight, tabHeight*2)
    .setSize(tabHeight, (int)thumbnailHeight)
    .setCaptionLabel("^");

  cp5.addButton("pageDown")
    .setPosition(width-tabMargin-tabHeight, tabHeight*2+tabMargin*2+tabWidth)
    .setSize(tabHeight, (int)thumbnailHeight)
    .setCaptionLabel("v");

  cp5.addSlider("blackAlpha")
    .plugTo(blackAlpha)
    .setPosition(tabMargin, height-tabMargin*5-tabHeight*2)   
    .setSize(width-tabMargin*2-tabWidth, tabHeight)
    .setCaptionLabel("BLACK")
    .setRange(0.f, 255.f);

  cp5.addSlider("trailAlpha")
    .plugTo(trailAlpha)
    .setPosition(tabMargin, height-tabMargin*2-tabHeight)   
    .setSize(width-tabMargin*2-tabWidth, tabHeight)
    .setCaptionLabel("TRAIL")
    .setRange(180.f, 255.f);
}

public void drawThumbnails() {
  if (blackAlpha != blackAlphaPre) {
    println("Black changed");
    if (oscConnected) {
      oscSendArray("/blackAlpha", new float[]{blackAlpha});
    }
  }

  if (trailAlpha != trailAlphaPre) {
    println("Trail changed");
    if (oscConnected) {
      oscSendArray("/trailAlpha", new float[]{trailAlpha});
    }
  }

  blackAlphaPre = blackAlpha;
  trailAlphaPre = trailAlpha;


  stroke(mainYellow);
  strokeWeight(width/250);
  rectMode(CORNER);
  if ((selectedThumbnailIndex >= (thumbnailAmount*thumbnailPageIndex)) && (selectedThumbnailIndex < (thumbnailAmount*(thumbnailPageIndex+1)))) {
    rect(cp5.getController("ve"+(selectedThumbnailIndex%thumbnailAmount)).getPosition()[0]-1, 
      cp5.getController("ve"+(selectedThumbnailIndex%thumbnailAmount)).getPosition()[1]-1, 
      cp5.getController("ve"+(selectedThumbnailIndex%thumbnailAmount)).getWidth(), 
      cp5.getController("ve"+(selectedThumbnailIndex%thumbnailAmount)).getHeight()
      );
  }

  for (int i = 0; i < thumbnailAmount; i++) {
    int tempThumbnailIndex = i+(thumbnailAmount*thumbnailPageIndex);
    if (tempThumbnailIndex < visuals.size()) {
      if (selectedThumbnailIndex == tempThumbnailIndex) {
        fill(mainYellow);
      } else {
        fill(textColor);
      }
      textFont(pFont, width/60);
      text(visuals.get(tempThumbnailIndex).name.toUpperCase(), 
        cp5.getController("ve"+i).getPosition()[0], 
        cp5.getController("ve"+i).getPosition()[1]+cp5.getController("ve"+i).getHeight()+(textAscent()+textDescent()));
    }
  }
}

public void updateThumbnails() {
  for (int i = 0; i < thumbnailAmount; i++) {
    int tempThumbnailIndex = i+(thumbnailAmount*thumbnailPageIndex);
    if (tempThumbnailIndex < visuals.size()) {
      try {
        cp5.getController("ve"+i).setImage(visualThumbnails.get(tempThumbnailIndex));
        cp5.getController("ve"+i).updateSize();
      } 
      catch(Exception e) {
        println(e);
      }

      cp5.getController("ve"+i).show();
    } else {
      cp5.getController("ve"+i).hide();
    }
  }
}

void showThumbnailGUI(boolean show) {
  if (show) {
    updateThumbnails();
    cp5.getController("pageUp").show(); 
    cp5.getController("pageDown").show();
    cp5.getController("blackAlpha").show();
    cp5.getController("trailAlpha").show();
  } else {
    for (int i = 0; i < thumbnailAmount; i++) {
      cp5.getController("ve"+i).hide();
    }
    cp5.getController("pageUp").hide();
    cp5.getController("pageDown").hide();
    cp5.getController("blackAlpha").hide();
    cp5.getController("trailAlpha").hide();
  }
}

public void pageUp() {
  thumbnailPageIndex++;
  thumbnailPageIndex = constrain(thumbnailPageIndex, 0, visuals.size()/thumbnailAmount);
  updateThumbnails();
}

public void pageDown() {
  thumbnailPageIndex--;
  thumbnailPageIndex = constrain(thumbnailPageIndex, 0, visuals.size()/thumbnailAmount);
  updateThumbnails();
}

public void ve0() {
  selectedThumbnailIndex = 0+(thumbnailPageIndex*thumbnailAmount);
  oscSendArray("/selectVisual", new float[]{selectedThumbnailIndex});
}

public void ve1() {
  selectedThumbnailIndex = 1+(thumbnailPageIndex*thumbnailAmount);
  oscSendArray("/selectVisual", new float[]{selectedThumbnailIndex});
}

public void ve2() {
  selectedThumbnailIndex = 2+(thumbnailPageIndex*thumbnailAmount);
  oscSendArray("/selectVisual", new float[]{selectedThumbnailIndex});
}

public void ve3() {
  selectedThumbnailIndex = 3+(thumbnailPageIndex*thumbnailAmount);
  oscSendArray("/selectVisual", new float[]{selectedThumbnailIndex});
}

public void ve4() {
  selectedThumbnailIndex = 4+(thumbnailPageIndex*thumbnailAmount);
  oscSendArray("/selectVisual", new float[]{selectedThumbnailIndex});
}

public void ve5() {
  selectedThumbnailIndex = 5+(thumbnailPageIndex*thumbnailAmount);
  oscSendArray("/selectVisual", new float[]{selectedThumbnailIndex});
}

public void ve6() {
  selectedThumbnailIndex = 6+(thumbnailPageIndex*thumbnailAmount);
  oscSendArray("/selectVisual", new float[]{selectedThumbnailIndex});
}

public void ve7() {
  selectedThumbnailIndex = 7+(thumbnailPageIndex*thumbnailAmount);
  oscSendArray("/selectVisual", new float[]{selectedThumbnailIndex});
}