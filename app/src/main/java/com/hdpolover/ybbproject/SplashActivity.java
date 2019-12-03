package com.hdpolover.ybbproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hdpolover.ybbproject.adapters.SingletonVolley;
import com.hdpolover.ybbproject.constant.Constant;
import com.hdpolover.ybbproject.models.JSONDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class SplashActivity extends AppCompatActivity {

    private int loadingTime = 1500;

    private String info_url = Constant.URL;
    private String blog_url = Constant.BLOG_URL;

    public static List<JSONDataModel> infoJsonData;
    public static List<JSONDataModel> blogJsonData;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setMessage("Getting info...");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //jump to dashboard activity after splash screen
                Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        }, loadingTime);

        //getting the data from wp
//        infoJsonData = new ArrayList<>();
//        blogJsonData = new ArrayList<>();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, info_url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                try {
//                    Gson gson = new Gson();
//                    JSONArray ParentArray = new JSONArray(s);
//                    for (int i = 0; i < ParentArray.length(); i++){
//                        JSONObject ParentObject = ParentArray.getJSONObject(i);
//                        JSONDataModel jsonDataModel = gson.fromJson(ParentObject.toString(), JSONDataModel.class);
//                        jsonDataModel.setId(ParentObject.getInt("id"));
//                        jsonDataModel.setDate(ParentObject.getString("date").substring(0,10));
//                        jsonDataModel.setLink(ParentObject.getString("link"));
//                        jsonDataModel.setTitle(ParentObject.getJSONObject("title").getString("rendered"));
//                        jsonDataModel.setContent(ParentObject.getJSONObject("content").getString("rendered"));
//                        jsonDataModel.setExcerpt(ParentObject.getJSONObject("excerpt").getString("rendered"));
//                        jsonDataModel.setJetpack_featured_media_url(ParentObject.getString("jetpack_featured_media_url"));
//                        infoJsonData.add(jsonDataModel);
//                        Log.e("in", infoJsonData.get(i).getTitle());
//                    }
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                finally {
//                    if (infoJsonData!=null) {
//                        progressDialog.dismiss();
//                        Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//                writeToFile(s);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                volleyError.printStackTrace();
//                Toast.makeText(getApplicationContext(), volleyError + "",Toast.LENGTH_LONG).show();
//                try {
//                    Gson gson = new Gson();
//
//                    JSONArray ParentArray = new JSONArray(readFromFile());
//                    for (int i = 0; i < ParentArray.length(); i++){
//                        JSONObject ParentObject = ParentArray.getJSONObject(i);
//                        JSONDataModel jsonDataModel = gson.fromJson(ParentObject.toString(), JSONDataModel.class);
//                        jsonDataModel.setId(ParentObject.getInt("id"));
//                        jsonDataModel.setDate(ParentObject.getString("date").substring(0,10));
//                        jsonDataModel.setLink(ParentObject.getString("link"));
//                        jsonDataModel.setTitle(ParentObject.getJSONObject("title").getString("rendered"));
//                        jsonDataModel.setTitle(ParentObject.getJSONObject("content").getString("rendered"));
//                        jsonDataModel.setTitle(ParentObject.getJSONObject("excerpt").getString("rendered"));
//                        jsonDataModel.setJetpack_featured_media_url(ParentObject.getString("jetpack_featured_media_url"));
//                        infoJsonData.add(jsonDataModel);
//                        Log.e("in", infoJsonData.get(i).getTitle());
//                    }
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                finally {
//                    if (infoJsonData!=null) {
//                        progressDialog.dismiss();
//                        Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            }
//        });
//        SingletonVolley.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void writeToFile(String data){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("dont_delete.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFromFile(){
        String result = "";
        try {
            InputStream inputStream = openFileInput("dont_delete.txt");
            if (inputStream!=null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String tempString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((tempString = bufferedReader.readLine())!=null){
                    stringBuilder.append(tempString);
                }
                inputStream.close();
                result = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
