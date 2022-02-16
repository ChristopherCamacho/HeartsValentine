package com.example.heartsvalentine;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.heartsvalentine.viewModels.FileNameHplMapViewModel;
import com.example.heartsvalentine.viewModels.HeartValParametersViewModel;
import com.example.heartsvalentine.viewModels.HplFileNameMapViewModel;
import com.example.heartsvalentine.viewModels.HyphenFilesListViewModel;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import top.defaults.colorpicker.ColorPickerPopup;

public class Settings extends Fragment {

    Spinner spinner;
    TextView needToDownloadText;
    androidx.appcompat.widget.SwitchCompat hyphenateSwitch;
    String selectedItem = null;
    ArrayList<String> hyphenFilesList;
    HashMap<String, String> hplFileNameMap;
    HashMap<String, String> fileNameHplMap;
    HeartValParameters hvp;
    boolean neededToDownloadText = false;
    boolean wasHyphenFileListEmpty;
    private FragmentActivity fragmentActivityContext;

    public Settings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container != null) {
            container.removeAllViews();
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        fragmentActivityContext = (FragmentActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        HeartValParametersViewModel heartValParametersViewModel = new ViewModelProvider(requireActivity()).get(HeartValParametersViewModel.class);
        hvp = heartValParametersViewModel.getSelectedItem().getValue();
        HyphenFilesListViewModel hyphenFilesListViewModel = new ViewModelProvider(requireActivity()).get(HyphenFilesListViewModel.class);
        hyphenFilesList = hyphenFilesListViewModel.getSelectedItem().getValue();
        HplFileNameMapViewModel hplFileNameMapViewModel = new ViewModelProvider(requireActivity()).get(HplFileNameMapViewModel.class);
        hplFileNameMap = hplFileNameMapViewModel.getSelectedItem().getValue();
        FileNameHplMapViewModel fileNameHplMapViewModel = new ViewModelProvider(requireActivity()).get(FileNameHplMapViewModel.class);
        fileNameHplMap = fileNameHplMapViewModel.getSelectedItem().getValue();

        final View button = view.findViewById(R.id.downloadHyphenNavButton);
        button.setOnClickListener(v -> navigateToHyphenationFragment());

        spinner = view.findViewById(R.id.spinner);
        needToDownloadText = view.findViewById(R.id.needToDownLoad);
        hyphenateSwitch = view.findViewById(R.id.hyphenateSwitch);

        selectedItem = fileNameHplMap.get(hvp.getHyphenFileName());

        updateHyphenDropdown();

        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        Object item = parent.getItemAtPosition(pos);
                        if (hplFileNameMap.containsKey(item.toString())) {
                            String fileName = hplFileNameMap.get(item.toString());
                            hvp.setHyphenFileName(fileName);
                        }
                    }
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        hyphenateSwitch.setChecked(hvp.getHyphenateText());
        hyphenateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> hvp.setHyphenateText(isChecked));

        /*
        androidx.appcompat.widget.AppCompatButton heartsColorButton = view.findViewById(R.id.heartsColorButton);
        heartsColorButton.setBackgroundColor(hvp.getHeartsColor());
        heartsColorButton.setOnClickListener(v -> onClickHeartsColorButton(v, heartsColorButton));
        */

        final View buttonNavToFrmShapeSettings = view.findViewById(R.id.frameShapeSettings);
        buttonNavToFrmShapeSettings.setOnClickListener(v -> navigateToFrameShapeSettingsFragment());

        androidx.appcompat.widget.AppCompatButton backgroundColorButton = view.findViewById(R.id.backgroundColorButton);
        backgroundColorButton.setBackgroundColor(hvp.getBackgroundColor());
        backgroundColorButton.setOnClickListener(v -> onClickBackgroundColorButton(v, backgroundColorButton));

        androidx.appcompat.widget.AppCompatButton textColorButton = view.findViewById(R.id.textColorButton);
        textColorButton.setBackgroundColor(hvp.getTextColor());
        textColorButton.setOnClickListener(v -> onClickTextColorButton(v, textColorButton));

        androidx.appcompat.widget.SwitchCompat optimizeSpacingSwitch = view.findViewById(R.id.optimizeSpacingSwitch);
        optimizeSpacingSwitch.setChecked(hvp.getOptimizeSpacing());
        optimizeSpacingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> hvp.setOptimizeSpacing(isChecked));

        EditText heartsToTextNumber = view.findViewById(R.id.heartsToTextNumber);
        heartsToTextNumber.setText(String.format(Locale.getDefault(), "%d", hvp.getTxtHeartsMargin()), TextView.BufferType.EDITABLE);
        heartsToTextNumber.setFilters( new InputFilter[]{ new MinMaxFilter( "0" , "50" )}) ;
        heartsToTextNumber.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    hvp.setTxtHeartsMargin(Integer.parseInt(s.toString()));
                }
                else {
                    hvp.setTxtHeartsMargin(0);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        EditText outerMarginNumber = view.findViewById(R.id.outerMarginNumber);
        outerMarginNumber.setText(String.format(Locale.getDefault(), "%d", hvp.getOuterMargin()), TextView.BufferType.EDITABLE);
        outerMarginNumber.setFilters( new InputFilter[]{ new MinMaxFilter( "0" , "500" )}) ;
        outerMarginNumber.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    hvp.setOuterMargin(Integer.parseInt(s.toString()));
                }
                else {
                    hvp.setOuterMargin(0);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        /*
        activateDeactivateHeartColorButton(view, !hvp.getUseEmoji());
        androidx.appcompat.widget.SwitchCompat useEmojiSwitch = view.findViewById(R.id.useEmojiSwitch);
        useEmojiSwitch.setChecked(hvp.getUseEmoji());
        useEmojiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {hvp.setUseEmoji(isChecked);
            activateDeactivateHeartColorButton(view, !isChecked);});
        */

        final View saveSettingsButton = view.findViewById(R.id.saveSettings);
        saveSettingsButton.setOnClickListener(v -> saveSettings());

        final View deleteSettingsButton = view.findViewById(R.id.deleteSettings);
        deleteSettingsButton.setOnClickListener(v -> deleteSettings());
    }
