package com.cmbk.seb.musicplayer.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("CMBK_CMBK").putExtra("actionname", intent.getAction()));
    }
}
