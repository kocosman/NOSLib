package com.nosvisuals.engine;

import processing.core.*;
import processing.data.*;
import processing.net.*;

import controlP5.*;
import java.util.*;
import java.io.*;

import peasy.PeasyCam;

// import oscP5.*;
// import netP5.*;


public class NOSLib {
	private PApplet papplet;

	public ControlFrame guiFrame;
	public SoundAnalyzer soundAnalyzer;
	public ArrayList<VisualEngine> engines;

	public int currentEngineIndex = -1;
	public int previousEngineIndex= -1;
	public int nextEngineIndex    = -1;
	public boolean engineChanged  = false;
	public boolean mouseReleased  = false;

	public int soundFocusOffset;
	public int soundFocusSize;

	//PRESETS
	public int presetIndex        = -1;
	public int savePreset         = 0;
	public boolean presetChanged  = false;

	// PEASYCAM
	public PeasyCam cam;
	public float[] camRotations   = new float[3];
	public float[] camLookAt      = new float[3];
	public float camDistance;

	// CAMERA
	public float camRotX;
	public float camRotY;
	public float camRotZ;

	public float camRotXInc;
	public float camRotYInc;
	public float camRotZInc;

	public float camRotXIncPre;
	public float camRotYIncPre;
	public float camRotZIncPre;

	public float camRotXRef;
	public float camRotYRef;
	public float camRotZRef;

	public boolean stopRotX;
	public boolean stopRotY;
	public boolean stopRotZ;
	
	// SERVER / OSC
	public String ipAddress       = "";
	public int incomingPort;
	public int outgoingPort;
	public OscInterface osc;
	private int flushCounter      = 0;

	//-------

	// public OscP5 osc;
	// public NetAddress myRemoteLocation;

	// public boolean newMessage  = false;
	// public String oscAddr      = "";
	// public float[] oscData;
	// public boolean isConnected = false;

	//-------

	// MIDI	
	public MidiInterface midi;

	// FOREGROUND
	public float trailAlpha       = 0.f;
	public float blackAlpha       = 0.f;

	// PREVIEW
	public PreviewServer previewServer;

	public float aspectRatio;
	public String path = "";

	// PRINT FLAGS
	public boolean printMidi      = false;
	public boolean printValueChange          = false;
	public boolean printDebug     = true;
	public boolean printOsc       = true;


	public NOSLib(PApplet _papplet) {
		this.papplet   = _papplet;
		papplet.println("noslib setup start papplet");
		String selfPath= NOSLib.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		selfPath       = selfPath.substring(0, selfPath.lastIndexOf('/')+1);
		papplet.println("NOSLib path         : " + selfPath);
		papplet.println("NOSLib loaded");

		aspectRatio    = papplet.width/papplet.height;

		path= papplet.sketchPath();


		guiFrame       = new ControlFrame(papplet);
		guiFrame.path  = path;
		soundAnalyzer  = new SoundAnalyzer(papplet);

		guiFrame.soundSpectrum    = new float[soundAnalyzer.spectrumSize];
		guiFrame.soundSpectrumLPF = new float[soundAnalyzer.spectrumSize];

		for (int i     = 0; i < soundAnalyzer.spectrumSize; i++) {
			guiFrame.soundSpectrum[i]        = 0;
			guiFrame.soundSpectrumLPF[i]     = 0;
		}

		engines        = new ArrayList<VisualEngine>();

		midi= new MidiInterface(papplet);
		midi.printMidi = true;

		papplet.registerMethod("pre", this);
		papplet.registerMethod("post", this);
		papplet.registerMethod("stop", this);
		
		cam = new PeasyCam(papplet, 100);
		cam.setMinimumDistance(1);
		cam.setMaximumDistance(5000000);
		papplet.perspective(papplet.PI / 3, papplet.width/(float)papplet.height, 1, 10000000);

		//ipAddress    = Server.ip();
		osc = new OscInterface();
		// osc.papplet = papplet;


		previewServer  = new PreviewServer(papplet);
		try{
			changeVisualEngine(0);
			engines.get(0).showGUI(true);
		} catch (Exception e){
			papplet.println("Error visual engine 0");
		}

	}

