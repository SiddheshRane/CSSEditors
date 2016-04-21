/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
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
import org.controlsfx.control.PopOver;

/**
 * A container for layout of visual nodes representing
 * {@link javafx.scene.paint.Stop}s in gradient colors. Provides methods for
 * adding, deleting, updating and sorting Stops. The sorted stops can directly
 * be used in either {@link  LinearGradient} or {@link RadialGradient}
 *
 * @author Siddhesh
 */
public abstract class GradientEditor extends Pane {

    //TODO: This should'nt be here. Find a more asthetically pleasing place for this
    public static final CornerRadii ROUND = new CornerRadii(50, true);
    private static final Comparator<Stop> STOP_COMPARATOR = Comparator.comparingDouble(Stop::getOffset);
    //maps Stops to their visual nodes
    protected HashMap<StackPane, Stop> stopMap;
    private ObservableList<Stop> stops;
    //PENDING: Implement a stop selection mechanism
    SelectionModel<Stop> stopSelection;
    private final ObjectProperty<CycleMethod> cycleMethod = new SimpleObjectProperty<>(CycleMethod.NO_CYCLE);
    private final BooleanProperty proportional = new SimpleBooleanProperty(true);
    private final ListChangeListener<Stop> stopsListener;

    public GradientEditor() {
        stopMap = new HashMap<>(7);
        stops = FXCollections.observableArrayList();
        stopsListener = new ListChangeListener<Stop>() {
            boolean sorting;

            @Override
            public void onChanged(ListChangeListener.Change<? extends Stop> c) {
                if (sorting) {
                    return;
                }
                boolean sort = false;
                while (c.next()) {
                    if (c.wasReplaced()) {
                        int diff = c.getAddedSize() - c.getRemovedSize();
                        int intersectionTill = c.getTo() - (diff > 0 ? diff : 0);
                        //reuse existing StackPanes for the replaced components
                        for (int i = c.getFrom(); i < intersectionTill; i++) {
                            Stop old = c.getRemoved().get(i - c.getFrom());
                            Stop now = stops.get(i);
//                            System.out.println(old.toString()+"->"+now.toString());
                            //check whether the replaced Stop needs sorting
                            if (!sort) {/*Skip the check if you are going to sort anyway*/
                                int index = i;
                                double newOffset = now.getOffset();
                                double floorOffset = index == 0 ? -1 : stops.get(index - 1).getOffset();
                                double ceilOffset = ++index == stops.size() ? 2 : stops.get(index).getOffset();
                                //Check if the new Stop fits in the same spot
                                if (newOffset < floorOffset || newOffset > ceilOffset) {
                                    sort = true;
                                }
                            }
                            stopMap.entrySet().stream().filter(e -> e.getValue() == old).findAny().ifPresent((t) -> {
//                                System.out.println("t = " + t);
                                t.setValue(now);
                                t.getKey().setBackground(new Background(new BackgroundFill(now.getColor(), ROUND, Insets.EMPTY)));
                            });
                        }
                        if (diff > 0) {
                            //more to be added than removed. Extra StackPanes need to be created
                            final List<? extends Stop> subList = c.getAddedSubList().subList(intersectionTill, c.getTo());
                            addRange(subList);
                            sort = true;
                        } else if (diff < 0) {
                            //more to be removed than added. Existing StackPanes need to be removed
                            final List<? extends Stop> subList = c.getRemoved().subList(-diff + 1, c.getRemovedSize());
                            removeRange(subList);
                        }
                    } else if (c.wasAdded()) {
//                        System.out.println("ADD");
                        addRange(c.getAddedSubList());
                        sort = true;
                    } else if (c.wasRemoved()) {
//                        System.out.println("REMOVE");
                        removeRange(c.getRemoved());
                    }
                }
                if (sort) {
                    sorting = true;
                    stops.sort(STOP_COMPARATOR);
                    sorting = false;
                }
            }
        };
        stops.addListener(stopsListener);
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
        ClickFilter.attach(this);
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

    private void removeRange(List<? extends Stop> stops) {
        for (Stop s : stops) {
            StackPane p = stopMap.entrySet().stream().filter(entry -> entry.getValue() == s).findFirst().map(Map.Entry::getKey).get();
//            System.out.println("p:"+p.toString()+" s:"+s.toString());
            stopMap.remove(p);
            getChildren().remove(p);
        }
    }

    private void addRange(List<? extends Stop> addedStops) {
        for (Stop addedStop : addedStops) {
            StackPane stopMark = new StackPane();
            stopMark.getStyleClass().add("stop");
            stopMark.setBackground(new Background(new BackgroundFill(addedStop.getColor(), ROUND, Insets.EMPTY)));
            //TODO: Color picker consumes all input events.ColorPicker buggy on linux
//                            final ColorPicker colorPicker = new ColorPicker(s.getColor());
//                            colorPicker.valueProperty().addListener((ob, old, nw) -> updateStop(stopMark, new Stop(stopMap.get(stopMark).getOffset(), nw)));
//                            stopMark.getChildren().add(colorPicker);
            stopMark.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                PopOver pop;
                private ColorRectPane colorRectPane;

                {
                    pop = new PopOver();
                    pop.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
                    colorRectPane = new ColorRectPane();
                    colorRectPane.setCustomColor(addedStop.getColor());
                    StackPane stackPane = new StackPane(colorRectPane);
                    stackPane.setPadding(new Insets(7));
                    pop.setContentNode(stackPane);
                    pop.setAutoHide(true);
                    colorRectPane.customColorProperty().addListener((ob, ol, nw) -> {
                        updateStop(stopMark, new Stop(stopMap.get(stopMark).getOffset(), nw));
                    });
                }

                @Override
                public void handle(MouseEvent event) {
                    colorRectPane.setCustomColor(stopMap.get(stopMark).getColor());
                    pop.show(stopMark);
                    pop.requestFocus();
                }
            });
            stopMap.put(stopMark, addedStop);
            getChildren().add(stopMark);
        }
    }

    protected void addStop(Stop s) {
        stops.add(s);
    }

    public void updateStop(StackPane p, Stop s) {
        Stop old = stopMap.get(p);
        int index = stops.indexOf(old);
        stops.set(index, s);
    }

    public void deleteStop(StackPane p) {
        Stop toRemove = stopMap.get(p);
        stops.remove(toRemove);
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

    public abstract double stopLayoutX(double t);

    public abstract double stopLayoutY(double t);

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
    public abstract double getOffset(double mx, double my);

}
