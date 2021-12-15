package com.example.heartsvalentine.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class HyphenFilesListViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<String>> selectedItems = new MutableLiveData<>();
    public void selectItems(ArrayList<String> items) {
        selectedItems.setValue(items);
    }
    public LiveData<ArrayList<String>> getSelectedItem() {
        return selectedItems;
    }
}
