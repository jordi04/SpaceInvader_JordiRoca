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

public class VictoryScreen implements Screen {
    private final Main game;
    private Stage stage;
    private final int score;

    // Textures i estils
    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private TextButton.TextButtonStyle buttonStyle;
    private Label.LabelStyle titleStyle;
    private Label.LabelStyle scoreStyle;

    public VictoryScreen(final Main game, int score) {
        this.game = game;
        this.score = score;
        this.stage = new Stage(game.viewport, game.batch);

        // Crea textures pels botons
        buttonTexture = createColorTexture(200, 80, new Color(0.4f, 0.4f, 0.5f, 0.8f));
        buttonPressedTexture = createColorTexture(200, 80, new Color(0.5f, 0.5f, 0.7f, 0.8f));

        // Crea estils i UI
        createStyles();
        createVictoryUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void createStyles() {
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = game.font;
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));
        buttonStyle.fontColor = Color.WHITE;

        titleStyle = new Label.LabelStyle(game.titleFont, Color.YELLOW);
        scoreStyle = new Label.LabelStyle(game.font, Color.WHITE);
    }

    private void createVictoryUI() {
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Títol
        Label victoryLabel = new Label("VICTÒRIA!", titleStyle);

        // Puntuació
        Label scoreLabel = new Label("Puntuació final: " + score, scoreStyle);

        // Botó de tornar a jugar
        TextButton playAgainButton = new TextButton("TORNAR A JUGAR", buttonStyle);
        playAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
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
        table.add(victoryLabel).padBottom(10).row();
        table.add(scoreLabel).padBottom(10).row();
        table.add(playAgainButton).pad(20).width(128).height(32).row();
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
