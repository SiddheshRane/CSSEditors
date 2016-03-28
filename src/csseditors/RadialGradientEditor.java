/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class RadialGradientEditor extends GradientEditor {

    double centerX = 0.5, centerY = 0.5;
    double radius = 0.5, focus = 0, focusAngle = 90;

    Region rCenter, rFocus, rRadius;
    Line line;
    Ellipse ellipse;
    StackPane add;

    ComboBox<CycleMethod> cycleMethodBox;
    Slider sRadius, sFocus, sFocusAngle;

    //UI States
    StackPane selectedStop;
    double selectedOffset;
    double mouseOffset;
    double mouseX, mouseY;
    boolean showingEndPoints;

    //true if the value of any Slider was set from code
    //false if human interaction with GUI
    private boolean localChange;

    public RadialGradientEditor() {
        rCenter = new Region();
        rFocus = new Region();
        rRadius = new Region();
        line = new Line();
        ellipse = new Ellipse();
        add = new StackPane();
        sRadius = new Slider(0, 1, radius);
        sFocus = new Slider(-1, 1, focus);
        sFocusAngle = new Slider(-180, 180, focusAngle);
        cycleMethodBox = new ComboBox<>(FXCollections.observableArrayList(CycleMethod.values()));
        initUIControls();

        //css styles
        getStylesheets().add("/csseditors/lge.css");
        getStyleClass().add("lge");
        setPrefSize(200, 400);
        rCenter.getStyleClass().add("center");
        rFocus.getStyleClass().add("focus");
        rRadius.getStyleClass().add("radius");
        line.getStyleClass().add("line");

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);
        getChildren().addAll(line, ellipse, add, rCenter, rFocus, rRadius);
        addEventHandler(MouseEvent.MOUSE_CLICKED, onClick);
        addEventHandler(MouseEvent.MOUSE_MOVED, moved);
        //addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, onExitStop);
        new CenterDraggable().drag(this);

        addStop(new Stop(0, Color.RED));
        addStop(new Stop(1, Color.GREEN));
        addStop(new Stop(0.5, Color.YELLOW));

        showEndPoints(false);
        //test

        ellipse.setStroke(Color.RED);
        ellipse.setFill(Color.TRANSPARENT);
        //circle.setVisible(false);
        ellipse.setMouseTransparent(true);
        add.getStyleClass().add("add");

    }

    //<editor-fold defaultstate="collapsed" desc="stopLayout">
    @Override
    protected double stopLayoutX(double t) {
        double r = radius * getWidth();
        double cos = Math.cos(Math.toRadians(focusAngle));
        double fx = r * focus * cos + centerX * getWidth();
        double deltax = r * (1 + Math.abs(focus)) * cos;
        deltax = focus < 0 ? deltax : -deltax;

        double x = fx + t * deltax;
        return x;
    }

    @Override
    protected double stopLayoutY(double t) {
        double r = radius * getHeight();
        double sin = Math.sin(Math.toRadians(focusAngle));
        double fy = r * focus * sin + centerY * getHeight();
        double deltay = r * (1 + Math.abs(focus)) * sin;
        deltay = focus < 0 ? deltay : -deltay;

        double y = fy + t * deltay;
        return y;
    }
