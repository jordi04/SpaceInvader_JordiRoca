package io.github.SpaceInvadersCopycat_RocaJordi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class PauseScreen implements Screen {
    private final Main game;
    private final GameScreen gameScreen;
    private Stage stage;

    // Textures i estils
    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private Texture overlayTexture;
    private TextButton.TextButtonStyle buttonStyle;
    private Label.LabelStyle titleStyle;

    public PauseScreen(final Main game, final GameScreen gameScreen) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.stage = new Stage(game.viewport, game.batch);

        // Crea textures pels botons i l'overlay
        buttonTexture = createColorTexture(200, 80, new Color(0.4f, 0.4f, 0.5f, 0.8f));
        buttonPressedTexture = createColorTexture(200, 80, new Color(0.5f, 0.5f, 0.7f, 0.8f));
        overlayTexture = createColorTexture(1, 1, new Color(0, 0, 0, 0.6f));

        // Crea estils i UI
        createStyles();
        createPauseUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void createStyles() {
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = game.font;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));
        buttonStyle.fontColor = Color.WHITE;

        titleStyle = new Label.LabelStyle(game.titleFont, Color.WHITE);
    }

    private void createPauseUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Títol de pausa
        Label pauseLabel = new Label("PAUSA", titleStyle);

        // Botó de reprendre
        TextButton resumeButton = new TextButton("REPRENDRE", buttonStyle);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(gameScreen);
                dispose();
            }
        });

        // Botó de tornar al menú
        TextButton menuButton = new TextButton("MENÚ PRINCIPAL", buttonStyle);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // Afegeix elements a la taula
        table.add(pauseLabel).padBottom(10).row();
        table.add(resumeButton).pad(20).width(128).height(32).row();
        table.add(menuButton).pad(20).width(128).height(32).row();

        stage.addActor(table);
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
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Primer renderitzem el joc pausat de fons
        gameScreen.render(0);

        // Després dibuixem l'overlay semitransparent
        game.batch.begin();
        game.batch.draw(overlayTexture, 0, 0, game.worldWidth, game.worldHeight);
        game.batch.end();

        // Finalment dibuixem la UI de pausa
        stage.act(delta);
        stage.draw();
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
        overlayTexture.dispose();
    }
}
