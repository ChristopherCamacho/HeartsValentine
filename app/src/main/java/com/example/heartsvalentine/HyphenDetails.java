package com.example.heartsvalentine;

public class HyphenDetails {

    HyphenDetails(int id, String hpl, boolean downLoaded, String fileName, String downloadLink) {
        this.id = id;
        this.hpl = hpl;
        this.downLoaded = downLoaded;
        this.fileName = fileName;
        this.downloadLink = downloadLink;
    }

    int getId() {
        return id;
    }

    String getHyphenatePatternLanguage() {
        return hpl;
    }

    boolean getDownLoaded() {
        return downLoaded;
    }

    void setDownLoaded(boolean downLoaded) {
        this.downLoaded = downLoaded;
    }

    String getFileName() {
        return fileName;
    }

    String getDownloadLink() {
        return downloadLink;
    }

    private final int id;
    private final String hpl;
    private boolean downLoaded;
    private final String fileName;
    private final String downloadLink;
}
