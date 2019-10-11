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
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

public class GameEngine extends SurfaceView implements Runnable {

    // Android debug variables
    final static String TAG = "TAPPY-SPACESHIP";

    // screen size
    int screenHeight;
    int screenWidth;
    float mouseX;
    float mouseY;

    String playerTapped = "";

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
    //ArrayList<Rect> bullets = new ArrayList<>();
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<EnemyLeg> enemyLegs = new ArrayList<>();
    int frameNumber = 0;
    Bitmap roboArmHorizontal;
    Bitmap roboArmHorizontalLight;
    Bitmap bgGame;
    Bitmap laserBitmap;
    public GameEngine(Context context, int w, int h) {
        super(context);
        this.holder = this.getHolder();
        this.paintbrush = new Paint();
        this.screenWidth = w;
        this.screenHeight = h;

        //Setting the background

        this.bgGame = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_bg);

        this.bgGame = Bitmap.createScaledBitmap(
                this.bgGame,
                this.screenWidth,
                this.screenHeight,
                false
        );

        player = new Player(100, 350);
        player.setImage(BitmapFactory.decodeResource(this.getContext().getResources(),
                R.drawable.ic_player_shipp));
        player.setHitbox(new Rect(100, 350, player.getImage().getWidth() + 100, player.getImage().getHeight() + 350));

        enemy = new Enemy((int) (screenWidth * 0.65), 400);
        enemy.setImage(BitmapFactory.decodeResource(this.getContext().getResources(),
                R.drawable.ic_robo_resized));
        enemy.setHitbox(new Rect((int) (screenWidth * 0.65), 400, enemy.getImage().getWidth() + (int) (screenWidth * 0.65), enemy.getImage().getHeight() + 350));


        roboArmHorizontal = BitmapFactory.decodeResource(this.getContext().getResources(),
                R.drawable.ic_robo_h);
        roboArmHorizontalLight = BitmapFactory.decodeResource(this.getContext().getResources(),
                R.drawable.ic_robo_light);

        //Form the laser bitmap from drawable
        laserBitmap = BitmapFactory.decodeResource(this.getContext().getResources(),
                R.drawable.ic_laser);
        //Adding the enemy arms and legs

        //1. Bottom Right Arm
        for (int i = 1; i < 4; i++) {
            enemyLegs.add(new EnemyLeg(
                    100, roboArmHorizontal,
                    enemy.getxPos() + 100 * i,
                    enemy.getyPos() + 100 * i
            ));

        }
        //2. Top Left Arm
        for (int i = 1; i < 4; i++) {
            enemyLegs.add(new EnemyLeg(
                    100, roboArmHorizontal,
                    enemy.getxPos() - 100 * i,
                    enemy.getyPos() - 100 * i
            ));
        }
        //3. Top Right Arm
        for (int i = 1; i < 4; i++) {
            enemyLegs.add(new EnemyLeg(
                    100, roboArmHorizontal,
                    enemy.getxPos() + 100 * i,
                    enemy.getyPos() - 100 * i
            ));
        }
        //4. Bottom Right Arm
        for (int i = 1; i < 4; i++) {
            enemyLegs.add(new EnemyLeg(
                    100, roboArmHorizontal,
                    enemy.getxPos() - 100 * i,
                    enemy.getyPos() + 100 * i
            ));
        }


        //Top Piece
        enemyLegs.add(new EnemyLeg(
                100, roboArmHorizontal,
                enemy.getxPos(),
                enemy.getyPos() - 100
        ));
        //Bottom Piece
        enemyLegs.add(new EnemyLeg(
                100, roboArmHorizontal,
                enemy.getxPos(),
                enemy.getyPos() + 100
        ));
        //Left Piece
        enemyLegs.add(new EnemyLeg(
                100, roboArmHorizontal,
                enemy.getxPos() - 100,
                enemy.getyPos()
        ));
        //Right Piece
        enemyLegs.add(new EnemyLeg(
                100, roboArmHorizontal,
                enemy.getxPos() + 100,
                enemy.getyPos()
        ));
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

    public void shootBullets() {

        Bullet newBullet = new Bullet(this.player.getxPos() + this.player.getImage().getWidth()+10,
                this.player.getyPos() + this.player.getImage().getHeight() / 2,laserBitmap);
        bullets.add(newBullet);
//      player.addBullet();
        paintbrush.setColor(Color.BLUE);
        paintbrush.setStyle(Paint.Style.FILL);
        paintbrush.setStrokeWidth(5);
//        bullets = player.getBullets();
    }
    // ------------------------------
    // GAME ENGINE FUNCTIONS
    // - update, draw, setFPS
    // ------------------------------

