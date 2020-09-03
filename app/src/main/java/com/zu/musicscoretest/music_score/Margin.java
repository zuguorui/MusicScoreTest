package com.zu.musicscoretest.music_score;

import androidx.annotation.NonNull;

public class Margin implements Cloneable {
    public float leftMargin = 0f;
    public float rightMargin = 0f;
    public float topMargin = 0f;
    public float bottomMargin = 0f;

    public Margin()
    {

    }

    public Margin(float leftMargin, float topMargin, float rightMargin, float bottomMargin)
    {
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;
        this.rightMargin = rightMargin;
        this.bottomMargin = bottomMargin;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        Margin result = new Margin(leftMargin, topMargin, rightMargin, bottomMargin);
        return result;
    }
}