//</editor-fold>

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        rCenter.relocate(
                centerX * getWidth() - rCenter.getWidth() / 2,
                centerY * getHeight() - rCenter.getHeight() / 2);
        rFocus.relocate(
                stopLayoutX(0) - rFocus.getWidth() / 2,
                stopLayoutY(0) - rFocus.getHeight() / 2);
        rRadius.relocate(
                stopLayoutX(1) - rRadius.getWidth() / 2,
                stopLayoutY(1) - rRadius.getHeight() / 2);
        line.setStartX(stopLayoutX(0));
        line.setStartY(stopLayoutY(0));
        line.setEndX(stopLayoutX(1));
        line.setEndY(stopLayoutY(1));
        add.relocate(mouseX - add.getWidth() / 2, mouseY - add.getHeight() / 2);

        /* ellipse.setCenterX(stopLayoutX(focus * 0.5 / (1 + focus)));
        ellipse.setCenterY(stopLayoutY(focus * 0.5 / (1 + focus)));
        ellipse.setRadiusX(radius * 0.5 * getWidth());
        ellipse.setRadiusY(radius * 0.5 * getHeight());*/
        updatePreview();

        /*//test
         circle.setRadius(radius * mouseOffset * getWidth());
         double focus = this.focus < 0 ? -this.focus : this.focus;
         circle.setCenterX(stopLayoutX(focus * mouseOffset / (1 + focus)));
         circle.setCenterY(stopLayoutY(focus * mouseOffset / (1 + focus)));
         //test end */
    }

    //<editor-fold defaultstate="collapsed" desc="getOffset">
    /**
     *
     * @param mx mouse x coordinate in local bounds of unitBox
     * @param my mouse y coordinate in local bounds of unitBox
     * @return a double with fractional part that indicates the Stop offset and
     * integer part that represents the ring no if the pattern repeated or
     * reflected
     */
    private double getMouseOffset(double mx, double my) {
        double fx = stopLayoutX(0); //focus x
        double fy = stopLayoutY(0); //focus y
        double cx = centerX * getWidth();
        double cy = centerY * getHeight();

        double mfx = mx - fx;
        double mfy = my - fy;
        double fcx = fx - cx;
        double fcy = fy - cy;

        double A = (mfx * mfx) + (mfy * mfy);
        double B = 2 * (mfx * fcx + mfy * fcy);
        double C = (fcx * fcx) + (fcy * fcy) - radius * radius * getWidth() * getHeight();
        double D = B * B - 4 * A * C;

        if (D >= 0) {
            D = Math.sqrt(D);
            D = (-B + D) / 2 / A;

            if (D == 0 || D != D) {
                return 0;
            }
            D = 1 / D;

            return D;
        }

        return -1.0;
    }

    private double getNormalisedOffset(double D) {
        if (D > 1) {
            switch (cycleMethod.get()) {
                case NO_CYCLE:
                    return 1;
                case REPEAT:
                    return D - (int) D;

                case REFLECT:
                    double C = D - (int) D;
                    if ((int) D % 2 == 1) {
                        return 1.0 - C;
                    }
                    return C;
            }
        } else if (D < 0) {
            D = 0;
        }
        return D;
    }

    @Override
    protected double getOffset(double mx, double my) {

        double D = getMouseOffset(mx, my);

        return getNormalisedOffset(D);

    }
