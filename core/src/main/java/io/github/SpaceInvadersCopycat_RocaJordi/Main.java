package io.github.SpaceInvadersCopycat_RocaJordi;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main implements ApplicationListener {
    Texture backgroundTexture;
    Texture playerTexture;
    Texture enemyTexture;
    Texture bulletTexture;
    Sound dropSound;
    Music music;
    SpriteBatch spriteBatch;
    FitViewport viewport;
    Sprite playerSprite;
    Vector2 touchPos;
    Array<Sprite> enemySprites;
    Array<Sprite> bulletSprites;
    float dropTimer;
    Rectangle playerRectangle;
    Rectangle enemyRectangle;
    Array<Rectangle> bulletRectangles;
    float leftLimit = 0;
    boolean enemiesSpawned = false;
    private boolean movingRight = true;
    private float enemySpeed = 2.0f; // Adjust as needed
    boolean canShoot = true;
    private float shootCooldown = 1f; // half a second cooldown
    private float shootTimer = 0f;
    @Override
    public void create() {
        backgroundTexture = new Texture("background.png");
        playerTexture = new Texture("Player.png");
        enemyTexture = new Texture("enemy.png");
        bulletTexture = new Texture("bullet.png");
        //dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        //music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
        playerSprite = new Sprite(playerTexture);
        playerSprite.setSize(0.6f, 0.9f);
        touchPos = new Vector2();
        enemySprites = new Array<>();
        bulletSprites = new Array<>();
        playerRectangle = new Rectangle();
        enemyRectangle = new Rectangle();
        bulletRectangles = new Array<>();
        //music.setLooping(true);
        //music.setVolume(.5f);
        //music.play();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();
    }

    private void input() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerSprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerSprite.translateX(-speed * delta);
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            playerSprite.setCenterX(touchPos.x);
        }
    }

    private void logic() {
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        float playerWidth = playerSprite.getWidth();
        float playerHeight = playerSprite.getHeight();

        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, worldWidth - playerWidth));
        if (!enemiesSpawned)
        {
            createEnemyGrid();
            enemiesSpawned = true;
        }
        float delta = Gdx.graphics.getDeltaTime();
        playerRectangle.set(playerSprite.getX(), playerSprite.getY(), playerWidth, playerHeight);

        updateEnemies(delta);
        //revisa si el cooldown ha acabat
        if (!canShoot) {
            shootTimer -= delta;
            if (shootTimer <= 0f) {
                canShoot = true;
            }
        }

        if (canShoot) {
            shootBullet();
            canShoot = false;
            shootTimer = shootCooldown;
        }
        moveBullets(delta);

    }
    private void moveBullets(float deltaTime) {
        for (int i = bulletSprites.size - 1; i >= 0; i--) {
            Sprite bullet = bulletSprites.get(i);
            Rectangle bulletRect = bulletRectangles.get(i);

            bullet.translateY(3.5f * deltaTime);
            bulletRect.setPosition(bullet.getX(), bullet.getY());

            boolean hit = false;
            for (int j = enemySprites.size - 1; j >= 0; j--) {
                Sprite enemy = enemySprites.get(j);
                Rectangle enemyRect = new Rectangle(enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight());

                if (bulletRect.overlaps(enemyRect)) {
                    bulletSprites.removeIndex(i);
                    bulletRectangles.removeIndex(i);
                    enemySprites.removeIndex(j);
                    hit = true;
                    break; // stop checking other enemies for this bullet
                }
            }

            if (!hit && bullet.getY() > viewport.getWorldHeight()) {
                bulletSprites.removeIndex(i);
                bulletRectangles.removeIndex(i);
            }
        }
    }

    private void shootBullet() {
        float shootPositionX = playerRectangle.getX();
        float shootPositionY = playerRectangle.getY() + 1; // offset upward

        Sprite bulletSprite = new Sprite(bulletTexture);
        bulletSprite.setSize(0.125f, 0.575f);
        bulletSprite.setPosition(shootPositionX, shootPositionY);
        bulletSprites.add(bulletSprite);

        // Create matching rectangle hitbox
        Rectangle bulletRect = new Rectangle(shootPositionX, shootPositionY, bulletSprite.getWidth(), bulletSprite.getHeight());
        bulletRectangles.add(bulletRect);
    }

    private void updateEnemies(float deltaTime) {
        float worldWidth = viewport.getWorldWidth();

        // Check if any enemy will hit the edge
        for (Sprite enemy : enemySprites) {
            float nextX = enemy.getX() + (movingRight ? 1 : -1) * enemySpeed * deltaTime;
            if (nextX < 0 || nextX + enemy.getWidth() > worldWidth) {
                movingRight = !movingRight; // Reverse direction
                break;
            }
        }
        // Move all enemies in the current direction
        for (Sprite enemy : enemySprites) {
            enemy.translateX((movingRight ? 1 : -1) * enemySpeed * deltaTime);
            enemyRectangle.set(enemy.getX(), enemy.getY(), enemy.getWidth(), enemyTexture.getHeight());
        }
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight);
        playerSprite.draw(spriteBatch);

        for (Sprite enemySprite : enemySprites) {
            enemySprite.draw(spriteBatch);
        }
        for (Sprite bulletSprite : bulletSprites) {
            bulletSprite.draw(spriteBatch);
        }
        spriteBatch.end();
    }

    private void createEnemyGrid() {
        int rows = 2;
        int columns = 6;
        float enemyWidth = 0.8f;
        float enemyHeight = 0.8f;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        float horizontalSpacing = enemyWidth * 1.5f; // 50% space between enemies
        float verticalSpacing = enemyHeight * 1.5f;

        float totalGridWidth = (columns - 1) * horizontalSpacing + enemyWidth;
        float totalGridHeight = (rows - 1) * verticalSpacing + enemyHeight;

        float startX = (worldWidth - totalGridWidth) / 2f;
        float startY = worldHeight - enemyHeight; // Top of the screen

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                float x = startX + col * horizontalSpacing;
                float y = startY - row * verticalSpacing;

                Sprite enemySprite = new Sprite(enemyTexture);
                enemySprite.setSize(enemyWidth, enemyHeight);
                enemySprite.setPosition(x, y);
                enemySprites.add(enemySprite);
            }
        }
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
