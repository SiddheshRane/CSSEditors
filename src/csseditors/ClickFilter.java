package csseditors;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Remove the onClick events generated after a mouse drag operation.
 *
 * @author Siddhesh Rane
 */
public class ClickFilter {

    private ClickFilter() {
    }
    static boolean dragged;
    static double dragX, dragY;
    static final EventHandler<MouseEvent> CLICK_FILTER = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            EventType<? extends MouseEvent> eventType = event.getEventType();
            if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
                dragX = event.getScreenX();
                dragY = event.getScreenY();
                dragged = false;
            } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                dragged = true;
            }
            if (eventType.equals(MouseEvent.MOUSE_CLICKED)) {
                if (dragged) {
                    dragX = event.getScreenX() - dragX;
                    dragY = event.getScreenY() - dragY;
                    double abs = dragX * dragX + dragY * dragY;
                    if (abs > 18) {
                        event.consume();
                    }
                }
            }
        }
    };

    public static void attach(Node n) {
        n.addEventFilter(MouseEvent.MOUSE_PRESSED, CLICK_FILTER);
        n.addEventFilter(MouseEvent.MOUSE_DRAGGED, CLICK_FILTER);
        n.addEventFilter(MouseEvent.MOUSE_CLICKED, CLICK_FILTER);
    }

    public static void detach(Node n) {
        n.removeEventFilter(MouseEvent.MOUSE_CLICKED, CLICK_FILTER);
        n.removeEventFilter(MouseEvent.MOUSE_DRAGGED, CLICK_FILTER);
        n.removeEventFilter(MouseEvent.MOUSE_PRESSED, CLICK_FILTER);
    }
}
