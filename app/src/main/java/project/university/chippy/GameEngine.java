package project.university.chippy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG="TAPPY-SPACESHIP";

    // screen size
    int screenHeight;
    int screenWidth;

    String playerTapped ="";

    // game state
    boolean gameIsRunning;
    Thread gameThread;

    SurfaceHolder holder;
    Canvas canvas;
    Paint paintbrush;
    Enemy enemy;
    Player player;
    float tappedX;
    float tappedY;
    ArrayList<Rect> bullets = new ArrayList<>();
    int frameNumber=0;

    public GameEngine(Context context, int w, int h) {
        super(context);
        this.holder = this.getHolder();
        this.paintbrush = new Paint();
        this.screenWidth = w;
        this.screenHeight = h;

        player = new Player(100,350);
        player.setImage( BitmapFactory.decodeResource(this.getContext().getResources(),
                R.drawable.ic_player_shipp));
        player.setHitbox(new Rect(100,350,player.getImage().getWidth()+100,player.getImage().getHeight()+350));

        enemy = new Enemy((int) (screenWidth*0.65),350);
        enemy.setImage( BitmapFactory.decodeResource(this.getContext().getResources(),
                R.drawable.alien_ship2));
        enemy.setHitbox(new Rect((int) (screenWidth*0.65),350,enemy.getImage().getWidth()+(int) (screenWidth*0.65),enemy.getImage().getHeight()+350));
        this.printScreenInfo();
    }


    private void printScreenInfo() {
        Log.d(TAG, "Screen (w, h) = " + this.screenWidth + "," + this.screenHeight);
    }

    // ------------------------------
    // GAME STATE FUNCTIONS (run, stop, start)
    // ------------------------------
    @Override
    public void run() {
        while (gameIsRunning) {
            this.updatePositions();
            this.redrawSprites();
            this.setFPS();
        }
    }


    public void pauseGame() {
        gameIsRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void startGame() {
        gameIsRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------

    public void updatePositions() {

        movePlayer(player.getImage(), this.mouseX, this.mouseY);


    }

    public void redrawSprites() {
        frameNumber++;
        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();
            this.canvas.drawColor(Color.argb(255,255,255,255));
            paintbrush.setColor(Color.WHITE);
            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);

            //Draw the player
            canvas.drawBitmap(player.getImage(), player.getxPos(), player.getyPos(), paintbrush);
            canvas.drawRect(this.player.getHitbox(), paintbrush);


            //Draw the enemy ship
            canvas.drawBitmap(enemy.getImage(), enemy.getxPos(), enemy.getyPos(), paintbrush);
            canvas.drawRect(this.enemy.getHitbox(), paintbrush);

            this.holder.unlockCanvasAndPost(canvas);


        }
    }

    float mouseX ;
    float mouseY ;


    public void movePlayer(Bitmap bullet, float mouseXPos, float mouseYPos) {

        // @TODO:  Move the square
        // 1. calculate distance between bullet and square
        double a = (mouseXPos - player.getxPos());
        double b = (mouseYPos - player.getyPos());
        double distance = Math.sqrt((a*a) + (b*b));

        // 2. calculate the "rate" to move
        double xn = (a / distance);
        double yn = (b / distance);

        // 3. move the bullet
        player.setxPos(player.getxPos() + (int)(xn * 15));
        player.setyPos(player.getyPos() + (int)(yn * 15));

        player.getHitbox().left = player.getxPos();
        player.getHitbox().right = player.getxPos()+player.getImage().getWidth();
        player.getHitbox().top = player.getyPos();
        player.getHitbox().bottom = player.getyPos()+player.getImage().getHeight();

    }


    public void setFPS() {
        try {
            gameThread.sleep(60);
        }
        catch (Exception e) {
        }
    }
    // ------------------------------
    // USER INPUT FUNCTIONS
    // ------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int userAction = event.getActionMasked();
        //@TODO: What should happen when person touches the screen?
        if (userAction == MotionEvent.ACTION_DOWN) {

            this.mouseX = event.getX();
            this.mouseY = event.getY();
            Log.d(TAG, "onTouchEvent: Y "+tappedY);
            Log.d(TAG, "onTouchEvent: X "+tappedX);
        }
        else if (userAction == MotionEvent.ACTION_UP) {
            playerTapped = "notshoot";
        }
        return true;
    }
}
