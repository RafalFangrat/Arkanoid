package com.dn.androidgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


public class BottomBar implements GameObjects{
    private Rect bar;
    private int h, w;
    private RotationVec rot;

    RotationVec getRot() {
        return rot;
    }

    Rect getRect(){
        return  bar;
    }

    BottomBar(Rect rect, RotationVec rot, int height, int width){
        this.h = height;
        this.w = width;
        this.bar = rect;
        this.rot = rot;
    }

    @Override
    public void draw(Canvas canv) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canv.drawRect(bar, paint);
    }

    @Override
    public void update() {

    }

    void update(int pos) {
        if (pos < bar.width()/2)
            pos = bar.width()/2;
        if (pos > w-(bar.width()/2))
            pos = w-bar.width()/2;
        bar.set(pos - bar.width()/2, h-bar.height(), pos + bar.width()/2, h);
    }

    boolean ballCollide(Ball ball){
        return Rect.intersects(bar, ball.getRect());
    }

}