// https://stackoverflow.com/questions/14810348/fragment-is-not-being-replaced-but-put-on-top-of-the-previous-one
    void navigateToHyphenationFragment() {
        Fragment fragment = new HyphenFiles();

        FragmentManager fragmentManager =  fragmentActivityContext.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    void navigateToFrameShapeSettingsFragment() {
        Fragment fragment = new FrameShapes();

        FragmentManager fragmentManager =  fragmentActivityContext.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }
/*
    public void onClickHeartsColorButton(View v, androidx.appcompat.widget.AppCompatButton heartsColorButton) {
        if (!hvp.getUseEmoji()) {
            new ColorPickerPopup.Builder(getContext()).initialColor(hvp.getHeartsColor())
                    .enableBrightness(true)
                    .enableAlpha(true)
                    .okTitle(getResources().getString(R.string.choose))
                    .cancelTitle(getResources().getString(R.string.cancel))
                    .showIndicator(true)
                    .showValue(true)
                    .build()
                    .show(v,
                            new ColorPickerPopup.ColorPickerObserver() {
                                @Override
                                public void
                                onColorPicked(int color) {
                                    heartsColorButton.setBackgroundColor(color);
                                    hvp.setHeartsColor(color);
                                }
                            });
        }
    }
*/
    public void onClickBackgroundColorButton(View v, androidx.appcompat.widget.AppCompatButton backgroundColorButton) {
        new ColorPickerPopup.Builder(getContext()).initialColor(hvp.getBackgroundColor())
                .enableBrightness(true)
                .enableAlpha(true)
                .okTitle(getResources().getString(R.string.choose))
                .cancelTitle(getResources().getString(R.string.cancel))
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(v,
                        new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void
                            onColorPicked(int color) {
                                backgroundColorButton.setBackgroundColor(color);
                                hvp.setBackgroundColor(color);
                            }
                        });
    }

    public void onClickTextColorButton(View v, androidx.appcompat.widget.AppCompatButton textColorButton) {

        new ColorPickerPopup.Builder(getContext()).initialColor(hvp.getTextColor())
                .enableBrightness(true)
                .enableAlpha(true)
                .okTitle(getResources().getString(R.string.choose))
                .cancelTitle(getResources().getString(R.string.cancel))
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(v,
                        new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void
                            onColorPicked(int color) {
                                textColorButton.setBackgroundColor(color);
                                hvp.setTextColor(color);
                            }
                        });
    }

    void saveSelectedItem() {
        selectedItem = (String)spinner.getSelectedItem();
        neededToDownloadText = hyphenFilesList.isEmpty();
    }

    void updateHyphenDropdown() {
        if (hyphenFilesList.isEmpty()) {
            needToDownloadText.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
            hyphenateSwitch.setVisibility(View.GONE);
            wasHyphenFileListEmpty = true;
        } else {
            needToDownloadText.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            hyphenateSwitch.setVisibility(View.VISIBLE);
            Context context = this.getContext();

            // If the hyphenFile list was empty and has just been filled, we switch hyphenation on.
            if (wasHyphenFileListEmpty) {
                hyphenateSwitch.setChecked(true);
            }
            wasHyphenFileListEmpty = false;

            if (context != null) {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getContext(),
                        R.layout.spinner_list, hyphenFilesList);
                arrayAdapter.setDropDownViewResource(R.layout.spinner_list);
                spinner.setAdapter(arrayAdapter);
            }

            if (selectedItem != null && hyphenFilesList.contains(selectedItem)) {
                int pos = hyphenFilesList.indexOf(selectedItem);
                spinner.setSelection(pos);
            }

            if (neededToDownloadText) {
                hyphenateSwitch.setChecked(true);
            }
        }
    }
