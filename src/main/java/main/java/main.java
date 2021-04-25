package main.java;

import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class main {
    public static void main(String[] args) throws IOException {
        StringBuffer weather = fetch_weather();

        JSONObject obj = new JSONObject(weather.toString());
        JSONArray data = obj.getJSONArray("list");

        JSONObject main = data.getJSONObject(1);
        JSONArray weather_data = main.getJSONArray("weather");

        JSONObject desc = (JSONObject) weather_data.get(0);
        String weather_type = desc.getString("main");


        System.out.println(weather_type);




    }

    public static StringBuffer fetch_weather() throws IOException {
//        'id=2650225' is edinburgh, for future work maybe get current location to choose city
        URL api = new URL("http://api.openweathermap.org/data/2.5/forecast?id=2650225&appid=04b52862199178582d63b48f1eef68e3");
        HttpURLConnection connection = (HttpURLConnection) api.openConnection();
        connection.setRequestMethod("GET");


        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while((inputLine = in.readLine()) != null){
            content.append(inputLine + '\n');
        }

        return content;
    }

}
