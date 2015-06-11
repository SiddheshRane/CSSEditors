/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 *
 * @author Siddhesh
 */
public class RegionProperties extends AnchorPane {

    Region r;

    @FXML
    private ListView<BackgroundFill> bgFillList;

    @FXML
    private VBox bgFillEditorPane;

    private ObjectProperty<Background> background;
    private ObjectProperty<BackgroundFill> currentBgFill;
    private int currentBgFillIndex = 0;

    BackgroundFillEditor bgFillEditor;
    ObservableList<BackgroundFill> bgFills;
    ChangeListener<Background> backgroundListener;

    public RegionProperties() {

        this.backgroundListener = (ObservableValue<? extends Background> observable, Background oldValue, Background newValue) -> {
            if (changeIsLocal) {
                changeIsLocal = false;
                return;
            }
            enumerateBackgroundFills(newValue);

        };
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Region.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(RegionProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
        bgFills = bgFillList.getItems();
        currentBgFill = new SimpleObjectProperty<>();
        bgFillEditor = new BackgroundFillEditor();
        bgFillEditorPane.getChildren().add(bgFillEditor);
        bgFillEditor.setBackgroundFill(currentBgFill);

        bgFillList.setCellFactory((ListView<BackgroundFill> param) -> new ListCell<BackgroundFill>() {

            Circle paintCircle = new Circle(5);

            @Override
            public void updateItem(BackgroundFill item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setText("Fill " + getIndex());
                    paintCircle.setFill(item.getFill());
                    setGraphic(paintCircle);
                }
            }
        });
        bgFillList.getSelectionModel().selectedIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            editBackgroundFill(newValue.intValue());
            System.out.println("ListView from " + oldValue + " to " + newValue);
        });

        currentBgFill.addListener((ObservableValue<? extends BackgroundFill> observable, BackgroundFill oldValue, BackgroundFill newValue) -> {
            if (currentBgFillIndex == -1) {
                return;
            }
            bgFills.set(currentBgFillIndex, newValue);
            update();
        });
    }

    private void editBackgroundFill(int index) {
        currentBgFillIndex = index;
        if (index == -1) {//nothing has been chosen

        } else {
            currentBgFill.set(bgFills.get(index));
        }
    }

    private void enumerateBackgroundFills(Background background) {
        if (background == null) {
            return;
        }
        List<BackgroundFill> fills = background.getFills();
        bgFills.setAll(fills);

    }

    boolean changeIsLocal = false;

    private void update() {
        background.set(new Background(bgFills, null));
        changeIsLocal = true;
    }

    public void registerNode(Region r) {
        if (this.r != null) {
            background.removeListener(backgroundListener);
        }
        this.r = r;
        if (r == null) {
            return;
        }
        enumerateBackgroundFills(r.getBackground());
        background = r.backgroundProperty();
        background.addListener(backgroundListener);

    }

}