	public void pre() {
		if (guiFrame.guiLoaded) {
			mapPresets();
			mapMidi();
			if(printOsc != osc.printEnable){
				osc.printEnable   = printOsc;
			}


			try{
				VisualEngine veTemp = engines.get(currentEngineIndex);
				for (VisualEngineParameter vepTemp : veTemp.vep) {
					if (vepTemp.type.equals("radioButton")) {
						vepTemp.renderRadio();
					} else {
						vepTemp.getValuesFromGUI();
					}
				}
			} catch(Exception e){
				if(printDebug){
					consoleOut("Debug: Pre error");	
				}
				
			}


			if (guiFrame.exportAndroid) {
				exportJSONforAndroid();
				guiFrame.exportAndroid = false;
			}

			if(guiFrame.oscConfigUpdate){
				saveConfigFile();
				guiFrame.oscConfigUpdate = false;
			}

			if(camRotXIncPre != guiFrame.rotX){
				if(printValueChange){
					consoleOut("Value Changed: " + "rotX" + " - " + guiFrame.rotX);
				}
				
				camRotXIncPre     = guiFrame.rotX;

				if(osc.isConnected && !osc.newMessage){
					osc.oscSendArray("/rotX",new float[]{guiFrame.rotX});
				}
			}

			if(camRotYIncPre != guiFrame.rotY){
				if(printValueChange){
					consoleOut("Value Changed: " + "rotY" + " - " + guiFrame.rotY);
				}
				camRotYIncPre     = guiFrame.rotY;

				if(osc.isConnected && !osc.newMessage){
					osc.oscSendArray("/rotY",new float[]{guiFrame.rotY});
				}
			}

			if(camRotZIncPre != guiFrame.rotZ){
				if(printValueChange){
					consoleOut("Value Changed: " + "rotZ" + " - " + guiFrame.rotZ);
				}
				camRotZIncPre     = guiFrame.rotZ;

				if(osc.isConnected && !osc.newMessage){
					osc.oscSendArray("/rotZ",new float[]{guiFrame.rotZ});
				}
			}


			if(!guiFrame.stopRotX){
				camRotXInc        = guiFrame.rotX;
			} else {
				camRotXInc *= 0.9;
			}

			if(!guiFrame.stopRotY){
				camRotYInc        = guiFrame.rotY;
			} else {
				camRotYInc *= 0.9;
			}

			if(!guiFrame.stopRotZ){
				camRotZInc        = guiFrame.rotZ;
			} else {
				camRotZInc *= 0.9;
			}
			
			if((guiFrame.stopRotX != stopRotX)){
				if(printValueChange){
					consoleOut("Value Changed: " + "stopRotX" + " - " + guiFrame.stopRotX);
				}

				if(guiFrame.stopRotX){
					if(osc.isConnected && !osc.newMessage){
						osc.oscSendArray("/stopRotX",new float[]{1.f});
					}
				} else if(!guiFrame.stopRotX){
					if(osc.isConnected && !osc.newMessage){
						osc.oscSendArray("/stopRotX",new float[]{0.f});
					}
				}
			}

			if(guiFrame.stopRotY != stopRotY){
				if(printValueChange){
					consoleOut("Value Changed: " + "stopRotY" + " - " + guiFrame.stopRotY);
				}

				if(guiFrame.stopRotY){
					if(osc.isConnected && !osc.newMessage){
						osc.oscSendArray("/stopRotY",new float[]{1.f});
					}
				} else if(!guiFrame.stopRotY){
					if(osc.isConnected && !osc.newMessage){
						osc.oscSendArray("/stopRotY",new float[]{0.f});
					}
				}
			}

			if(guiFrame.stopRotZ != stopRotZ){
				if(printValueChange){
					consoleOut("Value Changed: " + "stopRotZ" + " - " + guiFrame.stopRotZ);
				}

				if(guiFrame.stopRotZ){
					if(osc.isConnected && !osc.newMessage){
						osc.oscSendArray("/stopRotZ",new float[]{1.f});
					}
				} else if(!guiFrame.stopRotZ){
					if(osc.isConnected && !osc.newMessage){
						osc.oscSendArray("/stopRotZ",new float[]{0.f});
					}
				}
			}

			stopRotX   = guiFrame.stopRotX;
			stopRotY   = guiFrame.stopRotY;
			stopRotZ   = guiFrame.stopRotZ;

			camRotXRef += camRotXInc;
			camRotYRef += camRotYInc;
			camRotZRef += camRotZInc;

			if(guiFrame.rollPlus){
				guiFrame.rollPlus = false;
				camRotZRef += papplet.PI/2;
				if(printValueChange){
					consoleOut("Value Changed: Roll +");
				}
			}

			if(guiFrame.rollMinus){
				guiFrame.rollMinus= false;
				camRotZRef += -papplet.PI/2;
				if(printValueChange){
					consoleOut("Value Changed: Roll -");
				}
			}

			if(guiFrame.pitchPlus){
				guiFrame.pitchPlus= false;
				camRotXRef += papplet.PI/2;
				if(printValueChange){
					consoleOut("Value Changed: Pitch +");
				}
			}

			if(guiFrame.pitchMinus){
				guiFrame.pitchMinus          = false;
				camRotXRef += -papplet.PI/2;
				if(printValueChange){
					consoleOut("Value Changed: Pitch -");
				}
			}

			if(guiFrame.yawPlus){
				guiFrame.yawPlus  = false;
				camRotYRef += papplet.PI/2;
				if(printValueChange){
					consoleOut("Value Changed: Yaw +");
				}
			}

			if(guiFrame.yawMinus){
				guiFrame.yawMinus = false;
				camRotYRef += -papplet.PI/2;
				if(printValueChange){
					consoleOut("Value Changed: Yaw -");
				}
			}


			if(guiFrame.resetRot){
				camRotX= camRotX%papplet.TWO_PI;
				camRotY= camRotY%papplet.TWO_PI;
				camRotZ= camRotZ%papplet.TWO_PI;

				camRotXRef        = 0;
				camRotYRef        = 0;
				camRotZRef        = 0;
				guiFrame.resetRot = false;
			}

			if(guiFrame.randomRot){
				camRotX= camRotX%papplet.TWO_PI;
				camRotY= camRotY%papplet.TWO_PI;
				camRotZ= camRotZ%papplet.TWO_PI;

				camRotXRef        = papplet.random(papplet.TWO_PI);
				camRotYRef        = papplet.random(papplet.TWO_PI);
				camRotZRef        = papplet.random(papplet.TWO_PI);
				guiFrame.randomRot= false;

				if(printValueChange){
					consoleOut("Value Changed: " + camRotXRef + " - " + camRotYRef + " - " + camRotZRef);
				}
			}

			camRotX += (camRotXRef-camRotX)*0.1;
			camRotY += (camRotYRef-camRotY)*0.1;
			camRotZ += (camRotZRef-camRotZ)*0.1;
			

			papplet.rotateX(camRotX);
			papplet.rotateY(camRotY);
			papplet.rotateZ(camRotZ);


			if(osc.isConnected){
				if(osc.newMessage){
					getOscData(osc.oscAddr,osc.oscData);
				}
			}


		}
		backgroundRender();
	}

	public void loadConfigFile(){
		try{
			JSONArray config      = papplet.loadJSONArray("data/config.json");

			JSONObject soundParams= config.getJSONObject(0);
			consoleOut("soundFocusSize       : " + soundParams.getInt("soundFocusSize"));
			consoleOut("soundFocusOffset     : " + soundParams.getInt("soundFocusOffset"));
			guiFrame.soundFocusGUI.width     = soundParams.getInt("soundFocusSize");
			guiFrame.soundFocusGUI.x         = soundParams.getInt("soundFocusOffset");

			JSONObject oscParams  = config.getJSONObject(1);
			consoleOut("IncomingPort         : " + oscParams.getInt("incomingPort"));
			consoleOut("Remote IP : " + oscParams.getString("remoteIP"));
			consoleOut("OutgoingPort         : " + oscParams.getInt("outgoingPort"));
			guiFrame.incomingPort = oscParams.getInt("incomingPort");
			guiFrame.outgoingPort = oscParams.getInt("outgoingPort");
			guiFrame.remoteIpAddress         = oscParams.getString("remoteIP");

			JSONObject midiParams = config.getJSONObject(2);
			consoleOut("MIDI Device          : " + midiParams.getInt("midiDeviceIndex"));
			guiFrame.midiDeviceIndex         = midiParams.getInt("midiDeviceIndex");
			guiFrame.midiDeviceName          = midiParams.getString("midiDeviceName");

			JSONObject midiKnobAddresses     = config.getJSONObject(3);
			midi.knobAddress      = new int[midiKnobAddresses.size()];
			for(int i  = 0; i < midiKnobAddresses.size(); i++){
				midi.knobAddress[i]          = midiKnobAddresses.getInt("knob"+i);
			}

			JSONObject midiFaderAddresses    = config.getJSONObject(4);
			midi.faderAddress     = new int[midiFaderAddresses.size()];
			for(int i  = 0; i < midiFaderAddresses.size(); i++){
				midi.faderAddress[i]         = midiFaderAddresses.getInt("fader"+i);
			}
			
			JSONObject midiButtonAddresses   = config.getJSONObject(5);
			midi.buttonAddress    = new int[midiButtonAddresses.size()];
			for(int i  = 0; i < midiButtonAddresses.size(); i++){
				midi.buttonAddress[i]        = midiButtonAddresses.getInt("button"+i);
			}
			midi.updateMidiAddresses();
			
			JSONObject colorPaletteAddress   = config.getJSONObject(6);
			try{

			File colorPaletteFile = new File(colorPaletteAddress.getString("colorPalette"));
			guiFrame.selectColorPalette(colorPaletteFile);	
		} catch(Exception e){
						if(printDebug){
				consoleOut("Load Color Palette from JSON error ");
			}
		}


		} catch(Exception e){
			if(printDebug){
				consoleOut("Debug : Couldn't find Config file");
			}
		}

	}

