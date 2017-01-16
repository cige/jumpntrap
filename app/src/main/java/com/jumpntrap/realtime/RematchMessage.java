package com.jumpntrap.realtime;


import java.io.Serializable;

public final class RematchMessage implements Serializable {

    final private boolean wantsToRematch;

    public RematchMessage(final boolean wantsToRematch) {
        this.wantsToRematch = wantsToRematch;
    }

    public final boolean isWantsToRematch() {
        return wantsToRematch;
    }

}
