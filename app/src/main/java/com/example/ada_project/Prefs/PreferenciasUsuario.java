package com.example.ada_project.Prefs;

import android.content.SharedPreferences;

public class PreferenciasUsuario {

    public static void userData(SharedPreferences preferences, String userName) {

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("userName", userName);
        editor.commit();
    }
}