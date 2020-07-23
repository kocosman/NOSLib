public int mainYellow       ;
public int mainBlue         ;
public int mainBackground   ;
public int panelBackground  ;
public int label            ;
public int dimYellow        ;
public int thinLines        ;
public int inactive         ;
public int textColor        ;
public int valueLabel       ;

void initializeColors() {
  colorMode(HSB);
  mainYellow       = color(27, 228, 251);
  mainBlue         = color(137, 252, 100);
  mainBackground   = color(0, 0, 52);
  panelBackground  = color(156, 16, 48);
  label            = color(0, 0, 202);
  dimYellow        = color(27, 255, 148);
  thinLines        = color(0, 0, 102);
  inactive         = color(0, 0, 61);
  textColor        = color(255, 0, 176);
  //valueLabel       = color(27, 219, 220);
  valueLabel       = label;
  colorMode(RGB);
}