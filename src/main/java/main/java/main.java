package main.java;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;
import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class main {

    public interface SPI extends StdCallLibrary {

        //from MSDN article
        long SPI_SETDESKWALLPAPER = 20;
        long SPIF_UPDATEINIFILE = 0x01;
        long SPIF_SENDWININICHANGE = 0x02;

        SPI INSTANCE = (SPI) Native.loadLibrary("user32", SPI.class, new HashMap<String, Object>() {
            {
                put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
                put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
            }
        });

        void SystemParametersInfo(
                WinDef.UINT_PTR uiAction,
                WinDef.UINT_PTR uiParam,
                String pvParam,
                WinDef.UINT_PTR fWinIni
        );
    }

    public static void main(String[] args) throws IOException {
        String wallpaper_path = "";

        TimerTask update = new TimerTask() {
            @Override
            public void run() {
                try{
                    StringBuffer weather = fetch_weather();
                    String weather_type = processJson(weather);
                    ChangeWallpaper(wallpaper_path, weather_type);

                    System.out.println("System updated Wallpaper");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(update, 0, 600000);


    }

    public static void ChangeWallpaper(String wallpaper_path, String weather_type){

        switch (weather_type){
            case "Clear":
                wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\firewatch_sunny.jpg";
                break;

            case "Rain":
                wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\firewatch_rain.jpg";
                break;

            case "Snow":
                wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\firewatch_snow.png";
                break;
            case "Clouds":
                wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\cloudy.jpg";
                break;
            default:
                break;
        }

        SPI.INSTANCE.SystemParametersInfo(
                new WinDef.UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new WinDef.UINT_PTR(0),
                wallpaper_path,
                new WinDef.UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));
    }

    public static String processJson(StringBuffer weather){
        JSONObject obj = new JSONObject(weather.toString());
        JSONArray data = obj.getJSONArray("list");

        JSONObject main = data.getJSONObject(1);
        JSONArray weather_data = main.getJSONArray("weather");

        JSONObject desc = (JSONObject) weather_data.get(0);
        String weather_type = desc.getString("main");

        System.out.println(weather_data);
        return weather_type;
    }

    public static StringBuffer fetch_weather() throws IOException {
//        'id=2650225' and 'id=3333229' is edinburgh, for future work maybe get current location to choose city
        URL api = new URL("http://api.openweathermap.org/data/2.5/forecast?id=2650225&appid=04b52862199178582d63b48f1eef68e3");
        HttpURLConnection connection = (HttpURLConnection) api.openConnection();
        connection.setRequestMethod("GET");


        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while((inputLine = in.readLine()) != null){
            content.append(inputLine).append('\n');
        }

        return content;
    }

}
