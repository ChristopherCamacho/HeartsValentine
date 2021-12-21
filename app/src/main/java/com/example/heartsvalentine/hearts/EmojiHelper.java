package com.example.heartsvalentine.hearts;

import android.graphics.Paint;

public class EmojiHelper {
    public static boolean isCharEmojiAtPos(String str, int pos) {
        char chr = str.charAt(pos);
        // chr == 0x270D is an annoying exception - it's the writing hand that can be coloured like other emojis
        // and has only 1 utf 8 character, instead of 2.
        return chr == 0xD83C || chr == 0xD83D || chr == 0xD83E || chr == 0x270D;
    }
    // We already know we have an emoji
    public static int emojiLengthAtPos(String str, int pos) {
        final int pos_incr = (str.charAt(pos) == 0x270D)? pos + 1 : pos + 2; // Length 2 is min except for hand exception
        int new_pos = pos_incr;

        boolean isCountryFlag = false; // Used country as in computing flag has a different meaning.
        boolean activateSkinColorFilters = false;

        if (pos + 1 < str.length()) {
            char chr = str.charAt(pos + 1);

            if (chr > 0xDDE0 && chr <= 0xDDFF) {
                isCountryFlag = true;
            }
        }

        for (;;) {
            if (new_pos >= str.length()) {
                break; // Security but should never get here.
            }
            char chr = str.charAt(new_pos);
            if (chr == 0xFE0F) {
                new_pos++;
            }
            else if (chr == 0x200D) {
                new_pos += 3;
            }
            else if (isCountryFlag) {
                if (new_pos == pos + 2) {
                    if (chr == 0xD83C) {
                        new_pos++;
                    }
                    else {
                        break;
                    }
                }
                else if (new_pos == pos + 3) {
                    if (chr > 0xDDE0 && chr <= 0xDDFF) {
                        new_pos++;
                    }
                    else {
                        new_pos--;
                    }
                    break;
                }
            }
            else if (new_pos == pos_incr && chr == 0xD83C) { // colored body parts handled here
                new_pos++;
                activateSkinColorFilters = true;
            }
            else if (activateSkinColorFilters) {
                if (chr >= 0xDFFB && chr <= 0xDFFF) {
                    new_pos++;
                    activateSkinColorFilters = false;
                } else {
                    new_pos--;
                    break;
                }
            }
            else {
                break;
            }
        }
        return new_pos - pos;
    }

    public static float getEmojiWidth(String str, int pos, int len, Paint paint) {
        return paint.measureText(str.substring(pos, pos + len));
    }
}
