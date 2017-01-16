package com.jumpntrap.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jumpntrap.R;

import java.util.List;

public final class PlayerAdapter extends ArrayAdapter<PlayerItem> {

    private final int resource;

    public PlayerAdapter(final Context context, final int resource, final List<PlayerItem> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(resource, null);
        }

        final TextView tv = (TextView) convertView.findViewById(R.id.text_view);
        final PlayerItem item = getItem(position);
        if (item != null) {
            tv.setText(item.getText());
        }

        return convertView;
    }

}
