package com.cmbk.seb.musicplayer.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cmbk.seb.musicplayer.R;

import java.util.List;

public class PlayListItemAdapter extends BaseAdapter {

    private Context context;
    private List<PlayListItem> playlistItemList;
    private String name;
    private LayoutInflater inflater;

    public PlayListItemAdapter(Context context,List<PlayListItem> playlistitemlist){
        this.context = context;
        this.playlistItemList = playlistitemlist;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return playlistItemList.size();
    }

    @Override
    public PlayListItem getItem(int position) {
        return playlistItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.adapter_playlist,null);
        PlayListItem currentitem = getItem(position);
        name = currentitem.getName();
        TextView itemnameView = convertView.findViewById(R.id.namePlayliste);
        itemnameView.setText(name);



        return convertView;
    }
}
