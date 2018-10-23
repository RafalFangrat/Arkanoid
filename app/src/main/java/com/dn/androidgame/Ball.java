package com.dn.androidgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;


import java.util.concurrent.ThreadLocalRandom;


public class Ball implements GameObjects{
    private Rect rect;
    private BitmapDrawable mDrawable;

    private Point startingPosition;
    private Point ePosition;

    private float dx = 0.0f;
    private float dy = 0.0f;
    private float speedRatio = 0.0f;

    private int gameAreaHeight;
    private int gameAreaWidth;

    private static final int SIZE_RATIO = 20;

    Rect getRect(){
        return rect;
    }


    Ball(Context ctx, int width, int height, int playerHeight){
        ePosition = new Point(width/2, height-playerHeight );
        this.rect = new Rect (0,0, width/SIZE_RATIO, width/SIZE_RATIO);
        ePosition.y-=this.rect.height()/2;
        startingPosition = new Point(ePosition);
        calcDelta(0,height/3);


        mDrawable = (BitmapDrawable) ContextCompat.getDrawable(ctx, R.drawable.dot_red);
        gameAreaHeight = height;
        gameAreaWidth = width;


    }

    @Override
    public void draw(Canvas canv) {
        mDrawable.setBounds(rect);
        mDrawable.draw(canv);
    }

    @Override
    public void update() {
        ePosition.x += (dx+dx*speedRatio);
        ePosition.y += (dy+dy*speedRatio);


        Log.e("dy+dy*speedRatio",dy+dy*speedRatio + "");
        if (ePosition.x <= 0)
            ePosition.x = 0;
        if (ePosition.x > gameAreaWidth)
            ePosition.x = gameAreaWidth;
        if (ePosition.y <= 0)
            ePosition.y = 0;
        if (ePosition.y > gameAreaHeight)
            ePosition.y = gameAreaHeight;

        rect.set(ePosition.x - rect.width()/2,  ePosition.y-rect.height()/2, ePosition.x + rect.width()/2, ePosition.y+rect.height()/2);
    }

    void setDirection(RotationVec rot){
        setDirection( rot, 0.0f);
    }


    void setDirection(RotationVec rot, float speed){
        dx *= rot.x;
        dy *= rot.y;

        if(dx * (speedRatio + speed) < rect.width()/2 || dy * (speedRatio + speed)< rect.height()/2)
            speedRatio += speed;

    }

    void reset(){
        ePosition = new Point(startingPosition);
    }

    private void calcDelta(int x, int y){
        int randomNum = ThreadLocalRandom.current().nextInt(0, 100 + 1);

        dx = (x - ePosition.x)/50f;
        dy = (y - ePosition.y + randomNum)/50f;
        Log.e("delta", dy+"");
    }
}

