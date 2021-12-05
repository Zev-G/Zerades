package tmw.me.com.app.gui.game;

import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tmw.me.com.app.game.Game;
import tmw.me.com.app.gui.SVG;
import tmw.me.com.app.gui.home.HomePage;
import tmw.me.com.app.tools.control.EditableLabel;
import tmw.me.com.app.tools.control.SVGHoverButton;

import java.io.File;

public class GamePreview extends VBox {

    private final EditableLabel gameTitle = new EditableLabel();
    private final SVGHoverButton hoverButton = new SVGHoverButton(SVG.TRASH);
    private final HBox titleRow = new HBox(gameTitle, hoverButton);
    private final VBox dataBox = new VBox();

    private final Game game;
    private final HomePage homePage;

    public static GamePreview fromGame(Game game) {
        return game.getPreview();
    }

    public GamePreview(Game game, HomePage page) {
        this(game.getName(), game, page);
    }

    private GamePreview(String title, Game game, HomePage page) {
        homePage = page;

        super.setPadding(new Insets(7.5, 15, 7.5, 15));
        super.getStyleClass().addAll("list-page", "preview", "rounded-corners");
        super.setSpacing(3);

        this.setCursor(Cursor.HAND);
        this.setMinHeight(225);
        this.setOnMouseReleased(mouseEvent -> {
            if (mouseEvent.getPickResult().getIntersectedNode() == this) {
                homePage.animateOut(300, () -> {
                    game.getGameEditor().hide();
                    homePage.getZerades().setNode(game.getGameEditor());
                    game.getGameEditor().animateIn();
                });
            }
        });

        gameTitle.getStyleClass().addAll("h3-bold");
        titleRow.getStyleClass().addAll("darkish-bg");

        Label things = new Label();
        Label actions = new Label();
        dataBox.getChildren().addAll(things, actions);
        dataBox.setPadding(new Insets(12, 3, 0, 3));

        gameTitle.setAlignment(Pos.CENTER);
        titleRow.setPrefWidth(100);
        titleRow.setAlignment(Pos.CENTER);
        this.getChildren().addAll(titleRow, dataBox);

        gameTitle.setText(title);
        gameTitle.textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.equals(game.getName())) {
                game.setName(t1);
            }
        });
        game.nameProperty().addListener((observableValue, s, t1) -> {
            if (!t1.equals(gameTitle.getText())) {
                gameTitle.setText(t1);
            }
        });
        this.game = game;
        game.getThings().addListener((ListChangeListener<String>) change -> things.setText("Things: " + game.getThingCount()));
        game.getActions().addListener((ListChangeListener<String>) change -> actions.setText("Actions: " + game.getActionCount()));

        if (!game.getName().equals(title)) {
            game.setName(title);
        }
        things.setText("Things: " + game.getThingCount());
        actions.setText("Actions: " + game.getActionCount());

        hoverButton.setOnAction(actionEvent -> {
            page.getItemsPane().removeChild(this);
            File file = game.getFile();
            if (file != null && file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.err.println("Couldn't delete the file for game: " + game.getName() + ".\nGo to: " + game.getFile().getPath() + " to delete it yourself.");
                }
            }
        });
    }

    public Game getGame() {
        return game;
    }
}
