package com.jumpntrap.realtime;


import java.io.Serializable;

/**
 * RematchMessage defines a rematch message.
 */
public final class RematchMessage implements Serializable {
    /**
     * Flag to indicate if a player wants to rematch.
     */
    final private boolean wantsToRematch;

    /**
     * Constructor.
     * @param wantsToRematch flag to indicate if a player wants to rematch.
     */
    public RematchMessage(final boolean wantsToRematch) {
        this.wantsToRematch = wantsToRematch;
    }

    /**
     * Check if the player wants to rematch.
     * @return true if the player wants to rematch.
     */
    public final boolean isWantsToRematch() {
        return wantsToRematch;
    }
}
