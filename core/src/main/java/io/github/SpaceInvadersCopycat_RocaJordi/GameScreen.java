package io.github.SpaceInvadersCopycat_RocaJordi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class GameScreen implements Screen {
    private final Main game;
    private Stage stage;

    // Entitats del joc
    private Sprite playerSprite;
    private Array<Sprite> enemySprites;
    private Array<Sprite> bulletSprites;
    private Array<Rectangle> bulletRectangles;
    private Rectangle playerRectangle;
    private Vector2 touchPos;
    private Array<Explosion> explosions;
    private boolean[] enemyHit;
    private float[] enemyHitTimer;

    // Variables de joc
    private boolean enemiesSpawned = false;
    private boolean secondPhase = false;
    private int score = 0;
    private float bossLives = 16f;
    private int numberOfBosses = 1;
    private boolean movingRight = true;
    private float enemySpeed = 60f;
    private float moveDownSpeed = 30f;
    private boolean moveDown = false;
    private boolean canShoot = true;
    private float shootCooldown = 0.5f;
    private float shootTimer = 0f;

    // UI
    private Label scoreLabel;
    private TextButton pauseButton;
    private TextButton.TextButtonStyle buttonStyle;
    private Label.LabelStyle labelStyle;
    private Texture buttonTexture;
    private Texture buttonPressedTexture;

    // PowerUps
    private Array<PowerUp> powerUps;
    private float powerUpChance = 0.1f;

    // RapidFire PowerUp
    private boolean shootCooldownBoostActive = false;
    private float shootCooldownBoostTimer = 0f;
    private float shootCooldownBoostDuration = 5f;
    private float normalShootCooldown;
    private float boostedShootCooldown = 0.15f;

    // TripleShot PowerUp
    private boolean tripleShotActive = false;
    private float tripleShotTimer = 0f;
    private float tripleShotDuration = 5f;

    // PowerUp actius (per mostrar a la UI)
    private String activePowerUps = "";

    public GameScreen(final Main game) {
        this.game = game;
        this.stage = new Stage(game.viewport, game.batch);

        // Inicialitza entitats
        playerSprite = new Sprite(game.playerTexture);
        playerSprite.setSize(32, 48);
        playerSprite.setPosition(game.worldWidth / 2 - playerSprite.getWidth() / 2, 0);

        enemySprites = new Array<>();
        bulletSprites = new Array<>();
        bulletRectangles = new Array<>();
        playerRectangle = new Rectangle();
        touchPos = new Vector2();
        explosions = new Array<>();
        enemyHit = new boolean[0];
        enemyHitTimer = new float[0];

        powerUps = new Array<>();
        normalShootCooldown = shootCooldown;

        // Crea textures pels botons
        buttonTexture = createColorTexture(200, 80, new Color(0.4f, 0.4f, 0.5f, 0.8f));
        buttonPressedTexture = createColorTexture(200, 80, new Color(0.5f, 0.5f, 0.7f, 0.8f));

        // Crea estils i UI
        createStyles();
        createGameUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void createStyles() {
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = game.font;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));
        buttonStyle.fontColor = Color.WHITE;

        labelStyle = new Label.LabelStyle(game.font, Color.WHITE);
    }

    private void createGameUI() {
        // Puntuació
        scoreLabel = new Label("Puntuació: 0", labelStyle);
        Table scoreTable = new Table();
        scoreTable.top().left();
        scoreTable.setFillParent(true);
        scoreTable.add(scoreLabel).pad(10);
        stage.addActor(scoreTable);

        // Botó de pausa
        pauseButton = new TextButton("Pausa             ", buttonStyle);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showPauseScreen();
            }
        });

        Table pauseTable = new Table();
        pauseTable.top().right();
        pauseTable.setFillParent(true);
        pauseTable.add(pauseButton).pad(20).width(3).height(3);
        stage.addActor(pauseTable);
    }

    private Texture createColorTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Neteja la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualitza explosions
        for (int i = explosions.size - 1; i >= 0; i--) {
            explosions.get(i).update(delta);
            if (explosions.get(i).isFinished()) {
                explosions.removeIndex(i);
            }
        }

        // Actualitza els timers de hit pels enemics
        for (int i = 0; i < enemyHit.length; i++) {
            if (enemyHit[i]) {
                enemyHitTimer[i] -= delta;
                if (enemyHitTimer[i] <= 0) {
                    enemyHit[i] = false;
                }
            }
        }

        // Processa inputs i lògica
        handleInput();
        updateGame(delta);

        // Configura matriu de projecció
        game.batch.setProjectionMatrix(stage.getCamera().combined);

        // Dibuixa els elements del joc
        game.batch.begin();
        game.batch.draw(game.backgroundTexture, 0, 0, game.worldWidth, game.worldHeight);

        // Dibuixa jugador amb animació
        TextureRegion playerFrame = game.playerAnimation.getKeyFrame(game.stateTime, true);
        game.batch.draw(playerFrame, playerSprite.getX(), playerSprite.getY(),
            playerSprite.getWidth(), playerSprite.getHeight());

        // Dibuixa enemics amb animació
        for (int i = 0; i < enemySprites.size; i++) {
            Sprite enemy = enemySprites.get(i);

            // Configura el color segons si està impactat
            if (i < enemyHit.length && enemyHit[i]) {
                game.batch.setColor(Color.RED);
            } else {
                game.batch.setColor(Color.WHITE);
            }

            // Dibuixa segons el tipus d'enemic
            if (enemy.getTexture() == game.bossTexture) {
                TextureRegion bossFrame = game.bossAnimation.getKeyFrame(game.stateTime, true);
                game.batch.draw(bossFrame, enemy.getX(), enemy.getY(),
                    enemy.getWidth(), enemy.getHeight());
                //enemy.draw(game.batch);
            } else {
                TextureRegion enemyFrame = game.enemyAnimation.getKeyFrame(game.stateTime, true);
                game.batch.draw(enemyFrame, enemy.getX(), enemy.getY(),
                    enemy.getWidth(), enemy.getHeight());
            }
        }
        game.batch.setColor(Color.WHITE); // Restaura el color

        // Dibuixa bales amb animació
        for (Sprite bullet : bulletSprites) {
            TextureRegion bulletFrame = game.bulletAnimation.getKeyFrame(game.stateTime, true);
            game.batch.draw(bulletFrame, bullet.getX(), bullet.getY(),
                bullet.getWidth(), bullet.getHeight());
        }

        // Dibuixa explosions
        for (Explosion explosion : explosions) {
            explosion.render(game.batch);
        }

        // Dibuixa PowerUps
        for (PowerUp powerUp : powerUps) {
            powerUp.render(game.batch, game.stateTime);
        }

        // Mostra PowerUps actius
        activePowerUps = "";
        if (shootCooldownBoostActive) {
            activePowerUps += "Dispar Ràpid: " + (int)shootCooldownBoostTimer + "s ";
        }
        if (tripleShotActive) {
            activePowerUps += "Triple Dispar: " + (int)tripleShotTimer + "s";
        }

        if (!activePowerUps.isEmpty()) {
            game.font.draw(game.batch, activePowerUps, 10, game.worldHeight - 50);
        }

        game.batch.end();

        // Dibuixa la UI
        stage.act(delta);
        stage.draw();

        // Tecla P per pausa
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            showPauseScreen();
        }
    }

    private void handleInput() {
        float speed = 4f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerSprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerSprite.translateX(-speed * delta);
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            game.viewport.unproject(touchPos);
            playerSprite.setCenterX(touchPos.x);
        }
    }

    private void updateGame(float delta) {
        // Limita la posició del jugador
        playerSprite.setX(MathUtils.clamp(playerSprite.getX(), 0, game.worldWidth - playerSprite.getWidth()));

        // Actualitza el rectangle del jugador
        playerRectangle.set(playerSprite.getX(), playerSprite.getY(),
            playerSprite.getWidth(), playerSprite.getHeight());

        // Genera enemics si cal
        if (!enemiesSpawned) {
            createEnemyGrid();
            enemiesSpawned = true;
        }

        // Comprova si no queden enemics
        if (enemySprites.size < 1 && enemiesSpawned) {
            if (secondPhase) {
                // Victoria!
                game.setScreen(new VictoryScreen(game, score));
                dispose();
            } else {
                // Segona fase: spawnejar bosses
                secondPhase = true;
                for (int i = 0; i < numberOfBosses; i++) {
                    Sprite bossSprite = new Sprite(game.bossTexture);
                    bossSprite.setSize(128, 128);
                    bossSprite.setPosition(200 + (i * 2), 300);
                    enemySprites.add(bossSprite);
                }
                enemySpeed *= 3;
                moveDownSpeed *= 3;
            }
        }

        // Actualitza moviment dels enemics
        updateEnemies(delta);

        // Actualitza els PowerUps
        updatePowerUps(delta);

        // Actualitza l'estat del powerup de cooldown
        if (shootCooldownBoostActive) {
            shootCooldownBoostTimer -= delta;
            if (shootCooldownBoostTimer <= 0) {
                shootCooldownBoostActive = false;
                shootCooldown = normalShootCooldown; // Restaura el cooldown normal
            }
        }

        // Actualitza l'estat del powerup de triple shot
        if (tripleShotActive) {
            tripleShotTimer -= delta;
            if (tripleShotTimer <= 0) {
                tripleShotActive = false;
            }
        }

        // Gestiona el dispar
        if (!canShoot) {
            shootTimer -= delta;
            if (shootTimer <= 0) {
                canShoot = true;
            }
        }
        if (canShoot) {
            shootBullet();
            canShoot = false;
            shootTimer = shootCooldown;
        }

        // Mou les bales i comprova col·lisions
        moveBullets(delta);

        // Comprova game over
        for (Sprite enemy : enemySprites) {
            if (enemy.getY() <= 0.3f) {
                game.setScreen(new LossScreen(game, score));
                dispose();
                break;
            }
        }
    }

    private void updatePowerUps(float delta) {
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            powerUp.update(delta);

            // Comprova col·lisió amb el jugador
            if (powerUp.getBounds().overlaps(playerRectangle)) {
                // Activa el powerup segons el seu tipus
                if (powerUp.getType() == PowerUp.Type.RAPID_FIRE) {
                    activateShootCooldownBoost();
                } else if (powerUp.getType() == PowerUp.Type.TRIPLE_SHOT) {
                    activateTripleShot();
                }

                // Elimina el powerup recollit
                powerUps.removeIndex(i);

                // Reprodueix so de recollida (opcional)
                game.powerupSound.play(0.5f);
            }
            // Elimina el powerup si surt de la pantalla
            else if (powerUp.isOutOfScreen(game.worldHeight)) {
                powerUps.removeIndex(i);
            }
        }
    }

    private void activateShootCooldownBoost() {
        shootCooldownBoostActive = true;
        shootCooldownBoostTimer = shootCooldownBoostDuration;
        shootCooldown = boostedShootCooldown;
    }

    private void activateTripleShot() {
        tripleShotActive = true;
        tripleShotTimer = tripleShotDuration;
    }

    private void updateEnemies(float delta) {
        // Comprova si els enemics han d'invertir direcció
        for (Sprite enemy : enemySprites) {
            float nextX = enemy.getX() + (movingRight ? 1 : -1) * enemySpeed * delta;
            if (nextX < 0 || nextX + enemy.getWidth() > game.worldWidth) {
                movingRight = !movingRight;
                moveDown = true;
                break;
            }
        }

        // Mou els enemics
        for (Sprite enemy : enemySprites) {
            enemy.translateX((movingRight ? 1 : -1) * enemySpeed * delta);
            if (moveDown) {
                enemy.translateY(-moveDownSpeed);
                if (secondPhase) {
                    moveDownSpeed *= 2f * delta;
                }
            }
        }
        moveDown = false;
    }

    private void shootBullet() {
        float shootPositionX = playerSprite.getX() + playerSprite.getWidth() / 2 - 0.0625f;
        float shootPositionY = playerSprite.getY() + playerSprite.getHeight();

        if (tripleShotActive) {
            // Bala central (recta)
            createBullet(shootPositionX, shootPositionY, 0);

            // Bala esquerra (30 graus)
            createBullet(shootPositionX, shootPositionY, -30);

            // Bala dreta (30 graus)
            createBullet(shootPositionX, shootPositionY, 30);
        } else {
            // Només una bala recta
            createBullet(shootPositionX, shootPositionY, 0);
        }

        // Reprodueix el so de dispar
        game.shootSound.play(0.5f);
    }

    private void createBullet(float x, float y, float angle) {
        Sprite bulletSprite = new Sprite(game.bulletTexture);
        bulletSprite.setSize(11, 24f);
        bulletSprite.setPosition(x, y);
        bulletSprite.setRotation(angle);
        bulletSprites.add(bulletSprite);

        Rectangle bulletRect = new Rectangle(x, y, bulletSprite.getWidth(), bulletSprite.getHeight());
        bulletRectangles.add(bulletRect);
    }

    private void moveBullets(float deltaTime) {
        for (int i = bulletSprites.size - 1; i >= 0; i--) {
            Sprite bullet = bulletSprites.get(i);
            Rectangle bulletRect = bulletRectangles.get(i);

            // Calcula la trajectòria basada en la rotació
            float rotation = bullet.getRotation();
            float velocityX = (float) Math.sin(Math.toRadians(rotation)) * 64 * deltaTime;
            float velocityY = (float) Math.cos(Math.toRadians(rotation)) * 128 * deltaTime;

            // Mou la bala
            bullet.translate(velocityX, velocityY);
            bulletRect.setPosition(bullet.getX(), bullet.getY());

            boolean hit = false;
            for (int j = enemySprites.size - 1; j >= 0; j--) {
                Sprite enemy = enemySprites.get(j);
                Rectangle enemyRect = new Rectangle(enemy.getX(), enemy.getY(),
                    enemy.getWidth(), enemy.getHeight());

                if (bulletRect.overlaps(enemyRect)) {
                    // Crea una explosió quan la bala xoca
                    float explosionSize = bullet.getWidth() * 4;
                    explosions.add(new Explosion(
                        bullet.getX(),
                        bullet.getY(),
                        game.explosionAnimation,
                        explosionSize
                    ));

                    // Reprodueix el so d'explosió
                    game.explosionSound.play(0.7f);

                    // Elimina la bala
                    bulletSprites.removeIndex(i);
                    bulletRectangles.removeIndex(i);

                    // Gestiona l'impacte
                    if (enemy.getTexture() != game.bossTexture) {
                        enemySprites.removeIndex(j);
                        addScore(100);

                        // Comprova si ha de generar un PowerUp
                        if (MathUtils.random() < powerUpChance) {
                            // 50% probabilitat per cada tipus de powerup
                            if (MathUtils.randomBoolean()) {
                                spawnPowerUp(enemy.getX() + enemy.getWidth()/2, enemy.getY() + enemy.getHeight()/2, PowerUp.Type.RAPID_FIRE);
                            } else {
                                spawnPowerUp(enemy.getX() + enemy.getWidth()/2, enemy.getY() + enemy.getHeight()/2, PowerUp.Type.TRIPLE_SHOT);
                            }
                        }
                    } else {
                        bossLives--;
                        if (bossLives <= 0) {
                            enemySprites.removeIndex(j);
                            addScore(500);
                            // Genera ambdós tipus de powerUp quan mor un boss
                            spawnPowerUp(enemy.getX() + enemy.getWidth()/4, enemy.getY() + enemy.getHeight()/2, PowerUp.Type.RAPID_FIRE);
                            spawnPowerUp(enemy.getX() + enemy.getWidth()*3/4, enemy.getY() + enemy.getHeight()/2, PowerUp.Type.TRIPLE_SHOT);
                        }
                    }

                    hit = true;
                    break;
                }
            }

            // Si la bala surt de la pantalla o ha colpejat, l'elimina
            if (!hit && (bullet.getY() > game.worldHeight || bullet.getY() < 0 ||
                bullet.getX() < 0 || bullet.getX() > game.worldWidth)) {
                bulletSprites.removeIndex(i);
                bulletRectangles.removeIndex(i);
            }
        }
    }

    private void spawnPowerUp(float x, float y, PowerUp.Type type) {
        Animation<TextureRegion> animation;

        // Selecciona l'animació segons el tipus
        if (type == PowerUp.Type.RAPID_FIRE) {
            animation = game.rapidFireAnimation;
        } else {
            animation = game.tripleShotAnimation;
        }

        PowerUp powerUp = new PowerUp(x, y, animation, 24f, type);
        powerUps.add(powerUp);
    }

    private void addScore(int amount) {
        score += amount;
        scoreLabel.setText("Puntuació: " + score);
    }

    private void createEnemyGrid() {
        int rows = 4;
        int columns = 15;
        float enemyWidth = 32f;
        float enemyHeight = 32f;
        float spacing = 16f;
        float verticalSpacing = enemyHeight * 1.5f;

        float worldWidth = game.worldWidth;
        float worldHeight = game.worldHeight;

        float totalGridWidth = columns * enemyWidth + (columns - 1) * spacing;
        float startX = (worldWidth - totalGridWidth) / 2f;
        float startY = worldHeight - enemyHeight * 1.5f;

        // Inicialitza els arrays d'estat dels enemics
        enemyHit = new boolean[rows * columns];
        enemyHitTimer = new float[rows * columns];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                float x = startX + col * (enemyWidth + spacing);
                float y = startY - row * verticalSpacing;

                Sprite enemySprite = new Sprite(game.enemyTexture);
                enemySprite.setSize(enemyWidth, enemyHeight);
                enemySprite.setPosition(x, y);
                enemySprites.add(enemySprite);
            }
        }
    }

    private void showPauseScreen() {
        game.setScreen(new PauseScreen(game, this));
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        buttonTexture.dispose();
        buttonPressedTexture.dispose();
    }
}
