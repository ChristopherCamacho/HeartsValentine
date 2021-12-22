package com.example.heartsvalentine;

// This class holds all the parameters needed to create the image
public class HeartValParameters {
    private String hyphenFileName = null;
    private boolean optimizeSpacing = true;
    private boolean hyphenateText = false;
    private int txtHeartsMargin = 20;
    private static final int maxTxtHeartsMargin = 50;
    private int outerMargin = 15;
    private int textColor = 0xFF000000;
    private int heartsColor = 0xFFFF0000;
    private int backgroundColor =0xFFFFFFFF;
    private boolean useEmoji = false;

    HeartValParameters() {
    }

    void setHyphenFileName(String hyphenFileName) {
        this.hyphenFileName = hyphenFileName;
    }

    String getHyphenFileName() {
        return hyphenFileName;
    }

    void setOptimizeSpacing(boolean optimizeSpacing) {
        this.optimizeSpacing = optimizeSpacing;
    }

    boolean getOptimizeSpacing() {
        return optimizeSpacing;
    }

    void setHyphenateText(boolean hyphenateText) {
        this.hyphenateText = hyphenateText;
    }

    boolean getHyphenateText() {
        return hyphenateText;
    }

    void setTxtHeartsMargin(int txtHeartsMargin) {
        if (txtHeartsMargin <= maxTxtHeartsMargin)
            this.txtHeartsMargin = txtHeartsMargin;
    }

    int getTxtHeartsMargin() {
        return txtHeartsMargin;
    }

    void setOuterMargin(int outerMargin) {
        this.outerMargin = outerMargin;
    }

    int getOuterMargin() {
        return outerMargin;
    }

    void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    int getTextColor() {
        return textColor;
    }

    void setHeartsColor(int heartsColor) {
        this.heartsColor = heartsColor;
    }

    int getHeartsColor() {
        return heartsColor;
    }

    void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    int getBackgroundColor() {
        return backgroundColor;
    }

    void setUseEmoji(boolean useEmoji) {
        this.useEmoji = useEmoji;
    }

    boolean getUseEmoji() {
        return useEmoji;
    }
}
