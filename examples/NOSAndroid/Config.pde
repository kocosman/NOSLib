
String[] jsonString;
String jsonMaster = "";
JSONArray configJSON = new JSONArray();
JSONObject oscConfig = new JSONObject();
JSONObject colorPaletteConfig = new JSONObject();

void loadConfig() {
  jsonString = loadStrings("androidConfig.json");
  println(jsonString.length);
  for (int i = 0; i < jsonString.length; i++) {
    jsonMaster = jsonMaster+jsonString[i];
  }

  configJSON = JSONArray.parse(jsonMaster);
  println(configJSON.size ());

  oscConfig = configJSON.getJSONObject(0);

  for (int i = 1; i < configJSON.size()-1; i++) {
    JSONArray visualsJSON = configJSON.getJSONArray(i);
    JSONObject visualGeneral = visualsJSON.getJSONObject(0);
    String visualName = visualGeneral.getString("Name: ");
    int visualIndex = visualGeneral.getInt("Engine Index: ");

    VisualEngineRemote visTemp = new VisualEngineRemote(visualName, visualIndex);

    for (int j = 1; j < visualsJSON.size(); j++) {
      JSONObject vepTemp = visualsJSON.getJSONObject(j);

      String parameterName = vepTemp.getString("Name: ");
      String parameterType = vepTemp.getString("Type: ");
      int parameterIndex = vepTemp.getInt("Index: ");
      float parameterMin = vepTemp.getFloat("Min: ");
      float parameterMax = vepTemp.getFloat("Max: ");
      String parameterLabel = vepTemp.getString("Label: ");

      visTemp.addParameter(parameterName, parameterType, parameterIndex, parameterMin, parameterMax, parameterLabel);
    }
    visuals.add(visTemp);
  }

  colorPaletteConfig = configJSON.getJSONObject(configJSON.size()-1);
}