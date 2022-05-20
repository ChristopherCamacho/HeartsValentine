package com.example.heartsvalentine.frameShapes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.heartsvalentine.HeartValParameters;
import com.example.heartsvalentine.R;
import com.example.heartsvalentine.Settings;
import com.example.heartsvalentine.Utilities;
import com.example.heartsvalentine.viewModels.HeartValParametersViewModel;

import top.defaults.colorpicker.ColorPickerPopup;

public class FrameShapes extends Fragment {

    private FragmentActivity fragmentActivityContext;
    HeartValParameters hvp;
    EmojiCellCtrl emojiButton;
    ShapeCellCtrl filledShapeButton;
    MainShapeCellCtrl unfilledShapeButton;
    Point emojiPopUpPt, shapePopUpPt, mainShapePopupPt;
    Boolean popUpPointsInitialized = false;
    Boolean useEmojiSwitch = false;

    public FrameShapes() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        androidx.appcompat.widget.AppCompatButton heartsColorButton = view.findViewById(R.id.heartsColorButton);
        heartsColorButton.setBackgroundColor(hvp.getHeartsColor());
        heartsColorButton.setOnClickListener(v -> onClickHeartsColorButton(v, heartsColorButton));

        emojiButton = view.findViewById(R.id.emojiButton);
        emojiButton.setEmoji(hvp.getEmoji());
        emojiButton.setOnClickListener(v -> openEmojiPopup());

        filledShapeButton = view.findViewById(R.id.filledShapeButton);
        if (hvp.getShapeType() == ShapeType.None) {
            filledShapeButton.setSymbol(hvp.getSymbol());
        }
        else {
            filledShapeButton.setShapeType(hvp.getShapeType());
        }
        filledShapeButton.setOnClickListener(v -> openShapePopup());

