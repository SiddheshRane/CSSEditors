/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Siddhesh
 */
public class NodeEditor extends AnchorPane {

    @FXML
    private TextField rotate;

    @FXML
    private CheckBox visible;

    @FXML
    private TextField tx;

    @FXML
    private TextField ty;

    @FXML
    private ComboBox<BlendMode> blend;

    @FXML
    private Slider opacity;

    NumericField xtf, ytf, rtf;
    Node n;

    public NodeEditor() {
        this(null);
    }

    public NodeEditor(Node n) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Node.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(NodeEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        blend.setItems(FXCollections.observableArrayList(BlendMode.values()));
        xtf = new NumericField(tx, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        ytf = new NumericField(ty, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        rtf = new NumericField(rotate, 0, 360);
        registerNode(n);
    }

    public void registerNode(Node n) {
        if (this.n == n) {
            return;
        }
        unregisterNode();
        if (n == null) {
            return;
        }
        this.n = n;
        //bind properties

        blend.valueProperty().bindBidirectional(n.blendModeProperty());
        visible.selectedProperty().bindBidirectional(n.visibleProperty());
        opacity.valueProperty().bindBidirectional(n.opacityProperty());
        xtf.valueProperty().bindBidirectional(n.translateXProperty());
        ytf.valueProperty().bindBidirectional(n.translateYProperty());
        rtf.valueProperty().bindBidirectional(n.rotateProperty());

    }

    public void unregisterNode() {
        if (n == null) {
            return;
        }

        //unbind properties
        n.blendModeProperty().unbindBidirectional(blend.valueProperty());
        n.visibleProperty().unbindBidirectional(visible.selectedProperty());
        n.opacityProperty().unbindBidirectional(opacity.valueProperty());
        n.translateXProperty().unbindBidirectional(xtf.valueProperty());
        n.translateYProperty().unbindBidirectional(ytf.valueProperty());
        n.rotateProperty().unbindBidirectional(rtf.valueProperty());
    }

}
