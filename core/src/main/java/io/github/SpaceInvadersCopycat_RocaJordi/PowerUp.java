package io.github.SpaceInvadersCopycat_RocaJordi;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PowerUp {
    public enum Type {
        RAPID_FIRE,
        TRIPLE_SHOT
    }

    private Vector2 position;
    private Animation<TextureRegion> animation;
    private float size;
    private float speed;
    private Rectangle bounds;
    private Type type;

    public PowerUp(float x, float y, Animation<TextureRegion> animation, float size, Type type) {
        this.position = new Vector2(x, y);
        this.animation = animation;
        this.size = size;
        this.speed = 50f;
        this.bounds = new Rectangle(x, y, size, size);
        this.type = type;
    }

    public void update(float delta) {
        // Mou el powerup cap avall
        position.y -= speed * delta;
        bounds.setPosition(position.x, position.y);
    }

    public void render(SpriteBatch batch, float stateTime) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, position.x, position.y, size, size);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isOutOfScreen(float worldHeight) {
        return position.y < -size;
    }

    public Type getType() {
        return type;
    }
}
