package com.nosvisuals.engine;

import processing.core.*;
import java.util.*;

public class VisualEngineParameter {
	
	public String 	name;
	public String 	type;
	public int 		index;
	public float 	min;
	public float 	max;
	public float 	value;
	public float 	valuePre;
	public String 	label;
	
	public ArrayList<controlP5.Controller> controllers;

	public float[] radioValues;
	public float[] radioValuesPre;

	public int length;
	public int radioButtonIndex = -1;
	public boolean bangEnable = false;

	public boolean isChanged = false;

	// for Knob & Fader
	public VisualEngineParameter(String _name, String _type, int _index, float _min, float _max, String _label){
		name = _name;
		type = _type;
		index = _index;
		min = _min;
		max = _max;
		label = _label;
		controllers = new ArrayList<controlP5.Controller>();
	}

	// for Toggle & Button & Bang
	public VisualEngineParameter(String _name, String _type, int _index, String _label){
		name = _name;
		type = _type;
		index = _index;
		label = _label;
		min = 0.f;
		max = 1.f;
		controllers = new ArrayList<controlP5.Controller>();
	}

	// for RadioButton
	public VisualEngineParameter(String _name, String _type, int _index, int _length, String _label){
		name = _name;
		type = _type;
		index = _index;
		label = _label;
		length = _length;
		min = 0.f;
		max = _length;

		controllers = new ArrayList<controlP5.Controller>();

		radioValues = new float[_length];
		radioValuesPre = new float[_length];
		for(int i = 0; i < _length; i++){
			radioValues[i] = 0;
			radioValuesPre[i] = 0;
		}
	}


	public void renderRadio(){
		int isZero = 0;
		valuePre = value;
		for(int i = 0; i < length; i++){
			radioValuesPre[i] = radioValues[i];
			radioValues[i] = controllers.get(i).getValue();
			if(radioValues[i]>radioValuesPre[i]){
				deactivateOthers(i);
				controllers.get(length).setValue(i);
				value = i;
			}
			isZero += radioValues[i];
		}
		if(isZero == 0){
			controllers.get(length).setValue(-1);
			value = -1;
		}
	}

	public void toggleRadio(float _data, int _radioIndex){
		if(_data > 0){
			controllers.get(_radioIndex).setValue((controllers.get(_radioIndex).getValue() == 0) ? 1 : 0);
		}
	}

	public void activateRadio(int _radioIndex){
		if(_radioIndex < length){
			controlP5.Controller c = controllers.get(_radioIndex);
			c.setValue(1);
			radioValues[_radioIndex] = 1;
		}
		controlP5.Controller c = controllers.get(length);
		c.setValue(_radioIndex);
		value = _radioIndex;
		deactivateOthers(_radioIndex);
	}

	public void deactivateRadio(int _radioIndex){
		if(_radioIndex < length){
			controlP5.Controller c = controllers.get(_radioIndex);
			c.setValue(0);
			radioValues[_radioIndex] = 0;
		}
	}

	public void deactivateOthers(int _radioIndex){
		for(int i = 0; i < length; i++){
			if(i != _radioIndex){
				controlP5.Controller c = controllers.get(i);
				c.setValue(0);
				radioValues[i] = 0;
			}
		}
	}

	public void deactivateAll(){
		for(int i = 0; i < length; i++){
			controlP5.Controller c = controllers.get(i);
			c.setValue(0);
			radioValues[i] = 0;
		}
	}

	public void getValuesFromGUI(){
		if(!type.equals("radioButton")){
			valuePre = value;
			value = controllers.get(0).getValue();
		}
	}

	public void setValuesFromGUI(float _data){
		if(!type.equals("radioButton")){
			controllers.get(0).setValue(_data);
			valuePre = value;
			value = _data;
		}
	}

	public void setToggle(float _data){
		controllers.get(0).setValue(_data);		
	}

	public void update(float _data){
		if(type.equals("knob") || type.equals("fader")){
			controllers.get(0).setValue(_data);
		} else if(type.equals("toggle")){
			if(_data > 0){
				controllers.get(0).setValue((controllers.get(0).getValue() == 0) ? 1 : 0);
			} 
		} else if(type.equals("bang")){
			if(_data > 0){
				controllers.get(0).setValue(1);
			}
		} else if(type.equals("button")){
			if(_data > 0){
				controllers.get(0).setValue(1);
			} else if(_data == 0){
				controllers.get(0).setValue(0);
			}
		}
	}

	public boolean checkChange(){
		if(value != valuePre){
			isChanged = true;
		} else {
			isChanged = false;
		}
		return isChanged;
	}


}
