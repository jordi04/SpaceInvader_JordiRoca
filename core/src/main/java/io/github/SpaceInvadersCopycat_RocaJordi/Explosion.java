package io.github.SpaceInvadersCopycat_RocaJordi;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Explosion {
    private Vector2 position;
    private Animation<TextureRegion> animation;
    private float stateTime;
    private boolean finished;
    private float size;

    public Explosion(float x, float y, Animation<TextureRegion> animation, float size) {
        this.position = new Vector2(x, y);
        this.animation = animation;
        this.stateTime = 0f;
        this.finished = false;
        this.size = size;
    }

    public void update(float delta) {
        stateTime += delta;
        if (animation.isAnimationFinished(stateTime)) {
            finished = true;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, false);
        batch.draw(currentFrame, position.x - size/2, position.y - size/2, size, size);
    }

    public boolean isFinished() {
        return finished;
    }
}
