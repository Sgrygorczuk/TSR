package com.packt.tsr.background;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class BuddySprite {

    //The movement animations

    Animation[] animations = new Animation[5];

    float[] animationTime = new float[]{0,0,0,0,0};

    private float[] x;
    private float[] y;


    public BuddySprite(float[] x, float[] y, TextureRegion[][] spriteSheet) {

        this.x = x;
        this.y = y;

        animations[3] = new Animation<>(0.2f, spriteSheet[0][0], spriteSheet[0][1]);
        animations[2] = new Animation<>(0.2f, spriteSheet[1][0], spriteSheet[1][1]);
        animations[0] = new Animation<>(0.2f, spriteSheet[2][0], spriteSheet[2][1]);
        animations[4]= new Animation<>(0.2f, spriteSheet[3][0], spriteSheet[3][1]);
        animations[1] = new Animation<>(0.2f, spriteSheet[4][0], spriteSheet[4][1]);

        for(int i = 0; i < 5; i++){
            animations[i].setPlayMode(Animation.PlayMode.LOOP);
        }
    }

    public void update(float delta, int spriteChoice) { animationTime[spriteChoice] += delta; }

    /*
    Input: Sprite Batch
    Output: Void
    Purpose: Draws the current frame of animation
    */
    public void draw(SpriteBatch batch, int spriteChoice) {
        batch.draw((TextureRegion) animations[spriteChoice].getKeyFrame(animationTime[spriteChoice]), x[spriteChoice], y[spriteChoice]);
    }

}
