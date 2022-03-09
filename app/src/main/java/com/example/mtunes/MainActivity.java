package com.example.mtunes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  {
RecyclerView rcv;
MyAdapter myAdapter;
    ArrayList<String> items =new ArrayList<>();


     ArrayList<File> mySongs = new ArrayList<File>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rcv = findViewById(R.id.rclView);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                        mySongs= fectSongs(Environment.getExternalStorageDirectory());
                       // String [] items = new String[mySongs.size()];

//                        for(int i=0;i<mySongs.size();i++)
//                        {
//                            items.add(mySongs.get(i).getName().replace(".mp3",""));
//                        }

                        myAdapter= new MyAdapter(MainActivity.this,mySongs);
                       rcv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        rcv.setAdapter(myAdapter);
                        RecyclerView.ItemDecoration divider = new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL);
                        rcv.addItemDecoration(divider);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();

    }
    public ArrayList<File> fectSongs(File file)
    {
        ArrayList arrayList = new ArrayList();
        File [] songs = file.listFiles();
        if(songs!=null)
        {
            for(File myFile:songs){
                if(!myFile.isHidden() && myFile.isDirectory())
                {
                    arrayList.addAll(fectSongs(myFile));
                }
                else
                {
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith("."))
                    {
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem= menu.findItem(R.id.search_song);
//        SearchView searchView = (SearchView) menuItem.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                myAdapter.getFilter().filter(s);
//                return false;
//            }
//        });

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myAdapter.getFilter().filter(query.toString());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.getFilter().filter(newText.toString());
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}