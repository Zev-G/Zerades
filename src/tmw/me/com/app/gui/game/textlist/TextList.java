package tmw.me.com.app.gui.game.textlist;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import tmw.me.com.app.gui.SVG;
import tmw.me.com.app.tools.control.EditableLabel;
import tmw.me.com.app.tools.control.SVGHoverButton;

import java.util.List;


public class TextList extends VBox {

    private final Label title = new Label();
    private final HBox topBox = new HBox(title);
    private final VBox textItems = new VBox();
    private final ScrollPane textItemsHolder = new ScrollPane(textItems);
    private final TextField newItem = new EditableLabel();
    private final SVGHoverButton addButton = new SVGHoverButton(SVG.PLUS);
    private final HBox bottomBox = new HBox(newItem, addButton);

    private final VBox mainBox = new VBox(textItemsHolder, bottomBox);

    private final List<String> syncTo;

    private final BooleanProperty showing = new SimpleBooleanProperty(false);

    public TextList(String name, List<String> syncTo) {
        this.syncTo = syncTo;

        showing.bind(topBox.hoverProperty());

        super.setSpacing(9);
        mainBox.setSpacing(5);
        super.setMinHeight(700);
        super.setMinWidth(400);
        super.setMaxHeight(700);
        super.setMaxWidth(400);
        mainBox.setAlignment(Pos.BOTTOM_CENTER);
        mainBox.setPadding(new Insets(7.5));

        this.getStyleClass().addAll("dark-bg", "rounded-corners", "text-list");
        newItem.getStyleClass().addAll("h2");
        bottomBox.getStyleClass().addAll("darker-bg", "bottom-rounded-corners");
        title.getStyleClass().add("h2-bold");
        topBox.getStyleClass().addAll("page-title-box", "darker-bg", "top-rounded-corners");
        textItems.getStyleClass().addAll("dark-bg");

        topBox.setAlignment(Pos.CENTER);
        bottomBox.setAlignment(Pos.CENTER);
        textItems.setPadding(new Insets(0, 20, 0, 20));
        textItems.setAlignment(Pos.TOP_CENTER);
        textItemsHolder.setFitToWidth(true);
        textItemsHolder.setFitToHeight(true);
        newItem.setPromptText("New Item");
        title.setText(name);

        BorderPane mainBoxHolder = new BorderPane();
//        mainBoxHolder.getStyleClass().add("testing");
        mainBoxHolder.setBottom(mainBox);
        Platform.runLater(() -> mainBoxHolder.setMinHeight(640));
        this.getChildren().addAll(topBox, mainBoxHolder);

        newItem.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                addButton.fire();
            }
        });
        addButton.setOnAction(actionEvent -> {
            if (newItem.getText().trim().length() > 0) {
                addItem(newItem.getText().trim());
                newItem.setText("");
            }
        });
        textItems.heightProperty().addListener((observableValue, number, t1) -> Platform.runLater(() -> textItemsHolder.setVvalue(1)));
    }

    public void addItem(String text) {
        addItem(text, true);
    }
    public void addItem(String text, boolean addToList) {
        Label label = new Label("Hidden");
        label.setAccessibleText(text);
        label.getStyleClass().addAll("h3");
        showing.addListener((observableValue, aBoolean, t1) -> {
            if (t1)
                label.setText(label.getAccessibleText());
            else
                label.setText("Hidden");
        });
        SVGHoverButton trashButton = new SVGHoverButton(SVG.TRASH);
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(label);
        borderPane.setRight(trashButton);
        trashButton.setOnAction(actionEvent -> {
            textItems.getChildren().remove(borderPane);
            syncTo.remove(text);
        });
        textItems.getChildren().add(borderPane);
        if (addToList) {
            syncTo.add(text);
        }
    }

}
