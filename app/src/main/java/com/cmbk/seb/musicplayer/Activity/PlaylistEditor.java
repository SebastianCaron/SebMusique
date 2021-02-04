package com.cmbk.seb.musicplayer.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;

import com.cmbk.seb.musicplayer.Model.SongsList;
import com.cmbk.seb.musicplayer.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

public class PlaylistEditor extends AppCompatActivity {

    private int pos = 0;
    private List<PlayListItem> playListItemList = new ArrayList<>();
    private ArrayList<SongsList> songList = new ArrayList<>();
    private RecyclerView adaptere;
    String path = "";
    String name = "";
    ListView playlistsonglistview;

    EditText Nameplaylist;
    Button btsave,btadd,btnextplaylist;
    Intent FileIntent;
    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_editor);
        Intent inte = getIntent();
        if(inte.hasExtra("pos")){
            pos = inte.getIntExtra("pos",0);
        }
        playlistsonglistview = findViewById(R.id.playlistviewv);
        playlistsonglistview.setVisibility(View.VISIBLE);
        btnextplaylist = findViewById(R.id.btnextpalylisted);
        //adapter = new RecyclerView(this, adapter);
        adaptere = findViewById(R.id.playlistviewpos);
        adaptere.setVisibility(View.INVISIBLE);
        Nameplaylist = findViewById(R.id.Nameplay);
        btsave = findViewById(R.id.Save);
        btsave.setVisibility(View.INVISIBLE);
        btadd = findViewById(R.id.addbt);

        adapter = new MyRecyclerViewAdapter(this, playListItemList);
        adaptere.setLayoutManager(new LinearLayoutManager(this));
        adaptere.setAdapter(adapter);
        editPlaylist(pos);
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,0) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int position_dragged = dragged.getAdapterPosition();
                int position_target = target.getAdapterPosition();
                System.out.println(dragged);
                System.out.println(target);


                Collections.swap(playListItemList,position_dragged,position_target);
                Collections.swap(songList,position_dragged,position_target);
                adapter.notifyItemMoved(position_dragged,position_target);


                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        helper.attachToRecyclerView(adaptere);
        btsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlaylist(pos);
            }
        });

        btnextplaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistsonglistview.setVisibility(View.INVISIBLE);
                btnextplaylist.setVisibility(View.INVISIBLE);
                btadd.setVisibility(View.INVISIBLE);
                adaptere.setVisibility(View.VISIBLE);
                btsave.setVisibility(View.VISIBLE);
            }
        });

        btadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseafile();
            }
        });


    }

    private void editPlaylist(int position){
        System.out.println("position :" + position);
        int nptemp = 0;
        nptemp = position + position+1;
        File filee;
        filee = new File(getFilesDir(),"playlist.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(filee));
            String line;

            int nline = 0;
            while ((line = br.readLine()) != null) {
                if(nline == nptemp){
                    path = line;
                }
                if(nline == nptemp - 1){
                    name = line;
                }
                nline += 1;
            }
            br.close();
            System.out.println(name);
            Nameplaylist.setText(name);
            File filetoread = new File(getFilesDir(),name+".cmbk");
            BufferedReader br2 = new BufferedReader(new FileReader(filetoread));
            line = "";
            nline = 0;
            while ((line = br2.readLine()) != null){
                File temp = new File(line);
                songList.add(new SongsList(temp.getName(),"",line));
                playListItemList.add(new PlayListItem(temp.getName()));
            }
            br2.close();


        }catch (IOException e) {
            System.out.println(e);
        }


        adapter = new MyRecyclerViewAdapter(this, playListItemList);
        adaptere.setLayoutManager(new LinearLayoutManager(this));
        adaptere.setAdapter(adapter);
        playlistsonglistview.setAdapter(new PlayListItemAdapter(this, playListItemList));

        playlistsonglistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(PlaylistEditor.this);
                dialog.setTitle("Supprimer ?");
                dialog.setMessage("Supprimer ?");
                dialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        supprlastitem(position);
                        dialog.cancel();
                    }
                });
                dialog.show();
                return false;
            }
        });


    }

    private void supprlastitem(int pos){
        songList.remove(pos);
        playListItemList.remove(pos);
        playlistsonglistview.setAdapter(new PlayListItemAdapter(this, playListItemList));
        adapter = new MyRecyclerViewAdapter(this, playListItemList);
        adaptere.setLayoutManager(new LinearLayoutManager(this));
        adaptere.setAdapter(adapter);

    }
    private void savePlaylist(int pos){

        int nptemp = 0;
        nptemp = pos + pos+1;
        if(songList.size() != 0){
            try {
                File filetoread = new File(getFilesDir(),name+".cmbk");
                filetoread.delete();
                File file = new File(getFilesDir(),"playlist.txt");
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                File newFile = new File(getFilesDir(),"buffer.txt");
                FileWriter myWriter = new FileWriter(newFile);
                System.out.println(pos);
                int nline = 0;
                while ((line = br.readLine()) != null) {

                    if(nline != nptemp && nline != nptemp -1){
                        myWriter.write(line + "\n");
                    }
                    nline +=1;
                }
                myWriter.write(Nameplaylist.getText().toString() + "\n");
                myWriter.write(getFilesDir()+ "/" + Nameplaylist.getText().toString()+".cmbk" + "\n");
                br.close();
                myWriter.close();

                File buffer = new File(getFilesDir(),"buffer.txt");
                br = new BufferedReader(new FileReader(buffer));
                line = "";
                File playlistfile = new File(getFilesDir(),"playlist.txt");
                myWriter = new FileWriter(playlistfile);
                while ((line = br.readLine()) != null){
                    myWriter.write(line + "\n");
                }
                myWriter.close();
                br.close();

                File filesavesongs = new File(getFilesDir(),Nameplaylist.getText().toString() + ".cmbk");
                FileWriter mWriter = new FileWriter(filesavesongs,true);
                int i = 0;
                while(i < songList.size()){

                    mWriter.append(songList.get(i).getPath() + "\n");
                    System.out.println("Write value");
                    i+=1;
                }

                mWriter.close();


            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            finish();


        }else{
            try {
                File file = new File(getFilesDir(),"playlist.txt");
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                File newFile = new File(getFilesDir(),"buffer.txt");
                FileWriter myWriter = new FileWriter(newFile);
                System.out.println(pos);
                int nline = 0;
                while ((line = br.readLine()) != null) {

                    if(nline != nptemp -1){
                        myWriter.write(line + "\n");
                    }else{
                        myWriter.write(Nameplaylist.getText().toString() + "\n");

                    }
                    nline +=1;
                }


                br.close();
                myWriter.close();

                File buffer = new File(getFilesDir(),"buffer.txt");
                br = new BufferedReader(new FileReader(buffer));
                line = "";
                File playlistfile = new File(getFilesDir(),"playlist.txt");
                myWriter = new FileWriter(playlistfile);
                while ((line = br.readLine()) != null){
                    myWriter.write(line + "\n");
                }
                myWriter.close();
                br.close();




            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            finish();
        }
    }

    public void chooseafile(){

        FileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        FileIntent.setType("*/*");
        //FileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(FileIntent,11);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){

            case 11:
                Uri returnUrie = data.getData();

                path = getRealPathFromURI_API19(this, returnUrie);
                File tempfile = new File(path);
                songList.add(new SongsList(tempfile.getName(),"",path));
                playListItemList.add(new PlayListItem(tempfile.getName()));
                System.out.println(songList.size());
                System.out.println(path);
                playlistsonglistview.setAdapter(new PlayListItemAdapter(this, playListItemList));
                adapter = new MyRecyclerViewAdapter(this, playListItemList);
                adaptere.setLayoutManager(new LinearLayoutManager(this));
                adaptere.setAdapter(adapter);
                break;
        }
    }



    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
