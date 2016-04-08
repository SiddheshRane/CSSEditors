/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * A GUI component for editing {@link LinearGradient} paint.
 *
 * @author Siddhesh
 */
public class LinearGradientEditor extends GradientEditor {

    //correspond to respective quantities in LinearGradient
    private final DoubleProperty startX = new SimpleDoubleProperty(0);
    private final DoubleProperty startY = new SimpleDoubleProperty(0);
    private final DoubleProperty endX = new SimpleDoubleProperty(1);
    private final DoubleProperty endY = new SimpleDoubleProperty(1);
    //control for setting startX , startY , endX , endY in LinearGradient
    Region start;
    Region end;
    //Line passes through all stops to show the direction of gradient
    Line line;
    //Cycle Method comboBox
    ComboBox<CycleMethod> cycleMethodBox;
    private boolean showingEndPoints = false;
    /* **************** *
    *    PROPERTIES    *
    * **************** */
    private final ObjectProperty<LinearGradient> gradient = new SimpleObjectProperty<>();
    EventHandler<MouseEvent> onClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            //check for right click(context menu)
            if (event.getButton() == MouseButton.SECONDARY) {
                showingEndPoints = !showingEndPoints;
                enableEndPoints(showingEndPoints);
            } else if (showingEndPoints) {
            } else if (event.getTarget() instanceof StackPane) {
                //If the mouse is clicked on an existing stop then make it the current selection
                StackPane p = (StackPane) event.getTarget();
                if (stopMap.containsKey(p)) {
                    //   listView.getSelectionModel().select(stopMap.get(p));
                }
            } else {
                //Mouse is clicked on empty region.So add a new stop at the particular offset.
                double offset = getOffset(event.getX(), event.getY());
                addStop(new Stop(offset, Color.WHITESMOKE));
            }
        }
    };
    EventHandler<MouseEvent> onDrag = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.getTarget() == start || event.getTarget() == end) {
                //start or end have been dragged.
                //These correspond to startX, startY ,endX,endY in LG.
                Insets p = getPadding();
                //TODO:This assumes proportional gradient. Modify it to accept absolute values
                double x = (event.getX() - p.getLeft()) / (getWidth() - p.getLeft() - p.getRight());
                
                if (x > 1) {
                    x = 1;
                } else if (x < 0) {
                    x = 0;
                }
                //TODO:This assumes proportional gradient. Modify it to accept absolute values
                double y = (event.getY() - p.getTop()) / (getHeight() - p.getTop() - p.getBottom());
                if (y > 1) {
                    y = 1;
                } else if (y < 0) {
                    y = 0;
                }
                
                if (event.getTarget() == start) {
                    setStartX(x);
                    setStartY(y);
                } else if (event.getTarget() == end) {
                    setEndX(x);
                    setEndY(y);
                }
                
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
            }
        }
    };
    public LinearGradientEditor() {
        //initialise
        start = new Region();
        end = new Region();
        line = new Line();
        cycleMethodBox = new ComboBox<>(FXCollections.observableArrayList(CycleMethod.values()));
        
        cycleMethodBox.valueProperty().bindBidirectional(cycleMethodProperty());
        cycleMethodBox.setVisibleRowCount(3);
        cycleMethodBox.getStyleClass().add("cycle-method");
        
        //css styles
        getStylesheets().add("/csseditors/gradients.css");
        getStyleClass().add("lge");
        setPrefSize(200, 400);
        
        start.getStyleClass().add("start");
        end.getStyleClass().add("end");
        line.getStyleClass().add("line");
        
        Text t = new Text("CycleMethod");
        HBox h = new HBox(t, cycleMethodBox);
        h.getStyleClass().add("cycle-method-hbox");
        t.getStyleClass().add("cycle-method-label");
        getChildren().add(h);
        getChildren().addAll(line, start, end);
        addEventHandler(MouseEvent.MOUSE_CLICKED, onClick);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, onDrag);
        
        addStop(new Stop(0, Color.ALICEBLUE));
        addStop(new Stop(.3, Color.SKYBLUE));
        addStop(new Stop(0.8, Color.STEELBLUE));
        
    }

    public double getStartX() {
        return startX.get();
    }

    public void setStartX(double value) {
        startX.set(value);
    }

    public DoubleProperty startXProperty() {
        return startX;
    }


    public double getStartY() {
        return startY.get();
    }

    public void setStartY(double value) {
        startY.set(value);
    }

    public DoubleProperty startYProperty() {
        return startY;
    }

    public double getEndX() {
        return endX.get();
    }

    public void setEndX(double value) {
        endX.set(value);
    }

    public DoubleProperty endXProperty() {
        return endX;
    }

    public double getEndY() {
        return endY.get();
    }

    public void setEndY(double value) {
        endY.set(value);
    }

    public DoubleProperty endYProperty() {
        return endY;
    }
 


    public LinearGradient getGradient() {
        return gradient.get();
    }

    public void setGradient(LinearGradient value) {
        gradient.set(value);
    }

    public ObjectProperty gradientProperty() {
        return gradient;
    }


    @Override
    public double stopLayoutX(double t) {
        //works only for proportional gradient
        Insets pad = getPadding();
        double x = (getWidth() - pad.getLeft() - pad.getRight())
                * (getStartX() + t * (getEndX() - getStartX()))
                + pad.getLeft();
        return x;
    }

    @Override
    public double stopLayoutY(double t) {
        //works only for proportional gradient
        Insets pad = getPadding();
        double y = (getHeight() - pad.getTop() - pad.getBottom())
                * (getStartY() + t * (getEndY() - getStartY()))
                + pad.getTop();
        return y;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        start.relocate(stopLayoutX(0) - start.getWidth() / 2, stopLayoutY(0) - start.getHeight() / 2);
        end.relocate(stopLayoutX(1) - end.getWidth() / 2, stopLayoutY(1) - end.getHeight() / 2);
        line.setStartX(stopLayoutX(0));
        line.setStartY(stopLayoutY(0));
        line.setEndX(stopLayoutX(1));
        line.setEndY(stopLayoutY(1));
        layoutStops();
        //TODO: Dont call update gradient in layoutChildren. It recomputes the gradient unnecessarily
        updateGradient();
    }

    /**
     * Calculates the stop offset at a particular coordinate in the layout
     * bounds of unitBox. Calculates the offset at the projection of the point
     * <code>(mx,my)</code> onto the line joining the ends of the linear
     * gradient.
     *
     * @param mx the layout x of mouse pointer in unitBox
     * @param my the layout y of mouse pointer in unitBox
     * @return the offset at the projection of (mx,my) onto the linear gradient
     * stops line.
     */
    @Override
    public double getOffset(double mx, double my) {
        double m = (getEndY() - getStartY()) / (getEndX() - getStartX()) * getHeight() / getWidth();
        double offset;
        Insets p = getPadding();

        if (m == 0) {
            offset = (mx - p.getLeft()) / (getWidth() - p.getLeft() - p.getRight());
            if (getStartX() > getEndX()) {
                offset = 1 - offset;
            }
        } else if (m == Double.POSITIVE_INFINITY) {
            offset = (my - p.getTop()) / (getHeight() - p.getTop() - p.getBottom());
        } else if (m == Double.NEGATIVE_INFINITY) {
            offset = 1 - (my - p.getTop()) / (getHeight() - p.getTop() - p.getBottom());
        } else {
            double x1 = stopLayoutX(0);
            double y1 = stopLayoutY(0);
            double x = my - y1;
            x += m * x1;
            x += mx / m;
            x /= m + 1 / m;

            offset = x - x1;
            offset /= (getEndX() - getStartX())
                    * (getWidth()
                    - getPadding().getLeft()
                    - getPadding().getRight());
        }

        if (offset > 1) {
            offset = 1;
        } else if (offset < 0) {
            offset = 0;
        }
        return offset;
    }

    @Override
    public void updateGradient() {
        LinearGradient lg = new LinearGradient(getStartX(), getStartY(), getEndX(), getEndY(),isProportional() , getCycleMethod(), getStops());
        gradient.set(lg);
        setBackground(new Background(new BackgroundFill(lg, CornerRadii.EMPTY, getPadding())));

    }

    private void enableEndPoints(boolean activate) {

        for (StackPane p : stopMap.keySet()) {
            p.setDisable(activate);
        }
        start.toFront();
        end.toFront();
        start.setVisible(activate);
        end.setVisible(activate);
        start.setMouseTransparent(!activate);
        end.setMouseTransparent(!activate);
        line.setDisable(activate);
    }


    //<editor-fold defaultstate="collapsed" desc="LGEControls">
    class LGEControls extends TabPane {

        @FXML
        private ComboBox<CycleMethod> cycleMethodField;

        @FXML
        private TextField startXField;

        @FXML
        private TextField startYField;

        @FXML
        private TextField endYField;

        @FXML
        private TextField endXField;

        @FXML
        private CheckBox proportionalField;

        @FXML
        private GridPane grid;

        public LGEControls() {
            initFXMLControls();

        }

        private void initFXMLControls() {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/csseditors/lge.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            try {
                loader.load();
            } catch (Exception e) {
                System.err.println("error loading " + e);
                return;
            }
            cycleMethodField.setItems(FXCollections.observableArrayList(CycleMethod.values()));
            cycleMethodField.valueProperty().bindBidirectional(cycleMethodProperty());
            cycleMethodField.getSelectionModel().selectFirst();

            proportionalField.selectedProperty().bindBidirectional(proportionalProperty());

            Tab stopTab = new Tab("Stops");
            //stopTab.setContent(listView);
            getTabs().add(stopTab);
            LinearGradientEditor.this.getChildren().add(this);

            //test
            DoubleField df = new DoubleField(0, 0, 1);
            df.setStyle("-fx-border-color : blue");
            grid.add(df, 1, 2);

        }

    }
}


//</editor-fold>
