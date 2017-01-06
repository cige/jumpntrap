package com.jumpntrap.tbm;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

/**
 * Created by Admin on 30/12/2016.
 */

public class MatchInitiatedCallback
        implements ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> {
    private final static String TAG = "MatchInitiatedCallback";

    @Override
    public void onResult(@NonNull TurnBasedMultiplayer.InitiateMatchResult result) {
        // Check if the status code is not success.
        Status status = result.getStatus();
        if (!status.isSuccess()) {
            Log.e(TAG, "**Error : " + status.getStatusCode());
            return;
        }

        TurnBasedMatch match = result.getMatch();

        // Game has already started
        if (match.getData() != null) {
            //updateMatch(match);
        }
        else {
            //startMatch();
        }
    }


}
