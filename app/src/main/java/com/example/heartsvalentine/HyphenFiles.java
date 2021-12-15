package com.example.heartsvalentine;

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
import android.widget.ListView;

import com.example.heartsvalentine.viewModels.HyphenDetailsListViewModel;
import com.example.heartsvalentine.viewModels.HyphenFilesListViewModel;

import java.util.ArrayList;

public class HyphenFiles extends Fragment {

    ArrayList<HyphenDetails> hyphenDetailsList;
    ListView hyphenFileDetailsList;
    ArrayList<String> hyphenFilesList;
    private FragmentActivity fragmentActivityContext;

    public HyphenFiles() {
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
        View view = inflater.inflate(R.layout.fragment_hyphen_files, container, false);

        final View button = view.findViewById(R.id.backButton);
        button.setOnClickListener( v -> navigateToSettingsFragment());

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        fragmentActivityContext = (FragmentActivity) context;
        super.onAttach(context);
    }

   // https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        HyphenDetailsListViewModel hyphenDetailsListViewModel = new ViewModelProvider(requireActivity()).get(HyphenDetailsListViewModel.class);
        hyphenDetailsList = hyphenDetailsListViewModel.getSelectedItem().getValue();

        HyphenFilesListViewModel hyphenFilesListViewModel = new ViewModelProvider(requireActivity()).get(HyphenFilesListViewModel.class);
        hyphenFilesList = hyphenFilesListViewModel.getSelectedItem().getValue();

        hyphenFileDetailsList = view.findViewById(R.id.hyphenFileListView);

        // set the adapter to fill the data in the ListView
        HyphenDetails[] hd = hyphenDetailsList.toArray(new HyphenDetails[0]);
        HyphenDetailsListAdapter hyphenDetailsListAdapter = new HyphenDetailsListAdapter(view.getContext(), hd, hyphenFilesList);
        hyphenFileDetailsList.setAdapter(hyphenDetailsListAdapter);

    }

    void navigateToSettingsFragment() {
        Fragment fragment = new Settings();

        FragmentManager fragmentManager =  fragmentActivityContext.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, fragment)
                .setReorderingAllowed(true)
                .commit();
    }
}