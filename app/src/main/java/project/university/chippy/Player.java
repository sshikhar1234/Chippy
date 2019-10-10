package project.university.chippy;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

import static project.university.chippy.GameEngine.TAG;

public class Player {
    public int xPos;
    public int yPos;

    public ArrayList<Rect> getBullets() {
        return bullets;
    }

    public void setBullets(ArrayList<Rect> bullets) {
        this.bullets = bullets;
    }

    public ArrayList<Rect> bullets = new ArrayList<>();
    public Rect getHitbox() {
        return hitbox;
    }

    public void setHitbox(Rect hitbox) {
        this.hitbox = hitbox;
    }

    public Rect hitbox;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap image;

    public Player(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }
    public void addBullet(){
        Log.d(TAG, "addBullet: ");
        Rect newBullet = new Rect(this.xPos,this.xPos+10,
                this.yPos/2,
                this.yPos/2+10);
        bullets.add(newBullet);
    }
}
