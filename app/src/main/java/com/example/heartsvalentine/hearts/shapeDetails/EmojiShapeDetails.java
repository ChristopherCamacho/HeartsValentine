package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class EmojiShapeDetails implements ShapeDetails {

    private final String emoji;
    private float centerX;
    private float centerY;
    private float width;
    private float height;

    public EmojiShapeDetails(String emoji) {
        this.emoji = emoji;
        initialize();
    }

    private void initialize() {
        Typeface tf = Typeface.create("TimesRoman", Typeface.NORMAL);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(tf);
        paint.setTextSize(150);

        Rect rectHeart = new Rect();
        paint.getTextBounds(emoji, 0, emoji.length(), rectHeart);

        centerX = rectHeart.exactCenterX();
        // This looks a quickfix for heart shape. Investigate properly when time.
        centerY = rectHeart.exactCenterY() - 12; // have to add 12 or trespasses

        switch (emoji) {
            case "\uD83D\uDC8C️": // This is heart over envelop emoji 💌
                width = (float) (rectHeart.width() * 0.89);
                height = (float) (rectHeart.height() * 0.63);
                break;

            case "\uD83D\uDC93": // vibrating heart 💓
                width = (float) (rectHeart.width() * 0.95);
                height = (float) (rectHeart.height() * 0.93);
                break;

            case "\uD83D\uDC95":  // 2 hearts next to each other but not circling💕
            case "\uD83D\uDC9E": // 2 circling hearts next to each other 💞
            case "\uD83D\uDC98":  // Arrow pierced heart    💘
            case "\uD83D\uDD34":  // red circle 🔴
            case "\uD83D\uDFE0": // orange circle 🟠
            case "\uD83D\uDFE1": // yellow circle 🟡
            case "\uD83D\uDFE2": // green circle 🟢
            case "\uD83D\uDD35": // blue circle
            case "\uD83D\uDFE3": // purple circle
            case "⚫": // black circle
            case"⚪": // white circle
            case "\uD83D\uDFE4": // brown circle🟤
            case "\uD83D\uDD36": // yellow large diamond 🔶
            case "\uD83D\uDD37": // blue large diamond 🔷
            case "\uD83D\uDFE5": // red square 🟥
            case "\uD83D\uDFE7": // orange square 🟧
            case "\uD83D\uDFE8": // yellow square 🟨
            case "\uD83D\uDFE9": // green square 🟩
            case "\uD83D\uDFE6": // blue square 🟦
            case "\uD83D\uDFEA": // purple square 🟪
            case "⬛": // black square ⬛
            case "⬜": // white square ⬜
            case "\uD83D\uDFEB": // brown square 🟫
            case "\uD83D\uDCA5": // explosion 💥
            case "☀": // Sun ☀️
                width = (float) (rectHeart.width() * 0.89);
                height = (float) (rectHeart.height() * 0.95);
                break;

            case "\uD83D\uDD3A": // Up red triangle 🔺
            case "\uD83D\uDD3B": // Down red triangle 🔻
                width = (float) (rectHeart.width() * 0.48);
                height = (float) (rectHeart.height() * 0.46);
                break;

            case "\uD83D\uDD38": // yellow small diamond 🔸
            case "\uD83D\uDD39": // blue small diamond 🔹
                width = (float) (rectHeart.width() * 0.34);
                height = (float) (rectHeart.height() * 0.36);
                break;

            case "♠": // spade ♠️
                width = (float) (rectHeart.width() * 0.80);
                height = (float) (rectHeart.height() * 0.95);
                break;

            case "♣": // club ♣
                width = (float) (rectHeart.width() * 0.86);
                height = (float) (rectHeart.height() * 0.95);
                break;

            case "♦": // diamond ♦
                width = (float) (rectHeart.width() * 0.65);
                height = (float) (rectHeart.height() * 0.95);
                break;

            case "\uD83D\uDCA7":    // Water drop 💧
                width = (float) (rectHeart.width() * 0.54);
                height = (float) (rectHeart.height() * 0.95);
                break;

            case "\uD83E\uDE78": // Blood drop 🩸
                width = (float) (rectHeart.width() * 0.60);
                height = (float) (rectHeart.height() * 0.93);
                break;

            case "⭐": // Star
                width = (float) (rectHeart.width() * 0.89);
                height = (float) (rectHeart.height() * 0.93);
                break;

            case "\uD83C\uDF1F": // Shaking star 🌟
                width = (float) (rectHeart.width() * 0.89);
                height = (float) (rectHeart.height() * 0.99);
                break;

            case "✨": // 3 stars ✨
                width = (float) (rectHeart.width() * 0.89);
                height = (float) (rectHeart.height() * 0.92);
                break;

            case "\uD83D\uDD25": // fire 🔥
                width = (float) (rectHeart.width() * 0.70);
                height = (float) (rectHeart.height() * 0.95);
                break;

            case "\uD83D\uDC9D": // Present, wrapped heart with gold ribbon
                width = (float) (rectHeart.width() * 1.0);
                height = (float) (rectHeart.height() * 0.87);

            default:
                width = (float) (rectHeart.width() * 0.89);
                height = (float) (rectHeart.height() * 0.87);
        }
    }

    public float getVerticalAdjustment() {

        switch (emoji) {
            case "\uD83D\uDC8C️": // This is heart over envelop emoji 💌
                return 28.5f;
            case "\uD83D\uDC95":  // 2 hearts next to each other but not circling💕
                return 2;
            case "\uD83D\uDC9E": // 2 circling hearts next to each other 💞
                return 0;
            case "\uD83D\uDC93": // vibrating heart 💓
                return 0;
            case "\uD83D\uDC98":  // Arrow pierced heart    💘
                return 0;
            case "\uD83D\uDD34":  // red circle 🔴
            case "\uD83D\uDFE0": // orange circle 🟠
            case "\uD83D\uDFE1": // yellow circle 🟡
            case "\uD83D\uDFE2": // green circle 🟢
            case "\uD83D\uDD35": // blue circle
            case "\uD83D\uDFE3": // purple circle
            case "⚫": // black circle
            case"⚪": // white circle
            case "\uD83D\uDFE4": // brown circle🟤
                return 0;
            case "\uD83D\uDD3A": // Up red triangle 🔺
            case "\uD83D\uDD3B": // Down red triangle 🔻
                return 45;
            case "\uD83D\uDD38": // yellow small diamond 🔸
            case "\uD83D\uDD39": // blue small diamond 🔹
                return 52;
            case "\uD83D\uDD36": // yellow large diamond 🔶
            case "\uD83D\uDD37": // blue large diamond 🔷
            case "\uD83D\uDFE5": // red square 🟥
            case "\uD83D\uDFE7": // orange square 🟧
            case "\uD83D\uDFE8": // yellow square 🟨
            case "\uD83D\uDFE9": // green square 🟩
            case "\uD83D\uDFE6": // blue square 🟦
            case "\uD83D\uDFEA": // purple square 🟪
            case "⬛": // black square ⬛
            case "⬜": // white square ⬜
            case "\uD83D\uDFEB": // brown square 🟫
            case "♠": // spade ♠️
            case "♣": // club ♣
            case "♦": // diamond ♦
            case "\uD83D\uDCA7":    // Water drop 💧
                return 1;
            case "\uD83E\uDE78": // Blood drop 🩸
                return 2;
            case "⭐": // Star
                return 4;
            case "\uD83C\uDF1F": // Shaking star 🌟
                return -5;
            case "✨": // 3 stars ✨
                return 4;
            case "\uD83D\uDCA5": // explosion 💥
                return 2;
            case "\uD83D\uDD25": // fire 🔥
                return 0;
            case "☀": // Sun ☀️
                return 0;
        }

        return 8;
    }

    public float getHorizontalAdjustment() {

        switch (emoji) {
            case "\uD83D\uDC98":  // Arrow pierced heart    💘
                return -10;
            case "\uD83D\uDC9D": // // Present, wrapped heart with gold ribbon
                return -10;
            case "\uD83D\uDD34":  // red circle 🔴
            case "\uD83D\uDFE0": // orange circle 🟠
            case "\uD83D\uDFE1": // yellow circle 🟡
            case "\uD83D\uDFE2": // green circle 🟢
            case "\uD83D\uDD35": // blue circle
            case "\uD83D\uDFE3": // purple circle
            case "⚫": // black circle
            case"⚪": // white circle
            case "\uD83D\uDFE4": // brown circle🟤
                return -10;
            case "\uD83D\uDD3A": // Up red triangle 🔺
            case "\uD83D\uDD3B": // Down red triangle 🔻
                return -47;
            case "\uD83D\uDD38": // yellow small diamond 🔸
            case "\uD83D\uDD39": // blue small diamond 🔹
                return -60;
            case "\uD83D\uDD36": // yellow large diamond 🔶
            case "\uD83D\uDD37": // blue large diamond 🔷
            case "\uD83D\uDFE5": // red square 🟥
            case "\uD83D\uDFE7": // orange square 🟧
            case "\uD83D\uDFE8": // yellow square 🟨
            case "\uD83D\uDFE9": // green square 🟩
            case "\uD83D\uDFE6": // blue square 🟦
            case "\uD83D\uDFEA": // purple square 🟪
            case "⬛": // black square ⬛
            case "⬜": // white square ⬜
            case "\uD83D\uDFEB": // brown square 🟫
                return -11;
            case "♠": // spade ♠️
                return -18;
            case "♣": // club ♣
                return -13;
            case "♦": // diamond ♦
                return -32;
            case "\uD83D\uDCA7":    // Water drop 💧
                return -44;
            case "\uD83E\uDE78": // Blood drop 🩸
                return -37;
            case "⭐": // Star
                return -10;
            case "\uD83C\uDF1F": // Shaking star 🌟
                return -10;
            case "✨": // 3 stars ✨
                return -10;
            case "\uD83D\uDCA5": // explosion 💥
                return -10;
            case "\uD83D\uDD25": // fire 🔥
                return -28;
            case "☀": // Sun ☀️
                return -11;
        }

        return -8;
    }

/* Utility for testing placement of emojis.
    public void drawEmojiWithBoundaries(Canvas canvas, Paint paint) {
        // This code draws a rectangle and the bounding rect that should touch edges of heart.
        // Adjusted rect so heart reaches its sides
        paint.setColor(Color.BLUE);
 //       canvas.drawText(emoji, 150, 150, paint);
        canvas.drawRect((float)150  + centerX - width/2,
                (float)150 + centerY - height/2,
        		150 + centerX + width/2,
        		150 + centerY + height/2, paint);
         canvas.drawText(emoji, 150, 150, paint);
    }
*/

    @Override
    public void draw(Canvas canvas, float x, float y, Paint paint) {
        canvas.drawText(emoji, x, y, paint);
    }

    @Override
    public float getCenterX() {
        return centerX;
    }

    @Override
    public float getCenterY() {
        return centerY;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getBottomAdjustment() {
        return -33;
    }

    @Override
    public int getClosestDistance() {
        return 250;
    }
}
