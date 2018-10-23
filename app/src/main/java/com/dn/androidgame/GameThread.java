package com.dn.androidgame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;


public class GameThread extends Thread {
    private static final int MAX_FPS = 60;

    private final SurfaceHolder holder;
    private GameView gameView;
    private boolean running;

    void setRunning(boolean running){
        this.running = running;
    }

    GameThread(SurfaceHolder holder, GameView gameView){
        super();
        this.gameView = gameView;
        this.holder = holder;
    }

    @Override
    public void run(){
        long startTime;
        long time;
        long sleepTime;
        int frameCount = 0;
        long totalTime = 0;
        long targetTime = 1000/MAX_FPS;

        while(running){
            startTime = System.nanoTime();
            Canvas canv = null;
            try{
                canv = this.holder.lockCanvas();
                synchronized (holder) {
                    this.gameView.updateGame();
                    this.gameView.draw(canv);
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if (canv != null) {
                    try {
                        holder.unlockCanvasAndPost(canv);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            time = (System.nanoTime() - startTime)/1000000;
            sleepTime = targetTime - time;
            try{
                if(sleepTime > 0) {
                    sleep(sleepTime);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            totalTime +=System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == MAX_FPS){
                double fps = 1000/((totalTime/frameCount)/1000000);
                frameCount = 0;
                totalTime=0;
                //Log.e("fps",fps + "");
            }
        }
    }
}
