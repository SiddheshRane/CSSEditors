/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 * @author Siddhesh
 */
public class CSSEditors extends Application {

    @Override
    public void start(Stage primaryStage) {
        /*
        uncomment any one to play its demo
         */
//        backgroundFillEditorTest(primaryStage);
//        regionPropertiesTest(primaryStage);
//        colorRectPaneTest(primaryStage);
//        linearGradientEditorTest(primaryStage);
//        stopCellTest(primaryStage);
//        LinearGradientEditorTest(primaryStage);
        RadialGradientEditorTest(primaryStage);
//        backgroundLayerTest(primaryStage);
//compositeAppTest(primaryStage);
    }

    public void backgroundFillEditorTest(Stage s) {
        VBox box = new VBox();
        Button b = new Button("Stylized Button");
        BackgroundFill bgFill = new BackgroundFill(Color.DARKMAGENTA, new CornerRadii(3), Insets.EMPTY);

        b.setBackground(new Background(bgFill));

        BackgroundLayer layer = new BackgroundLayer(bgFill);

        layer.backgroundFillProperty().addListener((ObservableValue<? extends BackgroundFill> observable, BackgroundFill oldValue, BackgroundFill newValue) -> {
            b.setBackground(new Background(newValue));
        });
        layer.layoutXProperty().bind(b.layoutXProperty());
        layer.layoutYProperty().bind(b.layoutYProperty());
        layer.prefWidthProperty().bind(b.widthProperty());
        layer.prefHeightProperty().bind(b.heightProperty());

        BackgroundFillEditor editor = new BackgroundFillEditor(layer.backgroundFillProperty());

        Group g = new Group(layer);
        layer.setScaleX(4);
        layer.setScaleY(4);

        box.getChildren().addAll(b, editor, g);
        box.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(box);
        scene.getStylesheets().add("/csseditors/csseditors.css");
        s.setScene(scene);
        s.setTitle("BackgroundFillEditor Test");
        s.show();
    }

    public void regionPropertiesTest(Stage s) {
        Button but = new Button("But!");
        but.applyCss();
        but.setScaleX(3);
        but.setScaleY(3);

        RegionProperties rp = new RegionProperties();
        rp.registerNode(but);

        Pane box = new VBox(rp, new Group(but));
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

    public void RadialGradientEditorTest(Stage s) {
        AnchorPane pane = new AnchorPane();
        RadialGradientEditor radialGradientEditor = new RadialGradientEditor();
        pane.getChildren().add(radialGradientEditor);
        AnchorPane.setLeftAnchor(radialGradientEditor, 25d);
        AnchorPane.setRightAnchor(radialGradientEditor, 25d);
        AnchorPane.setTopAnchor(radialGradientEditor, 25d);
        AnchorPane.setBottomAnchor(radialGradientEditor, 25d);

        ComboBox<CycleMethod> cycleMethodBox = new ComboBox<>(FXCollections.observableArrayList(CycleMethod.values()));
        cycleMethodBox.valueProperty().bindBidirectional(radialGradientEditor.cycleMethodProperty());
    
        Label focus = new Label();
        focus.textProperty().bind(radialGradientEditor.focusProperty().asString("Focus: %2.2f"));
        Label focusAngle = new Label();
        focusAngle.textProperty().bind(radialGradientEditor.focusAngleProperty().asString("Focus Angle: %2.2f"));
        Label radius = new Label();
        radius.textProperty().bind(radialGradientEditor.radiusProperty().asString("Radius: %2.2f"));
        StopList listView = new StopList();
        listView.setItems(radialGradientEditor.getStops());
        listView.setPrefHeight(100);

        VBox vbox = new VBox(pane, cycleMethodBox,focus,focusAngle,radius,listView);
        VBox.setVgrow(pane, Priority.ALWAYS);

        Scene scene = new Scene(vbox);
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

    public void backgroundLayerTest(Stage s) {
        Button test = new Button(" ");
        //add the button to group to turn its transform into layout
        StackPane stack = new StackPane(test);
        Group g = new Group(stack);
        stack.setScaleX(6);
        stack.setScaleY(6);
        //add everything to a ScrollPane
        ScrollPane scrollPane = new ScrollPane(g);
        Scene scene = new Scene(scrollPane);
        //apply css to the button
        scrollPane.layout();
        scrollPane.applyCss();
        scrollPane.setPrefSize(400, 400);
        //get all bgfills of the button
        List<BackgroundFill> fills = test.getBackground().getFills();
        List<BackgroundLayer> bglayers = new ArrayList<>(fills.size());
        for (BackgroundFill fill : fills) {
            //create a BackgroundLayer for each BackgroundFill
            BackgroundLayer bglayer = new BackgroundLayer(fill);
            bglayers.add(bglayer);
            stack.getChildren().add(bglayer);
        }
        scene.getStylesheets().add("/csseditors/csseditors.css");
        s.setScene(scene);
        scrollPane.autosize();
        s.setTitle("BackgroundLayer Test");
        s.show();
    }

    public void compositeAppTest(Stage s){
        Rectangle rec = new Rectangle(200, 100);
        RadialGradientEditor editor = new RadialGradientEditor();
        editor.setOpacity(0.5);
        StackPane overlay = new StackPane(rec,editor);
        rec.setArcWidth(30);
        rec.setArcHeight(30);
        rec.widthProperty().bind(overlay.widthProperty());
        rec.heightProperty().bind(overlay.heightProperty());
        rec.fillProperty().bind(editor.gradientProperty());
        
        Circle circle = new Circle(50);
        circle.fillProperty().bind(editor.gradientProperty());
        TextArea css = new TextArea("CSS");
        css.textProperty().bind(editor.gradientProperty().asString());
        HBox hbox = new HBox(overlay, circle,css);
        Scene scene = new Scene(hbox);
        s.setScene(scene);
        s.setTitle("Composite Application Test");
        s.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