	public void saveConfigFile(){
		JSONArray config          = new JSONArray();

		JSONObject soundParams    = new JSONObject();
		soundParams.setInt("soundFocusSize", guiFrame.soundFocusGUI.width);
		soundParams.setInt("soundFocusOffset", guiFrame.soundFocusGUI.x);
		config.setJSONObject(0,soundParams);

		JSONObject oscParams      = new JSONObject();
		oscParams.setInt("incomingPort", guiFrame.incomingPort);
		oscParams.setString("remoteIP", guiFrame.remoteIpAddress);
		oscParams.setInt("outgoingPort", guiFrame.outgoingPort);
		config.setJSONObject(1,oscParams);

		JSONObject midiParams     = new JSONObject();
		midiParams.setInt("midiDeviceIndex", guiFrame.midiDeviceIndex);
		midiParams.setString("midiDeviceName", guiFrame.midiDeviceName);
		config.setJSONObject(2,midiParams);

		JSONObject midiKnobAddresses         = new JSONObject();
		for(int i      = 0; i < midi.knobAddress.length; i++){
			midiKnobAddresses.setInt("knob"+i,midi.knobAddress[i]);
		}
		config.setJSONObject(3,midiKnobAddresses);

		JSONObject midiFaderAddresses        = new JSONObject();
		for(int i      = 0; i < midi.faderAddress.length; i++){
			midiFaderAddresses.setInt("fader"+i,midi.faderAddress[i]);
		}
		config.setJSONObject(4,midiFaderAddresses);

		JSONObject midiButtonAddresses       = new JSONObject();
		for(int i      = 0; i < midi.buttonAddress.length; i++){
			midiButtonAddresses.setInt("button"+i,midi.buttonAddress[i]);
		}
		config.setJSONObject(5,midiButtonAddresses);

		JSONObject colorPaletteAddress       = new JSONObject();
		colorPaletteAddress.setString("colorPalette", guiFrame.lastColorPaletteAddress);
		config.setJSONObject(6,colorPaletteAddress);
		
		if(printDebug){
			consoleOut("Debug     : SAVING CONFIG FILE");
		}
		
		papplet.saveJSONArray(config, "data/config.json");
	}

	public void post() {
		if (guiFrame.guiLoaded) {
			if(engineChanged || guiFrame.thumbnailClicked){
				if(osc.isConnected){
					osc.oscSendArray("/thumbnailIndex",new float[]{guiFrame.selectedThumbnailIndex});
				}
			}

			VisualEngine veTemp   = engines.get(currentEngineIndex);
			for (VisualEngineParameter vepTemp     : veTemp.vep) {
				if (vepTemp.type.equals("bang")) {
					vepTemp.update(0.f);
				} else if (vepTemp.type.equals("button")) {
					if (guiFrame.mouseReleased) {
						vepTemp.update(0.f);
						guiFrame.mouseReleased     = false;
					}
				}

				if(engineChanged || presetChanged || guiFrame.thumbnailClicked){
					if(osc.isConnected && !osc.newMessage){
						osc.oscSendArray('/' + vepTemp.name,new float[]{vepTemp.value});
					}
				} else {
					if(vepTemp.checkChange()){
						if(printValueChange){
							consoleOut("Value Changed         : " + '/' + vepTemp.name + " - " + vepTemp.value);
						}
						
						if(osc.isConnected && !osc.newMessage){
							osc.oscSendArray('/' + vepTemp.name,new float[]{vepTemp.value});
						}
					}	
				}
			}


			if(engineChanged && engines.get(currentEngineIndex).startFromFirstPreset){
				if(printValueChange){
					consoleOut("Value Changed: Changing to PRESET 1");	
				}
				
				presetIndex       = -1;
				guiFrame.presetsRadio.activate(0);

				camRotXRef        = 0;
				camRotYRef        = 0;
				camRotZRef        = 0;
			}



			if(osc.isConnected){
				if(osc.newMessage){
					osc.newMessage= false;
				}
			}

			presetChanged         = false;
			engineChanged         = false;
		}
	}

