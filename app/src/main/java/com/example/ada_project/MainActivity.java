package com.example.ada_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ada_project.AsyncTask.ConnectAda;
import com.example.ada_project.AsyncTask.ConnectAsyncTask;
import com.example.ada_project.Prefs.PreferenciasUsuario;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.android.library.audio.utils.ContentType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.BaseRecognizeCallback;
import com.ibm.watson.developer_cloud.speech_to_text.v1.websocket.RecognizeCallback;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static String messages;// implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    Button button2;
    private ArrayList messageArrayList;
    StreamPlayer streamPlayer;
    private SpeechToText speechService;
    private GoogleApiClient googleApiClient;
    MicrophoneInputStream capture;
    Boolean listening = false;
    Double NowLat, NowLong;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    WatsonTask task = new WatsonTask();

    FloatingActionButton floatingActionButton;

    private TextToSpeech initTextToSpeeachService(){

        TextToSpeech service = new TextToSpeech();
        String apiKey = getString(R.string.text_speech_iam_apikey);
        service.setEndPoint(getString(R.string.text_speech_url));

        service.setUsernameAndPassword("apiKey", apiKey);
        return service;

    }
    public class WatsonTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // textView.setText("running the watson thread");
                }
            });

            TextToSpeech textToSpeech = initTextToSpeeachService();
            streamPlayer = new StreamPlayer();
            streamPlayer.playStream(textToSpeech.synthesize(String.valueOf(messages), Voice.PT_ISABELA).execute());
            return "Text";
        }

        @Override
        protected void onPostExecute(String s) {
            startVoiceInput();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);

        if(preferences.contains("welcome")) {
            messages = "Bem vindo, Para onde vamos agora";
        }else {
            PreferenciasUsuario prefs = new PreferenciasUsuario();
            messages = "Olá, Seja bem vindo. Você pode me chamar de êida, sou sua nova assistente virtual e irei guia-lo para qualquer lugar. Diga um local ou endereço para onde gostaria de ir";
            prefs.welcome(preferences, "s");
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceInput();
            }
        });

        task.execute(new String[]{});

    }


    // Para fala
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Olá, Como posso ajuda-lo?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String falado = result.get(0).toLowerCase();

                    String[] end = falado.split("ir para");
                    if(end.length > 0) {
                        if (end[1] != "") {
                            //Intent i = new Intent(MainActivity.this, MapsActivity.class);
                            //i.putExtra("endereco", end[1]);
                            //startActivity(i);
                            this.searchAddress(end[1]);
                        }
                    }

                }
                break;
            }

        }
    }

    public void searchAddress(String endereco){
        String urlTopass = "https://project-ada.mybluemix.net/getPlaces?searchText="+endereco.replaceAll(" ", "%20")+"&location=-23.5629,-46.6544";
        new ConnectAda(urlTopass).execute();
    }

    public static String addresses(String result){
        String[] title;
        String[] address;
        String lat;
        String lng;


        if(result != null){
            messages = "Encontrei alguns lugares para onde poderia gostar de ir";
            try {

                Log.d("ARRAYS", result);

                JSONObject jsonObject = new JSONObject(result);
                JSONArray geometry = jsonObject.getJSONArray("geometry");

                JSONObject locations;
                JSONObject locat;

                if(geometry.length() > 1) {
                    for (int i = 0; i < geometry.length(); i++) {
                        locations = new JSONObject(geometry.getString(i));
                        Log.i("TESTE", "nome=" + locations.getString("location"));
                    }
                }else{
//                    locations = new JSONObject(geometry.getString(0));
//                    String location = locations.getString("location");
//                    Log.d("LOCATION", location);
                    messages = "Ok irei adicionar a rota, só um instante";

                }

               // JSONArray routeArray = json.getJSONArray("name");

                //JSONObject routes = routeArray.getJSONObject(0);

              //  JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
        return result;
    }


    // Locations


//    @Override
//    protected void onStop() {
//        super.onStop();
//        stopConnection();
//    }
//    public void stopConnection() {
//        if (googleApiClient.isConnected()) {
//            googleApiClient.disconnect();
//        }
//    }
//
//    @Override
//    public void onConnected(Bundle bundle) {
//
//        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//
//        NowLat = lastLocation.getLatitude();
//        NowLong = lastLocation.getLongitude();
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        // Aguardando o GoogleApiClient reestabelecer a conexão.
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        //A conexão com o Google API falhou!
//        stopConnection();
//    }



}


