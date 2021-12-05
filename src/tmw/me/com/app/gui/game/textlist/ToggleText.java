package tmw.me.com.app.gui.game.textlist;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextBoundsType;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ToggleText extends Pane {

    private final Label text = new Label();
    private final Pane lineVersion = new Pane();
    private final SimpleStringProperty textProperty = new SimpleStringProperty();
    private final BooleanProperty enabled = new SimpleBooleanProperty();

    private Consumer<Line> lineConsumer;

    public ToggleText() {
        this("");
    }
    public ToggleText(String initialText) {
        text.textProperty().bind(textProperty);
        textProperty.addListener((observableValue, s, t1) -> {
            ArrayList<Node> nodes = new ArrayList<>();
            ArrayList<String> textPieces = new ArrayList<>();
            StringBuilder builder = new StringBuilder();
            boolean letters = true;
            for (char c : t1.toCharArray()) {
                if (c == ' ') {
                    if (letters) {
                        textPieces.add(builder.toString());
                        letters = false;
                        builder = new StringBuilder();
                    }
                } else if (!letters) {
                    textPieces.add(builder.toString());
                    letters = true;
                    builder = new StringBuilder();
                }
                builder.append(c);
            }
            if (builder.toString().length() != 0) {
                textPieces.add(builder.toString());
            }
            double currentX = 0;
            double y = Utils.computeTextHeight(text.getFont(), "A", 0, TextBoundsType.VISUAL) / 2;
            for (String piece : textPieces) {
                double width = Utils.computeTextWidth(text.getFont(), piece, 0);
                if (!piece.contains(" ")) {
                    Line line = new Line(currentX, y, currentX + width, y);
                    if (lineConsumer != null) {
                        lineConsumer.accept(line);
                    }
                    nodes.add(line);
                }
                currentX += width;
            }
            lineVersion.getChildren().setAll(nodes);
        });

        enabled.addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                getChildren().remove(lineVersion);
                getChildren().add(text);
            } else {
                getChildren().remove(text);
                getChildren().add(lineVersion);
            }
        });
        enabled.set(true);
        textProperty.set(initialText);
        text.fontProperty().addListener((observableValue, font, t1) -> textProperty.set(textProperty.get()));
    }

    public String getText() {
        return textProperty.get();
    }

    public SimpleStringProperty textProperty() {
        return textProperty;
    }

    public void setText(String textProperty) {
        this.textProperty.set(textProperty);
    }

    public boolean isEnabled() {
        return enabled.get();
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public Label getLabel() {
        return text;
    }

    public void setLineConsumer(Consumer<Line> lineConsumer) {
        this.lineConsumer = lineConsumer;
    }
    public Consumer<Line> getLineConsumer() {
        return lineConsumer;
    }
}
