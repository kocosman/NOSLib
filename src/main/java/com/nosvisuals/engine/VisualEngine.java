package com.nosvisuals.engine;

import processing.core.*;
import processing.data.*;
import controlP5.*;
import java.util.*;
import java.awt.*;


public abstract class VisualEngine {
	public PApplet papplet;
	public String name;
	
	public String path;
	public ArrayList<VisualEngineParameter> vep;

	public ControlP5 cp5;

	//Sound parameters
	public float[] soundInput;
	public float soundLevel;
	public float soundLevelLPF;
	public float[] soundSpectrum;
	public float[] soundSpectrumLPF;
	public float[] soundFocusSpectrum;
	public float[] soundFocusSpectrumLPF;
	public float[] soundFocusSpectrumLPFShuffled;
	public float soundFocusLevel;
	public float soundFocusLevelLPF;

	//Color
	public int[] colorPalette = new int[0];
	public float guiStrokeSaturation = 0;
	public float guiStrokeAlpha = 0;
	public float guiFillSaturation = 0;
	public float guiFillAlpha = 0;

	//Preset
	public boolean startFromFirstPreset = true;

	public VisualEngine(PApplet papplet, String _name) {
		this.papplet = papplet;
		name = _name;
		path = papplet.sketchPath();
		vep = new ArrayList<VisualEngineParameter>();
		papplet.println("VisualEngine " + name + " Loaded");

	}

	public abstract void init();
	public abstract void initGUI();
	public abstract void start();
	public abstract void update(); 
	public abstract void exit();

	public void plugToParameters(){
		try{
			consoleOut("Plug to parameters");
			for(VisualEngineParameter vepTemp : vep){
				String realName = vepTemp.name.substring(vepTemp.name.indexOf(name)+name.length(),vepTemp.name.length());
				if(vepTemp.type.equals("radioButton")){			
					vepTemp.controllers.get(vepTemp.controllers.size()-1).plugTo(this,realName);
				} else {
					for(controlP5.Controller c : vepTemp.controllers){
						c.plugTo(this,realName);
					}
				}			
			}	
		} catch(Exception e){
			consoleOut("Plug to parameters ERROR");
		}
		
	}


	public int getStrokeColor(float _index){
		int index = ((int)(_index*colorPalette.length));
		int c = colorPalette[papplet.constrain(index,0,colorPalette.length)];
		return papplet.color(papplet.hue(c), guiStrokeSaturation, papplet.brightness(c), guiStrokeAlpha);
	}

	public int getFillColor(float _index){
		int index = ((int)(_index*colorPalette.length));
		int c = colorPalette[papplet.constrain(index,0,colorPalette.length)];
		return papplet.color(papplet.hue(c), guiFillSaturation,  papplet.brightness(c), guiFillAlpha);
	}

	public JSONObject loadPreset(int _presetIndex){
		JSONObject _cam = new JSONObject();
		JSONObject _colorParams = new JSONObject();
		int tailJSONAmount = 2;


		if(_presetIndex != -1){
			papplet.println("Loading Preset " + (_presetIndex+1));	
			try{
				JSONArray parameters = papplet.loadJSONArray("data/presets/"+name+_presetIndex+".json");
				for(int i = 0; i < parameters.size()-tailJSONAmount; i++){
					JSONObject parameter = parameters.getJSONObject(i);
					for(int j = 0; j < vep.size(); j++){
						VisualEngineParameter v = vep.get(j);

						if(parameter.getString("Name: ").equals(v.name)){
							if(!v.type.equals("radioButton")){
								if(v.type.equals("toggle")){
									v.setToggle(parameter.getFloat("Value: "));
								} else {
									v.update(parameter.getFloat("Value: "));	
								}
							} else {
								if((int)parameter.getFloat("Value: ") == -1){
									v.deactivateAll();
								} else {
									v.activateRadio((int)parameter.getFloat("Value: "));	
								}
							}
						}
					}
				}

				papplet.println("Preset "+ (_presetIndex+1) + " for " + name + " loaded!");	

				_colorParams = parameters.getJSONObject(parameters.size()-2);

				guiStrokeSaturation = _colorParams.getFloat("StrokeSaturation");
				guiStrokeAlpha 		= _colorParams.getFloat("StrokeAlpha");
				guiFillSaturation 	= _colorParams.getFloat("FillSaturation");
				guiFillAlpha 		= _colorParams.getFloat("FillAlpha");

				_cam = parameters.getJSONObject(parameters.size()-1);

			} catch(Exception e){
				papplet.println("ERROR Loading preset"+ (_presetIndex+1) + " for " + name);
			}

		}
		return _cam;
	}


	public boolean savePreset(int _presetIndex, JSONObject _cam){
		if(_presetIndex != -1){
			papplet.println("Saving Preset " + (_presetIndex+1));

			JSONArray parameters = new JSONArray();

			for(int i = 0; i < vep.size(); i++){
				VisualEngineParameter v = vep.get(i);
				JSONObject parameter = new JSONObject();

				parameter.setString("Name: ",	v.name 	);
				parameter.setString("Type: ", 	v.type 	);
				parameter.setInt("Index: ", 	v.index );
				parameter.setFloat("Min: ", 	v.min 	);
				parameter.setFloat("Max: ", 	v.max 	);
				parameter.setFloat("Value: ", 	v.value );
				parameter.setString("Label: ", 	v.label );

				parameters.setJSONObject(i,parameter);
			}
			JSONObject colorParams = new JSONObject();

			colorParams.setFloat("StrokeSaturation",	guiStrokeSaturation);
			colorParams.setFloat("StrokeAlpha",	guiStrokeAlpha);
			colorParams.setFloat("FillSaturation",	guiFillSaturation);
			colorParams.setFloat("FillAlpha",	guiFillAlpha);

			parameters.setJSONObject(vep.size(),colorParams);
			parameters.setJSONObject(vep.size()+1,_cam);

			return papplet.saveJSONArray(parameters, "data/presets/"+name+_presetIndex+".json");
		}
		return false;
	}


	public void printParameters(){
		for(VisualEngineParameter vepTemp : vep){
			papplet.println(vepTemp.name + " - " + vepTemp.value);
		}
		papplet.println();
	}

	// For KNOB && FADER
	public void addParameter(String _name, String _type, int _index, float _min, float _max, String _label){
		VisualEngineParameter VEPTemp = new VisualEngineParameter(name+_name, _type, _index, _min, _max, _label);
		vep.add(VEPTemp);
	}

	// For TOGGLE && BANG && BUTTON
	public void addParameter(String _name, String _type, int _index, String _label){
		VisualEngineParameter VEPTemp = new VisualEngineParameter(name+_name, _type, _index, _label);
		vep.add(VEPTemp);	
	}

	// For RADIOBUTTON
	public void addParameter(String _name, String _type, int _index, int _length, String _label){
		VisualEngineParameter VEPTemp = new VisualEngineParameter(name+_name, _type, _index, _length, _label);
		vep.add(VEPTemp);	
	}

	public void showGUI(boolean show){
		if(show){
			for(VisualEngineParameter vepTemp : vep){
				for(controlP5.Controller c : vepTemp.controllers){
					c.show();
				}
				if(vepTemp.type.equals("radioButton")){
					vepTemp.controllers.get(vepTemp.controllers.size()-1).hide();	
				}
			} 
		} else {
			for(VisualEngineParameter vepTemp : vep){
				for(controlP5.Controller c : vepTemp.controllers){
					c.hide();
				}	
			} 
		}
	}

	public void consoleOut(String _text){
		papplet.println(name + ": " + _text);
	}

	public void test(){
		papplet.println(name);
	}



}
