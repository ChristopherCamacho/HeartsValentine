package com.example.heartsvalentine.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class FileNameHplMapViewModel extends ViewModel {
    private final MutableLiveData<HashMap<String, String>> selectedItem = new MutableLiveData<>();
    public void selectItem(HashMap<String, String> item) {
        selectedItem.setValue(item);
    }
    public LiveData<HashMap<String, String>> getSelectedItem() {
        return selectedItem;
    }
}
