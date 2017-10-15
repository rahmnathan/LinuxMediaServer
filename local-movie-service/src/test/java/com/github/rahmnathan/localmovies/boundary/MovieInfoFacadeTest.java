package com.github.rahmnathan.localmovies.boundary;

import com.github.rahmnathan.http.control.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import javax.json.Json;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MovieInfoFacadeTest {

//    private List<MediaFile> movieInfoList = new ArrayList<>();
//    private final MovieInfoFacade movieInfoFacade = new MovieInfoFacade(new MovieInfoControl(null, null, null));
//
//    @Before
//    public void initializeMovieList() throws Exception {
//        Random random = new Random();
//        for(int i = 0; i < 10; i++){
//            int randomInt = random.nextInt(50);
//            movieInfoList.add(MediaFile.Builder.newInstance()
//                    .setImage("")
//                    .setIMDBRating(String.valueOf(randomInt))
//                    .setMetaRating(String.valueOf(randomInt))
//                    .setPath("/test/path")
//                    .setTitle("My Fake Title " + randomInt)
//                    .setReleaseYear(String.valueOf(1990 + randomInt))
//                    .build()
//            );
//            Thread.sleep(100);
//        }
//
//        movieInfoList.get(2).addView();
//    }

//    @Test
//    public void jsonTest(){
//        StringBuilder sb = new StringBuilder();
//        Map<String, Integer> ips = new HashMap<>();
//        try(BufferedReader br = new BufferedReader(new FileReader("/home/nathan/development/full.log"))){
//            br.lines().forEachOrdered(line -> {
//                JSONObject jsonObject = new JSONObject(line);
//                String ip = jsonObject.getString("client_ip");
//                if(ips.containsKey(ip)){
//                    ips.put(ip, ips.get(ip) + 1);
//                } else {
//                    ips.put(ip, 1);
//                }
//            });
//        } catch (IOException e){
//            System.out.println(e.toString());
//        }
//
////        JSONArray jsonArray = new JSONArray(sb.toString());
////
////        Map<String, Integer> ips = new HashMap<>();
////
////        for(int i = 0; i < jsonArray.length(); i++){
////            JSONObject jsonObject = jsonArray.getJSONObject(i);
////            String ip = jsonObject.getString("client_ip");
////            if(ips.containsKey(ip)){
////                ips.put(ip, ips.get(ip) + 1);
////            } else {
////                ips.put(ip, 1);
////            }
////        }
//
//        Map<String, String> requestLocations = new HashMap<>();
//
//        ips.entrySet().stream()
//                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
//                .forEach(entry -> {
//                    JSONObject jsonObject = new JSONObject(HttpClient.getResponseAsString("http://ip-api.com/json/" + entry.getKey()));
//                    System.out.println(jsonObject);
//                    requestLocations.put(jsonObject.getString("regionName"), entry.getValue().toString());
//                });
//
//        requestLocations.entrySet().forEach(System.out::println);
//        HttpClient.getResponseAsString("http://ip-api.com/json/");
//    }
}
