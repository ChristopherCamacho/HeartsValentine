package com.example.heartsvalentine.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.material.tabs.TabLayout;

public class TabLayoutViewModel extends ViewModel {
    private final MutableLiveData<TabLayout> selectedItem = new MutableLiveData<>();
    public void selectItem(TabLayout item) {
        selectedItem.setValue(item);
    }
    public LiveData<TabLayout> getSelectedItem() {
        return selectedItem;
    }
}