	public void getOscData(String _addr, float[] _data){

		if(_addr.contains(engines.get(currentEngineIndex).name)){
			if(printDebug){
				consoleOut("Debug : gotVSP");	
			}
			
			for(int i  = 0; i < engines.get(currentEngineIndex).vep.size(); i++){
				VisualEngineParameter vepTemp= engines.get(currentEngineIndex).vep.get(i);
				if(vepTemp.name.equals(_addr.substring(1,_addr.length()))){
					if(printDebug){
						consoleOut("Debug    : " + vepTemp.name);
					}
					if(vepTemp.type.equals("toggle")){
						vepTemp.setToggle(_data[0]);
					} else if (vepTemp.type.equals("knob")||vepTemp.type.equals("fader")||vepTemp.type.equals("button")){
						vepTemp.update(_data[0]);
					} else if(vepTemp.type.equals("bang")){
						vepTemp.setValuesFromGUI(1.f);
					} else if(vepTemp.type.equals("radioButton")){
						if(_data[0]          == -1){
							vepTemp.deactivateAll();
						} else {
							vepTemp.activateRadio((int)_data[0]);
						}
					}
				}
			}
		} else if(_addr.equals("/selectVisual")){
			guiFrame.selectedThumbnailIndex  = (int)_data[0];
		} else if(_addr.equals("/blackAlpha")){
			guiFrame.cp5.getController("blackAlpha").setValue(_data[0]);
		} else if(_addr.equals("/trailAlpha")){
			guiFrame.cp5.getController("trailAlpha").setValue(_data[0]);

		} else if(_addr.equals("/randomAngle")){
			guiFrame.randomRot    = true;
		} else if(_addr.equals("/resetAngle")){
			guiFrame.resetRot     = true;

		} else if(_addr.equals("/plusRoll")){
			guiFrame.rollPlus     = true;
		} else if(_addr.equals("/minusRoll")){
			guiFrame.rollMinus    = true;
		} else if(_addr.equals("/plusPitch")){
			guiFrame.pitchPlus    = true;
		} else if(_addr.equals("/minusPitch")){
			guiFrame.pitchMinus   = true;
		} else if(_addr.equals("/plusYaw")){
			guiFrame.yawPlus      = true;
		} else if(_addr.equals("/minusYaw")){
			guiFrame.yawMinus     = true;

		} else if(_addr.equals("/rotX")){
			guiFrame.cp5.getController("rotX").setValue(_data[0]);
		} else if(_addr.equals("/rotY")){
			guiFrame.cp5.getController("rotY").setValue(_data[0]);
		} else if(_addr.equals("/rotZ")){
			guiFrame.cp5.getController("rotZ").setValue(_data[0]);

		} else if(_addr.equals("/stopRotX")){
			guiFrame.cp5.getController("stopRotX").setValue(_data[0]);
		} else if(_addr.equals("/stopRotY")){
			guiFrame.cp5.getController("stopRotY").setValue(_data[0]);
		} else if(_addr.equals("/stopRotZ")){
			guiFrame.cp5.getController("stopRotZ").setValue(_data[0]);

		} else if(_addr.equals("/strokeSaturation")){
			guiFrame.cp5.getController("strokeSaturation").setValue(_data[0]);
		} else if(_addr.equals("/strokeAlpha")){
			guiFrame.cp5.getController("strokeAlpha").setValue(_data[0]);
		} else if(_addr.equals("/fillSaturation")){
			guiFrame.cp5.getController("fillSaturation").setValue(_data[0]);
		} else if(_addr.equals("/fillAlpha")){
			guiFrame.cp5.getController("fillAlpha").setValue(_data[0]);

		} else if(_addr.equals("/colorFocus")){
			guiFrame.colorFocusGUI.x         = (int)papplet.map(_data[1], 0.f, 1.f, guiFrame.colorPreviewGUI.x, guiFrame.colorPreviewGUI.x+guiFrame.colorPreviewGUI.width);
			guiFrame.colorFocusGUI.width     = (int)papplet.map(_data[0], 0.f, 1.f, 0.f, guiFrame.colorPreviewGUI.width);
			guiFrame.colorFocusGUI.width     = papplet.constrain(guiFrame.colorFocusGUI.width,4,guiFrame.colorPreviewGUI.width);
			guiFrame.colorFocusGUI.x         = papplet.constrain(guiFrame.colorFocusGUI.x,guiFrame.colorPreviewGUI.x,guiFrame.colorPreviewGUI.x+guiFrame.colorPreviewGUI.width-guiFrame.colorFocusGUI.width);

		} else if(_addr.equals("/soundFocus")){
			guiFrame.soundFocusGUI.width     = (int)papplet.map(_data[0], 0.f, 1.f, 0.f, guiFrame.soundWavePanel.width);
			guiFrame.soundFocusGUI.x         = (int)papplet.map(_data[1], 0.f, 1.f, guiFrame.soundWavePanel.x, guiFrame.soundWavePanel.x+guiFrame.soundWavePanel.width);

		} else if(_addr.equals("/soundGain")){
			guiFrame.cp5.getController("guiSoundGain").setValue(_data[0]);
			if(printDebug){
				consoleOut("Debug : sounGain " + _data[0]);	
			}
			
		} else if(_addr.equals("/soundDecay")){
			guiFrame.cp5.getController("guiSoundDecay").setValue(_data[0]);

		} else if(_addr.equals("/sync")){
			guiFrame.oscFlush     = true;
			flushCounter          = 0;

		} else if(_addr.equals("/savePreset")){
			guiFrame.cp5.getController("savePreset").setValue(_data[0]);
		} else if(_addr.equals("/preset")){
			if(_data[0]== -1){
				guiFrame.presetsRadio.deactivateAll();
			} else {
				guiFrame.presetsRadio.activate((int)_data[0]);
			}
		} 

	}

	public void addVisualEngine(VisualEngine _ve) {

		engines.add(_ve);

		JSONArray tempJSON        = new JSONArray();
		try{
			for(int i  = 0; i < 8; i++){
				tempJSON          = papplet.loadJSONArray("data/presets/"+_ve.name+i+".json");			
			}
		} catch(Exception e){
			if(printDebug){
				consoleOut("Debug : Error loading Presets");	
			}
			
			tempJSON   = new JSONArray();
			for(int i  = 0; i < 8; i++){
				papplet.saveJSONArray(tempJSON, "data/presets/"+_ve.name+i+".json");
			}
		}		
	}

	public JSONObject setCamAsJSON() {
		JSONObject camJSON        = new JSONObject();

		camJSON.setFloat("camX", cam.getLookAt()[0]);
		camJSON.setFloat("camY", cam.getLookAt()[1]);
		camJSON.setFloat("camZ", cam.getLookAt()[2]);

		camJSON.setFloat("camRotX", cam.getRotations()[0]);
		camJSON.setFloat("camRotY", cam.getRotations()[1]);
		camJSON.setFloat("camRotZ", cam.getRotations()[2]);

		camJSON.setFloat("camDistance", (float)cam.getDistance());

		return camJSON;
	}

	public void getCamAsJSON(JSONObject _cam) {
		try {
			cam.lookAt(_cam.getFloat("camX"), _cam.getFloat("camY"), _cam.getFloat("camZ"));
			cam.setRotations(_cam.getFloat("camRotX"), _cam.getFloat("camRotY"), _cam.getFloat("camRotZ"));
			cam.setDistance(_cam.getFloat("camDistance"));
		} 
		catch(Exception e) {
			if(printDebug){
				papplet.println("Debug: GetCamAsJSON problem");
			}
		}
	}

	public void mapPresets() {

		if (guiFrame.cp5.getController("savePreset").getValue() == 0) {
			if (guiFrame.presetsRadio.getValue() != presetIndex) {
				presetIndex = (int)guiFrame.presetsRadio.getValue();

				getCamAsJSON(engines.get(currentEngineIndex).loadPreset(presetIndex));

				guiFrame.cp5.getController("strokeSaturation").setValue(engines.get(currentEngineIndex).guiStrokeSaturation);
				guiFrame.cp5.getController("strokeAlpha").setValue(engines.get(currentEngineIndex).guiStrokeAlpha);
				guiFrame.cp5.getController("fillSaturation").setValue(engines.get(currentEngineIndex).guiFillSaturation);
				guiFrame.cp5.getController("fillAlpha").setValue(engines.get(currentEngineIndex).guiFillAlpha);

				presetChanged = true;
				if(printValueChange){
					consoleOut("Value Changed: " + "preset" + " - " + presetIndex);	
				}
				
				if(osc.isConnected){
					osc.oscSendArray("/preset",new float[]{presetIndex});
				}

			}
		}

		if(savePreset != guiFrame.cp5.getController("savePreset").getValue()){
			if(osc.isConnected){
				osc.oscSendArray("/savePreset",new float[]{guiFrame.cp5.getController("savePreset").getValue()});
			}
		}

		if ((savePreset== 0) && (guiFrame.cp5.getController("savePreset").getValue() != 0)) {
			presetIndex= (int)guiFrame.presetsRadio.getValue();
			boolean saveSuccess   = engines.get(currentEngineIndex).savePreset(presetIndex, setCamAsJSON());
			if (saveSuccess) {
				papplet.println("Preset "+ (presetIndex+1) + " for " + engines.get(currentEngineIndex).name + " saved!");
			}
		}

		if (guiFrame.cp5.getController("savePreset").getValue() != 0) {
			if (guiFrame.presetsRadio.getValue() != presetIndex) {
				presetIndex       = (int)guiFrame.presetsRadio.getValue();
				boolean saveSuccess          = engines.get(currentEngineIndex).savePreset(presetIndex, setCamAsJSON());
				if (saveSuccess) {
					papplet.println("Preset "+ (presetIndex+1) + " for " + engines.get(currentEngineIndex).name + " saved!");
				}
			}
		}

		savePreset = (int)guiFrame.cp5.getController("savePreset").getValue();
	}

