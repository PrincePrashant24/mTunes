package com.example.mtunes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class PlaySong extends AppCompatActivity {

    TextView songName,timer,songDuration;
    ImageView pause,next,previous, mainImage, suffle, loop;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    SeekBar seekBar;
    int position;
    Thread updateSeek;
    long ml;
    private long timeRemainning=0;
    CountDownTimer c1;
    BlurView blurView;
    boolean loopOnOff = false;
    boolean suffleOnOff= false;
    boolean last = true;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        blurView=findViewById(R.id.blurLayout);
        blurBackground();
        songName = findViewById(R.id.songName);
        songDuration = findViewById(R.id.songDuration);
        timer = findViewById(R.id.timer);
        mainImage = findViewById(R.id.mainImage);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        seekBar = findViewById(R.id.seekBar);
        loop = findViewById(R.id.loop);
        suffle = findViewById(R.id.suffle);
        Intent intent = getIntent();
        Bundle bundle= intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        songName.setText(textContent);
        songName.setSelected(true);
        position = intent.getIntExtra("position",0);
        changeAllThings(position);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

                seekBar.setMax(mediaPlayer.getDuration());
                long abc = mediaPlayer.getDuration()-mediaPlayer.getCurrentPosition();
                c1.cancel();
                timer(abc);

            }
        });
        updateSeekbaar();
        timer(ml);

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying())
                {

                    pause.setImageResource(R.drawable.playpink);
                    mediaPlayer.pause();
                    c1.cancel();
                }
                else{

                    if(!last)
                    {
                        last=true;
                        pause.setImageResource(R.drawable.pausepink);
                        position=0;
                        changeAllThings(position);
                        c1.cancel();
                        timer(ml);
                        updateSeekbaar();

                    }
                    else
                    {
                        pause.setImageResource(R.drawable.pausepink);
                        mediaPlayer.start();
                        long update=timeRemainning;
                        timer(update);
                    }

                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                last = true;
                if(suffleOnOff && !loopOnOff)
                {
                    position = getRandom(songs.size()-1);
                }
                else if(!suffleOnOff && !loopOnOff )
                {
                    if(position!=0)
                    {
                        position = position-1;
                    }
                    else{
                        position = songs.size()-1;
                    }
                }
                c1.cancel();
                changeAllThings(position);
                timer(ml);
                updateSeekbaar();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                last = true;
                if(suffleOnOff && !loopOnOff)
                {
                    position = getRandom(songs.size()-1);
                }
                else if(!suffleOnOff && !loopOnOff )
                {
                    if(position!=songs.size()-1)
                    {
                        position = position+1;
                    }
                    else{
                        position=0;
                    }
                }
                c1.cancel();
                changeAllThings(position);
                timer(ml);
                updateSeekbaar();
            }
        });
        suffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(suffleOnOff)
                {
                    suffle.setImageResource(R.drawable.suffle_off);
                    suffleOnOff=false;
                }
                else{

                    suffle.setImageResource(R.drawable.suffle_on);
                    suffleOnOff=true;
                }


            }
        });
        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loopOnOff)
                {
                    loop.setImageResource(R.drawable.loop_off);
                    loopOnOff=false;
                }
                else{
                    loop.setImageResource(R.drawable.loop_on);
                    loopOnOff=true;
                }
            }
        });
    }
    public void metaData (Uri uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte [] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art!=null)
        {
         bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this,mainImage,bitmap);
        }
        else{

            Glide.with(PlaySong.this).asBitmap()
                    .load(R.drawable.spotify)
                    .into(mainImage);
//           bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
//            ImageAnimation(this,mainImage,bitmap);
        }
    }
    public byte[] getAlbum(String uri)
    {
        MediaMetadataRetriever retriever= new  MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void blurBackground()
    {
        float radius = 22f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setBlurAutoUpdate(true)
                .setHasFixedTransformationMatrix(true); // Or false if it's in a scrolling container or might be animated
    }
    public int getRandom(int i)
    {
        Random random = new Random();
        return random.nextInt(i+1);
    }
    public void timer(long setTime)
    {
        c1= new CountDownTimer(setTime,1000){
            public void onTick(long l) {
                NumberFormat f = new DecimalFormat("00");
                long hr =(l/3600000)%24;
                long min =(l/60000)%60;
                long sec =(l/1000)%60;
                timer.setText(f.format(hr)+":"+f.format(min)+":"+f.format(sec));
                timeRemainning=l;
            }
            public void onFinish() {
                timer.setText("00:00:00");
                if(suffleOnOff && !loopOnOff)
                {
                    position = getRandom(songs.size()-1);
                }
                else if(!suffleOnOff && !loopOnOff )
                {
                    if(position!=songs.size()-1)
                    {
                        position = position+1;
                        changeAllThings(position);
                        c1.cancel();
                        timer(ml);
                        updateSeekbaar();
                    }
                    else {
                       last = false;
                       pause.setImageResource(R.drawable.playpink);
                    }
                }
            }
        };
        c1.start();
    }
    public void updateSeekbaar()
    {
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try{
                    while(currentPosition<mediaPlayer.getDuration())
                    {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
    }
    public void changeAllThings(int updatePosition)
    {
        Uri uri = Uri.parse(songs.get(updatePosition).toString());
        metaData(uri);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        ml=mediaPlayer.getDuration();
        NumberFormat f = new DecimalFormat("00");
        songDuration.setText(f.format((ml/3600000)%24)+":"+f.format((ml/60000)%60)+":"+f.format((ml/1000)%60));
        seekBar.setMax(mediaPlayer.getDuration());
        textContent = songs.get(updatePosition).getName().toString().replace(".mp3","");;
        songName.setText(textContent);
    }
    public void ImageAnimation(Context context, ImageView mainImage, Bitmap bitmap)
    {
        Animation animOut = AnimationUtils.loadAnimation(context,android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context,android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
               Glide.with(context).load(bitmap).into(mainImage);
               animIn.setAnimationListener(new Animation.AnimationListener() {
                   @Override
                   public void onAnimationStart(Animation animation) {

                   }

                   @Override
                   public void onAnimationEnd(Animation animation) {

                   }

                   @Override
                   public void onAnimationRepeat(Animation animation) {

                   }
               });
               mainImage.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mainImage.startAnimation(animOut);
    }
}