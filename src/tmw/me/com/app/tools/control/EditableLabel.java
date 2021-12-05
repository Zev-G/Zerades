package tmw.me.com.app.tools.control;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;

public class EditableLabel extends TextField {

    public EditableLabel() {
        this("");
    }

    public EditableLabel(String text) {
        super(text);
        this.getStyleClass().add("editable-label");

    }

    public void twoWayBind(StringProperty stringProperty) {
        stringProperty.addListener((observableValue, s, t1) -> {
            if (!getText().equals(t1)) {
                setText(t1);
            }
        });

        textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.equals(stringProperty.get())) {
                stringProperty.set(t1);
            }
        });
    }

}
