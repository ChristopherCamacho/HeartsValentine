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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.example.heartsvalentine.hearts.DrawHeartsValentine;
import com.example.heartsvalentine.hearts.HeartsValException;
import com.example.heartsvalentine.hearts.TextFormattingDetails;
import com.example.heartsvalentine.viewModels.HeartValParametersViewModel;
import com.example.heartsvalentine.viewModels.TextInputViewModel;
import com.example.heartsvalentine.viewModels.UserFilesViewModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class UserFiles extends Fragment {

    HeartValParameters hvp;
    ListView userFileDetList;
    private FragmentActivity fragmentActivityContext;
    ArrayList<String> userFileList;
    private TextInputViewModel textInputViewModel;
    UserFileListAdapter userFileListAdapter;

    public UserFiles() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        View view = inflater.inflate(R.layout.fragment_user_files, container, false);

        final View backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> setToTextInputFragment());

        final View saveUserFileButton = view.findViewById(R.id.saveFileButton);
        saveUserFileButton.setOnClickListener(v -> saveFile(view));

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        fragmentActivityContext = (FragmentActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        textInputViewModel = new ViewModelProvider(requireActivity()).get(TextInputViewModel.class);
        UserFilesViewModel userFilesViewModel = new ViewModelProvider(requireActivity()).get(UserFilesViewModel.class);
        userFileList = userFilesViewModel.getSelectedItem().getValue();

        HeartValParametersViewModel heartValParametersViewModel = new ViewModelProvider(requireActivity()).get(HeartValParametersViewModel.class);
        hvp = heartValParametersViewModel.getSelectedItem().getValue();

        userFileDetList = view.findViewById(R.id.userFileListView);
        userFileListAdapter = new UserFileListAdapter(view.getContext(), userFileList, textInputViewModel, this);
        userFileDetList.setAdapter(userFileListAdapter);
    }

    void setToTextInputFragment() {
        Fragment fragment;
        fragment = new TextInput();

        FragmentManager fragmentManager =  fragmentActivityContext.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.text_input_frame, fragment)
                .setReorderingAllowed(true)
                .commit()
                ;
    }

    void saveFile(View view) {
        try {
            EditText fileName = view.findViewById(R.id.fileName);
            String strFileName = fileName.getText().toString();

            if (strFileName.length() == 0) {
                throw new HeartsValException(enterFileNameErrMsg());
            }

            String strTextInput = textInputViewModel.getSelectedItem().getValue();

            TextFormattingDetails tfd = new TextFormattingDetails(strTextInput, hvp.getOptimizeSpacing(), hvp.getHyphenateText(),
                    hvp.getHyphenFileName(), 50, 170, hvp.getTxtHeartsMargin(), hvp.getTextColor());
            DrawHeartsValentine heartsValentine = new DrawHeartsValentine(tfd, hvp.getUseEmoji(), hvp.getHeartsColor(), hvp.getBackgroundColor(), hvp.getOuterMargin());
            heartsValentine.computeTextFit(getContext());

            File userFile = new File(MainActivity.getUserFileFolder(true) + strFileName);
            FileWriter writer = new FileWriter(userFile);
            writer.append(strTextInput);
            writer.flush();
            writer.close();
            userFileList.add(strFileName);
            userFileListAdapter.notifyDataSetChanged();
            fileName.setText("");
        }
        catch (HeartsValException | IOException e){
            new AlertDialog.Builder(getContext())
                    .setTitle(generateImageTitle())
                    .setMessage(e.getMessage())
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

    String enterFileNameErrMsg() {
        String enterFileNameErrMsg = "Enter a file name";

        if (getContext() != null && getContext().getResources() != null) {
            enterFileNameErrMsg = getContext().getResources().getString(R.string.enter_file_name);
        }
        return enterFileNameErrMsg;
    }
}