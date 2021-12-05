package tmw.me.com.app.gui.styles;


import javafx.scene.paint.Color;

public final class Styles {

    public static final Color NEAR_WHITE = Color.valueOf("#d3dae6");

    public static String get(String sheet) {
        return Styles.class.getResource(sheet.endsWith(".css") ? sheet : sheet + ".css").toExternalForm();
    }

}
