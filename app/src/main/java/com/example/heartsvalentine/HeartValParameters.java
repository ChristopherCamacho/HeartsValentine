package com.example.heartsvalentine;

import com.example.heartsvalentine.hearts.shapeDetails.DrawCircleDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawHeartDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawSquareDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawStarDetails;
import com.example.heartsvalentine.hearts.shapeDetails.EmojiShapeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;

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
    private String emoji = Character.toString((char) 0x2665);
    private ShapeType shapeType = ShapeType.StraightHeart;
    private ShapeType mainShape = ShapeType.StraightHeart;

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

    void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    String getEmoji() {
        return emoji;
    }

    void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    ShapeType getShapeType() {
        return shapeType;
    }

    ShapeDetails getShapeDetails() {
        if (useEmoji) {
            return new EmojiShapeDetails(emoji);
        }

        switch (shapeType) {
            case StraightHeart:
                return new DrawHeartDetails(heartsColor, 92);
            case Circle:
                return new DrawCircleDetails(heartsColor, 92);
            case Square:
                return new DrawSquareDetails(heartsColor, 92);
            case Star:
                return new DrawStarDetails(heartsColor, 92);
        }

        return null;
    }

    void setMainShape(ShapeType shapeType) {
        this.mainShape = shapeType;
    }

    ShapeType getMainShape() {
        return mainShape;
    }
}
