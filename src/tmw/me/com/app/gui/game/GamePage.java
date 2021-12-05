package tmw.me.com.app.gui.game;

import com.sun.javafx.scene.control.skin.Utils;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import tmw.me.com.app.game.Game;
import tmw.me.com.app.gui.SVG;
import tmw.me.com.app.gui.Zerades;
import tmw.me.com.app.gui.game.textlist.TextList;
import tmw.me.com.app.gui.home.HomePage;
import tmw.me.com.app.tools.Transitions;
import tmw.me.com.app.tools.control.EditableLabel;
import tmw.me.com.app.tools.control.HoverButton;
import tmw.me.com.app.tools.control.SVGHoverButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePage extends VBox {

    private final SVGHoverButton backArrow = new SVGHoverButton(SVG.resizePath(SVG.ARROW, 2));
    private final VBox backArrowHolder = new VBox(backArrow);
    private final EditableLabel editableLabel = new EditableLabel();
    private final BorderPane topPane = new BorderPane();

    private final HoverButton startButton = new HoverButton("Start Game").addAllStyleClass("h0-bold", "text-accent", "dark-bg", "rounded-corners");
    private final HBox bottomButtons = new HBox(startButton);

    private final TextList thingsList;
    private final TextList actionsList;
    private final BorderPane middlePane = new BorderPane();

    private final HoverButton nextComboButton = new SVGHoverButton(SVG.resizePath(SVG.ARROW, 2.5)).addAllStyleClass("dark-bg", "rounded-corners");
    private final Pane currentComboPane = new Pane();
    private final Pane containCCP = new Pane(currentComboPane);
    private final BorderPane centerPane = new BorderPane();

    private final Game game;
    private final Zerades zerades;

    private final ArrayList<String> playableThings = new ArrayList<>();
    private final ArrayList<String> playableActions = new ArrayList<>();

    private ListChangeListener<String> allowNextListener;

    private boolean gameRunning = false;

    public GamePage(Game game, Zerades zerades, HomePage homePage) {
        super.setFillWidth(true);

        this.game = game;
        this.zerades = zerades;

        thingsList = new TextList("Things", game.getThings());
        actionsList = new TextList("Actions", game.getActions());

        editableLabel.getStyleClass().addAll("h1-bold");
        topPane.getStyleClass().addAll("page-title-box");
        startButton.getStyleClass().add("start-button");

        startButton.setTextAlignment(TextAlignment.CENTER);
        backArrowHolder.setAlignment(Pos.CENTER);
        editableLabel.setAlignment(Pos.CENTER);
        bottomButtons.setAlignment(Pos.CENTER);
        editableLabel.twoWayBind(game.nameProperty());
        editableLabel.setText(game.getName());
        backArrowHolder.setScaleX(-1);
        startButton.setFadeOutTo(0.85);
        nextComboButton.setPadding(new Insets(14.5, 35, 14.5, 35));
        bottomButtons.setSpacing(10);

        topPane.setCenter(editableLabel);
        topPane.setLeft(backArrowHolder);

        middlePane.setPadding(new Insets(175, 100, 0, 100));
        middlePane.setLeft(thingsList);
        middlePane.setRight(actionsList);
        centerPane.setBottom(bottomButtons);
        middlePane.setCenter(centerPane);
        middlePane.setMinHeight(875);
        BorderPane.setAlignment(bottomButtons, Pos.BOTTOM_CENTER);

        backArrow.setOnAction(actionEvent -> animateOut(() -> {
            zerades.setNode(homePage.getPage());
            homePage.animateIn(0, null);
        }));

        startButton.setOnAction(actionEvent -> {
            if (gameRunning) {
                endGame();
            } else {
                startGame();
            }
        });

        nextComboButton.setOnAction(actionEvent -> loadCombo(genPlayableCombo()));

        containCCP.widthProperty().addListener((observableValue, number, t1) -> Platform.runLater(() ->
                currentComboPane.setLayoutX(t1.doubleValue() / 2 - currentComboPane.getWidth() / 2)
        ));
        currentComboPane.widthProperty().addListener((observableValue, number, t1) ->
            currentComboPane.setLayoutX(containCCP.getWidth() / 2 - t1.doubleValue() / 2)
        );

        ListChangeListener<String> safeStartListener = change -> startButton.setDisable(game.getActions().isEmpty() || game.getThings().isEmpty());
        game.getActions().addListener(safeStartListener);
        game.getThings().addListener(safeStartListener);
        startButton.setDisable(game.getActions().isEmpty() || game.getThings().isEmpty());

        allowNextListener = change -> nextComboButton.setDisable(playableActions.isEmpty() || playableThings.isEmpty());
        nextComboButton.setDisable(playableActions.isEmpty() || playableThings.isEmpty());

        this.getChildren().addAll(topPane, middlePane);

        for (String thing : game.getThings()) {
            thingsList.addItem(thing, false);
        }
        for (String action : game.getActions()) {
            actionsList.addItem(action, false);
        }
    }

    public void hide() {
        topPane.setOpacity(0);
        middlePane.setOpacity(0);
    }

    public void animateIn() {
        animateIn(null);
    }
    public void animateIn(Runnable runAfter) {
        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().add(Transitions.fadeTransition(topPane, 300, 1));
        sequentialTransition.getChildren().add(Transitions.fadeTransition(middlePane, 300, 1));
        sequentialTransition.play();
        sequentialTransition.setOnFinished(actionEvent -> {
            if (runAfter != null) {
                runAfter.run();
            }
        });
    }

    public void animateOut() {
        animateOut(null);
    }
    public void animateOut(Runnable runAfter) {
        SequentialTransition sequentialTransition = new SequentialTransition();
        sequentialTransition.getChildren().add(Transitions.fadeTransition(middlePane, 300, 0));
        sequentialTransition.getChildren().add(Transitions.fadeTransition(topPane, 300, 0));
        sequentialTransition.play();
        sequentialTransition.setOnFinished(actionEvent -> {
            if (runAfter != null) {
                runAfter.run();
            }
        });
    }

    public void startGame() {
        playableThings.clear();
        playableActions.clear();
        playableThings.addAll(game.getThings());
        playableActions.addAll(game.getActions());
        startButton.setText("End Game");
        gameRunning = true;
        topPane.setLeft(null);
        editableLabel.setEditable(false);
        ParallelTransition fadeOutTextLists = new ParallelTransition();
        fadeOutTextLists.getChildren().addAll(Transitions.fadeTransition(thingsList, 300, 0), Transitions.fadeTransition(actionsList, 300, 0));
        fadeOutTextLists.setOnFinished(actionEvent -> {
            bottomButtons.getChildren().add(nextComboButton);
            middlePane.setRight(null);
            middlePane.setLeft(null);
            containCCP.setOpacity(0);
            centerPane.setCenter(containCCP);
            BorderPane.setAlignment(containCCP, Pos.TOP_CENTER);
            Transitions.fadeTransition(containCCP, 200, 1).play();
            loadCombo(genPlayableCombo());
        });
        fadeOutTextLists.play();
    }

    public void endGame() {
        gameRunning = false;
        startButton.setText("Start Game");
        topPane.setLeft(backArrowHolder);
        editableLabel.setEditable(true);
        middlePane.setRight(actionsList);
        middlePane.setLeft(thingsList);
        ParallelTransition fadeOutTextLists = new ParallelTransition();
        fadeOutTextLists.getChildren().addAll(Transitions.fadeTransition(thingsList, 300, 1), Transitions.fadeTransition(actionsList, 300, 1));
        bottomButtons.getChildren().remove(nextComboButton);
        fadeOutTextLists.play();
        centerPane.setCenter(null);
    }

    private void loadCombo(String... combo) {
        currentComboPane.getChildren().clear();
        int currentX = 0;
        for (int i = 0, comboLength = combo.length; i < comboLength; i++) {
            String comboPiece = combo[i];
            Label pieceLabel = new Label(comboPiece);
            pieceLabel.getStyleClass().addAll(i + 1 < comboLength ? "combo-piece" : "combo-piece-end", "h0");
            pieceLabel.setPadding(new Insets(5, 26, 5, 26));
            currentComboPane.getChildren().add(pieceLabel);
            pieceLabel.setLayoutX(currentX);
            double fontSize = Utils.computeTextWidth(Font.font(42), comboPiece, 0);
            currentX += (52 + fontSize) - 20;
        }
    }

    private <T> T randomItemFromList(List<T> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    private String[] genPlayableCombo() {
        String thing = randomItemFromList(playableThings);
        String action = randomItemFromList(playableActions);
        playableThings.remove(thing);
        playableActions.remove(action);
        allowNextListener.onChanged(null);
        return new String[]{ thing, action };
    }

}
