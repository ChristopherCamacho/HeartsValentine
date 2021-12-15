package com.example.heartsvalentine.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.heartsvalentine.HyphenDetails;

import java.util.ArrayList;

public class HyphenDetailsListViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<HyphenDetails>> selectedItem = new MutableLiveData<>();
    public void selectItem(ArrayList<HyphenDetails> item) {
        selectedItem.setValue(item);
    }
    public LiveData<ArrayList<HyphenDetails>> getSelectedItem() {
        return selectedItem;
    }
}