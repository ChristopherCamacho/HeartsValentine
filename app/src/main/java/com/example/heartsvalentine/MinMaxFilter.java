package com.example.heartsvalentine;

import android.text.InputFilter;
import android.text.Spanned;
public class MinMaxFilter implements InputFilter {
    private final int mIntMin, mIntMax;
    public MinMaxFilter (String minValue , String maxValue) {
        this . mIntMin = Integer. parseInt (minValue) ;
        this . mIntMax = Integer. parseInt (maxValue) ;
    }
    @Override
    public CharSequence filter (CharSequence source , int start , int end , Spanned dest ,int dStart , int dEnd) {
        try {
            int input = Integer. parseInt (dest.toString() + source.toString());
            if (isInRange( mIntMin , mIntMax , input))
                return null;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }
    private boolean isInRange ( int a , int b , int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a ;
    }
}
