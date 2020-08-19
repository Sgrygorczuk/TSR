package com.packt.tsr.background;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TrainingPanel {

    //The movement animations
    protected Animation animation;

    //Current animation frame time
    protected float animationTime = 0;

    private float x;
    private float y;

    public TrainingPanel(float x, float y, TextureRegion[][] spriteSheet, boolean spriteStart) {
        this.x = x;
        this.y = y;

        if(spriteStart) { animation = new Animation<>(0.2f, spriteSheet[0][0], spriteSheet[0][1]); }
        else{ animation = new Animation<>(0.2f, spriteSheet[0][1], spriteSheet[0][0]); }

        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void update(float delta) { animationTime += delta; }

    /*
    Input: Sprite Batch
    Output: Void
    Purpose: Draws the current frame of animation
    */
    public void draw(SpriteBatch batch) { batch.draw((TextureRegion) animation.getKeyFrame(animationTime), x, y); }
}
