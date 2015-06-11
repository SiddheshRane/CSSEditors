/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drag;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Siddhesh
 */
public abstract class Draggable {

    protected static double anchorX, anchorY, dragX, dragY;
    protected static double initX, initY;
    public static Node targetNode;


    EventHandler<MouseEvent> handler = (MouseEvent event) -> {
        EventType<? extends MouseEvent> eventType = event.getEventType();
        
        if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
            Point2D anchor = targetNode.getParent().sceneToLocal(anchorX, anchorY);
            Point2D current = targetNode.getParent().sceneToLocal(event.getSceneX(), event.getSceneY());
            
            dragX = current.getX() - anchor.getX();
            dragY = current.getY() - anchor.getY();
            dragged(event);
            event.consume();
            
        } else if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
            targetNode = (Node) event.getTarget();
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            
            initX = targetNode.getTranslateX();
            initY = targetNode.getTranslateY();
            
            pressed(event);
            event.consume();
        }
    };

    public Draggable() {
    }

    public void drag(Node targetNode) {
        targetNode.setOnMousePressed(handler);
        targetNode.setOnMouseDragged(handler);
        
    }

    protected abstract void dragged(MouseEvent event);

    protected abstract void pressed(MouseEvent event);

}
