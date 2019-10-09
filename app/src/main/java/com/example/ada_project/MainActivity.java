package com.example.ada_project;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    Button btnAction, irPara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        irPara = (Button) findViewById(R.id.IrPara);
        btnAction = (Button) findViewById(R.id.actionMaps);

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TesteActivity.class);
                startActivity(intent);
            }
        });
//        irPara.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, TesteActivity.class);
//                startActivity(intent);
//            }
//        });

    }



}
