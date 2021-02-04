package com.cmbk.seb.musicplayer.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cmbk.seb.musicplayer.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ColorChooser extends AppCompatActivity {


    Button btletsgo;
    ConstraintLayout menu1;
    ConstraintLayout notifmenu;
    ConstraintLayout toolbarmenu;
    ConstraintLayout Playerlectmenu;
    ConstraintLayout SongListMenu;
    ImageButton orangebtnotif;
    ImageButton bluebtnotif,yellowbtnotif,saumonbtnotif,redbtnotif,greenbtnotif,darkbtnotif,pinkbtnotif;
    Button NextNotif,NextToolbar,NextPlayerLect,NextSongList;
    String colornotif = "0";
    String colorPlayer = "0";
    String colorSong = "0";
    String colorToolbar = "0";
    private String step = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_chooser);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btletsgo = findViewById(R.id.letsgo);
        menu1 = findViewById(R.id.Menu1);
        notifmenu = findViewById(R.id.NotifMenu);
        NextNotif = findViewById(R.id.Nextnotif);
        NextToolbar = findViewById(R.id.Nexttoolbar);
        NextPlayerLect = findViewById(R.id.NextControlls);
        NextSongList = findViewById(R.id.NextSonglist);
        toolbarmenu = findViewById(R.id.ToolbarMenu);
        Playerlectmenu = findViewById(R.id.PlayerLectMenu);
        SongListMenu = findViewById(R.id.SongListMenu);
        orangebtnotif = findViewById(R.id.orangebtnotif);
        bluebtnotif = findViewById(R.id.bluenotif);
        yellowbtnotif = findViewById(R.id.yellowbtnotif);
        saumonbtnotif = findViewById(R.id.saumonbtnotif);
        redbtnotif = findViewById(R.id.Redbtnotif);
        greenbtnotif = findViewById(R.id.Greenbtnotif);
        darkbtnotif = findViewById(R.id.DarkBluebtnotif);
        pinkbtnotif = findViewById(R.id.Pinkbtnotif);


        menu1.setVisibility(View.VISIBLE);
        notifmenu.setVisibility(View.INVISIBLE);
        Playerlectmenu.setVisibility(View.INVISIBLE);
        SongListMenu.setVisibility(View.INVISIBLE);
        toolbarmenu.setVisibility(View.INVISIBLE);

        orangebtnotif.setVisibility(View.INVISIBLE);
        bluebtnotif.setVisibility(View.INVISIBLE);
        yellowbtnotif.setVisibility(View.INVISIBLE);
        saumonbtnotif.setVisibility(View.INVISIBLE);
        redbtnotif.setVisibility(View.INVISIBLE);
        greenbtnotif.setVisibility(View.INVISIBLE);
        darkbtnotif.setVisibility(View.INVISIBLE);
        pinkbtnotif.setVisibility(View.INVISIBLE);

        btletsgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu1.setVisibility(View.INVISIBLE);
                notifmenu.setVisibility(View.VISIBLE);
                orangebtnotif.setVisibility(View.VISIBLE);
                bluebtnotif.setVisibility(View.VISIBLE);
                yellowbtnotif.setVisibility(View.VISIBLE);
                saumonbtnotif.setVisibility(View.VISIBLE);
                redbtnotif.setVisibility(View.VISIBLE);
                greenbtnotif.setVisibility(View.VISIBLE);
                darkbtnotif.setVisibility(View.VISIBLE);
                pinkbtnotif.setVisibility(View.VISIBLE);
                step = "NOTIF";
            }
        });

        pinkbtnotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step == "NOTIF"){
                    colornotif = "8";
                }
                if(step.equals("SONG")){
                    colorSong = "8";
                }
                if(step.equals("TOOLBAR")){
                    colorToolbar = "8";
                }
                if(step.equals("PLAYER")){
                    colorPlayer = "8";
                }
                Toast.makeText(ColorChooser.this, "Rose", Toast.LENGTH_SHORT).show();
            }
        });

        darkbtnotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step == "NOTIF"){
                    colornotif = "7";
                }
                if(step.equals("SONG")){
                    colorSong = "7";
                }
                if(step.equals("TOOLBAR")){
                    colorToolbar = "7";
                }
                if(step.equals("PLAYER")){
                    colorPlayer = "7";
                }
                Toast.makeText(ColorChooser.this, "Bleu Foncé", Toast.LENGTH_SHORT).show();
            }
        });

        greenbtnotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step == "NOTIF"){
                    colornotif = "6";
                }
                if(step.equals("SONG")){
                    colorSong = "6";
                }
                if(step.equals("TOOLBAR")){
                    colorToolbar = "6";
                }
                if(step.equals("PLAYER")){
                    colorPlayer = "6";
                }
                Toast.makeText(ColorChooser.this, "Vert", Toast.LENGTH_SHORT).show();
            }
        });

        redbtnotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step == "NOTIF"){
                    colornotif = "5";
                }
                if(step.equals("SONG")){
                    colorSong = "5";
                }
                if(step.equals("TOOLBAR")){
                    colorToolbar = "5";
                }
                if(step.equals("PLAYER")){
                    colorPlayer = "5";
                }
                Toast.makeText(ColorChooser.this, "Rouge", Toast.LENGTH_SHORT).show();
            }
        });

        saumonbtnotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step == "NOTIF"){
                    colornotif = "4";
                }
                if(step.equals("SONG")){
                    colorSong = "4";
                }
                if(step.equals("TOOLBAR")){
                    colorToolbar = "4";
                }
                if(step.equals("PLAYER")){
                    colorPlayer = "4";
                }
                Toast.makeText(ColorChooser.this, "Saumon", Toast.LENGTH_SHORT).show();
            }
        });

        yellowbtnotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step == "NOTIF"){
                    colornotif = "3";
                }
                if(step.equals("SONG")){
                    colorSong = "3";
                }
                if(step.equals("TOOLBAR")){
                    colorToolbar = "3";
                }
                if(step.equals("PLAYER")){
                    colorPlayer = "3";
                }
                Toast.makeText(ColorChooser.this, "Jaune", Toast.LENGTH_SHORT).show();
            }
        });

        orangebtnotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step == "NOTIF"){
                    colornotif = "1";
                }
                if(step.equals("SONG")){
                    colorSong = "1";
                }
                if(step.equals("TOOLBAR")){
                    colorToolbar = "1";
                }
                if(step.equals("PLAYER")){
                    colorPlayer = "1";
                }
                Toast.makeText(ColorChooser.this, "Orange", Toast.LENGTH_SHORT).show();
            }
        });
        NextSongList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarmenu.setVisibility(View.VISIBLE);
                SongListMenu.setVisibility(View.INVISIBLE);
                step = "TOOLBAR";
            }
        });
        NextToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarmenu.setVisibility(View.INVISIBLE);
                Playerlectmenu.setVisibility(View.VISIBLE);
                step = "PLAYER";
            }
        });

        NextPlayerLect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        bluebtnotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(step == "NOTIF"){
                    colornotif = "2";
                }
                if(step == "SONG"){
                    colorSong = "2";
                }
                if(step == "TOOLBAR"){
                    colorToolbar = "2";
                }
                if(step == "PLAYER"){
                    colorPlayer = "2";
                }
                Toast.makeText(ColorChooser.this, "Bleu", Toast.LENGTH_SHORT).show();
            }
        });

        NextNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifmenu.setVisibility(View.INVISIBLE);
                SongListMenu.setVisibility(View.VISIBLE);
                step = "SONG";
            }
        });


    }

    private void save(){
        savedata();

        //Intent mainact = new Intent(ColorChooser.this, MainActivity.class);
        //startActivity(mainact);
        finish();

    }

    public void savedata(){


        try {
            File file = new File(getFilesDir(),"test.txt");
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(colorToolbar + "\n");
            myWriter.write(colorPlayer + "\n");
            myWriter.write( colorSong+"\n");
            myWriter.write(colornotif + "\n");

            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }


}
