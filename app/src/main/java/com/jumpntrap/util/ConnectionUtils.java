package com.jumpntrap.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ConnectionUtils defines utility methods for connection.
 */
public class ConnectionUtils {
    /**
     * Check if Internet connection is available.
     * @param context the context.
     * @return true if Internet connection is available.
     */
    public static boolean isInternetConnectionAvailable(final Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
