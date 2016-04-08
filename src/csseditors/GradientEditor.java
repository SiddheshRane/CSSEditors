/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import java.util.Comparator;
import java.util.HashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

/**
 * A container for layout of visual nodes representing
 * {@link javafx.scene.paint.Stop}s in gradient colors. Provides methods for
 * adding, deleting, updating and sorting Stops. The sorted stops can directly
 * be used in either {@link  LinearGradient} or {@link RadialGradient}
 *
 * @author Siddhesh
 */
public abstract class GradientEditor extends Pane {
     static final CornerRadii ROUND = new CornerRadii(50, true);
    private static final Comparator<Stop> STOP_COMPARATOR = Comparator.comparingDouble(Stop::getOffset);

    //maps Stops to their visual nodes
    protected HashMap<StackPane, Stop> stopMap;
    private ObservableList<Stop> stops;
    protected final ObjectProperty<CycleMethod> cycleMethod = new SimpleObjectProperty<>(CycleMethod.NO_CYCLE);
    protected final BooleanProperty proportional = new SimpleBooleanProperty(true);

    SelectionModel<Stop> stopSelection;
    //Mouse Event Filtering to remove onClick event after mouse gets dragged
    boolean dragged;
    EventHandler<MouseEvent> clickFilter = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
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
        }
    };
    public GradientEditor() {
        stopMap = new HashMap<>(7);
        stops = FXCollections.observableArrayList();
        stops.addListener(new ListChangeListener<Stop>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Stop> c) {
                boolean sort =false;
                while (c.next()) {
                    if (c.wasReplaced()) {
                        //check whether the replaced Stop needs sorting
                        Stop old = c.getRemoved().get(0);
                        Stop now = stops.get(c.getFrom());
                        int index = c.getFrom();
                        double newOffset = now.getOffset();
                        double floorOffset = index == 0 ? -1 : stops.get(index - 1).getOffset();
                        double ceilOffset = ++index == stops.size() ? 2 : stops.get(index).getOffset();
                        //Check if the new Stop fits in the same spot
                        if (newOffset < floorOffset || newOffset > ceilOffset) {
                            //mark for sorting
                            sort = true;
                        }
                    } else if (c.wasAdded()) {
                        sort = true;
                    }
                    /*else if (c.wasRemoved()) {
                        //do nothing
                    }*/
                }
                if (sort) {
                    stops.sort(STOP_COMPARATOR);
                }
            }
        });
         this.stopSelection = new SingleSelectionModel<Stop>() {
            @Override
            protected Stop getModelItem(int index) {
                return stops.get(index);
            }
            
            @Override
            protected int getItemCount() {
                return stops.size();
            }
        };
//<editor-fold defaultstate="collapsed" desc="list view code removed">
/* listView = new ListView<>();
listView.setItems(stops);
listView.setEditable(true);
listView.setPrefHeight(120);
listView.setMinHeight(100);
listView.setCellFactory((lv) -> {
return new StopCell();
});
listView.setOnEditCommit((ListView.EditEvent<Stop> b) -> {
System.out.println(b.getIndex() + " " + stops.get(b.getIndex()) + "->" + b.getNewValue());
if (b.getNewValue() == null) {
stops.remove(b.getIndex());
} else {
stops.set(b.getIndex(), b.getNewValue());
}
});*/
 /*   stopList.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
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
});*/
//</editor-fold>
        addEventFilter(MouseEvent.MOUSE_PRESSED, clickFilter);
        addEventFilter(MouseEvent.MOUSE_DRAGGED, clickFilter);
        addEventFilter(MouseEvent.MOUSE_CLICKED, clickFilter);

    }

//<editor-fold defaultstate="collapsed" desc="property getter/setter">
    public CycleMethod getCycleMethod() {
        return cycleMethod.get();
    }

    public void setCycleMethod(CycleMethod value) {
        cycleMethod.set(value);
    }

    public ObjectProperty<CycleMethod> cycleMethodProperty() {
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
    //</editor-fold>

    protected StackPane addStop(Stop s) {
        StackPane stopMark = new StackPane();
        stopMark.getStyleClass().add("stop");
        stopMark.setBackground(new Background(new BackgroundFill(s.getColor(), ROUND, Insets.EMPTY)));

        stopMap.put(stopMark, s);
        stops.add(s);
        stops.sort(STOP_COMPARATOR);
        getChildren().add(stopMark);
        updateGradient();
        return stopMark;
    }

    public void updateStop(StackPane p, Stop s) {
        Stop old = stopMap.replace(p, s);
        int index = stops.indexOf(old);
        stops.set(index, s);

        double newOffset = s.getOffset();
        double floorOffset = index == 0 ? -1 : stops.get(index - 1).getOffset();
        double ceilOffset = ++index == stops.size() ? 2 : stops.get(index).getOffset();
        //Check if the new Stop fits in the same spot
        if (newOffset < floorOffset || newOffset > ceilOffset) {
            //sort the stops
            stops.sort(STOP_COMPARATOR);
        }

        p.setBackground(new Background(new BackgroundFill(s.getColor(), ROUND, Insets.EMPTY)));
        layoutStop(p, s);
        updateGradient();
    }

    public void deleteStop(StackPane p) {
        Stop removed = stopMap.remove(p);
        stops.remove(removed);
        //do not sort. List is already sorted
        getChildren().remove(p);
        updateGradient();
    }

    public final ObservableList<Stop> getStops() {
        return stops;
    }

    protected void layoutStop(StackPane p, Stop s) {
        double t = s.getOffset();
        double x = stopLayoutX(t) - p.getWidth() / 2;
        double y = stopLayoutY(t) - p.getHeight() / 2;
        p.relocate(x, y);
    }

    protected void layoutStops() {
        stopMap.forEach((StackPane t, Stop u) -> {
            layoutStop(t, u);
        });

    }

    public  abstract double stopLayoutX(double t);

    public  abstract double stopLayoutY(double t);

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
//        layoutStops();
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
    public  abstract double getOffset(double mx, double my);

    @Deprecated
    public abstract void updateGradient();

}