/*
    void activateDeactivateHeartColorButton(View view, boolean activate) {
        // This methods just gives the disable look.
        TextView heartColorText = view.findViewById(R.id.heartColorText);
        heartColorText.setTextColor(activate? Color.BLACK : Color.GRAY);

        View heartColorButtonFrame  = view.findViewById(R.id.heartColorButtonFrame);
        if (getContext() != null && getContext().getResources() != null) {
            heartColorButtonFrame.setBackgroundColor(activate ? getContext().getResources().getColor(R.color.highlightBlue) : getContext().getResources().getColor(R.color.midDayFog));
        }
        androidx.appcompat.widget.AppCompatButton heartsColorButton = view.findViewById(R.id.heartsColorButton);
        heartsColorButton.setBackgroundColor(activate? hvp.getHeartsColor() : hvp.getHeartsColor() & 0x88FFFFFF);
    }
    */

    void saveSettings() {
        try {
            JSONObject jsonObj = MainActivity.GetJSonObjectFromHeartValParameters(hvp);
            String hvpSettingsString = jsonObj.toString();
            Context context = getContext();

            if (context == null)
                throw new Exception("getContext returned null.");

            String settingsPathName = MainActivity.getSettingsFileName(getContext());
            if (settingsPathName != null) {
                File file = new File(settingsPathName);
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(hvpSettingsString);
                bufferedWriter.close();
                if (getContext() != null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(getContext().getResources().getString(R.string.saveSettings))
                            .setMessage(getContext().getResources().getString(R.string.saveSettingsInfo))
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, null)
                            .setIcon(android.R.drawable.alert_light_frame)
                            .show();
                }
            }
        }
        catch (Exception e) {
            System.out.println("An error occurred in method saveSettings().");
            e.printStackTrace();
        }
    }

    void deleteSettings() {
        try {
            Context context = getContext();

            if (context == null)
                throw new Exception("getContext returned null.");

            String settingsPathName = MainActivity.getSettingsFileName(context);

            if (settingsPathName != null) {
                File file = new File(settingsPathName);
                if (file.exists()) {
                    if (file.delete() && getContext() != null) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(getContext().getResources().getString(R.string.deleteSettings))
                                .setMessage(getContext().getResources().getString(R.string.deleteSettingsInfo))
                                .setCancelable(false)
                                .setPositiveButton(android.R.string.ok, null)
                                .setIcon(android.R.drawable.alert_light_frame)
                                .show();
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("An error occurred in method saveSettings().");
            e.printStackTrace();
        }
    }
}