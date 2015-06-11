package csseditors;

import java.io.IOException;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 *
 * @author Siddhesh
 */
public class BackgroundFillEditor extends VBox {

    //UI
    InnerShadow shadow = new InnerShadow(85, Color.BLACK);
    Timeline timeline;

    @FXML
    private TextField rightInset;

    @FXML
    private TextField cornerBLx;
    @FXML
    private TextField cornerBLy;

    @FXML
    private TextField cornerTLx;
    @FXML
    private TextField cornerTLy;

    @FXML
    private TextField bottomInset;

    @FXML
    private TextField topInset;

    @FXML
    private TextField cornerBRx;
    @FXML
    private TextField cornerBRy;

    @FXML
    private TextField leftInset;

    @FXML
    private TextField cornerTRx;
    @FXML
    private TextField cornerTRy;
    @FXML
    private Label faintLabel;
    @FXML
    private StackPane fillPane;

    private ObjectProperty<BackgroundFill> backgroundFill;

    ChangeListener<BackgroundFill> listener = (ObservableValue<? extends BackgroundFill> observable, BackgroundFill oldValue, BackgroundFill newValue) -> {
        if (newValue == null) {
            System.out.println("BackgroundFillEditor newValue is null");
            setDisable(true);
            return;
        }
        setDisable(false);
        Paint fill = newValue.getFill();
        CornerRadii radii = newValue.getRadii();
        Insets insets1 = newValue.getInsets();
        setFill(fill);
        setCornerRadii(radii);
        setInsets(insets1);
    };

    public BackgroundFillEditor() {
        this(null);
    }

    public BackgroundFillEditor(ObjectProperty<BackgroundFill> fill) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("BackgroundLayerInfo.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
        }

        shadow.setWidth(0);
        shadow.setHeight(0);

        //insetsPane.setEffect(shadow);
        setBackgroundFill(fill);
    }

    public void disable(boolean t) {
        setDisable(t);
    }

    /**
     * Attaches a BackgroundFill property to this editor
     *
     *
     * @param fill the ObjectProperty that contains the BackgroundFill being
     * edited
     */
    public void setBackgroundFill(ObjectProperty<BackgroundFill> fill) {

        if (backgroundFill != null) {
            backgroundFill.removeListener(listener);

        }

        backgroundFill = fill;

        if (backgroundFill != null) {
            backgroundFill.addListener(listener);
            if(backgroundFill.get() == null)return;
            setInsets(backgroundFill.get().getInsets());
            setCornerRadii(backgroundFill.get().getRadii());
            setFill(backgroundFill.get().getFill());

            disable(false);
            //hideAlert();
        } else {
            //displayAlert("Background layer not set");
            disable(true);
        }

    }

    //updates the Insets editing UI
    public void setInsets(Insets in) {
        if (in != null) {
            topInset.setText(Double.toString(in.getTop()));
            rightInset.setText(Double.toString(in.getRight()));
            bottomInset.setText(Double.toString(in.getBottom()));
            leftInset.setText(Double.toString(in.getLeft()));
        }
    }

    //Updates the CornerRadii editing UI
    public void setCornerRadii(CornerRadii r) {
        if (r != null) {
            cornerTLx.setText(Double.toString(r.getTopLeftHorizontalRadius()));
            cornerTLy.setText(Double.toString(r.getTopLeftVerticalRadius()));

            cornerTRx.setText(Double.toString(r.getTopRightHorizontalRadius()));
            cornerTRy.setText(Double.toString(r.getTopRightVerticalRadius()));

            cornerBLx.setText(Double.toString(r.getBottomLeftHorizontalRadius()));
            cornerBLy.setText(Double.toString(r.getBottomLeftVerticalRadius()));

            cornerBRx.setText(Double.toString(r.getBottomRightHorizontalRadius()));
            cornerBRy.setText(Double.toString(r.getBottomRightVerticalRadius()));
        }
    }

    //Updates the Paint display area with Paint p
    public void setFill(Paint p) {
        if (p != null) {
            fillPane.setBackground(new Background(new BackgroundFill(p, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            fillPane.setBackground(Background.EMPTY);
        }
    }

    //Called when user edits the Insets UI
    @FXML
    public void handleInsetsChange(ActionEvent event) {
        if (backgroundFill == null) {

            return;
        }
        Insets in = backgroundFill.get().getInsets();
        try {
            double t = Double.parseDouble(topInset.getText());
            double r = Double.parseDouble(rightInset.getText());
            double b = Double.parseDouble(bottomInset.getText());
            double l = Double.parseDouble(leftInset.getText());
            in = new Insets(t, r, b, l);
        } catch (NumberFormatException ex) {

        }

        Paint fill = backgroundFill.get().getFill();
        CornerRadii radii = backgroundFill.get().getRadii();

        backgroundFill.set(new BackgroundFill(fill, radii, in));
    }

    //Called when the user edits corner radii UI
    @FXML
    public void handleCornerRadiiChange() {
        CornerRadii r = backgroundFill.get().getRadii();
        try {
            double TLx = Double.parseDouble(cornerTLx.getText());
            double TLy = Double.parseDouble(cornerTLy.getText());
            double TRx = Double.parseDouble(cornerTRx.getText());
            double TRy = Double.parseDouble(cornerTRy.getText());
            double BRx = Double.parseDouble(cornerBRx.getText());
            double BRy = Double.parseDouble(cornerBRy.getText());
            double BLx = Double.parseDouble(cornerBLx.getText());
            double BLy = Double.parseDouble(cornerBLy.getText());

            r = new CornerRadii(TLx, TLy, TRy, TRx, BRy, BRx, BLy, BLx, false, false, false, false, false, false, false, false);
        } catch (NumberFormatException ex) {
        }
        Paint fill = backgroundFill.get().getFill();
        Insets insets = backgroundFill.get().getInsets();
        backgroundFill.set(new BackgroundFill(fill, r, insets));
    }

}
