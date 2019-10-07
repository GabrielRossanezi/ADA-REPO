package com.example.ada_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ada_project.Prefs.PreferenciasUsuario;

public class LoginActivity extends AppCompatActivity {

    EditText name, password;
    TextView message;
    Button btnAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        message = (TextView) findViewById(R.id.message);

        name = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.userPass);

        btnAction = (Button) findViewById(R.id.actionLogin);

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameUser = name.getText().toString();
                String passUser = password.getText().toString();

                SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);

                if (preferences.contains("userName")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    if (nameUser.contains("teste")) {

                        PreferenciasUsuario prefs = new PreferenciasUsuario();
                        prefs.userData(preferences, nameUser);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        message.setText("Erro ao tentar logar");
                    }
                }
            }
        });
    }
}
