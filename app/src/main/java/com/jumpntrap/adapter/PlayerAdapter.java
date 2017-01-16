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

/**
 * PlayerAdapter defines an adapter for the RematchRemoteDialog.
 */
public final class PlayerAdapter extends ArrayAdapter<PlayerItem> {
    /**
     * View resource.
     */
    private final int resource;

    /**
     * Constructor.
     * @param context the context.
     * @param resource the view resource.
     * @param objects the list of the items.
     */
    public PlayerAdapter(final Context context, final int resource, final List<PlayerItem> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    /**
     * Get a view that display the data.
     * @param position the position of the item within the adapter's data set of the item whose view we want.
     * @param convertView the old view to reuse, if possible.
     * @param parent the parent that this view will eventually be attached to.
     * @return the view that display the data.
     */
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
