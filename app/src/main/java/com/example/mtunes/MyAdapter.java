package com.example.mtunes;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.makeRestartActivityTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.holder> implements Filterable
{
     ArrayList <File> SongsFile;
    ArrayList <File> SongsFileAll;
    Context context;
    public MyAdapter(Context context, ArrayList<File> SongsFile) {

        this.context = context;
        this.SongsFile = SongsFile;
        this.SongsFileAll = new ArrayList<>(SongsFile);

    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.singlerow,parent,false);

        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, @SuppressLint("RecyclerView") int position) {
        byte[] image = getAlbum(SongsFile.get(position).getPath());
        if (image!= null)
        {
            Glide.with(context).asBitmap()
                    .load(image)
                    .into(holder.img);
        }
        else
        {
            Glide.with(context)
                    .load(R.drawable.spotify)
                    .into(holder.img);
        }
        holder.tv.setText(SongsFile.get(position).getName().replace(".mp3",""));
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), PlaySong.class);

                String currentSong= SongsFile.get(position).getName();
                intent.putExtra("songList",SongsFile);
                intent.putExtra("currentSong",currentSong);
                intent.putExtra("position",position);
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            }
        });
        holder.songDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context,view);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        Intent intent = new Intent(context,SongDetails.class);
                        String currentSong= SongsFile.get(position).getName().replace(".mp3","");
                        intent.putExtra("songList",SongsFile);
                        intent.putExtra("currentSong",currentSong);
                        intent.putExtra("position",position);
                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        return true;
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return SongsFile.size();
    }
    public byte[] getAlbum(String uri)
    {
        MediaMetadataRetriever retriever= new  MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<File> filteredList = new ArrayList<File>();
            if(charSequence.toString().isEmpty())
            {
                filteredList.addAll(SongsFileAll);
            }
            else
            {

                for(File song: SongsFileAll){

                    if(song.getName().toLowerCase().contains(charSequence.toString().toLowerCase()))
                    {
                        filteredList.add(song);

                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            SongsFile.clear();
            SongsFile.addAll((Collection<? extends File>) filterResults.values);
            notifyDataSetChanged();
        }
    };
    class holder extends RecyclerView.ViewHolder
    {
        ImageView img,songDetails;
        TextView tv;
        RelativeLayout relativeLayout;

        public holder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView)itemView.findViewById(R.id.imageView);
            songDetails = (ImageView)itemView.findViewById(R.id.imageDetails);
            tv = (TextView) itemView.findViewById(R.id.textView);
            relativeLayout = itemView.findViewById(R.id.layout_id);

        }
    }
}
