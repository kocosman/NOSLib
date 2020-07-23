package com.nosvisuals.engine;

import processing.core.PApplet;
import processing.core.*;
import ddf.minim.analysis.*;
import ddf.minim.*;
import java.awt.*;

/*
input - mic

output
- soundInput (raw wave)
- soundLevel
- soundLevelLPF
- soundSpectrum
- soundSpectrumLPF
- soundFocusSpectrum
- soundFocusSpectrumLPF
- soundFocusLevel
- soundFocusLevelLPF

*/

public class SoundAnalyzer {
	private PApplet papplet;
	private Minim minim;

	public int spectrumSize = 256;

	private AudioInput in;
	public int sampleRate = 44100;
	public float[] soundInput = new float[spectrumSize];
	private FFT fftLin;

	public float gain = 1.f;
	public float decay = 0.1f;

	// Sound Level
	public float soundLevel;
	public float soundLevelLPF;
	private LPF soundLevelFilter = new LPF();

	// Sound Spectrum
	public float[] soundSpectrum = new float[spectrumSize];
	public float[] soundSpectrumLPF = new float[spectrumSize];
	private LPFArray soundSpectrumFilter;

	// Sound Focus
	public float[] soundFocusSpectrum;
	public float[] soundFocusSpectrumLPF;
	
	public float soundFocusLevel = 0;
	public float soundFocusLevelLPF = 0;

	public float soundFocusOffset;
	public float soundFocusSize;

	public SoundAnalyzer(PApplet papplet, int spectrumSize) {
		this.papplet = papplet;
		this.spectrumSize = spectrumSize;
		papplet.println("SoundAnalyzer loaded");

		try{
			minim = new Minim(papplet);
			in = minim.getLineIn(1,spectrumSize*2,sampleRate);
		} catch(Exception e){
			papplet.println("MIC UNAVAILABLE");
		}
		
		fftLin = new FFT(in.bufferSize(), in.sampleRate());

		soundFocusOffset = 0;
		soundFocusSize = spectrumSize;
		soundFocusSpectrum = new float[(int)soundFocusSize];
		soundFocusSpectrumLPF = new float[(int)soundFocusSize];
		soundSpectrumFilter = new LPFArray(spectrumSize);
	}

	public SoundAnalyzer(PApplet papplet) {
		this(papplet, 256);
	}

	public void analyze(){
		for(int i = 0; i < soundInput.length; i++){
			soundInput[i] = in.mix.toArray()[i]*gain;
		}

		soundLevel = in.mix.level()*gain;
		soundLevelLPF = soundLevelFilter.lpf(soundLevel);

		fftLin.forward(in.mix);
		for(int i = 0; i < fftLin.specSize() - 1; i++){
			soundSpectrum[i] = fftLin.getBand(i)*gain;
		}
		soundSpectrumLPF = soundSpectrumFilter.lpfArray(soundSpectrum);

		papplet.arrayCopy(soundSpectrum,(int)soundFocusOffset,soundFocusSpectrum,0,(int)soundFocusSize);
		papplet.arrayCopy(soundSpectrumLPF,(int)soundFocusOffset,soundFocusSpectrumLPF,0,(int)soundFocusSize);

		soundFocusLevel = 0;
		soundFocusLevelLPF = 0;
		for(int i = 0; i < (int)soundFocusSize; i++){
			soundFocusLevel += soundFocusSpectrum[i];
			soundFocusLevelLPF += soundFocusSpectrumLPF[i];
		}
		soundFocusLevel /= soundFocusSize;
		soundFocusLevelLPF /= soundFocusSize;

	}

	public void setGain(float gain_){
		gain = gain_;
	}

	public void setDecay(float decay_){
		decay = decay_;
		soundSpectrumFilter.decay = decay;
		soundLevelFilter.decay = decay;
	}

	public float getGain(){
		return gain;
	}

	public float getDecay(){
		return decay;
	}

	public void drawSoundInput(int x_, int y_, int w_, int h_){
		float soundInputMargin = (float)w_/(float)soundInput.length;
		for(int i = 0; i < soundInput.length-1; i++){
			papplet.line(
				x_+(i*soundInputMargin),
				y_+h_/2+soundInput[i]*gain,
				x_+((i+1)*soundInputMargin),
				y_+h_/2+soundInput[i+1]*gain
				);
		}
	}

	public void drawFFTRaw(int x_, int y_, int w_, int h_){
		float fftDrawThickness = (float)w_/(float)soundSpectrum.length;
		for(int i = 0; i < soundSpectrum.length; i++){
			float freqVal = papplet.constrain(soundSpectrum[i],0,h_/2);
			papplet.rect(
				x_+(i*fftDrawThickness),
				y_+((h_/2)-freqVal),
				fftDrawThickness,
				freqVal*2
				);
		}
	}

	public void drawFFTSmooth(int x_, int y_, int w_, int h_){
		float fftDrawThickness = (float)w_/(float)soundSpectrumLPF.length;
		for(int i = 0; i < soundSpectrumLPF.length; i++){
			float freqVal = papplet.constrain(soundSpectrumLPF[i],0,h_/2);
			papplet.rect(
				x_+(i*fftDrawThickness),
				y_+((h_/2)-freqVal),
				fftDrawThickness,
				freqVal*2
				);
		}
	}

	public void setSoundFocus(int offset_, int size_){
		soundFocusSize = size_;
		soundFocusOffset = offset_;
		constrainSoundFocus();
		soundFocusSpectrum = new float[(int)soundFocusSize];
		soundFocusSpectrumLPF = new float[(int)soundFocusSize];
	}

	public void constrainSoundFocus(){
		soundFocusSize = papplet.constrain(soundFocusSize,4,spectrumSize);
		soundFocusOffset = papplet.constrain(soundFocusOffset,0,spectrumSize-soundFocusSize);
	}

	public void drawSoundFocus(int x_, int y_, int w_, int h_){
		float mappedOffset = papplet.map(soundFocusOffset,0,spectrumSize,0,w_);
		float mappedSize = papplet.map(soundFocusSize,0,spectrumSize,0,w_);

		Rectangle rSelf = new Rectangle(x_+(int)mappedOffset-(int)mappedSize/2,y_,(int)mappedSize,h_);

		if(rSelf.contains(papplet.mouseX,papplet.mouseY)){
			papplet.fill(255, 50);
		} else {
			papplet.fill(255, 10);
		}
		papplet.noStroke();
		papplet.rect(rSelf.x,rSelf.y,rSelf.width,rSelf.height);
	}


}

