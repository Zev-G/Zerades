package tmw.me.com.app.json;

import javafx.scene.Node;

public interface JsonSavable<T extends Node> {

    T newFromJson(String json);
    String toJson();
    void applyJson(String json);

}
