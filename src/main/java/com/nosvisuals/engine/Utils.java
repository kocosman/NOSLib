package com.nosvisuals.engine;

import processing.core.*;
import processing.data.*;

public class Utils {


  public static float[] shuffleArray(float[] aFloatArray) {
    FloatList tempList = new FloatList();
    for (int i = 0; i < aFloatArray.length; i++) {
      tempList.append(aFloatArray[i]);
    }
    tempList.shuffle();
    for (int i = 0; i < aFloatArray.length; i++) {
      aFloatArray[i]=tempList.get(i);
    }  
    return aFloatArray;
  }

  public static int[] shuffleArray(int[] aFloatArray) {
    IntList tempList = new IntList();
    for (int i = 0; i < aFloatArray.length; i++) {
      tempList.append(aFloatArray[i]);
    }
    tempList.shuffle();
    for (int i = 0; i < aFloatArray.length; i++) {
      aFloatArray[i]=tempList.get(i);
    }  
    return aFloatArray;
  }


  public static float[] mapArrays(float[] in, int outputLength) {
    float[] out = new float[outputLength];

    if (in.length == out.length) {
      return in;
    } else {
      for (int i = 0; i < out.length; i++) {
        float index = PApplet.map(i, 0, out.length, 0, in.length);
        int indexBase = PApplet.floor(index);
        float indexLerp = index-indexBase;
        out[i] = PApplet.lerp(in[indexBase], in[PApplet.constrain(indexBase+1, 0, in.length-1)], indexLerp);
      }
    }
    return out;
  }


  public static float[] mapArrays(float[] in, float[] out_) {
    float[] out = new float[out_.length];
    if (in.length == out.length) {
      return in;
    } else {
      for (int i = 0; i < out.length; i++) {
        float index = PApplet.map(i, 0, out.length, 0, in.length);
        int indexBase = PApplet.floor(index);
        float indexLerp = index-indexBase;
        out[i] = PApplet.lerp(in[indexBase], in[PApplet.constrain(indexBase+1, 0, in.length-1)], indexLerp);
      }
    }
    return out;
  }
}