        activateDeactivateHeartColorButton(view, !hvp.getUseEmoji());
        androidx.appcompat.widget.SwitchCompat useEmojiSwitch = view.findViewById(R.id.useEmojiSwitch);
        useEmojiSwitch.setChecked(hvp.getUseEmoji());
        useEmojiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {hvp.setUseEmoji(isChecked);
            activateDeactivateHeartColorButton(view, !isChecked);});

        unfilledShapeButton = view.findViewById(R.id.unfilledShapeButton);
        unfilledShapeButton.setFillShape(false);
        unfilledShapeButton.setShapeType(hvp.getMainShape());
        unfilledShapeButton.setOnClickListener(v -> openMainShapePopup());

    }

    private void initializePopUpPoints() {
        if (!popUpPointsInitialized) {
            popUpPointsInitialized = true;

            emojiPopUpPt = new Point();
            shapePopUpPt = new Point();
            mainShapePopupPt = new Point();

            Point screenSizePt;
            Context context = getContext();

            if (context != null) {
                screenSizePt = Utilities.getRealScreenSize(context);
            } else {
                screenSizePt = new Point();
            }

            emojiPopUpPt.x = screenSizePt.x;
            emojiPopUpPt.y = screenSizePt.y;

            // now the shape button
            shapePopUpPt.x = screenSizePt.x;
            shapePopUpPt.y = emojiPopUpPt.y;

            // now the main shape popup button
            mainShapePopupPt.x = screenSizePt.x;
            mainShapePopupPt.y = screenSizePt.y;
        }
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_frame_shapes, container, false);

        final View button = view.findViewById(R.id.backButton);
        button.setOnClickListener( v -> navigateToSettingsFragment());

        return view;
    }

    void navigateToSettingsFragment() {
        Fragment fragment = new Settings();

        FragmentManager fragmentManager =  fragmentActivityContext.getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.settings_frame, fragment)
                .setReorderingAllowed(true)
                .commit();
    }

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

    void activateDeactivateHeartColorButton(View view, boolean activate) {

        TextView emojiText = view.findViewById(R.id.emojiText);
        emojiText.setTextColor(activate? Color.GRAY : Color.BLACK);

        TextView edgeShapeText = view.findViewById(R.id.edgeShapeText);
        edgeShapeText.setTextColor(activate? Color.BLACK : Color.GRAY);

        TextView shapeColorText = view.findViewById(R.id.shapeColorText);
        shapeColorText.setTextColor(activate? Color.BLACK : Color.GRAY);

        if (getContext() != null && getContext().getResources() != null) {
            View emojiButtonFrame  = view.findViewById(R.id.emojiButtonFrame);
            emojiButtonFrame.setBackgroundColor(!activate ? getContext().getResources().getColor(R.color.highlightBlue) : getContext().getResources().getColor(R.color.midDayFog));

            View filledShapeButtonFrame  = view.findViewById(R.id.filledShapeButtonFrame);
            filledShapeButtonFrame.setBackgroundColor(activate ? getContext().getResources().getColor(R.color.highlightBlue) : getContext().getResources().getColor(R.color.midDayFog));

            View heartColorButtonFrame  = view.findViewById(R.id.shapeColorButtonFrame);
            heartColorButtonFrame.setBackgroundColor(activate ? getContext().getResources().getColor(R.color.highlightBlue) : getContext().getResources().getColor(R.color.midDayFog));

            View unfilledShapeButtonFrame  = view.findViewById(R.id.unfilledShapeButtonFrame);
            unfilledShapeButtonFrame.setBackgroundColor(getContext().getResources().getColor(R.color.highlightBlue));
        }

        emojiButton.setIsActive(!activate);
        filledShapeButton.setIsActive(activate);
        useEmojiSwitch = !activate;

        androidx.appcompat.widget.AppCompatButton heartsColorButton = view.findViewById(R.id.heartsColorButton);
        heartsColorButton.setBackgroundColor(activate? hvp.getHeartsColor() : hvp.getHeartsColor() & 0x88FFFFFF);
    }

    void openEmojiPopup() {
        if (!useEmojiSwitch) {
            return;
        }

        initializePopUpPoints();

        Dialog alertDialog = new Dialog(this.getContext());

        EmojiTableCtrl etc = new EmojiTableCtrl(this.getContext());
        etc.setSelectedEmojiCtrl(emojiButton);

        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.setContentView(etc);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.gravity = Gravity.TOP | Gravity.START;

        layoutParams.x = 0;//emojiPopUpPt.x - etc.getComputedWidth();
        layoutParams.y = 0;//emojiPopUpPt.y + etc.getComputedStartDrawHeight();
        layoutParams.width = mainShapePopupPt.x;
        layoutParams.height = mainShapePopupPt.y;

        alertDialog.getWindow().setAttributes(layoutParams);
        etc.setOnClickListener(v -> emojiPopupReceivedClick(alertDialog));
        alertDialog.show();
    }

    void emojiPopupReceivedClick(Dialog alertDialog) {
        alertDialog.dismiss();
        hvp.setEmoji(emojiButton.getEmoji());
    }

    void openShapePopup() {
        if (useEmojiSwitch) {
            return;
        }

        initializePopUpPoints();

        Dialog alertDialog = new Dialog(this.getContext());
      //  ([ShapeType, String])[] shapeTypeArray = {[ShapeType.StraightHeart, 0], ShapeType.Circle, ShapeType.Square, ShapeType.Star, ShapeType.Spade, ShapeType.Club, ShapeType.Diamond};
        ShapeTableCtrl stc = new ShapeTableCtrl(this.getContext());
        stc.setSelectedShapeCtrl(filledShapeButton);

        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.setContentView(stc);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.gravity = Gravity.TOP | Gravity.START;

        layoutParams.x = shapePopUpPt.x;// - stc.getComputedWidth();
        layoutParams.y = shapePopUpPt.y;// + stc.getComputedStartDrawHeight();

        alertDialog.getWindow().setAttributes(layoutParams);
        stc.setOnClickListener(v -> openShapePopupReceivedClick(alertDialog));
        alertDialog.show();
    }

    void openShapePopupReceivedClick(Dialog alertDialog) {
        alertDialog.dismiss();
        hvp.setShapeType(filledShapeButton.getShapeType());
        hvp.setSymbol(filledShapeButton.getSymbol());
    }

    void openMainShapePopup() {
        initializePopUpPoints();

        Dialog alertDialog = new Dialog(this.getContext());
        ShapeType[] shapeTypeArray = {ShapeType.StraightHeart, ShapeType.Circle, ShapeType.Square};
        MainShapeTableCtrl mSTC = new MainShapeTableCtrl(this.getContext(), shapeTypeArray, false);
        mSTC.setSelectedEmojiCtrl(unfilledShapeButton);

        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.setContentView(mSTC);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.gravity = Gravity.TOP | Gravity.START;

        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = mainShapePopupPt.x;
        layoutParams.height = mainShapePopupPt.y;

        alertDialog.getWindow().setAttributes(layoutParams);
        mSTC.setOnClickListener(v -> openMainShapePopupReceivedClick(alertDialog));
        alertDialog.show();
    }

    void openMainShapePopupReceivedClick(Dialog alertDialog) {
        alertDialog.dismiss();
        hvp.setMainShape(unfilledShapeButton.getShapeType());
    }
}