package com.cmbk.seb.musicplayer.Activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.support.v4.media.session.MediaSessionCompat;


import com.cmbk.seb.musicplayer.Model.SongsList;
import com.cmbk.seb.musicplayer.R;

public class CreateNotification {

    public static  final String CHANNEL_ID = "channel1";
    public static  final String ACTION_PREV = "actionprev";
    public static  final String ACTION_PLAY = "actionplay";
    public static  final String ACTION_NEXT = "actionnext";
    public static int drw_playe;
    public static int test = 0;
    public static Notification notification;

    public static void createNotification(Context context, SongsList song, int playbutton, int pos, int size,int isplaying){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context,"tag");

            //Bitmap icon = B
            PendingIntent pendingIntentPrev;
            int drw_previous;
            PendingIntent pendingIntentPlay;
            int drw_play;
            PendingIntent pendingIntentNext;
            int drw_next;

            if(pos == 0){
                drw_previous = 0;
                pendingIntentPrev = null;
            }else{
                Intent intentprev = new Intent(context, NotificationActionService.class).setAction(ACTION_PREV);

                pendingIntentPrev = PendingIntent.getBroadcast(context,0,intentprev,PendingIntent.FLAG_UPDATE_CURRENT);

                drw_previous = R.drawable.previous_icon;
            }

            Intent intentplay = new Intent(context, NotificationActionService.class).setAction(ACTION_PLAY);

            pendingIntentPlay = PendingIntent.getBroadcast(context,0,intentplay,PendingIntent.FLAG_UPDATE_CURRENT);
            if(isplaying == 0){
                drw_play = R.drawable.play_icon;
            }else{
                drw_play = R.drawable.pause_icon;
            }


            if(pos == 0){
                drw_next = 0;
                pendingIntentNext = null;
            }else{
                Intent intentnext = new Intent(context, NotificationActionService.class).setAction(ACTION_NEXT);

                pendingIntentNext = PendingIntent.getBroadcast(context,0,intentnext,PendingIntent.FLAG_UPDATE_CURRENT);
                drw_next = R.drawable.next_icon;
            }

            String colorn = new MainActivity().colornotif;
            int col = 0;

            if(test == 1){
                 col = R.color.orange;
            }
            if(test == 0){
                col = R.color.colorPrimary;
            }
            if(test == 2){
                col = R.color.blue;
            }
            if(test == 3){
                col = R.color.Yellow;
            }
            if(test == 4){
                col = R.color.saumon;
            }
            if(test == 5){
                col = R.color.RED;
            }
            if(test == 6){
                col = R.color.green;
            }
            if(test == 7){
                col = R.color.DarkBlue;
            }
            if(test == 8){
                col = R.color.pink;
            }
            notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setSmallIcon(R.drawable.headset_icon)
                    .setContentTitle(song.getTitle())
                    .setContentText(song.getSubTitle())
                    .setColor(ContextCompat.getColor(context,col))
                    //.setLargeIcon(R.drawable.headset_icon)


                    .addAction(drw_previous,"Precedent",pendingIntentPrev)
                    .addAction(drw_play,"Jouer", pendingIntentPlay)
                    .addAction(drw_next,"Prochain",pendingIntentNext)
                    //.setStyle()
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setShowWhen(false)

                    .setPriority(NotificationCompat.PRIORITY_LOW)

                    .build();


            notificationManagerCompat.notify(1,notification);


        }
    }
    public void setcolor(int color){
        test = color;
    }
}
