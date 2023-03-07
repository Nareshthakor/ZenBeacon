package com.qsmp.Cardviewpager_extra;

/**
 * Model Created by farshid roohi on 12/12/17.
 */

public class Scan_Model {
    private String title;
    private int bgColor;

    public Scan_Model(String title, int bgColor) {
        this.title = title;
        this.bgColor = bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBgColor() {
        return bgColor;
    }

    public String getTitle() {
        return title;
    }
}
