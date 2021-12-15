package com.example.heartsvalentine;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.heartsvalentine.viewModels.HeartsValBitmapViewModel;

// very useful link: https://stackoverflow.com/questions/7344497/android-canvas-draw-rectangle

public class HeartsValImage extends Fragment {
    private View view = null;

    public HeartsValImage() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hearts_val_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        HeartsValBitmapViewModel heartsValBitmapViewModel = new ViewModelProvider(requireActivity()).get(HeartsValBitmapViewModel.class);
        Bitmap bm = heartsValBitmapViewModel.getSelectedItem().getValue();
        heartsValBitmapViewModel.selectImageFragment(this);
        this.view = view;

        if (bm != null) {
            ImageView imageView = view.findViewById(R.id.imageView);
            imageView.setImageBitmap(bm);
        }
    }

    public void updateImage(Bitmap bm) {
        if (bm != null && view != null) {
            ImageView imageView = view.findViewById(R.id.imageView);
            imageView.setImageBitmap(bm);
        }
    }
}