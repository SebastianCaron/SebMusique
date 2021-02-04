package com.cmbk.seb.musicplayer.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import com.cmbk.seb.musicplayer.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    String path;
    private List<String> temppathsaver = new ArrayList<>();

    Button pickfile;
    Intent FileIntent;
    ConstraintLayout fromdir,fromdirstep2,fromnothing,fromscstep2;
    Button btfromdir,btfromnothing, btfromdirsave,btpickfilesc,Btfromdirsavesc, btSuppr;
    EditText dirNamePlaylist, Namescplaylist;
    private List<PlayListItem> playListItemList = new ArrayList<>();
    ListView playlistsonglistview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        pickfile = findViewById(R.id.btpickfile);
        fromdir = findViewById(R.id.fromdir);
        fromdirstep2 = findViewById(R.id.fromdirstep2);
        fromnothing = findViewById(R.id.fromScratch);
        btfromdir = findViewById(R.id.btfromdir);
        btfromnothing = findViewById(R.id.btfromnothing);
        btfromdirsave = findViewById(R.id.btFromdirSave);
        dirNamePlaylist = findViewById(R.id.NamePlaylist);
        Namescplaylist = findViewById(R.id.NamescPlaylist);
        btpickfilesc = findViewById(R.id.btpickfilesc);
        Btfromdirsavesc = findViewById(R.id.btFromscSave);
        playlistsonglistview = findViewById(R.id.listsonginplaylist);
        fromscstep2 = findViewById(R.id.fromscstep2);


        fromdirstep2.setVisibility(View.INVISIBLE);
        fromdir.setVisibility(View.VISIBLE);
        fromnothing.setVisibility(View.VISIBLE);
        fromscstep2.setVisibility(View.INVISIBLE);

        Btfromdirsavesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveplaylistfromsc();
            }
        });

        btfromdirsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePlaylist();
            }
        });
        btpickfilesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseafile(1);
            }
        });
        btfromdir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromdirstep2.setVisibility(View.VISIBLE);
                fromdir.setVisibility(View.INVISIBLE);
                fromnothing.setVisibility(View.INVISIBLE);
            }
        });
        btfromnothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromscstep2.setVisibility(View.VISIBLE);
                fromdir.setVisibility(View.INVISIBLE);
                fromnothing.setVisibility(View.INVISIBLE);
            }
        });

        pickfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseafile(0);
            }
        });
        playlistsonglistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
                dialog.setTitle("Supprimer ?");
                dialog.setMessage("Supprimer la Playlist ?");
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
        temppathsaver.remove(pos);
        playListItemList.remove(pos);
        playlistsonglistview.setAdapter(new PlayListItemAdapter(this, playListItemList));

    }

    public void chooseafile(int step){
        if(step == 0){
            FileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            FileIntent.setType("*/*");
            //FileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(FileIntent,10);
        }
        if(step == 1){
            FileIntent = new Intent(Intent.ACTION_GET_CONTENT);
            FileIntent.setType("*/*");
            //FileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(FileIntent,11);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 10:
                Uri returnUri = data.getData();

                path = getRealPathFromURI_API19(this, returnUri);
                System.out.println(path);

                break;
            case 11:
                Uri returnUrie = data.getData();

                path = getRealPathFromURI_API19(this, returnUrie);
                File tempfile = new File(path);
                temppathsaver.add(path);
                playListItemList.add(new PlayListItem(tempfile.getName()));
                System.out.println(temppathsaver.size());
                System.out.println(path);
                playlistsonglistview.setAdapter(new PlayListItemAdapter(this, playListItemList));
                break;
        }
    }

    private void savePlaylist(){
        try {
            File file = new File(getFilesDir(),"playlist.txt");
            FileWriter myWriter = new FileWriter(file,true);
            myWriter.append(dirNamePlaylist.getText()+ "\n");
            myWriter.append(path+ "\n");
            System.out.println(path);

            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        finish();
    }

    private void saveplaylistfromsc(){
        if(temppathsaver.size() != 0){
            try {
                File file = new File(getFilesDir(),"playlist.txt");
                FileWriter myWriter = new FileWriter(file,true);
                File fileplaylistcontainer = new File(getFilesDir(),Namescplaylist.getText().toString()+".cmbk");
                FileWriter mywr = new FileWriter(fileplaylistcontainer,true);
                System.out.println(temppathsaver.size());
                int i = 0;
                while(i < temppathsaver.size()){

                    mywr.append(temppathsaver.get(i) + "\n");
                    System.out.println("Write value");
                    i+=1;
                }

                Uri uri = Uri.fromFile(fileplaylistcontainer);

                myWriter.append(Namescplaylist.getText() + "\n");
                myWriter.append(getRealPathFromURI_API19(this,uri)+ "\n");
                System.out.println(path);
                mywr.close();
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            finish();
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
