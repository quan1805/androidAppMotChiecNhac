package huce.fit.motchiecnhac;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    private Context mContext;
    static ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles) {
        this.mFiles = mFiles;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        holder.file_artist.setText(mFiles.get(position).getArtist());
        byte[] image = getAlbumArt(mFiles.get(position).getPath());
        if (image != null) {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.ic_music)
                    .into(holder.album_art);
        }
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, PlayMusic.class);
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        });
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((item) -> {
                    switch (item.getItemId()) {
                        case R.id.delete:
                            //Toast.makeText(mContext, "Delete Clicked!!!", Toast.LENGTH_SHORT).show();
                            deleteFile(position, v);
                            break;
                    }
                    return true;
                });
            }
        });
    }

    private void deleteFile(int position, View v) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId())); //contentUri
//        File file1 = new File("/storage/emulated/0/Download/Cài đặt Thư viện ImGUI và Assimp.docx");
//        boolean deleted1 = file1.delete();
        File file1 = new File("/storage/emulated/0/Download/Cài đặt Thư viện ImGUI và Assimp.docx");

        if(file1.exists()) {
            File file2 = new File(file1.getAbsolutePath());
            boolean deleted1 = file2.delete();
            System.out.println("Delete boolean: " + deleted1 + "AbsolutePath()"+file1.getAbsolutePath());
        }else
        {
            System.out.println("null");
        }

        File file = new File(mFiles.get(position).getPath());
        boolean deleted = file.delete(); //delete your file
        Toast.makeText(mContext, "Delete boolean: " + deleted + ", positon: "+position + "Path: " + mFiles.get(position).getPath() , Toast.LENGTH_SHORT).show();
        if (deleted) {
            mContext.getContentResolver().delete(contentUri, null, null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFiles.size());
            Snackbar.make(v, "File Deleted: ", Snackbar.LENGTH_LONG).show();
        }
        else
        {
            //may be file in sd card
            Snackbar.make(v, "Can't be Deleted: ", Snackbar.LENGTH_LONG).show();

        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView file_name;
        TextView file_artist;
        ImageView album_art, menuMore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            file_artist = itemView.findViewById(R.id.music_file_artist);
            album_art = itemView.findViewById(R.id.music_img);
            menuMore = itemView.findViewById(R.id.btnMore);
        }
    }

    private byte[] getAlbumArt(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
    void updateList(ArrayList<MusicFiles> musicFilesArrayList)
    {
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
