/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;

/**
 *
 * @author Siddhesh
 */
public class CSSEditors extends Application {

    @Override
    public void start(Stage primaryStage) {

//        backgroundFillEditorTest(primaryStage);
//        regionPropertiesTest(primaryStage);
//        colorRectPaneTest(primaryStage);
//        linearGradientEditorTest(primaryStage);
//        stopCellTest(primaryStage);
//        LinearGradientEditorTest(primaryStage);
        RadialGradientEditorTest(primaryStage);
    }

    public void backgroundFillEditorTest(Stage s) {
        VBox box = new VBox();
        Button b = new Button("Stylized Button");
        BackgroundFill bgFill = new BackgroundFill(Color.AQUAMARINE, new CornerRadii(3), Insets.EMPTY);

        b.setBackground(new Background(bgFill));

        BackgroundLayer layer = new BackgroundLayer(bgFill);

        layer.backgroundFillProperty().addListener((ObservableValue<? extends BackgroundFill> observable, BackgroundFill oldValue, BackgroundFill newValue) -> {
            b.setBackground(new Background(newValue));
        });
        BackgroundFillEditor editor = new BackgroundFillEditor(layer.backgroundFillProperty());

        Group g = new Group(layer);
        layer.setScaleX(2);
        layer.setScaleY(2);

        box.getChildren().addAll(b, editor, g);

        Scene scene = new Scene(box);
        scene.getStylesheets().add("/csseditors/csseditors.css");
        s.setScene(scene);
        s.setTitle("BackgroundFillEditor Test");
        s.show();
        layer.setMinHeight(b.getHeight());
        layer.setMinWidth(b.getWidth());
    }

    public void regionPropertiesTest(Stage s) {
        Button but = new Button("But!");
        but.applyCss();
        but.setScaleX(3);
        but.setScaleY(3);

        RegionProperties rp = new RegionProperties();
        rp.registerNode(but);

        VBox box = new VBox(rp, new Group(but));
        Scene scene = new Scene(box);
        s.setTitle("RegionProperties Test");
        s.setScene(scene);
        s.show();
    }

    public void colorRectPaneTest(Stage s) {
        ColorRectPane colorRectPane = new ColorRectPane();
        Scene scene = new Scene(new StackPane(colorRectPane));
        s.setTitle("ColorRectPane test");
        s.setScene(scene);
        s.show();

    }

    public void linearGradientEditorTest(Stage s) {
        AnchorPane pane = new AnchorPane();
        LinearGradientEditorDepracated linearGradientEditor = new LinearGradientEditorDepracated();
        pane.getChildren().add(linearGradientEditor);
        AnchorPane.setLeftAnchor(linearGradientEditor, 25d);
        AnchorPane.setRightAnchor(linearGradientEditor, 25d);
        AnchorPane.setTopAnchor(linearGradientEditor, 25d);
        AnchorPane.setBottomAnchor(linearGradientEditor, 25d);

        Scene scene = new Scene(pane);
        s.setScene(scene);
        s.setTitle("LinearGradientEditor Test");
        s.show();
    }

    public void LinearGradientEditorTest(Stage s) {
        AnchorPane pane = new AnchorPane();
        LinearGradientEditor linearGradientEditor = new LinearGradientEditor();
        pane.getChildren().add(linearGradientEditor);
        AnchorPane.setLeftAnchor(linearGradientEditor, 25d);
        AnchorPane.setRightAnchor(linearGradientEditor, 25d);
        AnchorPane.setTopAnchor(linearGradientEditor, 25d);
        AnchorPane.setBottomAnchor(linearGradientEditor, 25d);

        Scene scene = new Scene(pane);
        s.setScene(scene);
        s.setTitle("LGE Test");
        s.show();
    }

    public void RadialGradientEditorTest(Stage s){
    AnchorPane pane = new AnchorPane();
        RadialGradientEditor radialGradientEditor = new RadialGradientEditor();
        pane.getChildren().add(radialGradientEditor);
        AnchorPane.setLeftAnchor(radialGradientEditor, 25d);
        AnchorPane.setRightAnchor(radialGradientEditor, 25d);
        AnchorPane.setTopAnchor(radialGradientEditor, 25d);
        AnchorPane.setBottomAnchor(radialGradientEditor, 25d);

        Scene scene = new Scene(pane);
        s.setScene(scene);
        s.setTitle("RGE Test");
        s.show();
    }
    public void stopCellTest(Stage s) {
        AnchorPane pane = new AnchorPane();
        ListView<Stop> stopsList = new ListView<>();
        stopsList.setEditable(true);
        ObservableList<Stop> stops = FXCollections.observableArrayList(new Stop(0, Color.AQUA),
                new Stop(0.5, Color.ALICEBLUE), new Stop(0.6, Color.CHARTREUSE),
                new Stop(0.7, Color.BLANCHEDALMOND));

        stopsList.setItems(stops);
        stopsList.setCellFactory((ListView<Stop> param) -> new StopCell());

        pane.getChildren().add(stopsList);
        AnchorPane.setLeftAnchor(stopsList, 25d);
        AnchorPane.setRightAnchor(stopsList, 25d);
        AnchorPane.setTopAnchor(stopsList, 25d);
        AnchorPane.setBottomAnchor(stopsList, 25d);

        pane.getStylesheets().add("/csseditors/lge.css");
        Scene scene = new Scene(pane);
        s.setScene(scene);
        s.setTitle("StopCell Test");
        s.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