	public void mapMidi() {
		if(midi.printMidi != printMidi){
			midi.printMidi = printMidi;
		}

		if (midi.midiPlugged && midi.newMidiData) {
			VisualEngine veTemp = engines.get(currentEngineIndex);
			for (VisualEngineParameter vepTemp : veTemp.vep) {
				for (MidiData m : midi.midiData) {
					float incomingMidiValue  = papplet.map(m.data, 0, 127, vepTemp.min, vepTemp.max);
					float incomingMidiDiff   = 0;
					if (m.type.equals("knob") && vepTemp.type.equals("knob") && (m.index == vepTemp.index)) {
						incomingMidiDiff = papplet.map(m.getDiff(), 0, 127, 0, vepTemp.max-vepTemp.min);
						vepTemp.update(vepTemp.value+incomingMidiDiff);
					} else if (m.type.equals("fader") && vepTemp.type.equals("fader") && m.index == vepTemp.index) {
						incomingMidiDiff     = papplet.map(m.getDiff(), 0, 127, 0,vepTemp.max-vepTemp.min);
						vepTemp.update(vepTemp.value+incomingMidiDiff);
					} else if (m.type.equals("toggle") && vepTemp.type.equals("toggle") && m.index == vepTemp.index) {
						vepTemp.update(incomingMidiValue);
					} else if (m.type.equals("toggle") && vepTemp.type.equals("bang") && m.index == vepTemp.index) {
						vepTemp.update(incomingMidiValue);
					} else if (m.type.equals("toggle") && vepTemp.type.equals("button") && m.index == vepTemp.index) {
						vepTemp.update(incomingMidiValue);
					} else if (m.type.equals("toggle") && vepTemp.type.equals("radioButton") && (m.index >= vepTemp.index) && (m.index < vepTemp.index+vepTemp.length)) {
						vepTemp.toggleRadio(incomingMidiValue, (m.index - vepTemp.index));
					}
				}

			}

			for (MidiData m : midi.midiData) {

				if((m.type.equals("knob")) && (m.index == 6)){
					guiFrame.cp5.getController("strokeSaturation").setValue(guiFrame.cp5.getController("strokeSaturation").getValue()+papplet.map(m.getDiff(), 0, 127, 0,255));
				} else if((m.type.equals("knob")) && (m.index == 7)){
					guiFrame.cp5.getController("strokeAlpha").setValue(guiFrame.cp5.getController("strokeAlpha").getValue()+papplet.map(m.getDiff(), 0, 127, 0,255));
				} else if((m.type.equals("fader")) && (m.index== 6)){
					guiFrame.cp5.getController("fillSaturation").setValue(guiFrame.cp5.getController("fillSaturation").getValue()+papplet.map(m.getDiff(), 0, 127, 0,255));
				} else if((m.type.equals("fader")) && (m.index== 7)){
					guiFrame.cp5.getController("fillAlpha").setValue(guiFrame.cp5.getController("fillAlpha").getValue()+papplet.map(m.getDiff(), 0, 127, 0,255));
				} 
			}

			midi.newMidiData = false;
			midi.newMidiDataAddress = -1;
		}
	}

