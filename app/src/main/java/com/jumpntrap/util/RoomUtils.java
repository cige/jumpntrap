package com.jumpntrap.util;

import android.app.Activity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.realtime.Room;

/**
 * RoomUtils defines utility methods for a room.
 */
public final class RoomUtils {
    /**
     * Returned code of the waiting room.
     */
    public static final int RC_WAITING_ROOM = 10002;

    /**
     * Show a native waiting room.
     * @param activity the activity.
     * @param room the current room.
     * @param googleApiClient the Google API client.
     */
    public static void showWaitingRoom(final Activity activity, final Room room, final GoogleApiClient googleApiClient) {
        // Show waiting room
        activity.startActivityForResult(Games.RealTimeMultiplayer.getWaitingRoomIntent(
                googleApiClient,
                room,
                Integer.MAX_VALUE // MIN_PLAYERS
        ), RC_WAITING_ROOM);
    }
}
