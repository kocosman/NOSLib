package com.nosvisuals.engine;

import processing.core.*;
import java.util.*;

public class MidiData {

	public int address;
	public String type;		//Fader, Knob, Toggle
	public int index;
	public String name;

	public int data;
	public int dataPre;
	public int dataDiff;


	public MidiData() {
	}

	public MidiData(int channel, String t) {
		address = channel;
		type = t;
	}

	public MidiData(int _address, String _type, int _index) {
		address = _address;	
		type = _type;
		index = _index;
	}

	public MidiData(int _address, String _type, int _index, String _name) {
		address = _address;	
		type = _type;
		index = _index;
		name = _name;
	}

	public void update(int d) {
	//	dataPre = data;
		data = d;
	//	dataDiff = getDiff();
	}

	public int getDiff() {
		if (type.equals("fader") || type.equals("knob")) {
			dataDiff = data - dataPre;
		} else if (type.equals("toggle")) {
			if (data - dataPre != -127) {
				dataDiff = data - dataPre;
				dataDiff /= 127;
			}
		}
		dataPre = data;
		return dataDiff;
	}

	public int getVal() {
		return data;
	}

}