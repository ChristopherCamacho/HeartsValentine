package com.example.heartsvalentine.hearts;

import android.graphics.Paint;

public class EmojiHelper {
    public static boolean isCharEmojiAtPos(String str, int pos) {
        char chr = str.charAt(pos);
        return chr == 0xD83C || 0xD83D == chr;
    }
    // We already know we have an emoji
    public static int emojiLengthAtPos(String str, int pos) {
        int new_pos = pos + 2; // Length 2 is min

        boolean isCountryFlag = false; // Used country as in computing flag has a different meaning.

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
