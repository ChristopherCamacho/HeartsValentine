package com.example.heartsvalentine;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class HyphenDetailsListAdapter extends BaseAdapter {
    HyphenDetails[] hyphenDetails;
    LayoutInflater hyphenDetailsInflater;
    Context context;
    ArrayList<String> hyphenFilesList;

    HyphenDetailsListAdapter(Context context, HyphenDetails[] hyphenDetails, ArrayList<String> hyphenFilesList) {
        hyphenDetailsInflater = (LayoutInflater.from(context));
        this.hyphenDetails = hyphenDetails;
        this.context = context;
        this.hyphenFilesList = hyphenFilesList;
    }

    @Override
    public int getCount() {
        return hyphenDetails.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = hyphenDetailsInflater.inflate(R.layout.list_hyphen_items, null);

        TextView hyphenFileNameView = (TextView) view.findViewById(R.id.textView);
        hyphenFileNameView.setText(hyphenDetails[i].getHyphenatePatternLanguage());

        androidx.appcompat.widget.AppCompatButton downloadDeleteButton = (androidx.appcompat.widget.AppCompatButton) view.findViewById(R.id.button);

        if (hyphenDetails[i].getDownLoaded()) {
            setButtonToDeleteStatus(downloadDeleteButton, hyphenFileNameView);
        }
        else {
            setButtonToDownloadStatus(downloadDeleteButton, hyphenFileNameView);
        }

        downloadDeleteButton.setOnClickListener(v -> onDownloadDeleteButton(i, downloadDeleteButton, hyphenFileNameView));

        return view;
    }

    void onDownloadDeleteButton(int i, androidx.appcompat.widget.AppCompatButton downloadDeleteButton, TextView hyphenFileNameView) {
        if (hyphenDetails[i].getDownLoaded()) {
            String hyphenFileFolder = MainActivity.getHyphenFileFolder();

            if (hyphenFileFolder != null) {
                String hyphenFileName = hyphenFileFolder + hyphenDetails[i].getFileName();
                File hyphenFile = new File(hyphenFileName);

                if (hyphenFile.delete()) {
                    hyphenDetails[i].setDownLoaded(false);
                    setButtonToDownloadStatus(downloadDeleteButton, hyphenFileNameView);
                            hyphenFilesList.remove(hyphenDetails[i].getHyphenatePatternLanguage());
                        }
                    }
                }
                else {
                    DownloadHyphenFileTask dhfTask = new DownloadHyphenFileTask(i, downloadDeleteButton, hyphenFileNameView);
                    dhfTask.execute();
                    // so change to button immediately. If it fails, reverses back.
                    setButtonToDeleteStatus(downloadDeleteButton, hyphenFileNameView);
                }
            }



    // https://www.tutorialspoint.com/how-to-fix-android-os-networkonmainthreadexception
    private class DownloadHyphenFileTask extends AsyncTask<Void, Void, Void> {
        int idx;
        androidx.appcompat.widget.AppCompatButton downloadDeleteButton;
        TextView hyphenFileNameView;
        boolean success = false;

        DownloadHyphenFileTask(int idx, androidx.appcompat.widget.AppCompatButton downloadDeleteButton, TextView hyphenFileNameView) {
            this.idx = idx;
            this.downloadDeleteButton = downloadDeleteButton;
            this.hyphenFileNameView = hyphenFileNameView;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(hyphenDetails[idx].getDownloadLink());
                URLConnection conn = url.openConnection();
                int contentLength = conn.getContentLength();
                boolean downloaded = false;
                DataInputStream stream = new DataInputStream(url.openStream());
                byte[] buffer = new byte[contentLength];
                stream.readFully(buffer);
                stream.close();

                String content  = new String(buffer);

                if (IsHTML(content)) {
                    String hRef = "href=\"";
                    int idx = content.indexOf(hRef);

                    if (idx != 1) {
                        idx += hRef.length();
                        int idx2 = content.indexOf("\"", idx);

                        String newLink = content.substring(idx, idx2);

                        url = new URL(newLink);
                        conn = url.openConnection();
                        contentLength = conn.getContentLength();
                      //  conn.wait();
                        stream = new DataInputStream(url.openStream());
                        buffer = new byte[contentLength];
                        stream.readFully(buffer);
                        stream.close();
                        downloaded = true;
                    }
                }
                else {
                    downloaded = matches(content, "%");
                }

                if (downloaded) {
                    String hyphenFileFolder = MainActivity.getHyphenFileFolder();

                    if (hyphenFileFolder != null) {
                        String hyphenFileName = hyphenFileFolder + hyphenDetails[idx].getFileName();
                        try (FileOutputStream fos = new FileOutputStream(hyphenFileName)) {
                            fos.write(buffer);
                        }
                        success = true;
                    }
                    else {
                        success = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if (success) {
                hyphenDetails[idx].setDownLoaded(true);
                setButtonToDeleteStatus(downloadDeleteButton, hyphenFileNameView);
                hyphenFilesList.add(hyphenDetails[idx].getHyphenatePatternLanguage());
            }
            else {
                setButtonToDownloadStatus(downloadDeleteButton, hyphenFileNameView);
            }
            super.onPostExecute(aVoid);
        }
    }

    void setButtonToDownloadStatus(androidx.appcompat.widget.AppCompatButton downloadDeleteButton, TextView hyphenFileNameView) {
        downloadDeleteButton.setBackgroundColor(context.getResources().getColor(R.color.pinkMagenta));
        downloadDeleteButton.setText(context.getResources().getString(R.string.download));
        hyphenFileNameView.setTextColor(context.getResources().getColor(R.color.fog));
        hyphenFileNameView.setTypeface(null, Typeface.NORMAL);
    }

    void setButtonToDeleteStatus(androidx.appcompat.widget.AppCompatButton downloadDeleteButton, TextView hyphenFileNameView) {
        downloadDeleteButton.setBackgroundColor(context.getResources().getColor(R.color.navyBlue));
        downloadDeleteButton.setText(context.getResources().getString(R.string.delete));
        hyphenFileNameView.setTextColor(context.getResources().getColor(R.color.black));
        hyphenFileNameView.setTypeface(null, Typeface.BOLD);
    }

    static boolean IsHTML(String str) {
        return matches(str, "<!DOCTYPE HTML") || matches(str, "<!DOCTYPE HTML");
    }
    // Checks if string str1 starts with string str2
    static boolean matches(String str1, String str2) {
        return str1.regionMatches(0, str2, 0, str2.length());
    }
}