	public void draw() {
		try{
			initializeEnvironment();	
		} catch (Exception e){
			if(printDebug){
				consoleOut("Debug : initializeEnvironment error");	
			}
			
		}

		try{
			//------SOUND
			guiFrame.setSoundWaveArrays(soundAnalyzer.soundSpectrum, soundAnalyzer.soundSpectrumLPF);

			soundFocusSize        = (int)papplet.map(guiFrame.soundFocusGUI.width, 0, guiFrame.soundWavePanel.width, 0, soundAnalyzer.spectrumSize);
			soundFocusOffset      = (int)papplet.map(guiFrame.soundFocusGUI.x, guiFrame.soundWavePanel.x, guiFrame.soundWavePanel.x+guiFrame.soundWavePanel.width-guiFrame.soundFocusGUI.width, 0, soundAnalyzer.spectrumSize);

			soundFocusSize        = papplet.constrain(soundFocusSize,4,soundAnalyzer.spectrumSize);
			soundFocusOffset      = papplet.constrain(soundFocusOffset,0,soundAnalyzer.spectrumSize-soundFocusSize);

			if((soundFocusSize != soundAnalyzer.soundFocusSize) || (soundFocusOffset != soundAnalyzer.soundFocusOffset)){
				float soundFocusSizeNormalized     = papplet.map(guiFrame.soundFocusGUI.width, 0, guiFrame.soundWavePanel.width, 0.f, 1.f);
				// float soundFocusOffsetNormalized= papplet.map(guiFrame.soundFocusGUI.x, guiFrame.soundWavePanel.x, guiFrame.soundWavePanel.x+guiFrame.soundWavePanel.width-guiFrame.soundFocusGUI.width, 0.f ,1.f);
				float soundFocusOffsetNormalized   = papplet.map(guiFrame.soundFocusGUI.x, guiFrame.soundWavePanel.x, guiFrame.soundWavePanel.x+guiFrame.soundWavePanel.width, 0.f ,1.f);

				soundAnalyzer.setSoundFocus(soundFocusOffset, soundFocusSize);	
				if(osc.isConnected && !osc.newMessage){
					osc.oscSendArray("/soundFocus",new float[]{soundFocusSizeNormalized,soundFocusOffsetNormalized});
				}
			}

			soundAnalyzer.analyze();

			if(soundAnalyzer.getGain() != guiFrame.guiSoundGain){
				if(printValueChange){
					consoleOut("Value Changed: " + "soundGain" + " - " + guiFrame.guiSoundGain);	
				}
				
				if(osc.isConnected && !osc.newMessage){
					osc.oscSendArray("/soundGain",new float[]{guiFrame.guiSoundGain});
				}
			}

			if(soundAnalyzer.getDecay() != guiFrame.guiSoundDecay){
				if(printValueChange){
					consoleOut("Value Changed: " + "soundDecay" + " - " + guiFrame.guiSoundDecay);
				}
				
				if(osc.isConnected && !osc.newMessage){
					osc.oscSendArray("/soundDecay",new float[]{guiFrame.guiSoundDecay});
				}
			}

			soundAnalyzer.setGain(guiFrame.guiSoundGain);
			soundAnalyzer.setDecay(guiFrame.guiSoundDecay);
			//------SOUND
		} catch(Exception e){
			if(printDebug){
				consoleOut("Debug : NOSlib Draw Sound Error");	
			}
			
		}


		try{
			if(osc.isConnected){
				if(osc.newMessage){
					getOscData(osc.oscAddr,osc.oscData);
				}
			}
			
		}catch(Exception e){
			papplet.println("NOSLib GetOSCData Error");
		}


			try{
				if (currentEngineIndex != guiFrame.selectedThumbnailIndex) {
					if(printValueChange){
						consoleOut("Value Changed  : " + "selectedThumbnail" + " - " + guiFrame.selectedThumbnailIndex);	
					}
					
					if(osc.isConnected && !osc.newMessage){
						osc.oscSendArray("/thumbnailIndex",new float[]{guiFrame.selectedThumbnailIndex});
					}
					changeVisualEngine(guiFrame.selectedThumbnailIndex);
				}				
			}catch(Exception e){
				papplet.println("NOSLib Visual Change Error");
				//guiFrame.loadColorPalette();
			}



			try{
				if (currentEngineIndex       == guiFrame.selectedThumbnailIndex) {
					if(engines.get(currentEngineIndex).guiStrokeSaturation != guiFrame.strokeSaturation){
						if(printValueChange){
							consoleOut("Value Changed         : " + "strokeSaturation" + " - " + guiFrame.strokeSaturation);	
						}
						engines.get(currentEngineIndex).guiStrokeSaturation         =	guiFrame.strokeSaturation 		;
						if(osc.isConnected && !osc.newMessage){
							osc.oscSendArray("/strokeSaturation",new float[]{guiFrame.strokeSaturation});
						}
					}
					if(engines.get(currentEngineIndex).guiStrokeAlpha != guiFrame.strokeAlpha){
						if(printValueChange){
							consoleOut("Value Changed         : " + "strokeAlpha" + " - " + guiFrame.strokeAlpha);	
						}
						
						engines.get(currentEngineIndex).guiStrokeAlpha   =	guiFrame.strokeAlpha 		;
						if(osc.isConnected && !osc.newMessage){
							osc.oscSendArray("/strokeAlpha",new float[]{guiFrame.strokeAlpha});
						}
					}
					if(engines.get(currentEngineIndex).guiFillSaturation != guiFrame.fillSaturation){
						if(printValueChange){
							consoleOut("Value Changed         : " + "fillSaturation" + " - " + guiFrame.fillSaturation);	
						}
						
						engines.get(currentEngineIndex).guiFillSaturation=	guiFrame.fillSaturation 		;
						if(osc.isConnected && !osc.newMessage){
							osc.oscSendArray("/fillSaturation",new float[]{guiFrame.fillSaturation});
						}
					}
					if(engines.get(currentEngineIndex).guiFillAlpha != guiFrame.fillAlpha){
						if(printValueChange){
							consoleOut("Value Changed         : " + "fillAlpha" + " - " + guiFrame.fillAlpha);
						}
						
						engines.get(currentEngineIndex).guiFillAlpha     =	guiFrame.fillAlpha 		;
						if(osc.isConnected && !osc.newMessage){
							osc.oscSendArray("/fillAlpha",new float[]{guiFrame.fillAlpha});
						}
					}

				}				
			}catch(Exception e){
				papplet.println("NOSLib SaturationAlpha Change Error");
				//guiFrame.loadColorPalette();
			}


			try{
				if (currentEngineIndex       == guiFrame.selectedThumbnailIndex) {
					engines.get(currentEngineIndex).soundInput=	soundAnalyzer.soundInput				;
					engines.get(currentEngineIndex).soundLevel=	soundAnalyzer.soundLevel  			;
					engines.get(currentEngineIndex).soundLevelLPF        =	soundAnalyzer.soundLevelLPF 			;
					engines.get(currentEngineIndex).soundSpectrum        =	soundAnalyzer.soundSpectrum 			;
					engines.get(currentEngineIndex).soundSpectrumLPF     =	soundAnalyzer.soundSpectrumLPF 		;
					engines.get(currentEngineIndex).soundFocusSpectrum   =	soundAnalyzer.soundFocusSpectrum 		;
					engines.get(currentEngineIndex).soundFocusSpectrumLPF=	soundAnalyzer.soundFocusSpectrumLPF 	;
					engines.get(currentEngineIndex).soundFocusLevel      =	soundAnalyzer.soundFocusLevel 		;
					engines.get(currentEngineIndex).soundFocusLevelLPF   =	soundAnalyzer.soundFocusLevelLPF 		;

				}				
			}catch(Exception e){
				papplet.println("NOSLib SoundParams Error");
				//guiFrame.loadColorPalette();
			}

		try{
 			if (currentEngineIndex== guiFrame.selectedThumbnailIndex) 
			 {
				if (currentEngineIndex>-1) {
					
				
					if(guiFrame.colorFocusChanged){
						engines.get(currentEngineIndex).colorPalette     = 	new int[guiFrame.colorFocusGUI.width];
						engines.get(currentEngineIndex).colorPalette     =	guiFrame.colorPaletteFocus 		;	

						float colorFocusSizeNormalized        = papplet.map(guiFrame.colorFocusGUI.width, 0.f, guiFrame.colorPreviewGUI.width, 0.f, 1.f);
						float colorFocusOffsetNormalized      = papplet.map(guiFrame.colorFocusGUI.x, guiFrame.colorPreviewGUI.x, guiFrame.colorPreviewGUI.x+guiFrame.colorPreviewGUI.width-guiFrame.colorFocusGUI.width, 0.f ,1.f);

						if(osc.isConnected && !osc.newMessage){
							osc.oscSendArray("/colorFocus",new float[]{colorFocusSizeNormalized, colorFocusOffsetNormalized});
						}
						guiFrame.colorFocusChanged = false;
					}

				}
			}

		} 
		catch(Exception e){
			papplet.println("NOSLib Color Focus Error");
			//guiFrame.loadColorPalette();
		}
		

		// try{
 			if (currentEngineIndex== guiFrame.selectedThumbnailIndex) 
			 {
					engines.get(currentEngineIndex).update();
			}

		// } 
		// catch(Exception e){
		// 	papplet.println("NOSLib Engine Updat eError");
		// }
		

		try{
			if(guiFrame.midiLearn){
				if(midi.newMidiData){
					if(midi.newMidiDataAddress != -1){
						if(guiFrame.learnType.equals("fader")){
							midi.faderAddress[guiFrame.learnAddress]     = midi.newMidiDataAddress;
						} else if(guiFrame.learnType.equals("knob")){
							midi.knobAddress[guiFrame.learnAddress]      = midi.newMidiDataAddress;
						} else if(guiFrame.learnType.equals("button")){
							midi.buttonAddress[guiFrame.learnAddress]    = midi.newMidiDataAddress;
						}
						guiFrame.learnType   = "";
						guiFrame.learnAddress= -1;
						midi.updateMidiAddresses();
						saveConfigFile();
					}
				}
			}


			if(guiFrame.midiAttempt){
				guiFrame.midiAttempt         = false;
				midi.connect(guiFrame.midiDeviceIndex, guiFrame.midiDeviceName);
				saveConfigFile();
			}

			if(guiFrame.oscAttempt){
				guiFrame.oscAttempt          = false;
				osc.setIpAndPort(guiFrame.incomingPort, guiFrame.remoteIpAddress, guiFrame.outgoingPort);
				consoleOut("My IP : " + ipAddress);
				consoleOut("incomingPort     : " + guiFrame.incomingPort);
				consoleOut("Remote IP        : " + guiFrame.remoteIpAddress);
				consoleOut("outgoingPort     : " + guiFrame.outgoingPort);
				osc.oscConnect();

				// osc = new OscInterface();
				// osc.papplet    = papplet;
				// osc.setIpAndPort(guiFrame.incomingPort,guiFrame.remoteIpAddress,guiFrame.outgoingPort);
				// osc.oscConnect();

			}

			if(guiFrame.oscFlush && osc.isConnected){

				VisualEngine veTemp          = engines.get(currentEngineIndex);

				if(flushCounter   == 0){
					float colorFocusSizeNormalized = papplet.map(guiFrame.colorFocusGUI.width, 0.f, guiFrame.colorPreviewGUI.width, 0.f, 1.f);
					float colorFocusOffsetNormalized          = papplet.map(guiFrame.colorFocusGUI.x, guiFrame.colorPreviewGUI.x, guiFrame.colorPreviewGUI.x+guiFrame.colorPreviewGUI.width-guiFrame.colorFocusGUI.width, 0.f ,1.f);

					float soundFocusSizeNormalized = papplet.map(guiFrame.soundFocusGUI.width, 0, guiFrame.soundWavePanel.width, 0.f, 1.f);
					float soundFocusOffsetNormalized          = papplet.map(guiFrame.soundFocusGUI.x, guiFrame.soundWavePanel.x, guiFrame.soundWavePanel.x+guiFrame.soundWavePanel.width-guiFrame.soundFocusGUI.width, 0.f ,1.f);

					float[] flushData        = {
						guiFrame.blackAlpha,
						guiFrame.trailAlpha,
						guiFrame.rotX,
						guiFrame.rotY,
						guiFrame.rotZ,
						guiFrame.cp5.getController("stopRotX").getValue(),
						guiFrame.cp5.getController("stopRotY").getValue(),
						guiFrame.cp5.getController("stopRotZ").getValue(),
						colorFocusSizeNormalized,
						colorFocusOffsetNormalized,
						guiFrame.strokeSaturation,
						guiFrame.strokeAlpha,
						guiFrame.fillSaturation,
						guiFrame.fillAlpha,
						guiFrame.cp5.getController("savePreset").getValue(),
						guiFrame.presetsRadio.getValue(),
						soundFocusSizeNormalized,
						soundFocusOffsetNormalized,
						guiFrame.guiSoundGain,
						guiFrame.guiSoundDecay,
						guiFrame.selectedThumbnailIndex
					};
					osc.oscSendArray("/flush",flushData);
					flushCounter++;
				} 
				else if(flushCounter < veTemp.vep.size()+1){
					VisualEngineParameter vepTemp  = veTemp.vep.get(flushCounter-1);
					if(osc.isConnected ){
						osc.oscSendArray('/' + vepTemp.name,new float[]{vepTemp.value});
					}					
					flushCounter++;
				} 
				else {
					guiFrame.oscFlush        = false;
					flushCounter  = 0;
				}

			}

			if(guiFrame.newColorPalette){
				guiFrame.newColorPalette     = false;
				saveConfigFile();
			}

			guiFrame.oscConnected = osc.isConnected;
			guiFrame.midiConnected= midi.midiPlugged;


			if(guiFrame.saveNewThumbnail){
				guiFrame.saveNewThumbnail    = false;
				saveFrameAsThumbnail();
			}

			foregroundRender();
			if(guiFrame.previewEnable){
				previewServer.send();	
			}
		} catch(Exception e){
			if(printDebug){
				consoleOut("Debug : NOSLib others error");		
			}
		}
	}


