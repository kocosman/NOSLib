package com.nosvisuals.engine;

import processing.core.*;

public class LPF {
  public float decay;

  private float lpfOut;
  private float lpfOutPre;
  private PVector lpfOutVector = new PVector(0, 0, 0);
  private PVector lpfOutPreVector = new PVector(0, 0, 0);

  public LPF(float decay) {
    this.decay = decay;
  }

  public LPF() {
    this(0.1f);
  }

  public int lpf(int in) {
    lpfOut = (decay * in + (1-decay) * lpfOutPre);
    lpfOutPre = lpfOut;
    return (int)lpfOut;
  }

  public float lpf(float in) {
    lpfOut = (decay * in + (1-decay) * lpfOutPre);
    lpfOutPre = lpfOut;
    return lpfOut;
  }

  public PVector lpf(PVector in) {
    lpfOutVector.set(PVector.add(PVector.mult(in, decay), PVector.mult(lpfOutPreVector, (1-decay))));
    lpfOutPreVector.set(lpfOutVector);
    return lpfOutVector;
  }
}

