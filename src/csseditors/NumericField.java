/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
/**Experimental class for numerical validation of input fields.TO BE REMOVED
 * 
 * @author Siddhesh
 * @deprecated
 */

@Deprecated
public class NumericField implements ChangeListener<Number>, EventHandler<ActionEvent> {

    private final TextField tf;
    private final double min;
    private final double max;

    private final DoubleProperty value = new SimpleDoubleProperty();

    public NumericField(TextField tf, double min, double max) {
        this.tf = tf;
        this.min = min;
        this.max = max;

        tf.setOnAction(this);
        value.addListener(this);
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double d) {
        tf.setText("" + d);
    }

    public double getValue() {
        return value.get();
    }

    final double validateNumber(String num) {
        try {
            double parseDouble = Double.parseDouble(num);
            return parseDouble;
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    @Override
    public void handle(ActionEvent event) {
        double validateNumber = validateNumber(tf.getText());
        if (validateNumber == Double.NaN) {
            //tf.setText("Invalid Number!");
            tf.clear();
        } else if (validateNumber < min || validateNumber > max) {
            tf.setText("Enter in range " + min + "-" + max);
            tf.selectAll();
        } else {
            value.set(validateNumber);
        }
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        tf.setText(newValue.toString());
    }

}
