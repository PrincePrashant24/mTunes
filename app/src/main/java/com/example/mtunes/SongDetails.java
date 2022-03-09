package com.example.mtunes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class SongDetails extends AppCompatActivity {
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String currentSong;
    int position;
    long ml;
    float sizeInByte,sizeInKb,sizeInMb;
    TextView songName,songDuration,songSize,songLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_details);
        songName = findViewById(R.id.isong);
        songDuration = findViewById(R.id.iduration);
        songSize = findViewById(R.id.isize);
        songLocation = findViewById(R.id.ilocation);

        Intent intent = getIntent();

        Bundle bundle= intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        currentSong = intent.getStringExtra("currentSong");
        position = intent.getIntExtra("position",0);

        songName.setText(currentSong);
        DecimalFormat df = new DecimalFormat("0.00");
       sizeInByte = songs.get(position).length();
       sizeInKb = sizeInByte/1024;
       sizeInMb= sizeInKb/1024;
       if(sizeInMb>=1)
       {
           songSize.setText(String.valueOf(df.format(sizeInMb))+"MB");
       }
       else if(sizeInKb>=1)
       {
           songSize.setText(String.valueOf(df.format(sizeInKb))+"KB");
       }
       else{
           songSize.setText(String.valueOf(df.format(sizeInByte))+"Bytes");
       }
        //songSize.setText(String.valueOf(sizeInMb));
      //  songSize.setText(a);

        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        ml=mediaPlayer.getDuration();
     //   mediaPlayer.
        NumberFormat f = new DecimalFormat("00");
        songDuration.setText(f.format((ml/3600000)%24)+":"+f.format((ml/60000)%60)+":"+f.format((ml/1000)%60));
         songLocation.setText(songs.get(position).getPath());


    }
}