package main.java;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Map;

public class main {
    public static void main(String[] args) throws IOException {
        StringBuffer weather = fetch_weather();
        JsonElement weather_data = new JsonParser().parse(String.valueOf(weather));
        JsonObject data = weather_data.getAsJsonObject();

        JsonElement data_body = new JsonParser().parse(String.valueOf(data));
        JsonObject main = data_body.getAsJsonObject();

        JsonElement list = data.get("list");

        String data_weather = data.getAsJsonObject("list").getAsString("weather");

        Gson gson = new Gson();

        for(Map.Entry<String, JsonElement> entry : data.entrySet()){
            System.out.println("key = " + entry.getKey() + ", Value = " + entry.getValue());
            JsonElement val = entry.getValue();

        }



    }

    public static StringBuffer fetch_weather() throws IOException {
        URL api = new URL("http://api.openweathermap.org/data/2.5/forecast?id=524901&appid=04b52862199178582d63b48f1eef68e3");
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
