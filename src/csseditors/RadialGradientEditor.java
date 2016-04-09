/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class RadialGradientEditor extends GradientEditor {

    private final DoubleProperty centerX = new SimpleDoubleProperty(0.5);

    private final DoubleProperty centerY = new SimpleDoubleProperty(0.5);

    private final DoubleProperty focus = new SimpleDoubleProperty(0);

    private final DoubleProperty radius = new SimpleDoubleProperty(0.5);

    private final DoubleProperty focusAngle = new SimpleDoubleProperty(90);

    Region rCenter, rFocus, rRadius;
    Line line;
    Ellipse ellipse;
    StackPane add;

    StackPane selectedStop;
    //the stop currently being hovered
    StackPane hoverPane;
    double ellipseOffset;
    double mouseX, mouseY;
    boolean showingEndPoints;

    private final ObjectProperty<RadialGradient> gradient = new SimpleObjectProperty<>();
    //<editor-fold defaultstate="collapsed" desc="controls">
    //TODO: Shift UI Controls to a separate class
    /* private void initUIControls() {
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
    
    }*/

 /* private void updateUIControls() {
    localChange = true;
    sRadius.setValue(r);
    sFocus.setValue(f);
    sFocusAngle.setValue(fangle);
    localChange = false;
    }
    
    InvalidationListener controlListener = (Observable observable) -> {
    if (!localChange) {
    r = sRadius.getValue();
    f = sFocus.getValue();
    fangle = sFocusAngle.getValue();
    if (selectedStop != null) {
    selectStop(null);
    }
    requestLayout();
    }
    };*/
//</editor-fold>
    EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {

        double cx, cy, stopcx, stopcy;
        double pressX, pressY;

        void moved(MouseEvent event) {
            if (selectedStop != null || event.getTarget() == hoverPane) {
                //dont do anything as a stop is pre selected
                return;
            } else if (hoverPane != null) {
                //mouse is not on hover pane so hide it
//                hoverPane.setVisible(false);
//                hoverPane.setMouseTransparent(true);
                hoverPane = null;
            }
            //set the ellipse to show the gradient ring corresponding to the mouseHandler point
            double offset = ellipseOffset = getMouseOffset(event.getX(), event.getY());
            double normalOffset = getNormalisedOffset(offset);
            ellipse.setStrokeWidth(1);
            //TODO: replace with binary search on stops with fuzzy match
            for (Stop observableStop : stopMap.values()) {
                double delta = normalOffset - observableStop.getOffset();
                if (Math.abs(delta) < 0.05) {
                    if (getCycleMethod() == CycleMethod.REFLECT && (int) (offset) % 2 == 1) {
                        delta = -delta;
                    }
                    offset -= delta;
                    StackPane p = stopMap.entrySet().stream().filter(entry -> entry.getValue() == observableStop).findFirst().map(Map.Entry::getKey).get();
                    double fx = stopLayoutX(0);
                    double fy = stopLayoutY(0);
                    if (offset != 0) {
                        /*If ellipseOffset is 0 then offset/ellipseOffset gives NaN which enters into layout of node and eraneously
                       matches with every event's target node */
                        fx = fx + offset / ellipseOffset * (event.getX() - fx);
                        fy = fy + offset / ellipseOffset * (event.getY() - fy);
                    }
                    p.relocate(fx - p.getWidth() / 2, fy - p.getHeight() / 2);
                    if (hoverPane == null) {
                        p.setVisible(true);
                        p.setMouseTransparent(false);
                    }
                    hoverPane = p;
                    ellipseOffset = offset;
                    ellipse.setStroke(observableStop.getColor().invert());
                    ellipse.setStrokeWidth(2);
                    break;
                }
            }
            requestLayout();
        }

        void dragged(MouseEvent event) {
            if (event.getTarget() == rFocus) {
                //rFocus changes focusAngle and focus
                double dx = event.getX() / getWidth() - getCenterX();
                double dy = event.getY() / getHeight() - getCenterY();
                setFocusAngle(Math.toDegrees(Math.atan2(dy, dx)));
                double f = Math.sqrt((dx * dx) + (dy * dy));
                f = f / getRadius();
                f = f > 1 ? 1 : f;
                setFocus(f);
            } else if (event.getTarget() == rRadius) {
                double dx = getCenterX() - event.getX() / getWidth();
                double dy = getCenterY() - event.getY() / getHeight();
                setFocusAngle(Math.toDegrees(Math.atan2(dy, dx)));
                double r = Math.sqrt((dx * dx) + (dy * dy));
                r = r > 1 ? 1 : r;
                setRadius(r);
            } else if (event.getTarget() instanceof StackPane) {
                //update the offset of the dragged stop
                StackPane pane = (StackPane) event.getTarget();
                Stop s = stopMap.get(pane);
                if (s == null) {
                    return;
                }
                ellipseOffset = getMouseOffset(event.getX(), event.getY());
                double offset = getNormalisedOffset(ellipseOffset);
                updateStop(pane, new Stop(offset, s.getColor()));
                pane.relocate(event.getX() - pane.getWidth() / 2, event.getY() - pane.getHeight() / 2);
            } else if (selectedStop != null) {
                //move the selected gradient ring around by modifying its focus and focusAngle
                double x = (stopcx + (event.getX() - pressX)) / getWidth() - getCenterX();
                double y = (stopcy + (event.getY() - pressY)) / getHeight() - getCenterY();
                double d = Math.sqrt(x * x + y * y) / getRadius();
                double t = 1 - ellipseOffset;
                double f = d / t;
                if (f < 0) {
                    //Make focus positive and compensate by shifting the focusAngle by 180deg
                    f = Math.abs(f);
                    y = -y;
                    x = -x;
                }
                f = f > 1 ? 1 : f;
                setFocusAngle(Math.toDegrees(Math.atan2(y, x)));
                setFocus(f);
                f = Math.abs(f);
                ellipse.setCenterX(stopLayoutX(f * ellipseOffset / (1 + f)));
                ellipse.setCenterY(stopLayoutY(f * ellipseOffset / (1 + f)));
                ellipse.setRadiusX(getRadius() * ellipseOffset * getWidth());
                ellipse.setRadiusY(getRadius() * ellipseOffset * getHeight());

//                selectedStop.setVisible(false);
            } else { //drag center
                double ncx = cx + (event.getX() - pressX) / getWidth();
                double ncy = cy + (event.getY() - pressY) / getHeight();
                setCenterX(ncx > 1 ? 1 : ncx < 0 ? 0 : ncx);
                setCenterY(ncy > 1 ? 1 : ncy < 0 ? 0 : ncy);
            }
        }

        void click(MouseEvent event) {
            //check for right click(context menu)
            if (event.getButton() == MouseButton.SECONDARY) {
                selectedStop = null;
                showingEndPoints = !showingEndPoints;
                showEndPoints(showingEndPoints);
            } else if (showingEndPoints) {//do nothing
            } else if (event.getTarget() instanceof StackPane) {
                //If the mouseHandler is clicked on an existing stop then make it the current selection
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

        @Override
        public void handle(MouseEvent event) {
            final EventType<? extends MouseEvent> type = event.getEventType();
            if (type == MouseEvent.MOUSE_MOVED) {
                moved(event);
            } else if (type == MouseEvent.MOUSE_DRAGGED) {
                dragged(event);
            } else if (type == MouseEvent.MOUSE_PRESSED) {
                pressX = event.getX();
                pressY = event.getY();
                cx = getCenterX();
                cy = getCenterY();
                double f = Math.abs(getFocus());
                stopcx = stopLayoutX(f * ellipseOffset / (1 + f));
                stopcy = stopLayoutY(f * ellipseOffset / (1 + f));
            } else if (type == MouseEvent.MOUSE_CLICKED) {
                click(event);
            }
        }

    };

    public RadialGradientEditor() {
        rCenter = new Region();
        rFocus = new Region();
        rRadius = new Region();
        line = new Line();
        ellipse = new Ellipse();
        add = new StackPane();
//        cycleMethodBox = new ComboBox<>(FXCollections.observableArrayList(CycleMethod.values()));

//css styles
        setPrefSize(200, 200);
        getStylesheets().add("/csseditors/gradients.css");
        getStyleClass().add("lge");
        rCenter.getStyleClass().add("center");
        rFocus.getStyleClass().add("focus");
        rRadius.getStyleClass().add("radius");
        line.getStyleClass().add("line");
        add.getStyleClass().add("add");
//set clip so that the ellipse does not go outside this pane
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(widthProperty());
        clip.heightProperty().bind(heightProperty());
        setClip(clip);
//event handlers
        InvalidationListener redraw = o -> {
            updateGradient();
            layoutChildren();
        };
        radius.addListener(redraw);
        focus.addListener(redraw);
        centerX.addListener(redraw);
        centerY.addListener(redraw);
        focusAngle.addListener(redraw);
        cycleMethodProperty().addListener(redraw);
        proportionalProperty().addListener(redraw);
        setOnMouseClicked(mouseHandler);
        setOnMouseMoved(mouseHandler);
        setOnMouseDragged(mouseHandler);
        setOnMousePressed(mouseHandler);
//        addEventHandler(MouseEvent.MOUSE_MOVED, mouseHandler);

        getChildren().addAll(line, ellipse, add, rCenter, rFocus, rRadius);

        addStop(new Stop(0, Color.ALICEBLUE));
        addStop(new Stop(0.5, Color.SALMON));
        addStop(new Stop(1, Color.DARKRED));

        showEndPoints(false);
//        rCenter.layoutXProperty().bind(centerX.multiply(widthProperty()));
//        rCenter.layoutYProperty().bind(centerY.multiply(heightProperty()));
        ellipse.setStroke(Color.RED);
        ellipse.setFill(Color.TRANSPARENT);
        ellipse.setMouseTransparent(true);
        //FIXME: remove these ugly hacks later
        setOnMouseExited(e -> {
            ellipse.setOpacity(0);
        });
        setOnMouseEntered(ev -> ellipse.setOpacity(1));
    }

    public double getCenterX() {
        return centerX.get();
    }

    public void setCenterX(double value) {
        centerX.set(value);
    }

    public DoubleProperty centerXProperty() {
        return centerX;
    }

    public double getCenterY() {
        return centerY.get();
    }

    public void setCenterY(double value) {
        centerY.set(value);
    }

    public DoubleProperty centerYProperty() {
        return centerY;
    }

    public double getFocus() {
        return focus.get();
    }

    public void setFocus(double value) {
        focus.set(value);
    }

    public DoubleProperty focusProperty() {
        return focus;
    }

    public double getRadius() {
        return radius.get();
    }

    public void setRadius(double value) {
        radius.set(value);
    }

    public DoubleProperty radiusProperty() {
        return radius;
    }

    public double getFocusAngle() {
        return focusAngle.get();
    }

    public void setFocusAngle(double value) {
        focusAngle.set(value);
    }

    public DoubleProperty focusAngleProperty() {
        return focusAngle;
    }

    public RadialGradient getGradient() {
        return gradient.get();
    }

    public void setGradient(RadialGradient value) {
        gradient.set(value);
    }

    public ObjectProperty<RadialGradient> gradientProperty() {
        return gradient;
    }

    //<editor-fold defaultstate="collapsed" desc="stopLayout">
    @Override
    public double stopLayoutX(double t) {
        //NOTE: this assumes a proportional gradient
        //TODO: modify stopLayoutX to handle fixed width gradient
        double r = getRadius() * getWidth();
        double cos = Math.cos(Math.toRadians(getFocusAngle()));
        double fx = r * getFocus() * cos + getCenterX() * getWidth();
        double deltax = r * (1 + Math.abs(getFocus())) * cos;
        deltax = getFocus() < 0 ? deltax : -deltax;

        double x = fx + t * deltax;
        return x;
    }

    @Override
    public double stopLayoutY(double t) {
        //NOTE: this assumes a proportional gradient
        double r = getRadius() * getHeight();
        double sin = Math.sin(Math.toRadians(getFocusAngle()));
        double fy = r * getFocus() * sin + getCenterY() * getHeight();
        double deltay = r * (1 + Math.abs(getFocus())) * sin;
        deltay = getFocus() < 0 ? deltay : -deltay;

        double y = fy + t * deltay;
        return y;
    }
//</editor-fold>

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        //TODO: this assumes proportional gradient. Rework it to support absolute layout
        rCenter.relocate(getCenterX() * getWidth() - rCenter.getWidth() / 2,
                getCenterY() * getHeight() - rCenter.getHeight() / 2);
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
        double f = Math.abs(getFocus());
        ellipse.setCenterX(stopLayoutX(f * ellipseOffset / (1 + f)));
        ellipse.setCenterY(stopLayoutY(f * ellipseOffset / (1 + f)));
        ellipse.setRadiusX(getRadius() * ellipseOffset * getWidth());
        ellipse.setRadiusY(getRadius() * ellipseOffset * getHeight());
        add.relocate(
                stopLayoutX(f * ellipseOffset / (1 + f)) - add.getWidth() / 2,
                stopLayoutY(f * ellipseOffset / (1 + f)) - add.getHeight() / 2
        );
        for (Map.Entry<StackPane, Stop> entry : stopMap.entrySet()) {
            StackPane pane = entry.getKey();
            Stop stop = entry.getValue();
            if (pane != hoverPane) {
                layoutStop(pane, stop);
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="getOffset">
    /**
     *
     * @param mx mouseHandler x coordinate in local bounds of unitBox
     * @param my mouseHandler y coordinate in local bounds of unitBox
     * @return a double with fractional part that indicates the Stop offset and
     * integer part that represents the ring no if the pattern repeated or
     * reflected
     */
    private double getMouseOffset(double mx, double my) {
        double fx = stopLayoutX(0); //focus x
        double fy = stopLayoutY(0); //focus y

        //NOTE: this assumes a proportional gradient
        double cx = getCenterX() * getWidth();
        double cy = getCenterY() * getHeight();
        double rx = getRadius() * getWidth();
        double ry = getRadius() * getHeight();

        double mfx = (mx - fx) / rx;
        double mfy = (my - fy) / ry;
        double fcx = (fx - cx) / rx;
        double fcy = (fy - cy) / ry;

        double A = (mfx * mfx) + (mfy * mfy);
        double B = 2 * (mfx * fcx + mfy * fcy);
        double C = (fcx * fcx) + (fcy * fcy) - 1;
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
            switch (getCycleMethod()) {
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
    public double getOffset(double mx, double my) {
        double D = getMouseOffset(mx, my);
        return getNormalisedOffset(D);
    }
//</editor-fold>

    @Override
    public void updateGradient() {
        RadialGradient rg = new RadialGradient(
                getFocusAngle(), getFocus(), getCenterX(), getCenterY(), getRadius(),
                isProportional(),
                getCycleMethod(),
                getStops()
        );
        gradient.set(rg);
        setBackground(new Background(new BackgroundFill(rg, CornerRadii.EMPTY, Insets.EMPTY)));
        layoutStops();
    }

    public void showEndPoints(boolean activate) {
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

    public void selectStop(StackPane p) {
        if (selectedStop == p) {
            //deselect the stop
//            selectedStop.setVisible(false);
//            selectedStop.setMouseTransparent(true);
            selectedStop = null;
            ellipseOffset = -1;
        } else {
            if (selectedStop != null) {
//                selectedStop.setVisible(false);
//                selectedStop.setMouseTransparent(true);
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

}
