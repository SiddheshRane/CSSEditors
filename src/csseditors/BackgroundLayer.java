package csseditors;

import drag.Draggable;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
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
 * A {@code BackgroundLayer} contains insets, corner radii and background color
 * for one of several fills in a Background.
 *
 * @author Siddhesh
 */
public class BackgroundLayer extends StackPane {

    private static final String CORNER_RADII_ARC = "cornerRadiiArc";
    private static final EventHandler<MouseEvent> MOUSE_HANDLER = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            //PENDING: Transfer InsetsDraggable code here
        }
    };

    /**
     * The {@code BackgroundFill} that is being edited. Users should bind to
     * this property to listen to changes
     */
    private final ObjectProperty<BackgroundFill> backgroundFill = new SimpleObjectProperty<>();
    private final ObjectProperty<Insets> backgroundInset = new SimpleObjectProperty<>();
    private final ObjectProperty<CornerRadii> backgroundRadii = new SimpleObjectProperty<>();
    private final ObjectProperty<Paint> backgroundPaint = new SimpleObjectProperty<>();
    Background current;
    Arc arcTL;
    Arc arcTR;
    Arc arcBR;
    Arc arcBL;

    private boolean updating;

    public BackgroundLayer() {
        this(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY);
    }

    public BackgroundLayer(Paint paint, CornerRadii radii, Insets insets) {
        this(new BackgroundFill(paint, radii, insets));
    }

    public BackgroundLayer(BackgroundFill fill) {
        initArcs();
        setSnapToPixel(false);
        setPickOnBounds(false);

        ChangeListener fillBinder = (ob, old, now) -> {
            if (!updating) {
                updating = true;
                if (ob == backgroundFill) {
                    setBackgroundPaint(backgroundFill.get().getFill());
                    setBackgroundInset(backgroundFill.get().getInsets());
                    setBackgroundRadii(backgroundFill.get().getRadii());
                } else {
                    Insets insets = getBackgroundFill().getInsets();
                    CornerRadii radii = getBackgroundFill().getRadii();
                    Paint paint = getBackgroundFill().getFill();
                    if (ob == backgroundPaint) {
                        setBackgroundFill(new BackgroundFill(getBackgroundPaint(), radii, insets));
                    } else if (ob == backgroundInset) {
                        setBackgroundFill(new BackgroundFill(paint, radii, getBackgroundInset()));
                    } else if (ob == backgroundRadii) {
                        setBackgroundFill(new BackgroundFill(paint, getBackgroundRadii(), insets));
                    }
                }
                updating = false;
            }
            updateBackgroundFill();
        };
        backgroundFill.addListener(fillBinder);
        backgroundPaint.addListener(fillBinder);
        backgroundInset.addListener(fillBinder);
        backgroundFill.set(fill);
        layoutBoundsProperty().addListener(o -> updateArcs());
    }

    public BackgroundFill getBackgroundFill() {
        return backgroundFill.get();
    }

    public void setBackgroundFill(BackgroundFill value) {
        backgroundFill.set(value);
    }

    public ObjectProperty<BackgroundFill> backgroundFillProperty() {
        return backgroundFill;
    }

    public Insets getBackgroundInset() {
        return backgroundInset.get();
    }

    public void setBackgroundInset(Insets value) {
        backgroundInset.set(value);
    }

    public ObjectProperty<Insets> backgroundInsetProperty() {
        return backgroundInset;
    }

    public CornerRadii getBackgroundRadii() {
        return backgroundRadii.get();
    }

    public void setBackgroundRadii(CornerRadii value) {
        backgroundRadii.set(value);
    }

    public ObjectProperty backgroundRadiiProperty() {
        return backgroundRadii;
    }

    public Paint getBackgroundPaint() {
        return backgroundPaint.get();
    }

    public void setBackgroundPaint(Paint value) {
        backgroundPaint.set(value);
    }

    public ObjectProperty<Paint> backgroundPaintProperty() {
        return backgroundPaint;
    }

    public void updateBackgroundFill() {
        setPadding(backgroundFill.get().getInsets());
        current = new Background(backgroundFill.get());
        setBackground(current);
        updateArcs();
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
        CornerRadii radii = CornerRadii.EMPTY;

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

        InsetsDraggable draggable = new InsetsDraggable();
        draggable.drag(arcTL);
        draggable.drag(arcTR);
        draggable.drag(arcBR);
        draggable.drag(arcBL);
        draggable.drag(this);
    }

    /**
     * Updates the arc sizes whenever the cornerRadii or the layout bounds of
     * the container change. Checks if the arcs are overlapping and resizes them
     * to fit without overlapping.
     */
    private void updateArcs() {
        Insets insets = backgroundFill.get().getInsets();
        CornerRadii radii = backgroundFill.get().getRadii();
        double w = (getWidth() - insets.getLeft() - insets.getRight());
        double h = (getHeight() - insets.getTop() - insets.getBottom());
        double mul = 1;

        //Firstly apply the radii specified in backgroundRadii to the arcs
        //for horizontal component mul = w for percentage values
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

        double top, bottom, f;
        //Check if these radii dont exceed the layout bounds
        //calculate the least value of f for horizontal component
        top = arcTL.getRadiusX() + arcTR.getRadiusX();
        bottom = arcBL.getRadiusX() + arcBR.getRadiusX();
        double larger = top > bottom ? top : bottom;
        double fx = w / larger;
        //Now calculate the least value of f for vertical component
        top = arcTL.getRadiusY() + arcBL.getRadiusY();
        bottom = arcTR.getRadiusY() + arcBR.getRadiusY();
        larger = top > bottom ? top : bottom;
        double fy = h / larger;
        //choose the smaller value between horizontal and vertical f
        f = fx < fy ? fx : fy;
        if (f < 1 && f > 0) {
            arcTL.setRadiusX(arcTL.getRadiusX() * f);
            arcTL.setRadiusY(arcTL.getRadiusY() * f);

            arcTR.setRadiusX(arcTR.getRadiusX() * f);
            arcTR.setRadiusY(arcTR.getRadiusY() * f);

            arcBR.setRadiusX(arcBR.getRadiusX() * f);
            arcBR.setRadiusY(arcBR.getRadiusY() * f);

            arcBL.setRadiusX(arcBL.getRadiusX() * f);
            arcBL.setRadiusY(arcBL.getRadiusY() * f);
        }
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
