package com.example.ips;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIServer {
    private static final String TAG = "FastAPI";
    private static final String masterKey = "ewireless";
    private static final String userKey = "5wc0TX1ck4yz1rQJ7Hkuvg";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final String site = "https://openpositioning.org";


/*
    public static void submitAPI(byte[] bytes) throws IOException {

        OkHttpClient client = new OkHttpClient();
        // URL of the server
        String url =  site + "/api/live/trajectory/upload/"+ "5wc0TX1ck4yz1rQJ7Hkuvg" + "/?key=ewireless";

        byte[] trajBytes = Switch.data_collector_top.TrajectoryTop.build().toByteArray();

        String fileName = "example.pkt";
        String fileType = "application/json";

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,
                        RequestBody.create(trajBytes, MediaType.parse(fileType)))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
        } catch (IOException e) {
            Log.e(TAG, "Handle other errors.", e);
        }
    }
*/
    public static void submitAPI(byte[] bytes) throws IOException{
        //Create a new instance of the OkHttpClient
        OkHttpClient client = new OkHttpClient();
        //TrajectoryOuterClass.Trajectory trajectory = Switch.data_collector_top.TrajectoryTop.build();
        //byte[] bytes = trajectory.toByteArray();
        String dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
        String fileName = "Trajectory_" + dateFormat + ".pkt";
        //Create request body
        RequestBody requestBody = RequestBody.create(bytes,MediaType.parse("application/json"));

        //Create MultipartBody.Builder
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, requestBody);

        //Create the HTTP request
        Request request = new Request.Builder()
                //.url("https://openpositioning.org/api/live/trajectory/upload/"+"{"+userKey+"}/")
                .url("https://openpositioning.org/api/live/trajectory/upload/" + userKey + "/?key=ewireless")
                .post(builder.build())
                .build();

        try{
            //Make HTTP request
            Response response = client.newCall(request).execute();
            //Parse response data
            String responseData = response.body().string();
            // Check status code for success
            if (response.isSuccessful()) {
                System.out.println("API request successful!");
                Log.e(TAG, "API request successful!");
                //String responseData = response.body().string();
                // Process the responseData as needed
            } else {
                Log.e(TAG, "API request failed with status code:" + response.code());
                //System.out.println("API request failed with status code: " + response.code());
                Log.e(TAG, "Error message: " + response.message());
            }
        }catch (IOException e){
            Log.e(TAG, "Handle network error.", e);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void downloadAPI()throws IOException{
        OkHttpClient client = new OkHttpClient();
        //String url = "https://openpositioning.org/api/live/trajectory/download/"+"{"+userKey+"}";
        String url = "https://openpositioning.org/api/live/trajectory/download/" + userKey + "/?key=ewireless";
        //Create request
        Request request = new Request.Builder()
                .url(url)
                .build();

        //Execute
        try{
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void readAPI() throws IOException {
        OkHttpClient client = new OkHttpClient();
        //String url = "https://openpositioning.org/api/live/users/trajectories/" + "{" + userKey + "}";
        String url = "https://openpositioning.org/api/live/users/trajectories/" + userKey + "/?key=ewireless";
        //Create request
        Request request = new Request.Builder()
                .url(url)
                .build();

        //Execute
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}