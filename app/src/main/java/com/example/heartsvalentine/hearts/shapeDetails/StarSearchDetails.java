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

    @Override
    public boolean equals(Object object) {
        if (object instanceof StarSearchDetails) {
            StarSearchDetails ssd = (StarSearchDetails)object;
            if (this.widthHeight == ssd.widthHeight) {
                if (this.innerRadius == ssd.innerRadius) {
                    if (this.startAngle == ssd.startAngle) {
                        return (this.spikes == ssd.spikes);
                    }
                    else {
                        return false;
                    }

                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashcode = 1430287;
        hashcode = hashcode * 7302013 ^ String.valueOf(widthHeight).hashCode();
        hashcode = hashcode * 7302013 ^ String.valueOf(innerRadius).hashCode();
        hashcode = hashcode * 7302013 ^ String.valueOf(startAngle).hashCode();
        hashcode = hashcode * 7302013 ^ String.valueOf(spikes).hashCode();
        return hashcode;
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
