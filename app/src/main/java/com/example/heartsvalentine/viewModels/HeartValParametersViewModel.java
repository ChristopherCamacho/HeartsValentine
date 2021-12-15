package com.example.heartsvalentine.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.heartsvalentine.HeartValParameters;

public class HeartValParametersViewModel extends ViewModel {
    private final MutableLiveData<HeartValParameters> selectedItem = new MutableLiveData<>();
    public void selectItem(HeartValParameters item) {
        selectedItem.setValue(item);
    }
    public LiveData<HeartValParameters> getSelectedItem() {
        return selectedItem;
    }
}

