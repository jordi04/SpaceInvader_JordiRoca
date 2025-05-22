package io.github.SpaceInvadersCopycat_RocaJordi;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends Game {

    // Recursos compartits per totes les pantalles
    public SpriteBatch batch;
    public BitmapFont font;
    public BitmapFont titleFont;
    public float worldWidth = 1000;
    public float worldHeight = 400;
    public FitViewport viewport;

    // Textures
    public Texture backgroundTexture;
    public Texture playerTexture;
    public Texture enemyTexture;
    public Texture bulletTexture;
    public Texture bossTexture;
    public Texture rapidFireTexture;
    public Texture rapidFireTexture1;
    public Texture bossTexture1;
    public Animation<TextureRegion> rapidFireAnimation;

    public Texture tripleShotTexture;
    public Texture tripleShotTexture1;
    public Animation<TextureRegion> tripleShotAnimation;

    // So de powerup
    public Sound powerupSound;
    // Animacions
    public Animation<TextureRegion> playerAnimation;
    public Animation<TextureRegion> enemyAnimation;
    public Animation<TextureRegion> bulletAnimation;
    public Animation<TextureRegion> explosionAnimation;

    public Animation<TextureRegion> bossAnimation;

    // Frames addicionals
    public Texture playerTexture1;
    public Texture enemyTexture1;
    public Texture bulletTexture1;
    public Texture explosionTexture;
    public Texture explosionTexture1;

    // Temps d'animació
    public float stateTime;

    // Sons
    public Sound shootSound;
    public Sound explosionSound;private Music musicaFons;




    @Override
    public void create() {
        // Inicialitza recursos bàsics
        batch = new SpriteBatch();
        viewport = new FitViewport(worldWidth, worldHeight);
        stateTime = 0f;

        // Carrega textures
        backgroundTexture = new Texture("background.png");
        playerTexture = new Texture("Player.png");
        playerTexture1 = new Texture("Player1.png");
        enemyTexture = new Texture("enemy.png");
        enemyTexture1 = new Texture("enemy1.png");
        bulletTexture = new Texture("bullet.png");
        bulletTexture1 = new Texture("bullet1.png");
        bossTexture = new Texture("boss.png");
        bossTexture1 = new Texture("boss1.png");
        explosionTexture = new Texture("explosio.png");
        explosionTexture1 = new Texture("explosio1.png");
        rapidFireTexture = new Texture("powerup.png");
        rapidFireTexture1 = new Texture("powerup1.png");
        tripleShotTexture = new Texture("powerup2.png");
        tripleShotTexture1 = new Texture("powerup3.png");

        //aplica filtres a les textures per a que es vegi relativament bé el pixel art
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        playerTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        playerTexture1.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        enemyTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        enemyTexture1.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bulletTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bulletTexture1.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bossTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bossTexture1.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        explosionTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        explosionTexture1.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        rapidFireTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        rapidFireTexture1.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        tripleShotTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        tripleShotTexture1.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        // Crear animacions
        playerAnimation = createAnimation(playerTexture, playerTexture1, 0.5f);
        enemyAnimation = createAnimation(enemyTexture, enemyTexture1, 0.5f);
        bulletAnimation = createAnimation(bulletTexture, bulletTexture1, 0.25f);
        explosionAnimation = createAnimation(explosionTexture, explosionTexture1, 0.25f);
        rapidFireAnimation = createAnimation(rapidFireTexture, rapidFireTexture1, 0.2f);
        tripleShotAnimation = createAnimation(tripleShotTexture, tripleShotTexture1, 0.2f);
        bossAnimation = createAnimation(bossTexture, bossTexture1, 0.25f);
        // Carregar sons
        powerupSound = Gdx.audio.newSound(Gdx.files.internal("sounds/powerup.wav"));
        shootSound = Gdx.audio.newSound(Gdx.files.internal("sounds/shoot.wav"));
        explosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/explosion.wav"));
        musicaFons = Gdx.audio.newMusic(Gdx.files.internal("sounds/music.mp3"));
        musicaFons.setLooping(true);
        musicaFons.play();
        musicaFons.setVolume(0.5f);

        // Crea fonts millorades
        createFonts();

        // Inicia amb la pantalla de menú
        this.setScreen(new MainMenuScreen(this));
    }

    // Mètode auxiliar per crear animacions
    private Animation<TextureRegion> createAnimation(Texture tex1, Texture tex2, float frameDuration) {
        TextureRegion[] frames = new TextureRegion[2];
        frames[0] = new TextureRegion(tex1);
        frames[1] = new TextureRegion(tex2);
        return new Animation<>(frameDuration, frames);
    }

    private void createFonts() {
        try {
            // Utilitzem FreeTypeFontGenerator per fonts d'alta qualitat
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/roboto.ttf"));

            FreeTypeFontParameter parameter = new FreeTypeFontParameter();

            // Font normal
            parameter.size = 24;
            parameter.color = Color.WHITE;
            parameter.borderWidth = 1;
            parameter.borderColor = Color.BLACK;
            parameter.minFilter = Texture.TextureFilter.Linear;
            parameter.magFilter = Texture.TextureFilter.Linear;
            font = generator.generateFont(parameter);

            // Font per títols
            parameter.size = 48;
            parameter.color = Color.YELLOW;
            parameter.borderWidth = 2;
            parameter.borderColor = Color.BLACK;
            titleFont = generator.generateFont(parameter);

            // Activa el markup per permetre colors i altres efectes
            font.getData().markupEnabled = true;
            titleFont.getData().markupEnabled = true;

            generator.dispose();

        } catch (Exception e) {
            Gdx.app.error("Main", "Error carregant fonts FreeType: " + e.getMessage());
            font = new BitmapFont();
            font.getData().setScale(1.0f);
            titleFont = new BitmapFont();
            titleFont.getData().setScale(2.0f);
            font.getData().markupEnabled = true;
            titleFont.getData().markupEnabled = true;
        }
    }

    @Override
    public void render() {
        stateTime += Gdx.graphics.getDeltaTime(); // Actualitza temps d'animació
        super.render(); // Delega el render a la pantalla activa
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        titleFont.dispose();
        backgroundTexture.dispose();
        playerTexture.dispose();
        playerTexture1.dispose();
        enemyTexture.dispose();
        enemyTexture1.dispose();
        bulletTexture.dispose();
        bulletTexture1.dispose();
        bossTexture.dispose();
        explosionTexture.dispose();
        explosionTexture1.dispose();
        shootSound.dispose();
        explosionSound.dispose();
        rapidFireTexture.dispose();
        rapidFireTexture1.dispose();
        tripleShotTexture.dispose();
        tripleShotTexture1.dispose();
        powerupSound.dispose();

        if (screen != null) {
            screen.dispose();
        }
        if (musicaFons != null) {
            musicaFons.dispose();
        }
    }
}
