package com.dn.androidgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


public class Border implements GameObjects{

    private Rect bar;
    private RotationVec rot;

    RotationVec getRot(){
        return rot;
    }

    Border(Rect rect, RotationVec vec){
        this.bar = rect;
        this.rot = vec;
    }

    boolean ballCollide(Ball ball){
        return Rect.intersects(bar, ball.getRect());
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

}
