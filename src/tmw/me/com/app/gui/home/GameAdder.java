package tmw.me.com.app.gui.home;

import javafx.scene.text.TextAlignment;
import tmw.me.com.app.game.Game;
import tmw.me.com.app.gui.game.GamePreview;
import tmw.me.com.app.tools.control.HoverButton;

public class GameAdder extends HoverButton {

    public GameAdder(HomePage homePage) {
        super("New\nGame");

        this.getStyleClass().addAll("h2-bold", "dark-bg");
        this.setTextAlignment(TextAlignment.CENTER);

        setOnAction(actionEvent -> homePage.getItemsPane().addChild(new GamePreview(new Game(homePage), homePage), homePage.getItemsPane().getChildren().size() - 1));
    }

}
