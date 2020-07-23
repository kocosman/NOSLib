
void initializeVSP() {
}


void updateVSP() {
  visuals.get(selectedThumbnailIndex).update();
}


void showVSPGUI(boolean show) {
  if (show) {
    visuals.get(selectedThumbnailIndex).show(true);
  } else {
    for (int i = 0; i < visuals.size(); i++) {
      visuals.get(i).show(false);
    }
  }
}