package com.example.heartsvalentine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.example.heartsvalentine.viewModels.FileNameHplMapViewModel;
import com.example.heartsvalentine.viewModels.HeartValParametersViewModel;
import com.example.heartsvalentine.viewModels.HeartsValBitmapViewModel;
import com.example.heartsvalentine.viewModels.HplFileNameMapViewModel;
import com.example.heartsvalentine.viewModels.HyphenDetailsListViewModel;
import com.example.heartsvalentine.viewModels.HyphenFilesListViewModel;
import com.example.heartsvalentine.viewModels.TabLayoutViewModel;
import com.example.heartsvalentine.viewModels.TextInputViewModel;
import com.example.heartsvalentine.viewModels.UserFilesViewModel;
import com.google.android.material.tabs.TabLayout;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    private final ArrayList<HyphenDetails> hyphenDetailsList = new ArrayList<>();
    private final ArrayList<String> hyphenFilesList = new ArrayList<>();
    private TabLayout.Tab tabSetting = null;
    HashMap<String, String> hplFileNameMap = new HashMap<>();
    HashMap<String, String> fileNameHplMap = new HashMap<>();
    HeartValParameters hvp = new HeartValParameters();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            JSONArray hyphenDetailsArray = new JSONArray(loadJSONFromAsset());
            boolean hyphenFileSet = false;

            for ( int idx = 0 ; idx < hyphenDetailsArray.length(); idx++) {
                JSONObject hyphenDetail = hyphenDetailsArray.getJSONObject(idx);
                HyphenDetails hd = new HyphenDetails(
                        hyphenDetail.getInt("id"),
                        hyphenDetail.getString("hpl"),
                        hyphenDetail.getBoolean("downLoaded"),
                        hyphenDetail.getString("fileName"),
                        hyphenDetail.getString("downloadLink"));

                String hyphenFileFolder = getHyphenFileFolder(getApplicationContext());

                if (hyphenFileFolder != null) {
                    File hyphenFile = new File(hyphenFileFolder + hyphenDetail.getString("fileName"));
                    if (hyphenFile.exists()) {
                        hd.setDownLoaded(true);
                        hyphenFilesList.add(hyphenDetail.getString("hpl"));
                        hvp.setHyphenateText(true);

                        if (!hyphenFileSet) {
                            hvp.setHyphenFileName(hyphenDetail.getString("fileName"));
                            hyphenFileSet = true;
                        }
                    }
                }

                hyphenDetailsList.add(hd);

                hplFileNameMap.put(hyphenDetail.getString("hpl"), hyphenDetail.getString("fileName"));
                fileNameHplMap.put(hyphenDetail.getString("fileName"), hyphenDetail.getString("hpl"));
            }

            loadSavedSettings();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        // Tabs set up below:
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.pager);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.text_input)));
        tabSetting = tabLayout.newTab().setText(getResources().getString(R.string.settings));
        tabLayout.addTab(tabSetting);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.image)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final HeartsValAdapter adapter = new HeartsValAdapter(this, this, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == tabSetting) {
                    adapter.updateHyphenDropdown();
                }
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab == tabSetting) {
                    adapter.saveSelectedItem();
                }
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Data shared between fragments below
        // Text input
        TextInputViewModel textInputViewModel = new ViewModelProvider(this).get(TextInputViewModel.class);
        textInputViewModel.getSelectedItem().observe(this, item -> {});
        // User files
        UserFilesViewModel userFilesViewModel = new ViewModelProvider(this).get(UserFilesViewModel.class);
        ArrayList<String> userFileList = loadUserFile();
        userFilesViewModel.selectItems(userFileList);
        userFilesViewModel.getSelectedItem().observe(this, item -> {});
        // Hearts Valentine parameters
        HeartValParametersViewModel heartValParametersViewModel = new ViewModelProvider(this).get(HeartValParametersViewModel.class);
        heartValParametersViewModel.selectItem(hvp);
        heartValParametersViewModel.getSelectedItem().observe(this, item -> {});

        //  Hyphen Details List
        HyphenDetailsListViewModel hyphenDetailsListViewModel = new ViewModelProvider(this).get(HyphenDetailsListViewModel.class);
        hyphenDetailsListViewModel.selectItem(hyphenDetailsList);
        hyphenDetailsListViewModel.getSelectedItem().observe(this, item -> {});
        // Hyphen file List
        HyphenFilesListViewModel hyphenFilesListViewModel = new ViewModelProvider(this).get(HyphenFilesListViewModel.class);
        hyphenFilesListViewModel.selectItems(hyphenFilesList);
        hyphenFilesListViewModel.getSelectedItem().observe(this, item -> {});

        // Hearts Valentine bitmap image
        HeartsValBitmapViewModel heartsValBitmapViewModel = new ViewModelProvider(this).get(HeartsValBitmapViewModel.class);
        heartsValBitmapViewModel.getSelectedItem().observe(this, item -> {});

        // TabLayout. When generate image, 1st tab sets 3rd tab active, so text input fragment needs access to tabs.
        TabLayoutViewModel tabLayoutViewModel = new ViewModelProvider(this).get(TabLayoutViewModel.class);
        tabLayoutViewModel.selectItem(tabLayout);
        tabLayoutViewModel.getSelectedItem().observe(this, item -> {});

        // Hyphen pattern language to file name map
        HplFileNameMapViewModel hplFileNameMapViewModel = new ViewModelProvider(this).get(HplFileNameMapViewModel.class);
        hplFileNameMapViewModel.selectItem(hplFileNameMap);
        hplFileNameMapViewModel.getSelectedItem().observe(this, item -> {});

        // File name to hyphen pattern language map
        FileNameHplMapViewModel fileNameHplMapViewModel = new ViewModelProvider(this).get(FileNameHplMapViewModel.class);
        fileNameHplMapViewModel.selectItem(fileNameHplMap);
        fileNameHplMapViewModel.getSelectedItem().observe(this, item -> {});



    }

    private ArrayList<String> loadUserFile() {
        ArrayList<String> userFileList = new ArrayList<>();

        String usrFilePath = getUserFileFolder(false, getApplicationContext());

        if (usrFilePath != null) {
            File userFilesFolder = new File(usrFilePath);
            File[] files = userFilesFolder.listFiles();

            if (files != null) {
                for (File usrFile : files) {
                    if (usrFile.isFile()) {
                        userFileList.add(usrFile.getName());
                    }
                }
            }
        }
        return userFileList;
    }

    private void loadSavedSettings() throws IOException, JSONException {
        String settingsFileName = getSettingsFileName(getApplicationContext());
        if (settingsFileName != null) {
            File settingsFile = new File(settingsFileName);
            if (settingsFile.exists()) {
                String settings = readSavedSettingsFomFile();

                if (settings != null) {
                    JSONObject jsonObject = new JSONObject(settings);
                    hvp.setHyphenFileName((String) jsonObject.get("hyphenFileName"));
                    hvp.setOptimizeSpacing(jsonObject.getBoolean("optimizeSpacing"));
                    hvp.setHyphenateText(jsonObject.getBoolean("hyphenateText"));
                    hvp.setTxtHeartsMargin(jsonObject.getInt("txtHeartsMargin"));
                    hvp.setOuterMargin(jsonObject.getInt("outerMargin"));
                    hvp.setTextColor(jsonObject.getInt("textColor"));
                    hvp.setHeartsColor(jsonObject.getInt("heartsColor"));
                    hvp.setBackgroundColor(jsonObject.getInt("backgroundColor"));
                    hvp.setUseEmoji(jsonObject.getBoolean("useEmoji"));
                }
            }
        }
    }

    private String readSavedSettingsFomFile() throws IOException {
        String settingsFilePath = MainActivity.getSettingsFileName(getApplicationContext());

        if (settingsFilePath != null) {
            File file = new File(settingsFilePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
            return stringBuilder.toString();
        }
        return null;
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open( "hyphenDetails.json") ;
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String( buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String getHyphenFileFolder(Context context) {
        boolean folderCreated = true;
        File docDir = context.getFilesDir();

        String heartsValFolder = docDir.getPath() + "/HeartsVal";
        File heartsValDir = new File(heartsValFolder);

        if (!heartsValDir.exists()) {
            folderCreated = heartsValDir.mkdir();
        }

        if (folderCreated) {
            String hyphenFileFolder = heartsValFolder + "/Hyphenation";
            File hyphenFileDir = new File(hyphenFileFolder);

            if (!hyphenFileDir.exists()) {
                folderCreated = hyphenFileDir.mkdir();
            }
            return (folderCreated)? hyphenFileFolder + '/' : null;
        }
        return null;
    }

    public static String getSettingsFileName(Context context) {
        boolean folderCreated = true;
        File docDir = context.getFilesDir();

        String heartsValFolder = docDir.getPath() + "/HeartsVal";
        File heartsValDir = new File(heartsValFolder);

        if (!heartsValDir.exists()) {
            folderCreated = heartsValDir.mkdir();
        }

        if (folderCreated) {
            String settingsFolder = heartsValFolder + "/Settings";
            File settingsDir = new File(settingsFolder);

            if (!settingsDir.exists()) {
                folderCreated = settingsDir.mkdir();
            }
            return (folderCreated)? settingsFolder + "/settings.json" : null;
        }
        return null;
    }

    public static String getUserFileFolder(boolean appendBackslash, Context context) {
        boolean folderCreated = true;
        File docDir = context.getFilesDir();

        String heartsValFolder = docDir.getPath() + "/HeartsVal";
        File heartsValDir = new File(heartsValFolder);

        if (!heartsValDir.exists()) {
            folderCreated = heartsValDir.mkdir();
        }

        if (folderCreated) {
            String userFileFolder = heartsValFolder + "/UserFiles";
            File userFileDir = new File(userFileFolder);

            if (!userFileDir.exists()) {
                folderCreated = userFileDir.mkdir();
            }
            return folderCreated? userFileFolder + (appendBackslash ? '/' : "") : null;
        }
        return null;
    }

    static JSONObject GetJSonObjectFromHeartValParameters(HeartValParameters hvp) throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("hyphenFileName", hvp.getHyphenFileName());
        jsonObject.put("optimizeSpacing", hvp.getOptimizeSpacing());
        jsonObject.put("hyphenateText", hvp.getHyphenateText());
        jsonObject.put("txtHeartsMargin", hvp.getTxtHeartsMargin());
        jsonObject.put("outerMargin", hvp.getOuterMargin());
        jsonObject.put("textColor", hvp.getTextColor());
        jsonObject.put("heartsColor", hvp.getHeartsColor());
        jsonObject.put("backgroundColor", hvp.getBackgroundColor());
        jsonObject.put("useEmoji", hvp.getUseEmoji());
        return jsonObject;
    }
}