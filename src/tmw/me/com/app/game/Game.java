package tmw.me.com.app.game;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import tmw.me.com.app.gui.game.GamePage;
import tmw.me.com.app.gui.game.GamePreview;
import tmw.me.com.app.gui.home.HomePage;
import tmw.me.com.app.json.Json;

import java.io.File;
import java.util.ArrayList;

public class Game {

    private File file;
    private SimpleStringProperty name = new SimpleStringProperty();
    private ObservableList<String> things = FXCollections.observableArrayList();
    private ObservableList<String> actions = FXCollections.observableArrayList();

    private JsonGame jsonGame = new JsonGame();

    private HomePage page;

    private GamePreview preview;
    private GamePage gameEditor;

    public Game(HomePage page) {
        this("Unnamed Game", page);
    }

    public Game(String name, HomePage page) {
        this.name.set(name);
        this.page = page;

        syncJsonGame();

        this.name.addListener(observable -> syncJsonGameName());
        this.things.addListener((ListChangeListener<String>) change -> syncJsonGameThings());
        this.actions.addListener((ListChangeListener<String>) change -> syncJsonGameActions());
    }

    public static Game fromJson(JsonGame jsonGame, HomePage page, File file) {
        Game game = new Game(jsonGame.name, page);
        game.setFile(file);
        game.getThings().addAll(jsonGame.things);
        game.getActions().addAll(jsonGame.actions);
        return game;
    }


    public String getName() {
        return name.get();
    }
    public SimpleStringProperty nameProperty() {
        return name;
    }
    public void setName(String name) {
        this.name.set(name);
    }

    public int getThingCount() {
        return things.size();
    }

    public int getActionCount() {
        return actions.size();
    }


    public GamePreview getPreview() {
        if (preview == null) {
            preview = new GamePreview(this, page);
        }
        return preview;
    }

    public GamePage getGameEditor() {
        if (gameEditor == null) {
            gameEditor = new GamePage(this, page.getZerades(), page);
        }
        return gameEditor;
    }

    public ObservableList<String> getThings() {
        return things;
    }

    public ObservableList<String> getActions() {
        return actions;
    }

    private void syncJsonGame() {
        syncJsonGameName();
        syncJsonGameThings();
        syncJsonGameActions();
    }

    private void syncJsonGameName() {
        jsonGame.name = name.get();
    }

    private void syncJsonGameThings() {
        jsonGame.things = new ArrayList<>(things);
    }

    private void syncJsonGameActions() {
        jsonGame.actions = new ArrayList<>(actions);
    }

    public JsonGame getJsonGame() {
        return jsonGame;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
