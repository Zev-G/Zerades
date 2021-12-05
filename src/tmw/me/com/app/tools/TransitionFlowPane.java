package tmw.me.com.app.tools;

import javafx.animation.TranslateTransition;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.util.Duration;
import tmw.me.com.app.tools.concurrent.schedulers.ConsumerEventScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  It is recommended you don't use this since it is currently implemented in a possibly quite un-safe way.
 */
public class TransitionFlowPane extends FlowPane {

    public TransitionFlowPane(Node... children) {
        Arrays.asList(children).forEach(this::addChild);
        getChildren().addListener((ListChangeListener<Node>) change -> {
            while (change.next()) {
                for (Node child : change.getAddedSubList()) {
                    if (!(child instanceof Pane))
                        System.err.println("Do not add children to TransitionFlowPane without using the addChild() method.");
                }
            }
        });
    }


    public void addChildren(Node... children) {
        for (Node node : children) {
            addChild(node);
        }
    }
    public void addChildren(List<Node> children) {
        for (Node node : children) {
            addChild(node);
        }
    }
    public CustomPane addChild(Node child) {
        return addChild(child, true);
    }
    public CustomPane addChild(Node child, int loc) {
        return addChild(child, true, loc);
    }
    public CustomPane addChild(Node child, boolean draggable) {
        return addChild(child, draggable, true, getChildren().size());
    }
    public CustomPane addChild(Node child, boolean draggable, int loc) {
        return addChild(child, draggable, true, loc);
    }
    public CustomPane addChild(Node child, boolean draggable, boolean smoothMove) {
        return addChild(child, draggable, smoothMove, getChildren().size());
    }
    public CustomPane addChild(Node child, boolean draggable, boolean smoothMove, int loc) {
        CustomPane pane = smoothMove ? new CustomPane(draggable, child) {

            private final ConsumerEventScheduler<Point2D> relocateScheduler = new ConsumerEventScheduler<>(30, point2D -> {
                double v = point2D.getX();
                double v1 = point2D.getY();

                TranslateTransition translateTransition = new TranslateTransition(new Duration(200), this);
                translateTransition.setToX(v);
                translateTransition.setToY(v1);
                translateTransition.play();
            });

            @Override
            public void relocate(double v, double v1) {
                if (getBlockNextXDrags() > 0) {
                    setBlockNextXDrags(getBlockNextXDrags() - 1);
                    super.relocate(v, v1);
                } else {
                    relocateScheduler.accept(new Point2D(v, v1));
                }
            }
        } : new CustomPane(draggable, child);

        getChildren().add(loc, pane);
        
        return pane;
    }

    public void removeChildren(Node... children) {
        for (Node node : children) {
            removeChild(node);
        }
    }
    public void removeChildren(Collection<Node> children) {
        for (Node node : children) {
            removeChild(node);
        }
    }
    public void removeChild(Node child) {
        for (Node node : getChildren()) {
            if (!(node instanceof Pane))
                continue;
            if (((Pane) node).getChildren().contains(child)) {
                getChildren().remove(node);
                return;
            }
        }
    }

    public List<Node> getRealChildrenUnmodifiable() {
        ArrayList<Node> arrayList = new ArrayList<>();
        for (Node node : getChildren()) {
            if (!(node instanceof Pane))
                continue;
            arrayList.add(((Pane) node).getChildren().get(0));
        }
        return arrayList;
    }

    public class CustomPane extends Pane {

        private boolean draggable;
        private int blockNextXDrags = 0;

        private Popup dragPopup;
        private ImageView imgView;

        public CustomPane(Node... children) {
            this(true, children);
        }
        public CustomPane(boolean draggable, Node... children) {
            super(children);
            this.draggable = draggable;
            if (draggable) {
                this.setCache(true);
                this.setOnDragDetected(mouseEvent -> {
                    populatePopup();
                    this.setOpacity(0.05);
                    getDragPopup().show(getScene().getWindow());
                    getDragPopup().setX(mouseEvent.getScreenX() - getDragPopup().getWidth() / 2);
                    getDragPopup().setY(mouseEvent.getScreenY() - getDragPopup().getHeight() / 2);
                });
                AtomicReference<Node> glowing = new AtomicReference<>(null);
                this.setOnMouseReleased(mouseEvent -> {
                    this.setOpacity(1);
                    getDragPopup().hide();
                    if (glowing.get() != null) {
                        glowing.get().setEffect(null);
                        if (glowing.get() != this) {
                            int newIndex = TransitionFlowPane.this.getChildren().indexOf(glowing.get());
                            TransitionFlowPane.this.getChildren().remove(this);
                            TransitionFlowPane.this.getChildren().add(newIndex, this);
                        }
                        glowing.set(null);
                    }
                });
                this.setOnMouseDragged(mouseEvent -> {
                    getDragPopup().setX(mouseEvent.getScreenX() - getDragPopup().getWidth() / 2);
                    getDragPopup().setY(mouseEvent.getScreenY() - getDragPopup().getHeight() / 2);
                    Node node = mouseEvent.getPickResult().getIntersectedNode();
                    Node parentInParent = null;
                    while (node != null && node.getParent() != null) {
                        if (node.getParent() == TransitionFlowPane.this) {
                            parentInParent = node;
                            break;
                        } else {
                            node = node.getParent();
                        }
                    }
                    if (parentInParent instanceof CustomPane && ((CustomPane) parentInParent).draggable && parentInParent != this) {
                        if (glowing.get() != parentInParent) {
                            if (glowing.get() != null) {
                                glowing.get().setEffect(null);
                            }
                            glowing.set(parentInParent);
                            parentInParent.setEffect(new Glow(0.25));
                        }
                    } else if (node == null && glowing.get() != null) {
                        if (glowing.get() != null) {
                            glowing.get().setEffect(null);
                        }
                        glowing.set(null);
                    } else {
                        if (glowing.get() != null) {
                            glowing.get().setEffect(null);
                        }
                    }
                });
            }
        }

        public Popup getDragPopup() {
            if (dragPopup == null) {
                dragPopup = new Popup();
            }
            return dragPopup;
        }
        public void populatePopup() {
            Popup popup = getDragPopup();
            popup.getContent().clear();
            SnapshotParameters sp = new SnapshotParameters();
            sp.setFill(Color.TRANSPARENT);
            getImgView().setImage(this.snapshot(sp, null));
            popup.getContent().add(getImgView());
        }

        public ImageView getImgView() {
            if (imgView == null) {
                imgView = new ImageView();
                imgView.setMouseTransparent(true);
            }
            return imgView;
        }

        public int getBlockNextXDrags() {
            return blockNextXDrags;
        }

        public void setBlockNextXDrags(int blockNextXDrags) {
            this.blockNextXDrags = blockNextXDrags;
        }
    }

}
