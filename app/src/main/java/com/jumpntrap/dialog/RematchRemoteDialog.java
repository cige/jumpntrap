package com.jumpntrap.dialog;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.jumpntrap.R;
import com.jumpntrap.adapter.PlayerAdapter;
import com.jumpntrap.adapter.PlayerItem;
import com.jumpntrap.games.OneVSOneRemoteGame;
import com.jumpntrap.realtime.RematchMessage;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jumpntrap.R.string.players;


public class RematchRemoteDialog {

    private final AlertDialog alertDialog;
    private final Context context;

    private final GoogleApiClient googleApiClient;
    private final OneVSOneRemoteGame game;

    private final String roomId;
    private final Participant localParticipant;
    private final Participant remoteParticipant;
    private boolean localParticipantReady = false;
    private boolean remoteParticipantReady = false;

    private final boolean isHost;

    private final Map<Participant, PlayerItem> map;

    public RematchRemoteDialog(final Context context, final GoogleApiClient googleApiClient, final OneVSOneRemoteGame game, final String roomId, final Participant localParticipant, final Participant remoteParticipant, final boolean isHost) {
        this.context = context;
        this.googleApiClient = googleApiClient;
        this.game = game;

        this.roomId = roomId;
        this.localParticipant = localParticipant;
        this.remoteParticipant = remoteParticipant;
        this.isHost = isHost;

        // Create adapter
        final List<PlayerItem> data = new ArrayList<>(2);
        final PlayerAdapter adapter = new PlayerAdapter(context, R.layout.list_item, data);
        adapter.add(new PlayerItem(adapter, localParticipant.getDisplayName()));
        adapter.add(new PlayerItem(adapter, remoteParticipant.getDisplayName()));

        map = new HashMap<>(2);
        map.put(localParticipant, adapter.getItem(0));
        map.put(remoteParticipant, adapter.getItem(1));

        // Create alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(players))
                .setAdapter(adapter, null)
                .setPositiveButton(context.getString(R.string.ready), null)
                .setNegativeButton(context.getString(R.string.leave), null);

        // Create dialog
        alertDialog = builder.create();
    }

    private void setupButtonsListeners() {
        // Positive button = Ready
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Send rematch message to the other player
                RematchMessage message = new RematchMessage(true);
                byte[] buff = SerializationUtils.serialize(message);
                Games.RealTimeMultiplayer.sendUnreliableMessage(googleApiClient, buff, roomId, remoteParticipant.getParticipantId());

                // Disable ready button
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                localParticipantReady = true;
                setPlayerTextAsReady(localParticipant);
            }
        });

        // Negative button = Leave
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RematchMessage message = new RematchMessage(false);
                byte[] buff = SerializationUtils.serialize(message);
                Games.RealTimeMultiplayer.sendUnreliableMessage(googleApiClient, buff, roomId, remoteParticipant.getParticipantId());
            }
        });
    }

    private void setPlayerTextAsReady(final Participant participant) {
        final PlayerItem playerItem = map.get(participant);
        playerItem.setText(playerItem.getText() + " " + context.getString(R.string.icon_check));

        // The two players are ready : we can start a new game
        if (localParticipantReady && remoteParticipantReady) {
            // Reset game for both
            game.reset();

            if (isHost) {
                game.restart();
            }

            alertDialog.dismiss();
        }
    }

    public void setRemotePlayerTextAsReady() {
        remoteParticipantReady = true;
        setPlayerTextAsReady(remoteParticipant);
    }

    public void show() {
        alertDialog.show();
        setupButtonsListeners();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

}

