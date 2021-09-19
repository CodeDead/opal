package com.codedead.opal.domain;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public final class NumberTextField extends TextField {

    /**
     * Initialize a new NumberTextField
     */
    public NumberTextField() {
        this.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.UP) {
                int currentValue = 0;
                if (!this.getText().isEmpty()) {
                    currentValue = Integer.parseInt(this.getText());
                }
                currentValue++;
                this.setText(Integer.toString(currentValue));
            } else if (e.getCode() == KeyCode.DOWN) {
                int currentValue = 0;
                if (!this.getText().isEmpty()) {
                    currentValue = Integer.parseInt(this.getText());
                }
                currentValue--;
                this.setText(Integer.toString(currentValue));
            }
        });
    }

    @Override
    public void replaceText(final int start, final int end, final String text) {
        if (validate(text)) {
            super.replaceText(start, end, text);
        }
    }

    @Override
    public void replaceSelection(final String text) {
        if (validate(text)) {
            super.replaceSelection(text);
        }
    }

    /**
     * Validate whether the text is a number or not
     *
     * @param text The text that should be validated
     * @return True if the text is a number, otherwise false
     */
    private boolean validate(final String text) {
        return text.matches("[0-9]*");
    }
}
