package tmw.me.com.app.gui;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import tmw.me.com.app.gui.styles.Styles;

public class Zerades extends AnchorPane {

    private Node node;

    public Zerades() {
        getStylesheets().add(Styles.get("dark"));
    }

    public void setNode(Node node) {
        if (this.node != null) {
            getChildren().remove(node);
        }
        if (node == null) {
            clearNode();
            return;
        }
        this.node = node;
        getChildren().add(node);
        AnchorPane.setTopAnchor(node, 0D); AnchorPane.setBottomAnchor(node, 0D);
        AnchorPane.setRightAnchor(node, 0D); AnchorPane.setLeftAnchor(node, 0D);
    }

    public void clearNode() {
        if (this.node != null) {
            getChildren().remove(node);
            this.node = null;
        }
    }

}
