package com.nosvisuals.engine;

import processing.core.*;

public class LPFArray {
  
  public float decay = 0.01f;

  private int arraySize;
  public float[] lpfOut;
  public int[] lpfOutInt;
  private float[] lpfOutPre;
  public PVector[] lpfOutVector;
  private PVector[] lpfOutPreVector;

  public LPFArray(int inSize) {
    arraySize = inSize;
    lpfOut = new float[arraySize];
    lpfOutPre = new float[arraySize];
    lpfOutVector = new PVector[arraySize];
    lpfOutPreVector = new PVector[arraySize];
    for (int i = 0; i < arraySize; i++) {
      lpfOut[i] = 0.f;
      lpfOutPre[i] = 0.f;
      lpfOutVector[i] = new PVector(0, 0, 0);
      lpfOutPreVector[i] = new PVector(0, 0, 0);
    }
  }

  public LPFArray(int[] intArray) {
    arraySize = intArray.length;
    lpfOutInt = new int[arraySize];
    lpfOut = new float[arraySize];
    lpfOutPre = new float[arraySize];
    for (int i = 0; i < arraySize; i++) {
      lpfOutInt[i] = 0;
      lpfOut[i] = 0.f;
      lpfOutPre[i] = 0.f;
    }
  }

  public LPFArray(float[] floatArray) {
    arraySize = floatArray.length;
    lpfOut = new float[arraySize];
    lpfOutPre = new float[arraySize];
    for (int i = 0; i < arraySize; i++) {
      lpfOut[i] = 0.f;
      lpfOutPre[i] = 0.f;
    }
  }

  public LPFArray(PVector[] vectorArray) {
    arraySize = vectorArray.length;
    lpfOutVector = new PVector[arraySize];
    lpfOutPreVector = new PVector[arraySize];
    for (int i = 0; i < arraySize; i++) {
      lpfOutVector[i] = new PVector(0, 0, 0);
      lpfOutPreVector[i] = new PVector(0, 0, 0);
    }
  }

  public int[] lpfArray(int[] in) {
    arraySize = in.length;
    for (int i = 0; i < arraySize; i++) {
      lpfOut[i] = (decay * in[i] + (1-decay) * lpfOutPre[i]);
      lpfOutPre[i] = lpfOut[i];
      lpfOutInt[i] = (int)lpfOut[i];
    }
    return lpfOutInt;
  }

  public float[] lpfArray(float[] in) {
    arraySize = in.length;
    for (int i = 0; i < arraySize; i++) {
      lpfOut[i] = (decay * in[i] + (1-decay) * lpfOutPre[i]);
      lpfOutPre[i] = lpfOut[i];
    }
    return lpfOut;
  }

  public PVector[] lpfArray(PVector[] in) {
    arraySize = in.length;
    for (int i = 0; i < arraySize; i++) {
      lpfOutVector[i].set(PVector.add(PVector.mult(in[i], decay), PVector.mult(lpfOutPreVector[i], (1-decay))));
      lpfOutPreVector[i].set(lpfOutVector[i]);
    }
    return lpfOutVector;
  }
}
