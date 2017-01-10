package com.jumpntrap.realtime;

import java.io.Serializable;

/**
 * Created by Victor on 10/01/2017.
 */

public class GameDataMessage implements Serializable {
    private int[] positions;

    public  GameDataMessage() {
        positions = new int[4];
        for (int i = 0; i < positions.length; ++i) {
            positions[i] = i;
        }
    }

    public int[] getPositions() {
        return positions;
    }
}
