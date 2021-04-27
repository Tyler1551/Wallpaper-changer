package main.java;

import com.sun.jna.*;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;
import org.json.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.time.*;


//  Project TODO
/*
*
* BUG FIXES:
* Fix relative path issue (don't take absolute, find another way around this)
*
* FUTURE FEATURES
* Add day/night wallpapers and set at certain times of day with weather themed wallpapers (DONE)
* Get current location instead of hardcoded city ID in the API call (possible API search)
*
* FAR FUTURE FEATURES
* GUI for user experience - set own wallpapers based on time of day and weather
* Easy install for run on start etc
*
* */

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
                    Date date = new Date();
                    System.out.println(date.getHours() + ":" + date.getMinutes());
                    StringBuffer weather = fetch_weather();
                    String weather_type = processJson(weather);
                    init_wallpaper(weather_type);
//                    ChangeWallpaper(wallpaper_path, weather_type);

                    System.out.println("System updated Wallpaper");

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(update, 0, 120000);


    }

    public static void init_wallpaper(String weather_type){
        Date date = new Date();
        int hour = date.getHours();

        System.out.println(date.getHours());
        String wallpaper_path = "";

//        Morning
        if(hour >= 6 && hour < 12){
            switch (weather_type){
                case "Clouds":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Morning\\cloudy_morning.jpg";
                    break;
                case "Clear":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Morning\\clear_morning.jpg";
                    break;
                case "Rain":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Morning\\rain_morning.jpg";
                    break;
                case "Snow":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Morning\\snow_morning.png";
                    break;
            }
        }
//        Day
        else if(hour >= 12 && hour < 18){
            switch (weather_type){
                case "Clouds":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Day\\cloudy_day.jpg";
                    break;
                case "Clear":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Day\\clear_day.jpg";
                    break;
                case "Rain":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Day\\rain_day.jpg";
                    break;
                case "Snow":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Day\\snow_day.jpg";
                    break;
            }
        }
//        Evening
        else if(hour >= 18 && hour < 21){
            switch (weather_type){
                case "Clouds":
                    wallpaper_path = "C:\\Users\\tyler\\Desktop\\Wallpaper-Change\\Wallpapers\\Evening\\cloud_evening.jpg";
                    break;
                case "Clear":
                    wallpaper_path = "C:\\Users\\tyler\\Desktop\\Wallpaper-Change\\Wallpapers\\Evening\\clear_evening.jpg";
                    break;
                case "Rain":
                    wallpaper_path = "C:\\Users\\tyler\\Desktop\\Wallpaper-Change\\Wallpapers\\Evening\\rain_evening.jpg";
                    break;
                case "Snow":
                    wallpaper_path = "C:\\Users\\tyler\\Desktop\\Wallpaper-Change\\Wallpapers\\Evening\\snow_evening.jpg";
                    break;
            }
        }
//        Night
        else{
            switch (weather_type){
                case "Clouds":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Night\\cloudy_night.png";
                    break;
                case "Clear":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Night\\clear_night.jpg";
                    break;
                case "Rain":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Night\\rain_night.png";
                    break;
                case "Snow":
                    wallpaper_path = "C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\Wallpapers\\Night\\snow_night.jpg";
                    break;
            }
        }

        SPI.INSTANCE.SystemParametersInfo(
                new WinDef.UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new WinDef.UINT_PTR(0),
                wallpaper_path,
                new WinDef.UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));

    }

    public static StringBuffer fetch_weather() throws IOException {
//        'id=2650225' and 'id=3333229' is edinburgh, for future work maybe get current location to choose city
        URL api = new URL("http://api.openweathermap.org/data/2.5/weather?id=3333229&appid=04b52862199178582d63b48f1eef68e3");
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

    public static String processJson(StringBuffer weather){
        JSONObject obj = new JSONObject(weather.toString());
        JSONArray data = obj.getJSONArray("weather");

        JSONObject main = data.getJSONObject(0);
        String weather_type = main.getString("main");

        System.out.println(weather_type);
        return weather_type;
    }

}
