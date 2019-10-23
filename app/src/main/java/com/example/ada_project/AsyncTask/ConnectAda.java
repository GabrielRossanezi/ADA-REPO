package com.example.ada_project.AsyncTask;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ada_project.MainActivity;
import com.example.ada_project.MapsActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ConnectAda extends AsyncTask<Void, Void, String> {

    String url;

    public ConnectAda(String urlPass){
        url = urlPass;
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        JSONParserAda jParser = new JSONParserAda();
        String json = jParser.getJSONFromUrl(url);
        return json;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        MainActivity main = new MainActivity();
        if (result != null) {
            main.addresses(result);

        }
    }
}

class JSONParserAda {

    InputStream is = null;
    JSONObject jObj = null;
    String json = "";

    // constructor
    public JSONParserAda() {
    }

    public String getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            json = sb.toString();
            is.close();
        } catch (Exception e) {
            //Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;

    }
}
