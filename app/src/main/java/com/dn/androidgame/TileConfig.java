package com.dn.androidgame;


public class TileConfig {
    int color;
    int hits;
    RotationVec rot;

    public TileConfig(int color, int hits, RotationVec rot) {
        this.color = color;
        this.hits = hits;
        this.rot = rot;
    }
}
