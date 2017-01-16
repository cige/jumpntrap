package com.jumpntrap.dialog;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.jumpntrap.R;
import com.jumpntrap.adapter.PlayerAdapter;
import com.jumpntrap.adapter.PlayerItem;
import com.jumpntrap.games.OneVSOneRemoteGame;
import com.jumpntrap.listener.FinishActivityListener;
import com.jumpntrap.realtime.RematchMessage;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RematchRemoteDialog {

    private final AlertDialog dialog;
    private final Activity activity;

    private final GoogleApiClient googleApiClient;
    private final OneVSOneRemoteGame game;

    private final String roomId;
    private final Participant localParticipant;
    private final Participant remoteParticipant;
    private boolean localParticipantReady = false;
    private boolean remoteParticipantReady = false;

    private final boolean isHost;

    private final Map<Participant, PlayerItem> map;

    public RematchRemoteDialog(final Activity activity, final GoogleApiClient googleApiClient, final OneVSOneRemoteGame game, final String roomId, final Participant localParticipant, final Participant remoteParticipant, final boolean isHost) {
        this.activity = activity;
        this.googleApiClient = googleApiClient;
        this.game = game;

        this.roomId = roomId;
        this.localParticipant = localParticipant;
        this.remoteParticipant = remoteParticipant;
        this.isHost = isHost;

        // Create adapter
        final List<PlayerItem> data = new ArrayList<>(2);
        final PlayerAdapter adapter = new PlayerAdapter(activity, R.layout.list_item, data);
        adapter.add(new PlayerItem(adapter, localParticipant.getDisplayName()));
        adapter.add(new PlayerItem(adapter, remoteParticipant.getDisplayName()));

        map = new HashMap<>(2);
        map.put(localParticipant, adapter.getItem(0));
        map.put(remoteParticipant, adapter.getItem(1));

        // Create alert builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setAdapter(adapter, null)
               .setPositiveButton(activity.getString(R.string.ready), null);

        // Create dialog
        dialog = builder.create();
    }

    private void setupButtonsListeners() {
        // Positive button = Ready
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Disable ready button
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                // Send rematch message to the other player
                final RematchMessage message = new RematchMessage(true);
                final byte[] buff = SerializationUtils.serialize(message);
                Games.RealTimeMultiplayer.sendUnreliableMessage(googleApiClient, buff, roomId, remoteParticipant.getParticipantId());

                localParticipantReady = true;
                setPlayerTextAsReady(localParticipant);
            }
        });
    }

    private void setPlayerTextAsReady(final Participant participant) {
        final PlayerItem playerItem = map.get(participant);
        playerItem.setText(playerItem.getText() + " " + activity.getString(R.string.icon_check));

        // The two players are ready : we can start a new game
        if (localParticipantReady && remoteParticipantReady) {
            // Reset game for both
            game.reset();

            if (isHost) {
                game.restart();
            }

            dialog.dismiss();
        }
    }

    public void setRemotePlayerTextAsReady() {
        remoteParticipantReady = true;
        setPlayerTextAsReady(remoteParticipant);
    }

    public void show() {
        dialog.show();
        setupButtonsListeners();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new FinishActivityListener(activity, dialog));
    }

    public void dismiss() {
        dialog.dismiss();
    }

}