	public void backgroundRender() {
		cam.beginHUD();  
		papplet.noStroke();    
		papplet.blendMode(papplet.SUBTRACT);

		if(guiFrame.trailAlpha != trailAlpha){
			if(printValueChange){
				consoleOut("Value Changed    : " + "Trail" + " - " + guiFrame.trailAlpha);	
			}
			
			trailAlpha = guiFrame.trailAlpha;
			if(osc.isConnected && !osc.newMessage){
				osc.oscSendArray("/trailAlpha",new float[]{guiFrame.trailAlpha});
			}
		}

		if (trailAlpha < 181.f) {
			papplet.fill(255);
		} else {
			papplet.fill(255-trailAlpha);
		}
		papplet.rectMode(papplet.CORNER);
		papplet.rect(0, 0, papplet.width, papplet.height);
		papplet.blendMode(papplet.BLEND);
		cam.endHUD();
	}

	public void foregroundRender() {
		cam.beginHUD();  
		papplet.noLights();
		papplet.specular(0);

		if(guiFrame.blackAlpha != blackAlpha){
			if(printValueChange){
				consoleOut("Value Changed    : " + "Black" + " - " + guiFrame.blackAlpha);	
			}
			
			blackAlpha = guiFrame.blackAlpha;
			if(osc.isConnected && !osc.newMessage){
				osc.oscSendArray("/blackAlpha",new float[]{guiFrame.blackAlpha});
			}
		}

		if (blackAlpha > 1.f) {
			papplet.fill((papplet.color(0) & 0xffffff)|(int)blackAlpha << 24);
		} else {
			papplet.noFill();
		}

		papplet.noStroke();
		papplet.rect(0, 0, papplet.width, papplet.height);
		cam.endHUD();
	}


