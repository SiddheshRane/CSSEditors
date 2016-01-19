/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import java.util.Comparator;
import java.util.Map;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

/**
 * An abstract superclass for GUI components that edit gradient colors
 * containing {@link javafx.scene.paint.Stop}s. Provides methods for adding
 * ,deleting and updating {@link Stop}s.
 *
 * @author Siddhesh
 */
public abstract class GradientEditor extends VBox {

    //maps Stops to their visual nodes
    ObservableMap<StackPane, Stop> stopMap;
    //List of Stops
    ObservableList<Stop> observableStops;
    //list of StackPane nodes used to visually represent Stops
    ObservableList<StackPane> observableStacks;
    /*
     A list of Stop objects sorted according to their offset in ascending order.
     It is used to set data model for stopList and to construct the final
     Gradient.
     */
    SortedList<Stop> sortedStops;

    /*
     * UI COMPONENTS
     */
    //A ListView showing the list of stops in ascending order of their offset
    ListView<Stop> stopList;

    //Square shape holder for Gradient preview and editor
    protected Pane unitBox = new Pane() {
        {
            getStyleClass().add("unit-box");
            setSnapToPixel(true);
            setMinSize(150, 150);
        }

        @Override
        public Orientation getContentBias() {
            return Orientation.HORIZONTAL;
        }

        @Override
        protected double computePrefHeight(double width) {
            return width;
        }

        @Override
        protected double computeMaxHeight(double width) {
            return width;
        }

        @Override
        protected void layoutChildren() {
            super.layoutChildren();
            layoutUnitBoxContents();
        }
    };

    protected final ObjectProperty<CycleMethod> cycleMethod = new SimpleObjectProperty<>(CycleMethod.NO_CYCLE);
    protected final BooleanProperty proportional = new SimpleBooleanProperty(true);

    public GradientEditor() {
        //initialise
        stopMap = FXCollections.observableHashMap();
        observableStops = FXCollections.observableArrayList();
        observableStacks = FXCollections.observableArrayList();
        sortedStops = observableStops.sorted(Comparator.comparingDouble(Stop::getOffset));
        stopList = new ListView<>();

        stopMap.addListener((Observable ob) -> {
            observableStops.setAll(stopMap.values());
            observableStacks.setAll(stopMap.keySet());

        });
        stopList.setItems(sortedStops);
        stopList.setEditable(true);
        stopList.setPrefHeight(120);
        stopList.setMinHeight(100);
        stopList.setCellFactory((lv) -> {
            return new StopCell();
        });
        stopList.setOnEditCommit((ListView.EditEvent<Stop> b) -> {
            int i = sortedStops.getSourceIndex(b.getIndex());
            StackPane key = observableStacks.get(i);
            if (stopMap.containsKey(key)) {
                if (b.getNewValue() == null) {
                    //delete stop
                    deleteStop(key);
                } else {
                    updateStop(key, b.getNewValue());
                }
            }
        });
        stopList.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            System.out.println("ev : " + e.getEventType());
            if (e.getCode() == KeyCode.DELETE) {
                Stop stop = stopList.getSelectionModel().getSelectedItem();
                if (stop != null) {
                    int index = stopList.getSelectionModel().getSelectedIndex();
                    int i = sortedStops.getSourceIndex(index);
                    StackPane key = observableStacks.get(i);
                    deleteStop(key);
                }
            }
        });

        setFillWidth(true);
        VBox.setVgrow(stopList, Priority.SOMETIMES);
        getChildren().addAll(unitBox, stopList);

        cycleMethod.addListener((o) -> {
            updatePreview();
        });

        addEventFilter(MouseEvent.MOUSE_PRESSED, clickFilter);
        addEventFilter(MouseEvent.MOUSE_DRAGGED, clickFilter);
        addEventFilter(MouseEvent.MOUSE_CLICKED, clickFilter);

    }

    public CycleMethod getCycleMethod() {
        return cycleMethod.get();
    }

    public void setCycleMethod(CycleMethod value) {
        cycleMethod.set(value);
    }

    public ObjectProperty cycleMethodProperty() {
        return cycleMethod;
    }

    public boolean isProportional() {
        return proportional.get();
    }

    public void setProportional(boolean value) {
        proportional.set(value);
    }

    public BooleanProperty proportionalProperty() {
        return proportional;
    }

    protected StackPane addStop(Stop s) {
        StackPane stopMark = new StackPane();
        stopMark.getStyleClass().add("stop");
        stopMark.setBackground(new Background(new BackgroundFill(s.getColor(), new CornerRadii(50, true), Insets.EMPTY)));
        stopMap.put(stopMark, s);
        unitBox.getChildren().add(stopMark);
        return stopMark;
    }

    public void updateStop(StackPane p, Stop s) {
        stopMap.replace(p, s);
        p.setBackground(new Background(new BackgroundFill(s.getColor(), new CornerRadii(50, true), Insets.EMPTY)));
        layoutStop(p, s);
        unitBox.requestLayout();
    }

    public void deleteStop(StackPane p) {
        stopMap.remove(p);
        unitBox.getChildren().remove(p);
    }

    protected void layoutStop(StackPane p, Stop s) {
        double t = s.getOffset();
        double x = stopLayoutX(t) - p.getWidth() / 2;
        double y = stopLayoutY(t) - p.getHeight() / 2;
        p.relocate(x, y);
    }

    protected void layoutStops() {
        for (Map.Entry<StackPane, Stop> entry : stopMap.entrySet()) {
            StackPane stackPane = entry.getKey();
            Stop stop = entry.getValue();
            layoutStop(stackPane, stop);
        }
    }

    protected abstract double stopLayoutX(double t);

    protected abstract double stopLayoutY(double t);

    protected abstract void layoutUnitBoxContents();

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        unitBox.resizeRelocate((getWidth() - unitBox.getHeight()) / 2, unitBox.getLayoutY(), unitBox.getHeight(), unitBox.getHeight());
    }

    /**
     * Calculates the stop offset at a particular coordinate in the layout
     * bounds of unitBox. Calculates the offset at the projection of the point
     * <code>(mx,my)</code> onto the line joining the ends of the gradient.
     *
     * @param mx the layout x of mouse pointer in unitBox
     * @param my the layout y of mouse pointer in unitBox
     * @return the offset at the projection of ( mx , my ) onto the gradient
     * stops line.
     */
    protected abstract double getOffset(double mx, double my);

    public abstract void updatePreview();

    //Mouse Event Filtering to remove onClick event after mouse gets dragged
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
}
