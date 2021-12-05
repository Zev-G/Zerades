package tmw.me.com.app.tools.control;

import javafx.scene.shape.SVGPath;
import tmw.me.com.app.gui.styles.Styles;

public class SVGHoverButton extends HoverButton {

    private final SVGPath svgPath = new SVGPath();

    public SVGHoverButton(String path) {
        this.setGraphic(svgPath);
        this.getStyleClass().add("transparent-bg");
        this.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                setOpacity(fadeInToProperty().get());
            } else {
                setOpacity(fadeOutToProperty().get());
            }
        });
        svgPath.setContent(path);
        svgPath.setFill(Styles.NEAR_WHITE);
    }

    public SVGPath getSvgPath() {
        return svgPath;
    }
}