	public void initializeEnvironment() {

		if (guiFrame.guiReady && !guiFrame.guiDone) {

			loadConfigFile();	
			guiFrame.printDebug   = printDebug;
			try{
				for (int i        = 0; i < engines.size(); i++) {

					VisualEngine ve          = engines.get(i);
					guiFrame.thumbnailNames.add(ve.name);

					ve.soundInput = new float[soundAnalyzer.spectrumSize];
					ve.soundSpectrum         = new float[soundAnalyzer.spectrumSize];
					ve.soundSpectrumLPF      = new float[soundAnalyzer.spectrumSize];
					ve.soundFocusSpectrum    = new float[soundAnalyzer.spectrumSize];
					ve.soundFocusSpectrumLPF = new float[soundAnalyzer.spectrumSize];
					ve.colorPalette          = new int[guiFrame.colorFocusGUI.width];
					ve.cp5        = guiFrame.cp5;
					ve.init();
					ve.initGUI();	


					for (int j    = 0; j < ve.vep.size(); j++) {
						VisualEngineParameter vepTemp         = ve.vep.get(j);
						if (!(vepTemp.type.equals("knob") || 
							vepTemp.type.equals("fader") || 
							vepTemp.type.equals("toggle") || 
							vepTemp.type.equals("button") || 	
							vepTemp.type.equals("bang") || 
							vepTemp.type.equals("radioButton"))) {
							if(printDebug){
								consoleOut(vepTemp.type + " is not a valid type");	
							}
							
						}

						if (vepTemp.type.equals("knob")) {
							vepTemp.controllers.add(guiFrame.addKnob(vepTemp.name, vepTemp.index, vepTemp.min, vepTemp.max, vepTemp.label));
						} else if (vepTemp.type.equals("fader")) {
							vepTemp.controllers.add(guiFrame.addFader(vepTemp.name, vepTemp.index, vepTemp.min, vepTemp.max, vepTemp.label));					
						} else if (vepTemp.type.equals("toggle")) {
							vepTemp.controllers.add(guiFrame.addToggle(vepTemp.name, vepTemp.index, vepTemp.label));
						} else if (vepTemp.type.equals("button")) {
							vepTemp.controllers.add(guiFrame.addToggle(vepTemp.name, vepTemp.index, vepTemp.label));
						} else if (vepTemp.type.equals("bang")) {
							vepTemp.controllers.add(guiFrame.addToggle(vepTemp.name, vepTemp.index, vepTemp.label));
						} else if (vepTemp.type.equals("radioButton")) {
							vepTemp.controllers    = guiFrame.addRadioButton(vepTemp.name, vepTemp.index, vepTemp.length, vepTemp.label);
						}
					}
					ve.plugToParameters();
					ve.showGUI(false);

				}
				guiFrame.adjustColorFocusPosition();
				guiFrame.guiDone  = true;	

			} catch(Exception e){
				if(printDebug){
					consoleOut("initGUI problem");					
				}
				
			}

			if(guiFrame.midiDeviceName.equals(midi.midiAvailableDevices[guiFrame.midiDeviceIndex])){
				midi.connect(guiFrame.midiDeviceIndex, guiFrame.midiDeviceName);
			}

			guiFrame.midiDeviceNames         = midi.midiAvailableDevices;
			guiFrame.cp5.get(ScrollableList.class, "midiDevices").clear();				
			guiFrame.cp5.get(ScrollableList.class, "midiDevices").addItems(guiFrame.midiDeviceNames);

			// OSC
			guiFrame.myIpAddress  = ipAddress;
			if(printDebug){
				consoleOut("My IP address    : " + ipAddress);	
			}
			
		}

	}

	public void changeVisualEngine(int newIndex) {
		if (newIndex < 0) return;
		if (newIndex >= engines.size()) return;
		if (newIndex   == currentEngineIndex) return;

		papplet.println("Changing to visual engine #" + newIndex);
		if(printDebug){
			consoleOut("Changing to visual engine #" + newIndex);	
		}
		

		try {
			engines.get(currentEngineIndex).showGUI(false);
			engines.get(currentEngineIndex).exit();
		} 
		catch(Exception e) {
			papplet.println("Exit Visual Engine problem");
		}

		try {
			engines.get(newIndex).start();
			engines.get(newIndex).showGUI(true);
			guiFrame.reinitializeColorPalette();
			engines.get(newIndex).colorPalette     = 	new int[guiFrame.colorFocusGUI.width];
			engines.get(newIndex).colorPalette     =	guiFrame.colorPaletteFocus 		;
		} 
		catch(Exception e) {
			papplet.println("Start Visual Engine problem");
		}


		previousEngineIndex       = currentEngineIndex;
		currentEngineIndex        = newIndex;
		engineChanged  = true;
		guiFrame.reinitializeColorPalette();
	}  


	public void exportJSONforAndroid() {

		if(printDebug){
			consoleOut("Exporting JSON file for Android App");	
		}
		
		JSONArray androidConfig   = new JSONArray();

		JSONObject oscSettings    = new JSONObject();
		oscSettings.setString("IP : ", guiFrame.myIpAddress);
		oscSettings.setInt("IncomingPort     : ", guiFrame.incomingPort);
		oscSettings.setInt("OutgoingPort     : ", guiFrame.outgoingPort);


		androidConfig.setJSONObject(0, oscSettings);

		for(int i      = 0; i < engines.size(); i++){
			JSONArray visualEngineJSON       = new JSONArray();
			VisualEngine veJSON   = engines.get(i);

			JSONObject visualGeneral         = new JSONObject();
			visualGeneral.setString("Name    : ", veJSON.name);
			visualGeneral.setInt("Engine Index     : ", i);

			visualEngineJSON.setJSONObject(0,visualGeneral);

			for(int j  = 0; j < veJSON.vep.size(); j++){
				VisualEngineParameter vepJSON= veJSON.vep.get(j);
				JSONObject engineParameter   = new JSONObject();
				engineParameter.setString("Name    : ", vepJSON.name);
				engineParameter.setString("Type    : ", vepJSON.type);
				engineParameter.setInt("Index: ", vepJSON.index);
				engineParameter.setFloat("Min: ", vepJSON.min);
				engineParameter.setFloat("Max: ", vepJSON.max);
				engineParameter.setString("Label   : ", vepJSON.label);

				visualEngineJSON.setJSONObject(j+1,engineParameter);
			}
			androidConfig.setJSONArray(i+1,visualEngineJSON);
		}


		//color palette save
		JSONObject colorPaletteSettings      = new JSONObject();
		colorPaletteSettings.setString("colorPaletteFileName", "colorPalette.png");
		androidConfig.setJSONObject(engines.size()+1,colorPaletteSettings);
		guiFrame.colorPaletteImageFull.save(papplet.sketchPath() + "/android/"+"colorPalette.png");

		papplet.saveJSONArray(androidConfig, papplet.sketchPath() + "/android/androidConfig.json");

		for(int i      = 0; i < guiFrame.thumbnailNames.size(); i++){
			guiFrame.thumbnailImages.get(i).save(papplet.sketchPath() + "/android/"+guiFrame.thumbnailNames.get(i)+".jpg");
		}


	}


	public void stop(){
		saveConfigFile();
	}

	public void consoleOut(String _s) {
		guiFrame.consolePrint("NOSLib        : " + _s);
	}

	public void test() {
		papplet.println("NOSLib test");
	}

	public void saveFrameAsThumbnail(){
		if(printDebug){
			consoleOut("Saving thumbnail for " + engines.get(currentEngineIndex).name );	
		}
		
		PImage screen  = papplet.get();
		screen.resize(guiFrame.thumbnailImageWidth, guiFrame.thumbnailImageHeight);
		screen.save("data/thumbnails/" + engines.get(currentEngineIndex).name + ".jpg");
	}


	public void printSketchInfo() {
		if (papplet != null) {
			papplet.println(
				"Sketch, size " + papplet.width + "x" + papplet.height + ", " +
				"is using renderer " + papplet.sketchRenderer()
				);
		}
	}
}