//</editor-fold>

    @Override
    public void updatePreview() {
        RadialGradient rg = new RadialGradient(
                focusAngle,
                focus,
                centerX,
                centerY,
                radius,
                proportional.get(),
                cycleMethod.get(),
                getSortedStops()
        );
        setBackground(new Background(new BackgroundFill(rg, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void showEndPoints(boolean activate) {
        for (StackPane p : stopMap.keySet()) {
            p.setDisable(activate);

        }
        rCenter.toFront();
        rCenter.setVisible(activate);
        rCenter.setMouseTransparent(!activate);
        rFocus.toFront();
        rFocus.setVisible(activate);
        rFocus.setMouseTransparent(!activate);
        rRadius.toFront();
        rRadius.setVisible(activate);
        rRadius.setMouseTransparent(!activate);

        line.setDisable(activate);
    }

    private void selectStop(StackPane p) {
        if (selectedStop == p) {
            //deselect the stop
            selectedStop.setVisible(false);
            selectedStop.setMouseTransparent(true);
            selectedStop = null;
            selectedOffset = -1;
        } else {
            if (selectedStop != null) {
                selectedStop.setVisible(false);
                selectedStop.setMouseTransparent(true);
            }
            selectedStop = p;
            if (p == null) {
//                listView.getSelectionModel().clearSelection();
                return;
            }
            selectedStop.setVisible(true);
            selectedStop.setMouseTransparent(false);
//            listView.getSelectionModel().select(stopMap.get(selectedStop));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="controls">
    private void initUIControls() {
        Text cycleText = new Text("Cycle Method");
        Text radiusText = new Text("Radius");
        Text focusText = new Text("Focus Distance");
        Text angleText = new Text("Focus Angle");
        GridPane gridPane = new GridPane();
        gridPane.addColumn(0, cycleText, radiusText, focusText, angleText);
        gridPane.addColumn(1, cycleMethodBox, sRadius, sFocus, sFocusAngle);
        getChildren().add(gridPane);
        gridPane.setVgap(5);

        cycleMethodBox.valueProperty().bindBidirectional(cycleMethod);
        sRadius.valueProperty().addListener(controlListener);
        sFocus.valueProperty().addListener(controlListener);
        sFocusAngle.valueProperty().addListener(controlListener);

        sRadius.setBlockIncrement(0.1);
        sFocus.setBlockIncrement(0.1);

    }

    private void updateUIControls() {
        localChange = true;
        sRadius.setValue(radius);
        sFocus.setValue(focus);
        sFocusAngle.setValue(focusAngle);
        localChange = false;
    }

    InvalidationListener controlListener = (Observable observable) -> {
        if (!localChange) {
            radius = sRadius.getValue();
            focus = sFocus.getValue();
            focusAngle = sFocusAngle.getValue();
            if (selectedStop != null) {
                selectStop(null);
            }
            requestLayout();
        }
    };
//</editor-fold>

    EventHandler<MouseEvent> moved = new EventHandler<MouseEvent>() {

        //the stop currently being hovered
        StackPane hoverPane;

        @Override
        public void handle(MouseEvent event) {

            if (selectedStop != null || event.getTarget() == hoverPane) {
                return;
            } else if (hoverPane != null) {
                hoverPane.setVisible(false);
                hoverPane.setMouseTransparent(true);
                hoverPane = null;
            }
            mouseX = event.getX();
            mouseY = event.getY();
            double offset = getMouseOffset(event.getX(), event.getY());
            double normalOffset = getNormalisedOffset(offset);
            mouseOffset = offset;
            if (mouseOffset == 0) {
                return;
            }
            ellipse.setStrokeWidth(1);
            for (Stop observableStop : stopMap.values()) {
                double delta = normalOffset - observableStop.getOffset();
                if (Math.abs(delta) < 0.05) {

                    if (cycleMethod.get() == CycleMethod.REFLECT && (int) (offset) % 2 == 1) {
                        delta = -delta;
                    }
                    offset -= delta;
                    selectedOffset = offset;
                    ellipse.setStroke(observableStop.getColor().invert());
                    ellipse.setStrokeWidth(2);

                    StackPane p = stopMap.entrySet().stream().filter(entry -> entry.getValue() == observableStop).findFirst().map(Map.Entry::getKey).get();

                    double fx = stopLayoutX(0);
                    double fy = stopLayoutY(0);

                    fx = fx + offset / mouseOffset * (event.getX() - fx);
                    fy = fy + offset / mouseOffset * (event.getY() - fy);
                    mouseX = fx;
                    mouseY = fy;
                    p.relocate(fx - p.getWidth() / 2, fy - p.getHeight() / 2);
                    if (hoverPane == null) {
                        p.setVisible(true);
                        p.setMouseTransparent(false);
                    }
                    hoverPane = p;

                    break;
                }
            }
            double focus = Math.abs(RadialGradientEditor.this.focus);
            ellipse.setCenterX(stopLayoutX(focus * offset / (1 + focus)));
            ellipse.setCenterY(stopLayoutY(focus * offset / (1 + focus)));
            ellipse.setRadiusX(radius * offset * getWidth());
            ellipse.setRadiusY(radius * offset * getWidth());
            add.relocate(
                    stopLayoutX(focus * offset / (1 + focus)) - add.getWidth() / 2,
                    stopLayoutY(focus * offset / (1 + focus)) - add.getHeight() / 2
            );

        }
    };

    EventHandler<MouseEvent> onClick = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent event) {
            //check for right click(context menu)
            if (event.getButton() == MouseButton.SECONDARY) {
                showingEndPoints = !showingEndPoints;
                showEndPoints(showingEndPoints);

            } else if (showingEndPoints) {
            } else if (event.getTarget() instanceof StackPane) {
                //If the mouse is clicked on an existing stop then make it the current selection
                StackPane p = (StackPane) event.getTarget();
                if (stopMap.containsKey(p)) {
                    selectStop(p);
                }
            } else if (selectedStop != null) {
                selectStop(null);
            } else {
                //Mouse is clicked on empty region.So add a new stop at the particular offset.
                double offset = getOffset(event.getX(), event.getY());
                addStop(new Stop(offset, Color.WHITESMOKE));
            }
        }
    };

    private class CenterDraggable extends drag.Draggable {

        double cx, cy;
        double scx, scy, offset;

        @Override
        protected void dragged(MouseEvent event) {

            if (event.getTarget() == rFocus) {
                double x = event.getX();
                double y = event.getY();
                double cx = centerX * getWidth();
                double cy = centerY * getHeight();

                double theta = Math.toDegrees(Math.atan2(y - cy, x - cx));
                double f = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                f = f / radius / getWidth();
                f = f > 1 ? 1 : f;
                focus = f;
                focusAngle = theta;
                updateUIControls();
                requestLayout();

            } else if (event.getTarget() == rRadius) {
                double dx = event.getX() / getWidth() - centerX;
                double dy = event.getY() / getHeight() - centerY;

                focusAngle = Math.toDegrees(Math.atan2(-dy, -dx));
                dx = Math.sqrt((dx * dx) + (dy * dy));
                dx = dx > 1 ? 1 : dx;
                radius = dx;
                updateUIControls();
                requestLayout();

            } else if (event.getTarget() instanceof StackPane) {
                //update the offset of the dragged stop
                StackPane p = (StackPane) event.getTarget();
                Stop s = stopMap.get(p);
                if (s == null) {
                    return;
                }
                double offset = getOffset(event.getX(), event.getY());
                updateStop(p, new Stop(offset, s.getColor()));
                p.relocate(event.getX() - p.getWidth() / 2, event.getY() - p.getHeight() / 2);

            } else if (selectedStop != null) {
                //adjust focus and focusAngle
                double x = (scx + dragX) / getWidth() - centerX;
                double y = (scy + dragY) / getHeight() - centerY;
                double d = Math.sqrt(x * x + y * y) / radius;
                double t = 1 - offset;
                double f = d / t;

                f = f > 1 ? 1 : f < -1 ? -1 : f;
                focusAngle = Math.toDegrees(Math.atan2(y, x));
                focus = f;
                f = Math.abs(f);

                ellipse.setCenterX(stopLayoutX(f * offset / (1 + f)));
                ellipse.setCenterY(stopLayoutY(f * offset / (1 + f)));
                ellipse.setRadiusX(radius * offset * getWidth());
                ellipse.setRadiusY(radius * offset * getHeight());
                add.relocate(
                        stopLayoutX(f * offset / (1 + f)) - add.getWidth() / 2,
                        stopLayoutY(f * offset / (1 + f)) - add.getHeight() / 2
                );
                selectedStop.setVisible(false);
                updateUIControls();
                requestLayout();

            } else { //drag center

                centerX = cx + dragX / getWidth();
                centerY = cy + dragY / getHeight();
                centerX = centerX > 1 ? 1 : centerX < 0 ? 0 : centerX;
                centerY = centerY > 1 ? 1 : centerY < 0 ? 0 : centerY;

                requestLayout();
                //setTranslateX(-dragX);
                //setTranslateY(-dragY);

            }
        }

        @Override
        protected void pressed(MouseEvent event) {
            if (selectedStop == null) {
                cx = centerX;
                cy = centerY;
            } else {
                offset = selectedOffset;
                double f = Math.abs(focus);
                scx = stopLayoutX(f * offset / (1 + f));
                scy = stopLayoutY(f * offset / (1 + f));

            }
        }

    }

}
