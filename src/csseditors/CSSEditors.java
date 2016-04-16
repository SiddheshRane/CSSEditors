/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
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
//        backgroundLayerTest2(primaryStage);
//        compositeAppTest(primaryStage);
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

        Slider slider = new Slider(-180, +180, 90);
        slider.valueProperty().bindBidirectional(radialGradientEditor.focusAngleProperty());

        StopList listView = new StopList();
        listView.setItems(radialGradientEditor.getStops());
        listView.setPrefHeight(100);

        VBox vbox = new VBox(pane, cycleMethodBox, focus, focusAngle, slider, radius, listView);
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
        test.setOnAction(a -> System.out.println("Clicked me!"));
        test.setStyle(
                "-fx-background-color: cornflowerblue,skyblue,teal;"
                + "-fx-background-insets: 0,2,4;"
                + "-fx-background-radius: 30%;"
        );
        test.setMouseTransparent(true);
        test.setFocusTraversable(false);
        //add the button to group to turn its transform into layout
        StackPane stack = new StackPane(new Group(test));
        test.setScaleX(4);
        test.setScaleY(4);
        stack.setOnScroll(se -> {
            if (se.isControlDown()) {
                se.consume();
                System.out.println("delta X,Y = " + se.getDeltaX() + "," + se.getDeltaY());
                test.setScaleX(test.getScaleX() + se.getDeltaY() / 100);
                test.setScaleY(test.getScaleY() + se.getDeltaY() / 100);
            }
        });
        //add everything to a ScrollPane
        ScrollPane scrollPane = new ScrollPane(stack);
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
            if (fill.getFill() instanceof LinearGradient) {
                LinearGradientEditor editor = new LinearGradientEditor();
//                editor.setGradient((LinearGradient) fill.getFill());
//                editor.prefWidthProperty().bind(test.widthProperty());
//                editor.prefHeightProperty().bind(test.heightProperty());
//                bglayer.backgroundPaintProperty().bind(editor.gradientProperty());
//                bglayer.getChildren().add(editor);
            } else if (fill.getFill() instanceof Color) {
                ColorPicker picker = new ColorPicker();
                picker.valueProperty().addListener((observable, oldValue, newValue) -> bglayer.setBackgroundPaint(newValue));
                bglayer.getChildren().add(picker);
            }

        }

        //alternative view
        FlowPane flow = new FlowPane(10, 5);

        scene.getStylesheets().add("/csseditors/csseditors.css");
        s.setScene(scene);
        scrollPane.autosize();
        s.setTitle("BackgroundLayer Test");
        s.show();
    }

    public void backgroundLayerTest2(Stage s) {
        Region test = new Region();
        test.setStyle(
                "-fx-background-color: cornflowerblue,#eb703e,linear-gradient(to bottom,steelblue,springgreen);"
                + "-fx-background-insets: 0,2,4;"
                + "-fx-background-radius: 30%;"
        );
        test.setPrefSize(50, 50);
        test.setMouseTransparent(true);
        test.setFocusTraversable(false);
        //add the region to group to turn its transform into layout
        StackPane stack = new StackPane(new Group(test));
        test.setScaleX(1);
        test.setScaleY(1);
        stack.setOnScroll(se -> {
            if (se.isControlDown()) {
                se.consume();
                System.out.println("delta X,Y = " + se.getDeltaX() + "," + se.getDeltaY());
                test.setScaleX(test.getScaleX() + se.getDeltaY() / 100);
                test.setScaleY(test.getScaleY() + se.getDeltaY() / 100);
            }
        });
        //add everything to a ScrollPane
        FlowPane flow = new FlowPane(10, 5,stack);
        ScrollPane scrollPane = new ScrollPane(flow);
        Scene scene = new Scene(scrollPane);
        //apply css to the button
        scrollPane.layout();
        scrollPane.applyCss();
        scrollPane.setPrefSize(400, 400);
        //get all bgfills of the button
        List<BackgroundFill> fills = test.getBackground().getFills();
        List<BackgroundLayer> bglayers = new ArrayList<>(fills.size());
        ChangeListener cl = (ob, ol, nw) -> {
            System.out.println("At least called!");
            List<BackgroundFill> newBgFills = bglayers.stream().map(BackgroundLayer::getBackgroundFill).peek(fill -> System.out.println("STREAM" + fill)).collect(Collectors.toList());
            test.setBackground(new Background(newBgFills, null));
        };
        for (BackgroundFill fill : fills) {
            //create a BackgroundLayer for each BackgroundFill
            BackgroundLayer bglayer = new BackgroundLayer(fill);
            bglayer.backgroundFillProperty().addListener(cl);
            bglayers.add(bglayer);
            flow.getChildren().add(bglayer);
            if (fill.getFill() instanceof LinearGradient) {
                LinearGradientEditor editor = new LinearGradientEditor();
                editor.setGradient((LinearGradient) fill.getFill());
                editor.prefWidthProperty().bind(test.widthProperty());
                editor.prefHeightProperty().bind(test.heightProperty());
                bglayer.backgroundPaintProperty().bind(editor.gradientProperty());
                bglayer.getChildren().add(editor);
            } else if (fill.getFill() instanceof Color) {
                ColorPicker picker = new ColorPicker();
                picker.valueProperty().addListener((observable, oldValue, newValue) -> bglayer.setBackgroundPaint(newValue));
                bglayer.getChildren().add(picker);
            }

        }

        scene.getStylesheets().add("/csseditors/csseditors.css");
        s.setScene(scene);
        scrollPane.autosize();
        s.setTitle("BackgroundLayer Test");
        s.show();

    }

    public void compositeAppTest(Stage s) {
        Rectangle rec = new Rectangle(200, 100);
        RadialGradientEditor editor = new RadialGradientEditor();
//        editor.setOpacity(0.5);
        StackPane overlay = new StackPane(rec, editor);
        rec.setArcWidth(60);
        rec.setArcHeight(60);
        rec.widthProperty().bind(overlay.widthProperty());
        rec.heightProperty().bind(overlay.heightProperty());
        rec.fillProperty().bind(editor.gradientProperty());

        Circle circle = new Circle(50);
        circle.fillProperty().bind(editor.gradientProperty());
        TextArea css = new TextArea("CSS");
        css.textProperty().bind(editor.gradientProperty().asString());
        css.setWrapText(true);
        HBox hbox = new HBox(overlay, circle, css);
        hbox.setAlignment(Pos.CENTER);
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
