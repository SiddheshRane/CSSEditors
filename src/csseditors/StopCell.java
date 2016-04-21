/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import static javafx.scene.input.KeyCode.ENTER;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Stop;

/**
 *
 * @author Siddhesh
 */
public class StopCell extends ListCell<Stop> {

    Label offset;
    ColorPicker color;
    Slider offsetSlider;
    Region filler;

    StackPane cross;
    HBox hbox;

    public StopCell() {
        offset = new Label();
        color = new ColorPicker();
        offsetSlider = new Slider(0, 1, 0);
        cross = new StackPane();
        filler = new Region();
        hbox = new HBox(offset, color, filler, cross);
        offset.getStyleClass().add("offset");
        cross.getStyleClass().add("cross");
        color.getStyleClass().add("stop-color");
        hbox.getStyleClass().add("cell-hbox");

        filler.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(filler, Priority.ALWAYS);

        offset.setOnMouseClicked((MouseEvent e) -> {
            editOffset();
        });
        offsetSlider.setOnKeyPressed((ke) -> {
            if (ke.getCode().equals(ENTER)) {
                editOffset();
            }
        });
        /*color.valueProperty().addListener((Observable observable) -> {
         startEdit();
         commitEdit(new Stop(getItem().getOffset(), color.getValue()));
         });*/

        color.showingProperty().addListener((ob) -> {
            if (color.isShowing()) {
                startEdit();
            } else {
                commitEdit(new Stop(getItem().getOffset(), color.getValue()));
            }
        });

        cross.setOnMouseClicked((event) -> {
            deleteStop();
        });

    }

    @Override
    protected void updateItem(Stop item, boolean empty) {
        super.updateItem(item, empty);
        if (isEditing()) {
            return;
        }
        if (item != null) {
            offset.setText(String.format("%2.1f%%", item.getOffset() * 100));
            color.setValue(item.getColor());
            setGraphic(hbox);
        } else {
            setGraphic(null);
        }

    }

    boolean sliderShowing = false;

    private void showSlider() {

        if (!sliderShowing) {
            startEdit();
            hbox.getChildren().remove(color);
            hbox.getChildren().add(1, offsetSlider);
            offsetSlider.setValue(getItem().getOffset());
            offset.textProperty().bind(offsetSlider.valueProperty().multiply(100).asString("%2.1f%%"));
            sliderShowing = true;

        } else {
            offset.textProperty().unbind();
            hbox.getChildren().remove(offsetSlider);
            hbox.getChildren().add(1, color);

            sliderShowing = false;
        }
    }

    private void editOffset() {
        showSlider();
        if (!sliderShowing) {
            commitEdit(new Stop(offsetSlider.getValue(), getItem().getColor()));
        }

    }

    private void deleteStop() {
        startEdit();
        commitEdit(null);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        if (sliderShowing) {
            showSlider();
        }
        System.out.println("--cancel Edit");
    }

}
