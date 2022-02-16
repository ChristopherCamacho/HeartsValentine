package com.example.heartsvalentine.hearts.shapeDetails;

public class StarSearchDetails {
    private final float widthHeight;
    private final float innerRadius;
    private final double startAngle;
    private final int spikes;

    StarSearchDetails(float widthHeight, float innerRadius, double startAngle, int spikes) {
        this.widthHeight = widthHeight;
        this.innerRadius = innerRadius;
        this.startAngle = startAngle;
        this.spikes = spikes;
    }

    public float getWidthHeight() {
        return widthHeight;
    }

    public float getInnerRadius() {
        return innerRadius;
    }

    public double getStartAngle() {
        return startAngle;
    }

    public int getSpikes() {
        return spikes;
    }
}
