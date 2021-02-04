package com.cmbk.seb.musicplayer.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cmbk.seb.musicplayer.Adapter.ViewPagerAdapter;
import com.cmbk.seb.musicplayer.DB.FavoritesOperations;
import com.cmbk.seb.musicplayer.Fragments.AllSongFragment;
import com.cmbk.seb.musicplayer.Fragments.CurrentSongFragment;
import com.cmbk.seb.musicplayer.Fragments.FavSongFragment;
import com.cmbk.seb.musicplayer.Model.SongsList;
import com.cmbk.seb.musicplayer.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AllSongFragment.createDataParse, FavSongFragment.createDataParsed, CurrentSongFragment.createDataParsed, Playable{

    private Menu menu;


    private ViewPager playlist;
    private ListView playlistlistview;
    private ImageButton imgBtnPlayPause, imgbtnReplay, imgBtnPrev, imgBtnNext, imgBtnSetting,imgBtnRandom;
    private ConstraintLayout fulscreenlayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SeekBar seekbarController;
    private DrawerLayout mDrawerLayout;
    private TextView tvCurrentTime, tvTotalTime;
    public String colortoolbar,colorsonglist,colorplayerlect,colornotif;
    public boolean lectureplaylist;
    private List<PlayListItem> playListItemList = new ArrayList<>();
    private ArrayList<SongsList> songList;
    private int currentPosition;
    private String searchText = "";
    private SongsList currSong;
    private boolean randomb = false;
    private boolean checkFlag = false, repeatFlag = false, playContinueFlag = true, favFlag = true, playlistFlag = false;
    private final int MY_PERMISSION_REQUEST = 100;
    private int allSongLength;
    File filee = null;
    MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;

    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        hideSystemUI(this);
        setContentView(R.layout.activity_main);


        init();
        grantedPermission();
        createchannel();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createchannel();
            registerReceiver(broadcastReceiver,new IntentFilter("CMBK_CMBK"));
            startService(new Intent(getBaseContext(),OnClearFromRecentService.class));
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



        load();

        final AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.setUpdateFrom(UpdateFrom.GITHUB);
        //appUpdater.setGitHubUserAndRepo("SebastianCaron", "SebMusique");
        appUpdater.setUpdateJSON("https://raw.githubusercontent.com/SebastianCaron/SebMusique/master/app/update-changelog.json");

        appUpdater.start();
        appUpdater.setTitleOnUpdateAvailable("Mise a jour disponible")
                .setContentOnUpdateAvailable("Mettez a jour mon application")
                .setTitleOnUpdateNotAvailable("Pas de mise a jour disponible")
                .setContentOnUpdateNotAvailable("Pas de mise a jour disponible")
                .setButtonUpdate("Mettre a jour ?")
                .setButtonUpdateClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent browserIntente = new Intent(Intent.ACTION_VIEW, Uri.parse("maj"));
                        startActivity(browserIntente);
                    }
                })
	            .setButtonDismiss("Plus tard")
                .setButtonDismissClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        appUpdater.dismiss();
                    }
                })

	            .setIcon(R.drawable.musicb); // Notification icon

        appUpdater.setDisplay(Display.DIALOG);

    }


    public static void hideSystemUI(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void createchannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,"CMBK", NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            if(notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Initialising the views
     */

    private void init() {
        imgBtnPrev = findViewById(R.id.img_btn_previous);
        imgBtnNext = findViewById(R.id.img_btn_next);
        playlistlistview = findViewById(R.id.playlistlist);
        fulscreenlayout = findViewById(R.id.setfulscreen);
        fulscreenlayout.setVisibility(View.INVISIBLE);
        playlistlistview.setVisibility(View.INVISIBLE);
        imgbtnReplay = findViewById(R.id.img_btn_replay);
        playlist = findViewById(R.id.playlistviewpos);
        playlist.setVisibility(View.INVISIBLE);
        imgBtnSetting = findViewById(R.id.img_btn_setting);
        imgBtnRandom = findViewById(R.id.btn_random);
        imgBtnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(randomb){
                    randomb = false;
                    Toast.makeText(MainActivity.this, "Lecture Aléatoire désactivée", Toast.LENGTH_SHORT).show();
                }
                if(!randomb){
                    randomb = true;
                    Toast.makeText(MainActivity.this, "Lecture Aléatoire Activée", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        FloatingActionButton refreshSongs = findViewById(R.id.btn_refresh);
        seekbarController = findViewById(R.id.seekbar_controller);
        viewPager = findViewById(R.id.songs_viewpager);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        imgBtnPlayPause = findViewById(R.id.img_btn_play);
        Toolbar toolbar = findViewById(R.id.toolbar);
        handler = new Handler();
        mediaPlayer = new MediaPlayer();

        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);

        imgBtnNext.setOnClickListener(this);
        imgBtnPrev.setOnClickListener(this);
        imgbtnReplay.setOnClickListener(this);
        refreshSongs.setOnClickListener(this);
        imgBtnPlayPause.setOnClickListener(this);
        imgBtnSetting.setOnClickListener(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.nav_about:
                        about();
                        break;
                    case R.id.nav_theme_color:
                        colortab();
                        break;
                    case R.id.nav_sleep_timer:
                        createplaylist();
                        break;
                    case R.id.fullsrcreen:
                        //setfullscreen();
                        break;
                    case R.id.youtubetomp3:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mp3-youtube.download/fr/audio-fast-converter"));
                        startActivity(browserIntent);
                    case R.id.musictomp3t:
                        Intent browserIntente = new Intent(Intent.ACTION_VIEW, Uri.parse("https://singemp3.com"));
                        startActivity(browserIntente);
                }
                return true;
            }
        });
    }

    private void setfullscreen(){
        hideSystemUI(this);
        FloatingActionButton bt = findViewById(R.id.btn_fermer);
        final Animation rotate = AnimationUtils.loadAnimation(this, R.anim.anim);
        final ImageView disque = findViewById(R.id.disqueImage);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fermerfullscreen();
            }
        });
        Animation a = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        a.setRepeatCount(-1);
        a.setDuration(500);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setFillAfter(true);

        fulscreenlayout.setVisibility(View.VISIBLE);
        rotate.setRepeatCount(Animation.INFINITE);

        disque.startAnimation(a);



    }

    private void fermerfullscreen(){
        fulscreenlayout.setVisibility(View.INVISIBLE);
        final ImageView disque2 = findViewById(R.id.disqueImage);
        disque2.setAnimation(null);
    }

    /**
     * Function to ask user to grant the permission.
     */

    private void grantedPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Snackbar snackbar = Snackbar.make(mDrawerLayout, "Autorisez la permission d'accés au Stockage", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        } else {
            setPagerLayout();
        }
    }

    /**
     * Checking if the permission is granted or not
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permissions Autorisé", Toast.LENGTH_SHORT).show();
                        setPagerLayout();
                    } else {
                        Snackbar snackbar = Snackbar.make(mDrawerLayout, "Autorisez la permission d'accés au Stockage", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        finish();
                    }
                }
        }
    }

    /**
     * Setting up the tab layout with the viewpager in it.
     */

    private void setPagerLayout() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getContentResolver());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getText().equals("PLAYLIST")){
                    System.out.println("playlistTab");
                    setPlaylist();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getText().equals("PLAYLIST")){
                    unselectPlaylist();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }



    /**
     * Function to show the dialog for about us.
     */
    private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.about))
                .setMessage(getString(R.string.about_text))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                queryText();
                setPagerLayout();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_search:
                Toast.makeText(this, "Rechercher", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_favorites:
                if (checkFlag)
                    if (mediaPlayer != null) {
                        if (favFlag) {
                            Toast.makeText(this, "Ajouté au favoris", Toast.LENGTH_SHORT).show();
                            item.setIcon(R.drawable.ic_favorite_filled);
                            SongsList favList = new SongsList(songList.get(currentPosition).getTitle(),
                                    songList.get(currentPosition).getSubTitle(), songList.get(currentPosition).getPath());
                            FavoritesOperations favoritesOperations = new FavoritesOperations(this);
                            favoritesOperations.addSongFav(favList);
                            setPagerLayout();
                            favFlag = false;
                        } else {
                            item.setIcon(R.drawable.favorite_icon);
                            favFlag = true;
                        }
                    }
                return true;
        }

        return super.onOptionsItemSelected(item);

    }


    /**
     * Function to handle the click events.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_play:
                if (checkFlag) {
                    CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,1,1,1);
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        final ImageView disque = findViewById(R.id.disqueImage);
                        disque.setAnimation(null);
                        imgBtnPlayPause.setImageResource(R.drawable.play_icon);
                    } else if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        final ImageView disque = findViewById(R.id.disqueImage);
                        Animation a = new RotateAnimation(0.0f, 720.0f,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                                0.5f);
                        a.setRepeatCount(-1);
                        a.setDuration(1000);
                        a.setInterpolator(new AccelerateDecelerateInterpolator());
                        a.setFillAfter(true);
                        disque.setAnimation(a);
                        disque.startAnimation(a);
                        imgBtnPlayPause.setImageResource(R.drawable.pause_icon);
                        playCycle();
                    }
                } else {
                    Toast.makeText(this, "Choisissez une musique ...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_refresh:
                Toast.makeText(this, "Rafraichis", Toast.LENGTH_SHORT).show();
                setPagerLayout();
                break;
            case R.id.img_btn_replay:

                if (repeatFlag) {
                    Toast.makeText(this, "Replay Desactivé", Toast.LENGTH_SHORT).show();
                    mediaPlayer.setLooping(false);
                    repeatFlag = false;
                } else {
                    Toast.makeText(this, "Replay Ajouté", Toast.LENGTH_SHORT).show();
                    mediaPlayer.setLooping(true);
                    repeatFlag = true;
                }
                break;
            case R.id.img_btn_previous:

                if (checkFlag) {
                    CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,0,1,0);
                    if (mediaPlayer.getCurrentPosition() > 10) {
                        if (currentPosition - 1 > -1) {
                            attachMusic(songList.get(currentPosition - 1).getTitle(), songList.get(currentPosition - 1).getPath());
                            currentPosition = currentPosition - 1;
                        } else {
                            attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
                        }
                    } else {
                        attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
                    }
                } else {
                    Toast.makeText(this, "Choisissez une musique ...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_btn_next:

                if (checkFlag) {
                    CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,0,1,0);
                    if (currentPosition + 1 < songList.size() && !randomb) {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getPath());
                        currentPosition += 1;
                    }if (!(currentPosition + 1 < songList.size()) && !randomb){
                        Toast.makeText(this, "Playlist terminée", Toast.LENGTH_SHORT).show();
                    }
                    if(randomb){
                        Random rand = new Random();
                        int randompos = rand.nextInt(songList.size()+1);
                        attachMusic(songList.get(randompos).getTitle(), songList.get(randompos).getPath());
                    }
                } else {
                    Toast.makeText(this, "Choisissez une musique...", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_btn_setting:
                if (!playContinueFlag) {
                    playContinueFlag = true;
                    Toast.makeText(this, "Lecture Automatique Activée", Toast.LENGTH_SHORT).show();
                } else {
                    playContinueFlag = false;
                    Toast.makeText(this, "Lecture Automatique Désactivée", Toast.LENGTH_SHORT).show();
                }
                break;



        }

    }

    /**
     * Function to attach the song to the music player
     *
     * @param name
     * @param path
     */

    private void attachMusic(String name, String path) {

        imgBtnPlayPause.setImageResource(R.drawable.play_icon);
        setTitle(name);
        menu.getItem(1).setIcon(R.drawable.favorite_icon);
        favFlag = true;
        boolean found = false;
        int musica = 0;
        int posi = 0;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            setControls();



        } catch (Exception e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                imgBtnPlayPause.setImageResource(R.drawable.play_icon);
                if (playContinueFlag && !randomb) {
                    if (currentPosition + 1 < songList.size()) {
                        attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getPath());
                        currentPosition += 1;
                    } else {
                        Toast.makeText(MainActivity.this, "PlayList Terminée", Toast.LENGTH_SHORT).show();
                    }
                }
                if(playContinueFlag && randomb){
                    Random rand = new Random();
                    int randompos = rand.nextInt(songList.size()+1);
                    attachMusic(songList.get(randompos).getTitle(), songList.get(randompos).getPath());
                }
                CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,1,1,1);
            }
        });
        try{
            CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,1,1,1);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Function to set the controls according to the song
     */

    private void setControls() {

        seekbarController.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();
        playCycle();
        checkFlag = true;
        if (mediaPlayer.isPlaying()) {
            imgBtnPlayPause.setImageResource(R.drawable.pause_icon);
            tvTotalTime.setText(getTimeFormatted(mediaPlayer.getDuration()));
        }

        seekbarController.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress);
                    tvCurrentTime.setText(getTimeFormatted(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * Function to play the song using a thread
     */
    private void playCycle() {
        try {
            seekbarController.setProgress(mediaPlayer.getCurrentPosition());
            tvCurrentTime.setText(getTimeFormatted(mediaPlayer.getCurrentPosition()));
            if (mediaPlayer.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        playCycle();

                    }
                };
                handler.postDelayed(runnable, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTimeFormatted(long milliSeconds) {
        String finalTimerString = "";
        String secondsString;

        //Converting total duration into time
        int hours = (int) (milliSeconds / 3600000);
        int minutes = (int) (milliSeconds % 3600000) / 60000;
        int seconds = (int) ((milliSeconds % 3600000) % 60000 / 1000);

        // Adding hours if any
        if (hours > 0)
            finalTimerString = hours + ":";

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // Return timer String;
        return finalTimerString;
    }


    /**
     * Function Overrided to receive the data from the fragment
     *
     * @param name
     * @param path
     */

    @Override
    public void onDataPass(String name, String path) {
        Toast.makeText(this, name, Toast.LENGTH_LONG).show();

        attachMusic(name, path);


    }

    @Override
    public void getLength(int length) {
        this.allSongLength = length;
    }

    @Override
    public void fullSongList(ArrayList<SongsList> songList, int position) {
        this.songList = songList;
        this.currentPosition = position;
        this.playlistFlag = songList.size() == allSongLength;
        this.playContinueFlag = playlistFlag;
    }

    @Override
    public String queryText() {
        return searchText.toLowerCase();
    }

    @Override
    public SongsList getSong() {
        currentPosition = -1;
        return currSong;
    }

    @Override
    public boolean getPlaylistFlag() {
        return playlistFlag;
    }

    @Override
    public void currentSong(SongsList songsList) {
        CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,0,1,0);
        this.currSong = songsList;
    }

    @Override
    public int getPosition() {
        return currentPosition;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){
                case CreateNotification.ACTION_PREV:
                    CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,0,1,0);
                    OnSongPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if(mediaPlayer.isPlaying()){
                        CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,0,1,0);
                        onTrackPause();
                    }else{
                        CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,0,1,1);
                        OnsongPlay();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,0,1,0);
                    OnTrackNext();
                    break;
            }
        }
    };

    @Override
    public void OnSongPrevious() {
        if (checkFlag) {
            if (mediaPlayer.getCurrentPosition() > 10) {
                if (currentPosition - 1 > -1) {
                    attachMusic(songList.get(currentPosition - 1).getTitle(), songList.get(currentPosition - 1).getPath());
                    currentPosition = currentPosition - 1;
                } else {
                    attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
                }
            } else {
                attachMusic(songList.get(currentPosition).getTitle(), songList.get(currentPosition).getPath());
            }
        } else {
            Toast.makeText(this, "Choisissez une musique ...", Toast.LENGTH_SHORT).show();
        }
        CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,1,1,1);

    }

    @Override
    public void OnsongPlay() {
        final ImageView disque = findViewById(R.id.disqueImage);
        Animation a = new RotateAnimation(0.0f, 720.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        a.setRepeatCount(-1);
        a.setDuration(1000);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setFillAfter(true);
        disque.setAnimation(a);
        disque.startAnimation(a);
        if (checkFlag) {
            CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,1,1,1);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                imgBtnPlayPause.setImageResource(R.drawable.play_icon);


            } else if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();

                imgBtnPlayPause.setImageResource(R.drawable.pause_icon);
                playCycle();
            }
        } else {
            Toast.makeText(this, "Choisissez une musique ...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTrackPause() {
        final ImageView disque = findViewById(R.id.disqueImage);
        disque.setAnimation(null);
        if (checkFlag) {
            CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,0,1,0);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                imgBtnPlayPause.setImageResource(R.drawable.play_icon);

            } else if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();

                imgBtnPlayPause.setImageResource(R.drawable.pause_icon);
                playCycle();
            }
        } else {
            Toast.makeText(this, "Choisissez une musique ...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnTrackNext() {
        if (checkFlag) {
            if (currentPosition + 1 < songList.size()) {
                attachMusic(songList.get(currentPosition + 1).getTitle(), songList.get(currentPosition + 1).getPath());
                currentPosition += 1;

            } else {
                Toast.makeText(this, "Playlist terminé", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Choisissez une musique...", Toast.LENGTH_SHORT).show();
        }
        CreateNotification.createNotification(MainActivity.this,songList.get(getPosition()), R.drawable.headset_icon,1,1,1);
    }





    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }



    public void load(){
        File file = new File(getFilesDir(),"test.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            int nline = 0;
            while ((line = br.readLine()) != null) {
                nline++;
                if(nline == 1){
                    colortoolbar = line;
                }
                if(nline == 2){
                    colorplayerlect = line;
                }
                if(nline == 3){
                    colorsonglist = line;
                }
                if(nline == 4){
                    colornotif = line;

                }

            }

            Toolbar toolbar;
            toolbar = findViewById(R.id.toolbar);
            LinearLayout linearlayout;
            linearlayout = findViewById(R.id.ll_include_controls);
            ViewPager viewPager;
            viewPager = findViewById(R.id.songs_viewpager);
            ImageView backgroundfullscreen = findViewById(R.id.backgroundfulscreen);

            if(colorsonglist.equals("0")){
                viewPager.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                playlistlistview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                backgroundfullscreen.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            if(colorsonglist.equals("1")){
                viewPager.setBackgroundColor(getResources().getColor(R.color.orange));
                backgroundfullscreen.setBackgroundColor(getResources().getColor(R.color.orange));
                playlistlistview.setBackgroundColor(getResources().getColor(R.color.orange));
            }
            if(colorsonglist.equals("2")){
                viewPager.setBackgroundColor(getResources().getColor(R.color.blue));
                playlistlistview.setBackgroundColor(getResources().getColor(R.color.blue));
                backgroundfullscreen.setBackgroundColor(getResources().getColor(R.color.blue));
            }
            if(colorsonglist.equals("3")){
                viewPager.setBackgroundColor(getResources().getColor(R.color.Yellow));
                backgroundfullscreen.setBackgroundColor(getResources().getColor(R.color.Yellow));
                playlistlistview.setBackgroundColor(getResources().getColor(R.color.Yellow));
            }
            if(colorsonglist.equals("4")){
                viewPager.setBackgroundColor(getResources().getColor(R.color.saumon));
                backgroundfullscreen.setBackgroundColor(getResources().getColor(R.color.saumon));
                playlistlistview.setBackgroundColor(getResources().getColor(R.color.saumon));
            }
            if(colorsonglist.equals("5")){
                viewPager.setBackgroundColor(getResources().getColor(R.color.RED));
                backgroundfullscreen.setBackgroundColor(getResources().getColor(R.color.RED));
                playlistlistview.setBackgroundColor(getResources().getColor(R.color.RED));
            }
            if(colorsonglist.equals("6")){
                viewPager.setBackgroundColor(getResources().getColor(R.color.green));
                backgroundfullscreen.setBackgroundColor(getResources().getColor(R.color.green));
                playlistlistview.setBackgroundColor(getResources().getColor(R.color.green));
            }
            if(colorsonglist.equals("7")){
                viewPager.setBackgroundColor(getResources().getColor(R.color.DarkBlue));
                playlistlistview.setBackgroundColor(getResources().getColor(R.color.DarkBlue));
                backgroundfullscreen.setBackgroundColor(getResources().getColor(R.color.DarkBlue));
            }
            if(colorsonglist.equals("8")){
                viewPager.setBackgroundColor(getResources().getColor(R.color.pink));
                backgroundfullscreen.setBackgroundColor(getResources().getColor(R.color.pink));
                playlistlistview.setBackgroundColor(getResources().getColor(R.color.pink));
            }




            if(colortoolbar.equals("0")){
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            if(colortoolbar.equals("1")){
                toolbar.setBackgroundColor(getResources().getColor(R.color.orange));
            }
            if(colortoolbar.equals("2")){
                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));
            }
            if(colortoolbar.equals("3")){
                toolbar.setBackgroundColor(getResources().getColor(R.color.Yellow));
            }
            if(colortoolbar.equals("4")){
                toolbar.setBackgroundColor(getResources().getColor(R.color.saumon));
            }
            if(colortoolbar.equals("5")){
                toolbar.setBackgroundColor(getResources().getColor(R.color.RED));
            }
            if(colortoolbar.equals("6")){
                toolbar.setBackgroundColor(getResources().getColor(R.color.green));
            }
            if(colortoolbar.equals("7")){
                toolbar.setBackgroundColor(getResources().getColor(R.color.DarkBlue));
            }
            if(colortoolbar.equals("8")){
                toolbar.setBackgroundColor(getResources().getColor(R.color.pink));
            }



            if(colorplayerlect.equals("0")){
                linearlayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            if(colorplayerlect.equals("1")){
                linearlayout.setBackgroundColor(getResources().getColor(R.color.orange));
            }
            if(colorplayerlect.equals("2")){
                linearlayout.setBackgroundColor(getResources().getColor(R.color.blue));
            }
            if(colorplayerlect.equals("3")){
                linearlayout.setBackgroundColor(getResources().getColor(R.color.Yellow));
            }
            if(colorplayerlect.equals("4")){
                linearlayout.setBackgroundColor(getResources().getColor(R.color.saumon));
            }
            if(colorplayerlect.equals("5")){
                linearlayout.setBackgroundColor(getResources().getColor(R.color.RED));
            }
            if(colorplayerlect.equals("6")){
                linearlayout.setBackgroundColor(getResources().getColor(R.color.green));
            }
            if(colorplayerlect.equals("7")){
                linearlayout.setBackgroundColor(getResources().getColor(R.color.DarkBlue));
            }
            if(colorplayerlect.equals("8")){
                linearlayout.setBackgroundColor(getResources().getColor(R.color.pink));
            }

            CreateNotification co = new CreateNotification();
            co.setcolor(Integer.parseInt(colornotif));



            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            System.out.println(e);
            e.printStackTrace();
        }



    }


    private void colortab(){

        Intent coloract = new Intent(MainActivity.this, ColorChooser.class);
        startActivity(coloract);
    }

    private void createplaylist(){

        Intent playlist = new Intent(MainActivity.this, PlaylistActivity.class);
        startActivity(playlist);
    }

    private void setPlaylist(){
        playlist.setVisibility(View.VISIBLE);
        playlistlistview.setVisibility(View.VISIBLE);
        playlistlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this,"Clicked",Toast.LENGTH_SHORT).show();
                onClickPlaylistElement(position);
            }
        });
        playlistlistview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                LongClick(position);
                return false;
            }
        });
        playListItemList.clear();
        filee = new File(getFilesDir(),"playlist.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(filee));
            String line;
            int nline = 0;
            while ((line = br.readLine()) != null) {
                if(nline%2 == 0) {
                    System.out.println(line);
                    playListItemList.add(new PlayListItem(line));
                }
                nline += 1;
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        playlistlistview.setAdapter(new PlayListItemAdapter(this, playListItemList));
        //viewPager.setVisibility(View.INVISIBLE);


    }

    private void LongClick(final int position){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
                supprimerPlaylis(position);
                dialog.cancel();
            }
        });
        dialog.setNeutralButton("Editer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editplaylist(position);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void editplaylist(int pos){
        Intent playlist = new Intent(MainActivity.this, PlaylistEditor.class);
        playlist.putExtra("pos",pos);
        startActivity(playlist);
    }

    private void supprimerPlaylis(int position){
        System.out.println("position :" + position);
        int nptemp = 0;


        nptemp = position + position+1;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filee));
            String line;
            String Container = "";
            int nline = 0;
            while ((line = br.readLine()) != null) {
                if(!(nline == nptemp -1) && !(nline == nptemp)) {
                    Container += line + "\n";
                }else{
                    if(!(nline == nptemp)){
                        if(line.contains(".cmbk")){
                            File filetosup = new File(line);
                            filetosup.delete();
                        }
                    }
                }
                nline += 1;
            }
            br.close();

            File file = new File(getFilesDir(),"playlist.txt");
            FileWriter myWriter = new FileWriter(file);
            myWriter.append(Container);
            myWriter.close();
            playListItemList.clear();
            filee = new File(getFilesDir(),"playlist.txt");
            try {
                BufferedReader br2 = new BufferedReader(new FileReader(filee));
                String line2;
                int nline2 = 0;
                while ((line2 = br2.readLine()) != null) {
                    if(nline2%2 == 0) {
                        System.out.println(line2);
                        playListItemList.add(new PlayListItem(line2));
                    }
                    nline2 += 1;
                }
                br2.close();
            }
            catch (IOException e) {
                //You'll need to add proper error handling here
            }

            playlistlistview.setAdapter(new PlayListItemAdapter(this, playListItemList));
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
    }

    private void unselectPlaylist(){
        playlist.setVisibility(View.INVISIBLE);
        playlistlistview.setVisibility(View.INVISIBLE);
        viewPager.setVisibility(View.VISIBLE);
    }
    ArrayList<SongsList> newsonglistplay = new ArrayList<>();
    public void onClickPlaylistElement(int nplaylist){
        System.out.println(nplaylist);
        int nptemp = 0;


        nptemp = nplaylist + nplaylist+1;
        System.out.println(nptemp);

        File dir = null;

        try {

            BufferedReader br = new BufferedReader(new FileReader(filee));
            String line;
            int nline = 0;
            while ((line = br.readLine()) != null) {
                if(nline == nptemp) {
                    System.out.println(line);

                    dir = new File(line);

                }
                nline += 1;
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }
        newsonglistplay.clear();
        //System.out.println(dir.getAbsolutePath());
        if(!dir.getName().contains(".cmbk")){
            dir = new File(dir.getParent() + "/");
            File[] files = dir.listFiles();
            System.out.println(files.length);

            displayDirectoryContents(dir);
        }else{
            displayDirectoryContentscmbk(dir);
        }


        songList = newsonglistplay;
        fullSongList(songList,0);
        attachMusic(songList.get(0).getTitle(),songList.get(0).getPath());
    }

    public void displayDirectoryContents(File dir) {
        try {
            File[] files = dir.listFiles();
            if(dir.listFiles() != null){
                for (File file : files) {
                    Uri uri = Uri.fromFile(file);
                    if (file.isDirectory()) {
                        System.out.println("directory:" + file.getCanonicalPath());
                        displayDirectoryContents(file);
                    } else {
                        newsonglistplay.add(new SongsList(file.getName().replace(".mp3",""),"",getRealPathFromURI_API19(this,uri)));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void displayDirectoryContentscmbk(File dir) {
        try {

            BufferedReader br = new BufferedReader(new FileReader(dir));
            String line;
            int nline = 0;
            while ((line = br.readLine()) != null) {
                File file = new File(line);
                Uri uri = Uri.fromFile(file);
                newsonglistplay.add(new SongsList(file.getName(),"",getRealPathFromURI_API19(this,uri)));
                nline += 1;
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
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
