package com.example.heartsvalentine;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.heartsvalentine.hearts.DrawHeartsValentine;
import com.example.heartsvalentine.hearts.HeartsValException;
import com.example.heartsvalentine.hearts.TextFormattingDetails;
import com.example.heartsvalentine.viewModels.HeartValParametersViewModel;
import com.example.heartsvalentine.viewModels.HeartsValBitmapViewModel;
import com.example.heartsvalentine.viewModels.TabLayoutViewModel;
import com.example.heartsvalentine.viewModels.TextInputViewModel;
import com.google.android.material.tabs.TabLayout;

public class TextInput extends Fragment {

    private FragmentActivity fragmentActivityContext;
    private TextInputViewModel textInputViewModel;
    private HeartsValBitmapViewModel heartsValBitmapViewModel;
    private TabLayoutViewModel tabLayoutViewModel;
    private HeartValParametersViewModel heartValParametersViewModel;

    public TextInput() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                      Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View view = inflater.inflate(R.layout.fragment_text_input, container, false);

        final View generateImageButton = view.findViewById(R.id.generateImageButton);
        generateImageButton.setOnClickListener(v -> generateImage(view));

        final View loadSaveFileNavButton = view.findViewById(R.id.loadSaveFileNavButton);
        loadSaveFileNavButton.setOnClickListener(v -> OnExitToUserFilesFragment(view));

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        fragmentActivityContext = (FragmentActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        textInputViewModel = new ViewModelProvider(requireActivity()).get(TextInputViewModel.class);
        heartsValBitmapViewModel = new ViewModelProvider(requireActivity()).get(HeartsValBitmapViewModel.class);
        tabLayoutViewModel = new ViewModelProvider(requireActivity()).get(TabLayoutViewModel.class);
        heartValParametersViewModel = new ViewModelProvider(requireActivity()).get(HeartValParametersViewModel.class);

        EditText editTextInput = view.findViewById(R.id.editTextInput);
        editTextInput.setText((CharSequence) textInputViewModel.getSelectedItem().getValue());
    }

    void OnExitToUserFilesFragment(View view) {
        EditText editTextInput = view.findViewById(R.id.editTextInput);
        textInputViewModel.selectItem(editTextInput.getText().toString());
        setToUserFilesFragment();
    }

    void setToUserFilesFragment() {
        Fragment fragment = new UserFiles();

        FragmentManager fragmentManager =  fragmentActivityContext.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.text_input_frame, fragment)
                .setReorderingAllowed(true)
                .commit();
    }

    void generateImage(View view) {
        try {
            HeartValParameters hvp = heartValParametersViewModel.getSelectedItem().getValue();

            if (hvp != null) {
                EditText editTextInput = view.findViewById(R.id.editTextInput);
                String strTextInput = editTextInput.getText().toString();
                TextFormattingDetails tfd = new TextFormattingDetails(strTextInput, hvp.getOptimizeSpacing(), hvp.getHyphenateText(),
                        hvp.getHyphenFileName(), 50, 170, hvp.getTxtHeartsMargin(), hvp.getTextColor());
                DrawHeartsValentine heartsValentine = new DrawHeartsValentine(tfd, hvp.getUseEmoji(), hvp.getHeartsColor(), hvp.getBackgroundColor(), hvp.getOuterMargin());
                heartsValentine.computeTextFit(getContext());
                heartsValentine.draw();

                //MainActivity.setHeartValBitmap(heartsValentine.GetHeartValBitmapImage());
                // generate image file in Pictures folder:
                if (getActivity() != null) {
                    MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), heartsValentine.GetHeartValBitmapImage(), "Hearts Valentine", null);
                }
                heartsValBitmapViewModel.selectItem(heartsValentine.GetHeartValBitmapImage());

                HeartsValImage hvi = heartsValBitmapViewModel.getSelectedImageFragment().getValue();

                if (hvi != null) {
                    hvi.updateImage(heartsValentine.GetHeartValBitmapImage());
                }

            }
            TabLayout tabLayout = tabLayoutViewModel.getSelectedItem().getValue();

            if (tabLayout != null) {
                TabLayout.Tab tab = tabLayout.getTabAt(2);

                if (tab != null) {
                    tab.select();
                }
            }
        }
        catch (HeartsValException e) {
            new AlertDialog.Builder(getContext())
                    .setTitle(generateImageTitle())
                    .setMessage(e.getMessage())
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.alert_light_frame)
                    .show();
        }
        catch (Exception e) {
            new AlertDialog.Builder(getContext())
                    .setTitle(generateImageTitle())
                    .setMessage(getUnexpectedErrorMessage() + "\n" + e.getMessage())
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.alert_light_frame)
                    .show();

        }
    }
    String generateImageTitle() {
        String generateImageTitle = "Generate image";

        if (getContext() != null && getContext().getResources() != null) {
            generateImageTitle = getContext().getResources().getString(R.string.generateImage);
        }
        return generateImageTitle;
    }
    String getUnexpectedErrorMessage() {
        String unexpectedError = "An unexpected error occurred.";

        if (getContext() != null && getContext().getResources() != null) {
            getContext().getResources().getString(R.string.error_unexpected);
        }
        return unexpectedError;
    }
}