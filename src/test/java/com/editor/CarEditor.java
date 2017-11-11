package com.editor;

import com.entity.Car;

import java.beans.PropertyEditorSupport;

public class CarEditor extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text != null){
            String[] strings = text.split(",");
            String color = strings[0];
            String price = strings[1];
            super.setValue(new Car(color,price));
        }
    }
}
