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
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
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

    //maps Stops to their visual nodes
    protected HashMap<StackPane, Stop> stopMap;
    private ObservableList<Stop> stops;
    private SortedList<Stop> sortedStops;
    private boolean sortedStopsSynced = false;

    private static final CornerRadii ROUND = new CornerRadii(50, true);

    //A ListView showing the list of stops in ascending order of their offset
//    ListView<Stop> listView; //TODO:add somewhere else
    //Square shape holder for Gradient preview and editor
   
   /* protected Pane unitBox = new Pane() {
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
   };*/
    protected final ObjectProperty<CycleMethod> cycleMethod = new SimpleObjectProperty<>(CycleMethod.NO_CYCLE);
    protected final BooleanProperty proportional = new SimpleBooleanProperty(true);

    ListChangeListener<Stop> lcl = new ListChangeListener<Stop>() {
        @Override
        public void onChanged(ListChangeListener.Change<? extends Stop> c) {
            System.out.println("List:" + (c.getList() == stops ? "sortedStops" : "sorted"));

            while (c.next()) {
                if (c.wasPermutated()) {
                    System.out.print("Permutated ");
                }
                if (c.wasAdded()) {
                    System.out.print("Added ");
                }
                if (c.wasRemoved()) {
                    System.out.print("Removed ");
                }
                if (c.wasReplaced()) {
                    System.out.print("Replaced ");
                }
                if (c.wasUpdated()) {
                    System.out.print("Updated ");
                }
                System.out.println();
//                    sorted.stream().mapToDouble(Stop::getOffset).forEach(d -> System.out.print(d+","));
            }
        }
    };

    public GradientEditor() {
        stopMap = new HashMap<>(7);
        stops = FXCollections.observableArrayList();
        sortedStops = stops.sorted(Comparator.comparingDouble(Stop::getOffset));

        //initialise
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

        cycleMethod.addListener((o) -> {
            updatePreview();
        });

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
//</editor-fold>

    protected StackPane addStop(Stop s) {
        StackPane stopMark = new StackPane();
        stopMark.getStyleClass().add("stop");
        stopMark.setBackground(new Background(new BackgroundFill(s.getColor(), ROUND, Insets.EMPTY)));

        stopMap.put(stopMark, s);
        stops.add(s);
        getChildren().add(stopMark);
        sortedStopsSynced = false;
        return stopMark;
    }

    public void updateStop(StackPane p, Stop s) {
        Stop old = stopMap.replace(p, s);
        stops.remove(old);
        stops.add(s);
        if (old.getOffset() == s.getOffset()) {
            //do not sort the stops
        }
        sortedStopsSynced = false;
        p.setBackground(new Background(new BackgroundFill(s.getColor(), ROUND, Insets.EMPTY)));
        layoutStop(p, s);
    }

    public void deleteStop(StackPane p) {
        Stop removed = stopMap.remove(p);
        stops.remove(removed);
        sortedStopsSynced = false;
        getChildren().remove(p);
    }

    public final SortedList<Stop> getSortedStops() {
        return sortedStops;
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

    protected abstract double stopLayoutX(double t);

    protected abstract double stopLayoutY(double t);


    @Override
    protected void layoutChildren() {
        super.layoutChildren();
//        unitBox.resizeRelocate((getWidth() - unitBox.getHeight()) / 2, unitBox.getLayoutY(), unitBox.getWidth(), unitBox.getHeight());
        layoutStops();
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
}
