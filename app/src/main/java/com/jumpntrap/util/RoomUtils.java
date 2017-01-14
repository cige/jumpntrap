package com.jumpntrap.util;

import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.Room;


public class RoomUtils {
    public static final int RC_WAITING_ROOM = 10002;

    public static void showWaitingRoom(final Activity activity, final Room room, final GoogleApiClient googleApiClient) {
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(
                googleApiClient,
                room,
                MIN_PLAYERS
        );

        // Show waiting room
        activity.startActivityForResult(intent, RC_WAITING_ROOM);
    }
}
