package com.dn.androidgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


public class Tile implements GameObjects{
    private Rect bar;
    private RotationVec rot;
    private int hits;
    private int color;

    boolean isDown(){
        return --hits==0?true:false;
    }


    RotationVec getRot() {
        return rot;
    }

    Tile(Rect rect, RotationVec rot, int hits, int color){
        this.bar = rect;
        this.rot = rot;
        this.hits = hits;
        this.color = color;
    }

    boolean ballCollide(Ball ball){
        return Rect.intersects(bar, ball.getRect());
    }

    @Override
    public void draw(Canvas canv) {
        Paint paint = new Paint();
        paint.setColor(darker(color, 1f/hits));
        canv.drawRect(bar, paint);
    }

    @Override
    public void update() {

    }
    public static int darker (int color, float factor) {
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }
}
