package com.example.heartsvalentine;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// Invalid ID issue
// https://stackoverflow.com/questions/66340454/invalid-id-0x00000001-errors-after-viewpager2-migration
public class HeartsValAdapter extends FragmentStateAdapter {
    Context context;
    int totalTabs;
    Settings settings = null;
    public HeartsValAdapter(Context c, FragmentActivity fa, int totalTabs) {
        super(fa);
        context = c;
        this.totalTabs = totalTabs;
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TextInput();
            case 1:
                return new Settings();
            case 2:
            default:
                return new HeartsValImage();
        }
    }
    @Override
    public int getItemCount() {
        return totalTabs;
    }

    public void saveSelectedItem() {
        if (settings != null) {
            settings.saveSelectedItem();
        }
    }

    public void updateHyphenDropdown() {
        if (settings != null) {
            settings.updateHyphenDropdown();
        }
    }
}
