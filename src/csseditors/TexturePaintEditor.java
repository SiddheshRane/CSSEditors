/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;

/**
 *
 * @author Siddhesh
 */
public class TexturePaintEditor extends Pane {

    
    
    private final ObjectProperty<ImagePattern> paint = new SimpleObjectProperty<>();

    public ImagePattern getPaint() {
        return paint.get();
    }

    public void setPaint(ImagePattern value) {
        paint.set(value);
    }

    public ObjectProperty paintProperty() {
        return paint;
    }

    
    public TexturePaintEditor() {
    }
    
}
