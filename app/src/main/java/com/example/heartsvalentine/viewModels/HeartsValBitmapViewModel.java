package com.example.heartsvalentine.viewModels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.heartsvalentine.HeartsValImage;

public class HeartsValBitmapViewModel extends ViewModel {
    private final MutableLiveData<Bitmap> selectedItem = new MutableLiveData<>();
    public void selectItem(Bitmap item) {
        selectedItem.setValue(item);
    }
    public LiveData<Bitmap> getSelectedItem() {
        return selectedItem;
    }

    private final MutableLiveData<HeartsValImage> selectedImageFragment = new MutableLiveData<>();
    public void selectImageFragment(HeartsValImage item) {
        selectedImageFragment.setValue(item);
    }
    public LiveData<HeartsValImage> getSelectedImageFragment() {
        return selectedImageFragment;
    }
}