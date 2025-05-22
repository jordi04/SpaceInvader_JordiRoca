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

public class MainMenuScreen implements Screen {
    private final Main game;
    private Stage stage;

    // Textures i estils
    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private TextButton.TextButtonStyle buttonStyle;
    private Label.LabelStyle titleStyle;

    public MainMenuScreen(final Main game) {
        this.game = game;
        this.stage = new Stage(game.viewport, game.batch);

        // Crea textures pels botons
        buttonTexture = createColorTexture(200, 80, new Color(0.4f, 0.4f, 0.5f, 0.8f));
        buttonPressedTexture = createColorTexture(200, 80, new Color(0.5f, 0.5f, 0.7f, 0.8f));

        // Crea estils
        createStyles();
        createUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void createStyles() {
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = game.font;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));
        buttonStyle.fontColor = Color.WHITE;

        titleStyle = new Label.LabelStyle(game.titleFont, Color.YELLOW);
    }

    private void createUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Títol del joc amb nova font
        Label title = new Label("SPACE INVADERS", titleStyle);

        // Botó de jugar amb nova font
        TextButton playButton = new TextButton("START", buttonStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
                dispose();
            }
        });

        // Mides ajustades
        table.add(title).padBottom(50).row();
        table.add(playButton).width(128).height(32);

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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibuixa el fons
        game.batch.begin();
        game.batch.draw(game.backgroundTexture, 0, 0, game.worldWidth, game.worldHeight);
        game.batch.end();

        // Dibuixa la UI
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
    }
}
