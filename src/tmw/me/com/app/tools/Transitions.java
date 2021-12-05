package tmw.me.com.app.tools;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public final class Transitions {

    public static void scale(Node node, double duration, double scaleToX, double scaleToY) {
        ScaleTransition scaleTransition = new ScaleTransition(new Duration(duration), node);
        scaleTransition.setInterpolator(Interpolator.EASE_BOTH);
        scaleTransition.setFromX(node.getScaleX());
        scaleTransition.setToX(scaleToX);
        scaleTransition.setFromY(node.getScaleY());
        scaleTransition.setToY(scaleToY);

        scaleTransition.play();
    }

    public static FadeTransition fadeTransition(Node node, double duration, double to) {
        FadeTransition fadeTransition = new FadeTransition(new Duration(duration), node);
        fadeTransition.setToValue(to);
        fadeTransition.setOnFinished(actionEvent -> node.setOpacity(to));
        return fadeTransition;
    }

}
