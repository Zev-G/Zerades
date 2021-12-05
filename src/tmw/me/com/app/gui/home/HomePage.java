package tmw.me.com.app.gui.home;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import tmw.me.com.app.game.Game;
import tmw.me.com.app.game.JsonGame;
import tmw.me.com.app.gui.Zerades;
import tmw.me.com.app.gui.game.GamePreview;
import tmw.me.com.app.json.Json;
import tmw.me.com.app.tools.TransitionFlowPane;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class HomePage {

    public static File gameFolder;

    private final TransitionFlowPane itemsPane;
    private final AnchorPane layout;
    private final ScrollPane scrollPane;

    private final Zerades zerades;

    public HomePage(Zerades zerades) {

        this.zerades = zerades;

        itemsPane = new TransitionFlowPane();

        layout = new AnchorPane(itemsPane);
        AnchorPane.setLeftAnchor(itemsPane, 5D); AnchorPane.setRightAnchor(itemsPane, 5D);
        AnchorPane.setTopAnchor(itemsPane, 5D); AnchorPane.setBottomAnchor(itemsPane, 5D);

        scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        layout.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

        layout.setPadding(new Insets(25));
        itemsPane.setVgap(20);
        itemsPane.setHgap(20);

        itemsPane.getStyleClass().add("home-items");
        layout.getStyleClass().add("home-page");
        layout.getStyleClass().add("default-bg");

        layout.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getPickResult().getIntersectedNode() == layout)
                layout.requestFocus();
        });

        for (File file : Objects.requireNonNull(gameFolder.listFiles())) {
            try {
                Game game = Game.fromJson(Json.getFromFile(file, JsonGame.class), this, file);
                itemsPane.addChild(new GamePreview(game, this), true, 0);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, r -> {
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        });
        executorService.scheduleAtFixedRate(this::save, 1, 10, TimeUnit.SECONDS);

    }

    public void save() {
        for (Node node : itemsPane.getRealChildrenUnmodifiable()) {
            if (node instanceof GamePreview) {
                GamePreview gamePreview = (GamePreview) node;
                Game game = gamePreview.getGame();
                File gameFile = game.getFile() != null ? game.getFile() : new File(gameFolder.getPath() + "\\" + ((int) (Math.random() * 10000000)) + ".json");
                if (game.getFile() != gameFile) {
                    game.setFile(gameFile);
                }
                boolean fileExists = false;
                if (!gameFile.exists()) {
                    try {
                        fileExists = gameFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    fileExists = true;
                }
                if (fileExists) {
                    try {
                        Json.writeJsonToFile(gameFile, game.getJsonGame());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // TODO Handle game file creation error(s)
                }
            }
        }
        System.out.println("Saved Games");
    }

    public void animateOut(double delayAfter, Runnable runOnceFinished) {
        new Thread(() -> {
            int duration = 150;
            for (int i = itemsPane.getRealChildrenUnmodifiable().size() - 1; i >= 0; i--) {
                FadeTransition fadeTransition = new FadeTransition(new Duration(duration), itemsPane.getRealChildrenUnmodifiable().get(i).getParent());
                fadeTransition.setToValue(0);
                Platform.runLater(fadeTransition::play);
                try {
                    Thread.sleep((long) (duration * (((double) i + 1) / ((double) itemsPane.getRealChildrenUnmodifiable().size()))));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep((long) delayAfter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (runOnceFinished != null) {
                Platform.runLater(runOnceFinished);
            }
        }).start();
    }

    public void animateIn(double delayAfter, Runnable runOnceFinished) {
        new Thread(() -> {
            int duration = 150;
            for (int i = 0; i < itemsPane.getRealChildrenUnmodifiable().size(); i++) {
                FadeTransition fadeTransition = new FadeTransition(new Duration(duration), itemsPane.getRealChildrenUnmodifiable().get(i).getParent());
                fadeTransition.setToValue(1);
                Platform.runLater(fadeTransition::play);
                try {
                    Thread.sleep((long) (duration * (((double) itemsPane.getRealChildrenUnmodifiable().size() - i) / ((double) itemsPane.getRealChildrenUnmodifiable().size()))));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep((long) delayAfter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (runOnceFinished != null) {
                Platform.runLater(runOnceFinished);
            }
        }).start();
    }

    public ScrollPane getPage() {
        return scrollPane;
    }

    public TransitionFlowPane getItemsPane() {
        return itemsPane;
    }

    public Zerades getZerades() {
        return zerades;
    }
}
