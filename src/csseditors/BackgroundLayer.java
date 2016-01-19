package csseditors;

import drag.Draggable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

/**
 *
 * @author Siddhesh
 */
public class BackgroundLayer extends StackPane {

    /*
     Currently this class uses 3 different ObjectProperty for Insets ,Paint 
     and CornerRadii of a BackgroundFill.However any change to these properies 
     that is not made using the coresponding setter functions of this class will 
     not result in updation of the BackgroundFill. 
    
     The class is currently in transition phase.
     The 3 properties will be replaced with a single ObjectProperty<BackgroundFill>
    
     Transition Status: COMPLETE
     */
    private static final String CORNER_RADII_ARC = "cornerRadiiArc";
    
    private final ObjectProperty<BackgroundFill> backgroundFill = new SimpleObjectProperty<>();

    public BackgroundFill getBackgroundFill() {
        return backgroundFill.get();
    }

    public void setBackgroundFill(BackgroundFill value) {
        backgroundFill.set(value);
    }

    public ObjectProperty<BackgroundFill> backgroundFillProperty() {
        return backgroundFill;
    }

    Background original;
    Background current;

    Arc arcTL, arcTR, arcBR, arcBL;

    public BackgroundLayer() {
        this(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY);

    }

    public BackgroundLayer(Paint paint, CornerRadii radii, Insets insets) {
        this(new BackgroundFill(paint, radii, insets));
    }

    public BackgroundLayer(BackgroundFill fill) {
        backgroundFill.set(fill);
        backgroundFill.addListener(
                (ObservableValue<? extends BackgroundFill> observable,
                        BackgroundFill oldValue,
                        BackgroundFill newValue) -> {
                    updateBackgroundFill();
                });

        initArcs();
        setSnapToPixel(false);
        original = current;
    }

    public void updateBackgroundFill() {

        setPadding(backgroundFill.get().getInsets());
        current = new Background(backgroundFill.get());
        setBackground(current);
        checkCurveOverlap(getWidth(), getHeight());
    }

    public void showBackground(boolean b) {
        if (b) {
            setBackground(current);
        } else {
            setBackground(Background.EMPTY);
        }
    }

    public void showToggles(boolean b) {
        arcTL.setVisible(b);
        arcTR.setVisible(b);
        arcBR.setVisible(b);
        arcBL.setVisible(b);
    }

    public void outlineBackground(boolean b) {
        if (b) {
            setOpacity(0.3);

        } else {
            setOpacity(1);
        }
        showBackground(!b);
        setDisabled(b);
    }

    private void initArcs() {
        //top-left arc
        CornerRadii radii = backgroundFill.get().getRadii();

        double hr = radii.getTopLeftHorizontalRadius();
        double vr = radii.getTopLeftVerticalRadius();

        arcTL = new Arc(0, 0, hr, vr, 90, 90);
        arcTL.setType(ArcType.ROUND);
        arcTL.getStyleClass().add(CORNER_RADII_ARC);

        getChildren().add(arcTL);
        setAlignment(arcTL, Pos.TOP_LEFT);

        //top-right arc
        hr = radii.getTopRightHorizontalRadius();
        vr = radii.getTopRightVerticalRadius();
        arcTR = new Arc(0, 0, hr, vr, 0, 90);
        arcTR.setType(ArcType.ROUND);
        arcTR.getStyleClass().add(CORNER_RADII_ARC);

        getChildren().add(arcTR);
        setAlignment(arcTR, Pos.TOP_RIGHT);

        //bottom-right arc
        hr = radii.getBottomRightHorizontalRadius();
        vr = radii.getBottomRightVerticalRadius();
        arcBR = new Arc(0, 0, hr, vr, 270, 90);
        arcBR.setType(ArcType.ROUND);
        arcBR.getStyleClass().add(CORNER_RADII_ARC);

        getChildren().add(arcBR);
        setAlignment(arcBR, Pos.BOTTOM_RIGHT);

        //bottom-left arc
        hr = radii.getBottomLeftHorizontalRadius();
        vr = radii.getBottomLeftVerticalRadius();
        arcBL = new Arc(0, 0, hr, vr, 180, 90);
        arcBL.setType(ArcType.ROUND);
        arcBL.getStyleClass().add(CORNER_RADII_ARC);

        getChildren().add(arcBL);
        setAlignment(arcBL, Pos.BOTTOM_LEFT);

        //experimental
        InsetsDraggable draggable = new InsetsDraggable();
        draggable.drag(arcTL);
        draggable.drag(arcTR);
        draggable.drag(arcBR);
        draggable.drag(arcBL);
        draggable.drag(this);
    }

