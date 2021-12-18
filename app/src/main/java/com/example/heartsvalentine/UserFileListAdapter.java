package com.example.heartsvalentine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.heartsvalentine.viewModels.TextInputViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class UserFileListAdapter extends BaseAdapter {
    LayoutInflater userFilesInflater;
    ArrayList<String> userFileList;
    final private TextInputViewModel textInputViewModel;
    Context context;
    UserFiles userFile;

    UserFileListAdapter(Context context, ArrayList<String> userFileList, TextInputViewModel textInputViewModel, UserFiles userFile) {
        userFilesInflater = (LayoutInflater.from(context));
        this.context = context;
        this.userFileList = userFileList;
        this.textInputViewModel = textInputViewModel;
        this.userFile = userFile;
    }

    @Override
    public int getCount() {
        return  (userFileList != null)? userFileList.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return userFileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = userFilesInflater.inflate(R.layout.list_user_file_items, null);
        TextView userFileNameView = view.findViewById(R.id.fileName);
        userFileNameView.setText( userFileList.get(i));

        androidx.appcompat.widget.AppCompatButton loadButton = view.findViewById(R.id.loadButton);
        loadButton.setOnClickListener(v -> loadFile(i));

        androidx.appcompat.widget.AppCompatButton deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> deleteFile(i));

        return view;
    }

    void loadFile(int i) {
        String userFileFolder = MainActivity.getUserFileFolder(true, context);

        if (userFileFolder != null) {
            File usrFile = new File(userFileFolder + userFileList.get(i));

            if (usrFile.exists()) {
                StringBuilder fileContentBuilder = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(usrFile))) {
                    String currentLine;
                    while ((currentLine = br.readLine()) != null) {
                        fileContentBuilder.append(currentLine).append("\n");
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                String fileContent = fileContentBuilder.toString();
                textInputViewModel.selectItem(fileContent);
                userFile.setToTextInputFragment();
            }
        }
    }

    void deleteFile(int i) {
        String userFileFolder = MainActivity.getUserFileFolder(true, context);

        if (userFileFolder != null) {
            File usrFile = new File(userFileFolder + userFileList.get(i));

            if (usrFile.exists()) {
                if (usrFile.delete()) {
                    userFileList.remove(i);
                    this.notifyDataSetChanged();
                }
            }
        }
    }
}


