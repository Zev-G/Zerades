package tmw.me.com.app.tools;

public interface JFXConstructable<T> {

    T addStyleClass(String styleClass);
    T addAllStyleClass(String... styleClass);

    T removeStyleClass(String styleClass);
    T removeAllStyleClass(String... styleClass);

}
