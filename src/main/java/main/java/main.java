package main.java;

import com.maxmind.geoip2.WebServiceClient;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.util.*;


//  Project TODO
/*
*
* BUG FIXES:
* Fix relative path issue (don't take absolute, find another way around this) using basepath.concat gets from where the file is run
* This is potentially fixed at deployment by running .exe in app folder
*
* FUTURE FEATURES
* Add day/night wallpapers and set at certain times of day with weather themed wallpapers (DONE)
* Get current location instead of hardcoded city ID in the API call (possible API search)
*
* FAR FUTURE FEATURES
* GUI for user experience - set own wallpapers based on time of day and weather
* Easy install for run on start etc
*
*/

public class main {

    public static void main(String[] args) throws IOException {

//        Create new timer to update the wallpaper every 2 minutes
        TimerTask update = new TimerTask() {
            @Override
            public void run() {
                try{
//                  Get City location based off public ip
                    String city = get_location();
                    System.out.println(city);
//                    Fetch weather from api, storing data in StringBuffer to then be processed in Json
                    StringBuffer weather = fetch_weather(city);
                    String weather_type = processJson(weather);
//                    Change wallpaper based on the weather type
                    change_wallpaper(weather_type);


                } catch (IOException | GeoIp2Exception e) {
                    e.printStackTrace();
                }

            }
        };

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(update, 0, 120000);


    }

    public static String get_location() throws IOException, GeoIp2Exception {
        File database = new File("C:\\Users\\tyler\\IdeaProjects\\Wallpaper_Change\\City_DB");
        InetAddress ivp4 = InetAddress.getLocalHost();


        try(WebServiceClient client = new WebServiceClient.Builder(546716, "jazeWZHCldO4ov7a").host("geolite.info").build()){

            URL findip = new URL("http://bot.whatismyipaddress.com");

            BufferedReader br = new BufferedReader(new InputStreamReader(findip.openStream()));

            String ip = br.readLine().trim();

            InetAddress ipAddress = InetAddress.getByName(ip);

            CityResponse response = client.city(ipAddress);

            City c = response.getCity();
            String city = c.toString();


            JSONObject obj = new JSONObject(c);
            JSONObject names = obj.getJSONObject("names");
//            JSONObject en = names.getJSONObject("en");
            String city_name = names.getString("en");


            return city_name;
        }


    }

    public static void change_wallpaper(String weather_type){
//        get date object, separate hour
        Date date = new Date();
        int hour = date.getHours();

        System.out.println(date.getHours());
        String basePath = new File("").getAbsolutePath();


        System.out.println("Base path: " + basePath);
//        initialise a new wallpaper path
        String wallpaper_path = "";

//        Check hour of day, set wallpaper path depending on weather condition and time
//        Morning
        if(hour >= 6 && hour < 12){
            switch (weather_type) {
                case "Clouds" -> wallpaper_path = basePath.concat("/Wallpapers/Morning/cloudy_morning.jpg");
                case "Clear" -> wallpaper_path = basePath.concat("/Wallpapers/Morning/clear_morning.jpg");
                case "Rain" -> wallpaper_path = basePath.concat("/Wallpapers/Morning/rain_morning.jpg");
                case "Snow" -> wallpaper_path = basePath.concat("/Wallpapers/Morning/snow_morning.jpg");
            }
        }
//        Day
        else if(hour >= 12 && hour <= 17){
            wallpaper_path = switch (weather_type) {
                case "Clouds" -> basePath.concat("/Wallpapers/Day/cloudy_day.jpg");
                case "Clear" -> basePath.concat("/Wallpapers/Day/clear_day.jpg");
                case "Rain" -> basePath.concat("/Wallpapers/Day/rain_day.jpg");
                case "Snow" -> basePath.concat("/Wallpapers/Day/snow_day.jpg");
                default -> wallpaper_path;
            };
        }
//        Evening
        else if(hour >= 18 && hour < 21){
            wallpaper_path = switch (weather_type) {
                case "Clouds" -> basePath.concat("/Wallpapers/Evening/cloud_evening.jpg");
                case "Clear" -> basePath.concat("/Wallpapers/Evening/clear_evening.jpg");
                case "Rain" -> basePath.concat("/Wallpapers/Evening/rain_evening.jpg");
                case "Snow" -> basePath.concat("/Wallpapers/Evening/snow_evening.png");
                default -> wallpaper_path;
            };
        }
//        Night
        else{
            wallpaper_path = switch (weather_type) {
                case "Clouds" -> basePath.concat("/Wallpapers/Night/cloudy_night.png");
                case "Clear" -> basePath.concat("/Wallpapers/Night/clear_night.jpg");
                case "Rain" -> basePath.concat("/Wallpapers/Night/rain_night.jpg");
                case "Snow" -> basePath.concat("/Wallpapers/Night/snow_night.jpg");
                default -> wallpaper_path;
            };
        }



//        Set wallpaper using SPI class
        SPI.INSTANCE.SystemParametersInfo(
                new WinDef.UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new WinDef.UINT_PTR(0),
                wallpaper_path,
                new WinDef.UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));

    }

    public static StringBuffer fetch_weather(String city) throws IOException {
//        Open API connection using API data key and city ID for Edinburgh
        URL api = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city + ",GB&appid=04b52862199178582d63b48f1eef68e3");
        HttpURLConnection connection = (HttpURLConnection) api.openConnection();
        connection.setRequestMethod("GET");

//      Create new BufferedReader, taking each line of the JSON data and appending to a content string to then return
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

}
