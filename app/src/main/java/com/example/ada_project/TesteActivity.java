package com.example.ada_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;

import java.util.ArrayList;

public class TesteActivity extends AppCompatActivity {

    EditText text;
    Button btnOuvir;
    private ArrayList messageArrayList;
    StreamPlayer streamPlayer;
    private SpeechToText speechService;
    static String v;
    TextView textView;

    private TextToSpeech initTextToSpeeachService(){
        TextToSpeech service = new TextToSpeech();
        service.setApiKey("lLrMAvRPcVj-ztqHou1uo3wlhRoCGFoBXdLGMskF7x5h");
        return service;
    }


    private class WatsonTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Running then Watson Thread");
                }
            });

            TextToSpeech textToSpeech = initTextToSpeeachService();
            streamPlayer = new StreamPlayer();
            streamPlayer.playStream(textToSpeech.synthesize(v, Voice.PT_ISABELA).execute());
            return "Text";
        }

        @Override
        protected void onPostExecute(String s) {
            textView.setText("TTS status: " + s);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste);
        text = (EditText) findViewById(R.id.texto);
        v = String.valueOf(text.getText());
        btnOuvir = (Button) findViewById(R.id.ouvir);

        btnOuvir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Hello " + text.getText());
                textView.setText("Texto: " + text.getText());
                WatsonTask task = new WatsonTask();
                task.execute(new String[]{});
            }
        });
    }
}
