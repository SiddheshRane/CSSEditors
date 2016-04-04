/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Stop;
import java.util.Map;
import javafx.beans.property.ObjectProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * This class was originally written to be a linear gradient editor component
 * but the approach in this implementation was abandoned. For a working
 * implementation of a linear gradient editor refer to {@link LinearGradientEditor} This class is
 * nowhere used in the program
 *
 * @see LinearGradientEditor
 * @author Siddhesh
 */
@Deprecated
public class LinearGradientEditorDepracated extends StackPane {

    StopEditor stopEditor;
    StackPane preview;
    ColorRectPane colorRectPane;
    private ObjectProperty<Color> customColorProperty;

    TextField offsetField;

    public LinearGradientEditorDepracated() {
        colorRectPane = new ColorRectPane();
        colorRectPane.setOpacity(0);
        customColorProperty = colorRectPane.customColorProperty();

        preview = new StackPane(colorRectPane);
        stopEditor = new StopEditor();
        getChildren().addAll(preview, colorRectPane, stopEditor);
        setStyle("-fx-border-color:magenta");

    }

    private void editColor() {
        stopEditor.setVisible(false);
        colorRectPane.setVisible(true);
    }

    class StopEditor extends StackPane {

        StackPane start, end;
        ObservableMap<StackPane, Stop> stops;

        Text theta;
        //Custom Event Filtering
        boolean dragged;
        EventHandler<MouseEvent> clickFilter = (MouseEvent event) -> {
            EventType<? extends MouseEvent> eventType = event.getEventType();
            if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
                dragged = false;
            } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                dragged = true;
            }
            if (eventType.equals(MouseEvent.MOUSE_CLICKED)) {
                if (dragged) {
                    event.consume();
                }
            }
        };

        EventHandler<MouseEvent> onClick = (MouseEvent event) -> {
            if (event.getTarget() == this) {
                double y = event.getY();
                y = offsetAt(y);

                addStop(new Stop(y, Color.DARKSEAGREEN));
            } else if (event.getTarget() instanceof StackPane) {
                StackPane stopMark = (StackPane) event.getTarget();

            }

        };

        EventHandler<MouseEvent> onDrag = (MouseEvent event) -> {
            if (event.getTarget() instanceof StackPane) {
                event.consume();
                StackPane stopMark = (StackPane) event.getTarget();
                Stop stop = stops.get(stopMark);
                if (stop == null) {
                    return;
                }

                double offset = offsetAt(sceneToLocal(event.getSceneX(), event.getSceneY()).getY());

                stops.put(stopMark, new Stop(offset, stop.getColor()));
                layoutChildren();

            }
        };

        MapChangeListener<StackPane, Stop> updateStop = new MapChangeListener<StackPane, Stop>() {

            @Override
            public void onChanged(MapChangeListener.Change<? extends StackPane, ? extends Stop> change) {

            }
        };

        public StopEditor() {
            stops = FXCollections.observableHashMap();
            setPrefSize(250, 250);
            setStyle("-fx-border-color:green;");
            start = new StackPane();
            end = new StackPane();

            //css
            start.setMaxSize(10, 10);
            start.setStyle("-fx-background-color:yellow");
            end.setMaxSize(10, 10);
            end.setStyle("-fx-background-color:cyan");

            getChildren().addAll(start, end);
            StackPane.setAlignment(start, Pos.TOP_CENTER);
            StackPane.setAlignment(end, Pos.BOTTOM_CENTER);

            RotateDraggable draggable = new RotateDraggable();
            draggable.drag(this);
            addEventHandler(MouseEvent.MOUSE_CLICKED, onClick);

            addEventFilter(MouseEvent.MOUSE_PRESSED, clickFilter);
            addEventFilter(MouseEvent.MOUSE_DRAGGED, clickFilter);
            addEventFilter(MouseEvent.MOUSE_CLICKED, clickFilter);

            theta = new Text("text");
            getChildren().add(theta);
            StackPane.setAlignment(theta, Pos.TOP_LEFT);
            theta.rotateProperty().bind(rotateProperty().negate());
            setPadding(new Insets(4));
        }

        private void test() {
            stops.values().stream().sorted((Stop o1, Stop o2) -> {
                return 0;
            });
        }

        private Stop[] getStops() {
            return null;
        }

        /**
         * Creates a new StackPane representing the given Stop. The newly
         * created stackPane is added to the StopEditor. It is used as a key to
         * save s as a value in the Map stops.
         *
         * @param s the Stop represented by
         */
        public void addStop(Stop s) {
            StackPane stopMark = new StackPane();
            //css
            stopMark.setMaxSize(10, 10);
            stopMark.setPrefSize(10, 10);
            stopMark.setStyle("-fx-border-color:black;");
            stopMark.setSnapToPixel(true);
            stopMark.setManaged(true);
            stopMark.setOnMouseDragged(onDrag);

            stops.put(stopMark, s);
            getChildren().add(stopMark);
            layoutChildren();
        }

        /**
         * Relocates the layoutY of stackPane to represent the offset contained
         * in stop.
         *
         * @param stackPane the StackPane visually representing a stop
         * @param stop the Stop which stackPane represents
         */
        private void updateStop(StackPane stackPane, Stop stop) {
            stackPane.setLayoutY((getHeight() - stackPane.getHeight() - getPadding().getBottom()
                    - getPadding().getTop()) * stop.getOffset()
                    + getPadding().getTop());
        }

        private void layoutStops() {
            for (Map.Entry<StackPane, Stop> entry : stops.entrySet()) {
                StackPane stackPane = entry.getKey();
                Stop stop = entry.getValue();
                updateStop(stackPane, stop);
            }
        }

        @Override
        protected void layoutChildren() {
            double side = Math.min(getWidth(), getHeight());
            resize(side, side);
            super.layoutChildren();

            layoutStops();
            //System.out.println("layed out");
        }

        /**
         * Returns the offset that a Stop would have at the particular value of
         * layoutY in the StopEditor.
         *
         * @param layoutY the local y coordinate of the mouse pointer in this
         * StopEditor
         * @return the coordinate of layoutY normalized to a height of 1 unit
         */
        private double offsetAt(double layoutY) {
            double offset = (layoutY - getPadding().getTop()) / (getHeight() - getPadding().getTop() - getPadding().getBottom());
            if (offset < 0) {
                return 0;
            }
            if (offset > 1) {
                return 1;
            }
            return offset;
        }

        private final class RotateDraggable extends drag.Draggable {

            double theta0 = 0, theta1 = 0;
            Point2D center;

            @Override
            protected void dragged(MouseEvent event) {
                theta1 = Math.atan2(event.getSceneY() - center.getY(), event.getSceneX() - center.getX()) - theta0;

                StopEditor.this.setRotate(Math.round(Math.toDegrees(theta1)));

            }

            @Override
            protected void pressed(MouseEvent event) {

                center = StopEditor.this.localToScene(StopEditor.this.getBoundsInLocal().getWidth() / 2, StopEditor.this.getBoundsInLocal().getHeight() / 2);
                theta0 = Math.atan2(anchorY - center.getY(), anchorX - center.getX()) - Math.toRadians(StopEditor.this.getRotate());

                System.out.println("target:" + targetNode);
                System.out.println("source:" + event.getSource());
            }
        }
    }

}
