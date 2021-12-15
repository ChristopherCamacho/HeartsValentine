package com.example.heartsvalentine.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TextInputViewModel  extends ViewModel {
    private final MutableLiveData<String> selectedItem = new MutableLiveData<>();
    public void selectItem(String item) {
        selectedItem.setValue(item);
    }
    public LiveData<String> getSelectedItem() {
        return selectedItem;
    }
}