    private void resizeArcs(double f) {
        CornerRadii radii = backgroundFill.get().getRadii();
        arcTL.setRadiusX(arcTL.getRadiusX() * f);
        arcTL.setRadiusY(arcTL.getRadiusY() * f);

        arcTR.setRadiusX(arcTR.getRadiusX() * f);
        arcTR.setRadiusY(arcTR.getRadiusY() * f);

        arcBR.setRadiusX(arcBR.getRadiusX() * f);
        arcBR.setRadiusY(arcBR.getRadiusY() * f);

        arcBL.setRadiusX(arcBL.getRadiusX() * f);
        arcBL.setRadiusY(arcBL.getRadiusY() * f);

    }

    private void checkCurveOverlap(double w, double h) {
        Insets insets = backgroundFill.get().getInsets();
        CornerRadii radii = backgroundFill.get().getRadii();

        w = (w - insets.getLeft() - insets.getRight());
        h = (h - insets.getTop() - insets.getBottom());
        double top, bottom, f = 1, mul = 1;

        //Firstly apply the radii specified in backgroundRadii to the arcs
        //for horizontal component m = w for percentage values
        if (radii.isTopLeftHorizontalRadiusAsPercentage()) {
            mul = w;
        }
        arcTL.setRadiusX(radii.getTopLeftHorizontalRadius() * mul);
        mul = 1;
        if (radii.isTopRightHorizontalRadiusAsPercentage()) {
            mul = w;
        }
        arcTR.setRadiusX(radii.getTopRightHorizontalRadius() * mul);
        mul = 1;
        if (radii.isBottomRightHorizontalRadiusAsPercentage()) {
            mul = w;
        }
        arcBR.setRadiusX(radii.getBottomRightHorizontalRadius() * mul);
        mul = 1;
        if (radii.isBottomLeftHorizontalRadiusAsPercentage()) {
            mul = w;
        }
        arcBL.setRadiusX(radii.getBottomLeftHorizontalRadius() * mul);
        mul = 1;

        //for vertical components m = h for percentage values
        if (radii.isTopLeftVerticalRadiusAsPercentage()) {
            mul = h;
        }
        arcTL.setRadiusY(radii.getTopLeftVerticalRadius() * mul);
        mul = 1;

        if (radii.isTopRightVerticalRadiusAsPercentage()) {
            mul = h;
        }
        arcTR.setRadiusY(radii.getTopRightVerticalRadius() * mul);
        mul = 1;
        if (radii.isBottomRightVerticalRadiusAsPercentage()) {
            mul = h;
        }
        arcBR.setRadiusY(radii.getBottomRightVerticalRadius() * mul);
        mul = 1;

        if (radii.isBottomLeftVerticalRadiusAsPercentage()) {
            mul = h;
        }
        arcBL.setRadiusY(radii.getBottomLeftVerticalRadius() * mul);

        //Check if these radii dont exceed the layout bounds
        //calculate the least value of f for horizontal component
        top = arcTL.getRadiusX() + arcTR.getRadiusX();
        bottom = arcBL.getRadiusX() + arcBR.getRadiusX();
        top = top > bottom ? top : bottom;
        bottom = w / top;
        if (bottom < 1) {
            f = bottom;
        }
        //Now calculate the least value of f for vertical component
        top = arcTL.getRadiusY() + arcBL.getRadiusY();
        bottom = arcTR.getRadiusY() + arcBR.getRadiusY();
        top = top > bottom ? top : bottom;
        bottom = h / top;
        //choose the smaller value between horizontal and vertical f
        f = f < bottom ? f : bottom;
        if (f < 1 && f > 0) {
            resizeArcs(f);
        }
    }

    @Override
    protected void layoutChildren() {
        checkCurveOverlap(getWidth(), getHeight());
        super.layoutChildren();
    }

    private final class InsetsDraggable extends Draggable {

        double minX, maxX, minY, maxY;
        double t, r, b, l;

        public InsetsDraggable() {

        }

        public InsetsDraggable(double minX, double maxX, double minY, double maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        @Override
        protected void dragged(MouseEvent event) {
            Insets cur = null;
            if (targetNode == arcTL) {
                cur = new Insets(t + dragY, r, b, l + dragX);
            } else if (targetNode == arcTR) {
                cur = new Insets(t + dragY, r - dragX, b, l);
            } else if (targetNode == arcBR) {
                cur = new Insets(t, r - dragX, b - dragY, l);
            } else if (targetNode == arcBL) {
                cur = new Insets(t, r, b - dragY, l + dragX);
            } else if (targetNode == BackgroundLayer.this) {
                cur = new Insets(t + dragY, r - dragX, b - dragY, l + dragX);
            }

            if (cur != null) {
                setBackgroundFill(new BackgroundFill(
                        backgroundFill.get().getFill(),
                        backgroundFill.get().getRadii(),
                        cur));
            }
        }

        @Override
        protected void pressed(MouseEvent event) {
            Insets insets = backgroundFill.get().getInsets();
            t = insets.getTop();
            r = insets.getRight();
            b = insets.getBottom();
            l = insets.getLeft();

        }

    }
}
