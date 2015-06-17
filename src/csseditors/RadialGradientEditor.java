/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class RadialGradientEditor extends GradientEditor {

    double centerX = 0.5, centerY = 0.5;
    double radius = 0.5, focus = 0, focusAngle = 90;

    /*_______________
     |               |
     | UI COMPONENTS |
     |_______________|
     */
    Region rCenter, rFocus, rRadius;
    Line line;
    Circle circle;
    StackPane add;

    ComboBox<CycleMethod> cycleMethodBox;
    Slider sRadius, sFocus, sFocusAngle;

    double mouseOffset;
    double mouseX , mouseY;
    boolean stopSelected;
    //true if the value of any Slider was set from code
    //false if human interaction with GUI
    private boolean localChange;

    public RadialGradientEditor() {
        rCenter = new Region();
        rFocus = new Region();
        rRadius = new Region();
        line = new Line();
        circle = new Circle();
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
        clip.widthProperty().bind(unitBox.widthProperty());
        clip.heightProperty().bind(unitBox.heightProperty());
        unitBox.setClip(clip);
        unitBox.getChildren().addAll(line, circle, rCenter, rFocus, rRadius);
        unitBox.addEventHandler(MouseEvent.MOUSE_CLICKED, onClick);
        unitBox.addEventHandler(MouseEvent.MOUSE_MOVED, moved);
        new CenterDraggable().drag(unitBox);

        addStop(new Stop(0, Color.BLACK));
        addStop(new Stop(1, Color.web("001a80")));
        addStop(new Stop(0.5, Color.ALICEBLUE));

        selectStop(false);
        //test

        circle.setStroke(Color.RED);
        circle.setFill(Color.TRANSPARENT);
        add.getStyleClass().add("add");

    }

    @Override
    protected double stopLayoutX(double t) {
        double r = radius * unitBox.getWidth();
        double cos = Math.cos(Math.toRadians(focusAngle));
        double fx = r * focus * cos + centerX * unitBox.getWidth();
        double deltax = r * (1 + Math.abs(focus)) * cos;
        deltax = focus < 0 ? deltax : -deltax;

        double x = fx + t * deltax;
        return x;
    }

    @Override
    protected double stopLayoutY(double t) {
        double r = radius * unitBox.getWidth();
        double sin = Math.sin(Math.toRadians(focusAngle));
        double fy = r * focus * sin + centerY * unitBox.getHeight();
        double deltay = r * (1 + Math.abs(focus)) * sin;
        deltay = focus < 0 ? deltay : -deltay;

        double y = fy + t * deltay;
        return y;
    }

    @Override
    protected void layoutUnitBoxContents() {
        //layoutStops();
        rCenter.relocate(
                centerX * unitBox.getWidth() - rCenter.getWidth() / 2,
                centerY * unitBox.getHeight() - rCenter.getHeight() / 2);
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
        updatePreview();

        /*//test
         circle.setRadius(radius * mouseOffset * unitBox.getWidth());
         double focus = this.focus < 0 ? -this.focus : this.focus;
         circle.setCenterX(stopLayoutX(focus * mouseOffset / (1 + focus)));
         circle.setCenterY(stopLayoutY(focus * mouseOffset / (1 + focus)));
         //test end */
    }

    private void layoutCircle(double ringOffset) {

        {//when drag center starts
            circle.setVisible(false);
            add.setVisible(false);
        }
        {//when on a stop
            add.setVisible(false);

        }
    }

    /**
     *
     * @param mx mouse x coordinate in local bounds of unitBox
     * @param my mouse y coordinate in local bounds of unitBox
     * @return a double with fractional part that indicates the Stop offset and
     * integer part that represents the ring no if the pattern repeated or
     * reflected
     */
    private double getMouseOffset(double mx, double my) {
        double fx = stopLayoutX(0);
        double fy = stopLayoutY(0);
        double cx = centerX * unitBox.getWidth();
        double cy = centerY * unitBox.getHeight();

        double mfx = mx - fx;
        double mfy = my - fy;
        double fcx = fx - cx;
        double fcy = fy - cy;

        double A = (mfx * mfx) + (mfy * mfy);
        double B = 2 * (mfx * fcx + mfy * fcy);
        double C = (fcx * fcx) + (fcy * fcy) - radius * radius * unitBox.getWidth() * unitBox.getHeight();
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
                sortedStops
        );
        unitBox.setBackground(new Background(new BackgroundFill(rg, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void selectStop(boolean activate) {

        for (StackPane p : observableStacks) {
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

    private void initUIControls() {
        Text cycleText = new Text("Cycle Method");
        Text radiusText = new Text("Radius");
        Text focusText = new Text("Focus Distance");
        Text angleText = new Text("Focus Angle");
        GridPane gridPane = new GridPane();
        gridPane.addColumn(0, cycleText, radiusText, focusText, angleText);
        gridPane.addColumn(1, cycleMethodBox, sRadius, sFocus, sFocusAngle);
        getChildren().add(1, gridPane);
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
            unitBox.requestLayout();
        }
    };

    EventHandler<MouseEvent> onPressed = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            if (event.getTarget() == circle);
        }
    };

    EventHandler<MouseEvent> onReleased = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
        }
    };

    EventHandler<MouseEvent> moved = (MouseEvent event) -> {
        double offset = getMouseOffset(event.getX(), event.getY());
        mouseOffset = offset;
        System.out.println("Mouse Offset=" + offset);
        double focus = Math.abs(this.focus);

        circle.setStrokeWidth(1);

        for (Stop observableStop : observableStops) {

            double delta = getNormalisedOffset(offset) - observableStop.getOffset();

            if (Math.abs(delta) < 0.05) {

                if (cycleMethod.get() == CycleMethod.REFLECT && (int) (offset) % 2 == 1) {
                    delta = -delta;
                }
                offset -= delta;
                circle.setStroke(observableStop.getColor().invert());
                circle.setStrokeWidth(2);

                System.out.println("Stop " + observableStop.getOffset() + " offset : " + offset + " delta : " + delta);
                StackPane p = observableStacks.get(observableStops.indexOf(observableStop));

                double fx = stopLayoutX(0);
                double fy = stopLayoutY(0);

                add.relocate(fx, fy);

                fx = fx + offset / mouseOffset * (event.getX() - fx);
                fy = fy + offset / mouseOffset * (event.getY() - fy);
                p.relocate(fx - p.getWidth() / 2, fy - p.getHeight() / 2);

                break;
            }
        }

        circle.setCenterX(stopLayoutX(focus * offset / (1 + focus)));
        circle.setCenterY(stopLayoutY(focus * offset / (1 + focus)));
        circle.setRadius(radius * offset * unitBox.getWidth());
    };

    EventHandler<MouseEvent> onClick = (MouseEvent event) -> {
        //check for right click(context menu)
        if (event.isPopupTrigger()) {
            stopSelected = !stopSelected;
            selectStop(stopSelected);

        } else if (stopSelected) {
        } else if (event.getTarget() instanceof StackPane) {
            //If the mouse is clicked on an existing stop then make it the current selection
            StackPane p = (StackPane) event.getTarget();
            if (stopMap.containsKey(p)) {
                stopList.getSelectionModel().select(stopMap.get(p));
            }
        } else {
            //Mouse is clicked on empty region.So add a new stop at the particular offset.
            double offset = getOffset(event.getX(), event.getY());
            addStop(new Stop(offset, Color.WHITESMOKE));
        }
    };

//    EventHandler<MouseEvent> onDrag = (MouseEvent event) -> {
//        if (stopSelected) {
//
//        }
//        if (event.getTarget() == rFocus) {
//            double x = event.getX();
//            double y = event.getY();
//            double cx = centerX * unitBox.getWidth();
//            double cy = centerY * unitBox.getHeight();
//
//            double theta = Math.toDegrees(Math.atan2(y - cy, x - cx));
//            double f = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
//            f = f / radius / unitBox.getWidth();
//            f = f > 1 ? 1 : f;
//            focus = f;
//            focusAngle = theta;
//            unitBox.requestLayout();
//            
//
//        } else if (event.getTarget() instanceof StackPane) {
//            //update the offset of the dragged stop
//            StackPane p = (StackPane) event.getTarget();
//            Stop s = stopMap.get(p);
//            if (s == null) {
//                return;
//            }
//            double offset = getOffset(event.getX(), event.getY());
//            updateStop(p, new Stop(offset, s.getColor()));
//
//        } else { //drag center
//
//        }
//    };
    private class CenterDraggable extends drag.Draggable {

        double cx, cy;

        @Override
        protected void dragged(MouseEvent event) {

            if (event.getTarget() == rFocus) {
                double x = event.getX();
                double y = event.getY();
                double cx = centerX * unitBox.getWidth();
                double cy = centerY * unitBox.getHeight();

                double theta = Math.toDegrees(Math.atan2(y - cy, x - cx));
                double f = Math.sqrt((x - cx) * (x - cx) + (y - cy) * (y - cy));
                f = f / radius / unitBox.getWidth();
                f = f > 1 ? 1 : f;
                focus = f;
                focusAngle = theta;
                updateUIControls();
                unitBox.requestLayout();

            } else if (event.getTarget() == rRadius) {
                double dx = event.getX() / unitBox.getWidth() - centerX;
                double dy = event.getY() / unitBox.getHeight() - centerY;

                focusAngle = Math.toDegrees(Math.atan2(-dy, -dx));
                dx = Math.sqrt((dx * dx) + (dy * dy));
                dx = dx > 1 ? 1 : dx;
                radius = dx;
                updateUIControls();
                unitBox.requestLayout();

            } else if (event.getTarget() instanceof StackPane) {
                //update the offset of the dragged stop
                StackPane p = (StackPane) event.getTarget();
                Stop s = stopMap.get(p);
                if (s == null) {
                    return;
                }
                double offset = getOffset(event.getX(), event.getY());
                updateStop(p, new Stop(offset, s.getColor()));

            } else { //drag center

                centerX = cx + dragX / unitBox.getWidth();
                centerY = cy + dragY / unitBox.getHeight();
                centerX = centerX > 1 ? 1 : centerX < 0 ? 0 : centerX;
                centerY = centerY > 1 ? 1 : centerY < 0 ? 0 : centerY;
                unitBox.requestLayout();
                //unitBox.setTranslateX(-dragX);
                //unitBox.setTranslateY(-dragY);

            }
        }

        @Override
        protected void pressed(MouseEvent event) {
            cx = centerX;
            cy = centerY;
        }

    }

}