    public void updatePositions() {
        movePlayer(player.getImage(), this.mouseX, this.mouseY);

        //make the bullet move
        if (bullets.size() > 0) {
            for (Bullet bullet : bullets) {
                bullet.setxPos(bullet.getxPos()+35);
                Rect currentHitbox = bullet.getHibox();
                currentHitbox.left = currentHitbox.left +35;
                currentHitbox.right = currentHitbox.right +35;
                bullet.setHibox(currentHitbox);
                for(EnemyLeg currentLeg: enemyLegs){

                    //TODO: Handle the HealthPoints Logic for each piece of Enemy
                    //1. If the health is less than 50 then Change the image
                    if(currentLeg.getHitPoints()<=50) {
                        currentLeg.setImage(roboArmHorizontalLight);
                    }
                    //2. If the health is less than 0 then remove it from the screem
                    if(currentLeg.getHitPoints()<=0) {
                        currentLeg.setxPos(-100);
                        currentLeg.setyPos(-100);
                        Rect legHitbox = currentLeg.getHitbox();
                        legHitbox.left = -100;
                        legHitbox.right = -100;
                    }

                    //TODO: Check Collision between LASER and ENEMY
                    if(bullet.getHibox().intersect(currentLeg.getHitbox())){
                       currentLeg.setHitPoints(currentLeg.getHitPoints()-10);
                        bullet.setxPos(-100);
                        bullet.setyPos(-100);
                        currentHitbox.left = -100;
                        currentHitbox.right = -100;
                        currentHitbox.top = -100;
                        currentHitbox.bottom = -100;
                        Log.d(TAG, "updatePositions: BOOM!");
                    }
                }
            }
        }
        //TODO: Check Collision between enemy and player's bullets
        //To be continued...

    }

    public void redrawSprites() {
        //Keeps count of framenumber
        frameNumber++;

        if (this.holder.getSurface().isValid()) {
            this.canvas = this.holder.lockCanvas();

            this.canvas.drawColor(Color.argb(255, 255, 255, 255));
            paintbrush.setColor(Color.BLUE);
            paintbrush.setStyle(Paint.Style.STROKE);
            paintbrush.setStrokeWidth(5);

            //Draw the Background Image
            canvas.drawBitmap(bgGame, 0, 0, paintbrush);

            //Draw the enemy ship
            canvas.drawBitmap(enemy.getImage(), enemy.getxPos(), enemy.getyPos(), paintbrush);
            canvas.drawRect(this.enemy.getHitbox(), paintbrush);

            //Draw Enemy Protective Layer
            for (EnemyLeg enemyLeg : enemyLegs) {
                canvas.drawBitmap(enemyLeg.getImage(), enemyLeg.getxPos(), enemyLeg.getyPos(), paintbrush);
                paintbrush.setColor(Color.TRANSPARENT);
                canvas.drawRect(enemyLeg.getHitbox(), paintbrush);
                paintbrush.setColor(Color.BLUE);
            }
            paintbrush.setColor(Color.BLUE);

            //Draw the player
            canvas.drawBitmap(player.getImage(), player.getxPos(), player.getyPos(), paintbrush);
            paintbrush.setColor(Color.TRANSPARENT);
            canvas.drawRect(this.player.getHitbox(), paintbrush);
            paintbrush.setColor(Color.BLUE);

            //Handle the bullets
            if (frameNumber % 10 == 0) {
                //Add new new bullet every 10th frame
                shootBullets();
            }
            for (Bullet bullet : bullets) {
                canvas.drawBitmap(bullet.getImage(),bullet.getxPos(),bullet.getyPos(),paintbrush);
                paintbrush.setColor(Color.TRANSPARENT);
                canvas.drawRect(bullet.getHibox(), paintbrush);
                paintbrush.setColor(Color.BLUE);
            }

            this.holder.unlockCanvasAndPost(canvas);
        }
    }


    public void movePlayer(Bitmap bullet, float mouseXPos, float mouseYPos) {

        // @TODO:  Move the square
        // 1. calculate distance between bullet and square
        double a = (mouseXPos - player.getxPos());
        double b = (mouseYPos - player.getyPos());
        double distance = Math.sqrt((a * a) + (b * b));

        // 2. calculate the "rate" to move
        double xn = (a / distance);
        double yn = (b / distance);

        // 3. move the bullet
        player.setxPos(player.getxPos() + (int) (xn * 15));
        player.setyPos(player.getyPos() + (int) (yn * 15));

        player.getHitbox().left = player.getxPos();
        player.getHitbox().right = player.getxPos() + player.getImage().getWidth();
        player.getHitbox().top = player.getyPos();
        player.getHitbox().bottom = player.getyPos() + player.getImage().getHeight();

    }


    public void setFPS() {
        try {
            gameThread.sleep(40);
        } catch (Exception e) {
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
            Log.d(TAG, "onTouchEvent: Y " + tappedY);
            Log.d(TAG, "onTouchEvent: X " + tappedX);
        } else if (userAction == MotionEvent.ACTION_UP) {
            playerTapped = "notshoot";
        }
        return true;
    }
}
