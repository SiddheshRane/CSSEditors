/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drag;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;

public class TranslateDraggable extends Draggable {

    private boolean xDraggable = true;
    private boolean yDraggable = true;
    private final ObjectProperty<Bounds> bounds;

    public Bounds getBounds() {
        return bounds.get();
    }

    public void setBounds(Bounds value) {
        bounds.set(value);
    }

    public ObjectProperty boundsProperty() {
        return bounds;
    }

    public TranslateDraggable() {
        this(new BoundingBox(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    }

    public TranslateDraggable(Bounds b) {
        this.bounds = new SimpleObjectProperty<>(b);
    }

    @Override
    protected void dragged(MouseEvent event) {

        Bounds b = bounds.get();
        if (xDraggable) {
            double delta = dragX + initX;
            if (b.getMinX() <= delta && delta <= b.getMaxX()) {
                targetNode.setTranslateX(delta);
            }
        }
        if (yDraggable) {
            double delta = dragY + initY;
            if (b.getMinY() <= delta && delta <= b.getMaxY()) {
                targetNode.setTranslateY(delta);
            }
        }
    }

    @Override
    protected void pressed(MouseEvent event) {
    }

    /**
     * @return the xDraggable
     */
    public boolean isxDraggable() {

        return xDraggable;
    }

    /**
     * @param xDraggable the xDraggable to set
     */
    public void setxDraggable(boolean xDraggable) {
        this.xDraggable = xDraggable;
    }

    /**
     * @return the yDraggable
     */
    public boolean isyDraggable() {
        return yDraggable;
    }

    /**
     * @param yDraggable the yDraggable to set
     */
    public void setyDraggable(boolean yDraggable) {
        this.yDraggable = yDraggable;
    }

}
