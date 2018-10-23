package com.dn.androidgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class GameView extends SurfaceView implements  SurfaceHolder.Callback
{
    private GameThread gameThread;

    private int score;
    private int lives;

    private BottomBar player;
    private Border bottomBorder;
    private List<Border> borders;
    private List<Tile> tiles;
    private Ball ball;

    private int gameAreaHeight;
    private int playerPosition;

    private Context ctx;


    public static final float SPEED_RATIO = 0.02f;
    public static final int ROW_NUM = 4; //5
    public static final int COL_NUM = 6; //8
    public static final int POINTS = 100;
    public static final String GAME_OVER_TXT = "Game Over";
    public static final String WIN_TXT = "You Win !!!";

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        ctx = context;
        gameThread = new GameThread(getHolder(), this);
        score = 0;
        lives = 3;

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int screenHeight = metrics.heightPixels;
        gameAreaHeight = screenHeight *4/5;
        int screenWidth= metrics.widthPixels;

        // init borders l t r b
        borders = new ArrayList<>();
        int borderWidth = screenWidth/50;

        borders.add(new Border(
                        new Rect(0,0,borderWidth,gameAreaHeight),
                        new RotationVec(-1,1)));
        borders.add(new Border(
                        new Rect(screenWidth-borderWidth,0, screenWidth,gameAreaHeight),
                        new RotationVec(-1,1)));
        borders.add(new Border(
                        new Rect(0,0,screenWidth,borderWidth),
                        new RotationVec(1,-1)));

        bottomBorder = new Border(
                        new Rect(0,gameAreaHeight-borderWidth, screenWidth,gameAreaHeight+borderWidth*10),
                        new RotationVec(1,-1));


        // init tiles
        tiles = new ArrayList<>();
        //tile height/width ratio 2/5
        int tileWidth = (screenWidth - borderWidth * (COL_NUM + 3))/ COL_NUM;
        int tileHeight = tileWidth * 2/5;

        int margin = (screenWidth % (tileWidth + borderWidth) + borderWidth)/2;
        int x = margin;
        int y = borderWidth * 2;

        TileConfig[] config = { new TileConfig(Color.CYAN, 1, new RotationVec(1, -1)),
                                new TileConfig(Color.GREEN, 1, new RotationVec(-1, -1)),
                                new TileConfig(Color.BLUE, 3, new RotationVec(1, -1)),};
        int mod;
        for (int row=0; row < ROW_NUM; row++) {
            for (int col = 0; col < COL_NUM; col++) {
                mod = (x+y)%3;
                tiles.add(new Tile(
                        new Rect(x, y, x + tileWidth, y + tileHeight),
                        config[mod].rot,
                        config[mod].hits,
                        config[mod].color));
                x += tileWidth + borderWidth;
            }
            x = margin;
            y += tileHeight + borderWidth;
        }
        Collections.reverse(tiles);


        player = new BottomBar(
                        new Rect(0,0, screenWidth/4, screenWidth/16),
                        new RotationVec(1,-1),
                        gameAreaHeight,
                        screenWidth);
        playerPosition = screenWidth/2;

        ball = new Ball(ctx, screenWidth, gameAreaHeight, player.getRect().height());

        setFocusable(true);

    }



    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread = new GameThread(getHolder(), this);
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry){
            try{
                gameThread.setRunning(false);
                gameThread.join();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                playerPosition = (int)event.getX();
        }

        return true;
    }

    public void updateGame(){

        player.update(playerPosition);
        ball.update();


        if(bottomBorder.ballCollide(ball)){
            if(--lives<=0) {
                gameThread.setRunning(false);
                showDialog(GAME_OVER_TXT);
            }
            ball.reset();
            ball.setDirection(bottomBorder.getRot());
            return;
        }

        if(player.ballCollide(ball)){
            ball.setDirection(player.getRot());
            ball.update();
        }

        for (Border border : borders){
            if(border.ballCollide(ball)){
                ball.setDirection(border.getRot());
                ball.update();
            }
        }

        for (Iterator<Tile> iterator = tiles.iterator(); iterator.hasNext();) {
            Tile tile = iterator.next();
            if(tile.ballCollide(ball)){
                ball.setDirection(tile.getRot(), SPEED_RATIO);
                ball.update();
                if(tile.isDown()) {
                    iterator.remove();
                    score += POINTS;
                    break;
                }
            }
        }

        if(tiles.size()==0){
            gameThread.setRunning(false);
            showDialog(WIN_TXT);
        }



    }

    @Override
    public void draw (Canvas canv){
        super.draw(canv);

        canv.drawColor(Color.WHITE);
        player.draw(canv);

        for (Border border : borders){
            border.draw(canv);
        }

        for (Tile tile : tiles){
            tile.draw(canv);
        }

        ball.draw(canv);

        // draw score and lives
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        int step = gameAreaHeight/12;
        paint.setTextSize(gameAreaHeight/30);
        canv.drawText("Score: " + score, 10, gameAreaHeight + step, paint);
        canv.drawText("Lives: " + lives, 10, gameAreaHeight + step *2, paint);
    }

    private void showDialog(final String msg){
        ((MainActivity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                alert.setTitle(msg);
                alert.setMessage("Do you want start again?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)ctx).recreate();
                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((MainActivity)ctx).finishAndRemoveTask();
                    }
                });

                alert.show();
            }
        });
    }
}
