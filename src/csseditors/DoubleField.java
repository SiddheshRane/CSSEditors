/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csseditors;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextField;

/**
 * Experimental class for numerical validation.
 *
 * @author Siddhesh
 */
public class DoubleField extends TextField {

    private DoubleProperty value;

    public double getValue() {
        return value.get();
    }

    public void setValue(double Value) {
        value.set(Value);
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    private DoubleProperty max;

    public double getMax() {
        return max == null ? Double.POSITIVE_INFINITY : max.get();
    }

    public void setMax(double value) {
        if (max == null) {
            max = new SimpleDoubleProperty(Double.POSITIVE_INFINITY);
        }
        max.set(value);
    }

    public DoubleProperty maxProperty() {

        if (max == null) {
            max = new SimpleDoubleProperty(Double.POSITIVE_INFINITY);
        }
        return max;
    }

    private final DoubleProperty min;

    public double getMin() {
        return min == null ? Double.NEGATIVE_INFINITY : min.get();
    }

    public void setMin(double value) {
        min.set(value);
    }

    public DoubleProperty minProperty() {
        return min;
    }

    public DoubleField() {
        this(0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public DoubleField(double value, double max, double min) {
        this.value = new SimpleDoubleProperty(value);
        this.min = new SimpleDoubleProperty(min);
        this.max = new SimpleDoubleProperty(max);

        textProperty().addListener((o) -> {
            validateNumber(getText());
        });
    }

    protected void validateNumber(String num) {
        try {
            double parseDouble = Double.parseDouble(num);
            if (parseDouble > max.doubleValue()) {
                value.set(max.doubleValue());
            } else if (parseDouble < min.doubleValue()) {
                value.set(min.doubleValue());
            } else {
                value.set(parseDouble);
            }
        } catch (NumberFormatException e) {
            setText(value.getValue().toString());

        }
    }

}
