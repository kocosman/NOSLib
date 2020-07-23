//float[] shuffleArray(float[] aFloatArray) {
//  FloatList tempList = new FloatList();
//  for (int i = 0; i < aFloatArray.length; i++) {
//    tempList.append(aFloatArray[i]);
//  }
//  tempList.shuffle();
//  for (int i = 0; i < aFloatArray.length; i++) {
//    aFloatArray[i]=tempList.get(i);
//  }  
//  return aFloatArray;
//}

//int[] shuffleArray(int[] aFloatArray) {
//  IntList tempList = new IntList();
//  for (int i = 0; i < aFloatArray.length; i++) {
//    tempList.append(aFloatArray[i]);
//  }
//  tempList.shuffle();
//  for (int i = 0; i < aFloatArray.length; i++) {
//    aFloatArray[i]=tempList.get(i);
//  }  
//  return aFloatArray;
//}


PVector PVectorZero = new PVector(0,0,0);
PVector PVectorOne = new PVector(1,1,1);
color whiteColor = color(255);
color blackColor = color(0);



void pointPVector(PVector _in) {
  point(_in.x, _in.y, _in.z);
}

void linePVector(PVector _in1, PVector _in2) {
  line(_in1.x, _in1.y, _in1.z, _in2.x, _in2.y, _in2.z);
}

void vertexPVector(PVector _in) {
  vertex(_in.x, _in.y, _in.z);
}