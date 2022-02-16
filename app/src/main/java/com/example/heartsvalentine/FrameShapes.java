package com.example.heartsvalentine;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.heartsvalentine.viewModels.HeartValParametersViewModel;

import top.defaults.colorpicker.ColorPickerPopup;

public class FrameShapes extends Fragment {

    private FragmentActivity fragmentActivityContext;
    HeartValParameters hvp;
    EmojiCellCtrl emojiButton;
    ShapeCellCtrl filledShapeButton;
    Point emojiPopUpPt, shapePopUpPt;
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
        emojiButton.setOnClickListener(v -> openEmojiPopup(view));

        filledShapeButton = view.findViewById(R.id.filledShapeButton);
        filledShapeButton.setFillShape(true);
        filledShapeButton.setShapeType(hvp.getShapeType());
        filledShapeButton.setOnClickListener(v -> openShapePopup(view));

        activateDeactivateHeartColorButton(view, !hvp.getUseEmoji());
        androidx.appcompat.widget.SwitchCompat useEmojiSwitch = view.findViewById(R.id.useEmojiSwitch);
        useEmojiSwitch.setChecked(hvp.getUseEmoji());
        useEmojiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {hvp.setUseEmoji(isChecked);
            activateDeactivateHeartColorButton(view, !isChecked);});
    }

    private void initializePopUpPoints(View view) {
        if (!popUpPointsInitialized) {
            popUpPointsInitialized = true;

            emojiPopUpPt = new Point();
            shapePopUpPt = new Point();

            FrameLayout emojiButtonFrameLayout = view.findViewById(R.id.emojiButtonFrameLayout);
            LinearLayout.LayoutParams lpEmojiButtonFrame = (LinearLayout.LayoutParams) emojiButtonFrameLayout.getLayoutParams();
            Point screenSizePt;
            Context context = getContext();

            if (context != null) {
                screenSizePt = Utilities.getRealScreenSize(context);
            } else {
                screenSizePt = new Point();
            }

            emojiPopUpPt.x = screenSizePt.x - lpEmojiButtonFrame.getMarginEnd();

            int topHeight = Utilities.getTotalTopHeight(this.getContext());

            AppCompatButton backButton = view.findViewById(R.id.backButton);
            LinearLayout.LayoutParams lpBackButton = (LinearLayout.LayoutParams) backButton.getLayoutParams();
            int totalHeightBackButtonLayout = backButton.getHeight() + lpBackButton.topMargin + lpBackButton.bottomMargin;

            SwitchCompat useEmojiSwitch = view.findViewById(R.id.useEmojiSwitch);
            LinearLayout.LayoutParams lpUseEmojiSwitch = (LinearLayout.LayoutParams) useEmojiSwitch.getLayoutParams();
            int totalHeightUseEmojiSwitch = useEmojiSwitch.getHeight() + lpUseEmojiSwitch.topMargin + lpUseEmojiSwitch.bottomMargin;

            int correction = (int) Utilities.convertDpToPixel(8, getContext()); // Don't see where this comes from or what I have overlooked?

            emojiPopUpPt.y = topHeight + totalHeightBackButtonLayout + totalHeightUseEmojiSwitch + lpEmojiButtonFrame.topMargin + correction + emojiButton.getHeight();

            // now the shape button
            FrameLayout filledShapeButtonFrameLayout = view.findViewById(R.id.filledShapeButtonFrameLayout);
            LinearLayout.LayoutParams lpFilledShapeButtonFrame = (LinearLayout.LayoutParams) filledShapeButtonFrameLayout.getLayoutParams();

            shapePopUpPt.x = screenSizePt.x - lpFilledShapeButtonFrame.getMarginEnd();
            shapePopUpPt.y = emojiPopUpPt.y + lpEmojiButtonFrame.height + lpEmojiButtonFrame.bottomMargin + lpFilledShapeButtonFrame.topMargin;
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
        }

        emojiButton.setIsActive(!activate);
        filledShapeButton.setIsActive(activate);
        useEmojiSwitch = !activate;

        androidx.appcompat.widget.AppCompatButton heartsColorButton = view.findViewById(R.id.heartsColorButton);
        heartsColorButton.setBackgroundColor(activate? hvp.getHeartsColor() : hvp.getHeartsColor() & 0x88FFFFFF);
    }

    void openEmojiPopup(@NonNull View view) {
        if (!useEmojiSwitch) {
            return;
        }

        initializePopUpPoints(view);

        Dialog alertDialog = new Dialog(this.getContext());

        EmojiTableCtrl etc = new EmojiTableCtrl(this.getContext());
        etc.setSelectedEmojiCtrl(emojiButton);

        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.setContentView(etc);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.gravity = Gravity.TOP | Gravity.START;

        layoutParams.x = emojiPopUpPt.x - etc.getComputedWidth();
        layoutParams.y = emojiPopUpPt.y + etc.getComputedStartDrawHeight();

        alertDialog.getWindow().setAttributes(layoutParams);
        etc.setOnClickListener(v -> emojiPopupReceivedClick(alertDialog));
        alertDialog.show();
    }

    void emojiPopupReceivedClick(Dialog alertDialog) {
        alertDialog.dismiss();
        hvp.setEmoji(emojiButton.getEmoji());
    }

    void openShapePopup(@NonNull View view) {
        if (useEmojiSwitch) {
            return;
        }

        initializePopUpPoints(view);

        Dialog alertDialog = new Dialog(this.getContext());
        ShapeType[] shapeTypeArray = {ShapeType.StraightHeart, ShapeType.Circle, ShapeType.Square, ShapeType.Star};
        ShapeTableCtrl stc = new ShapeTableCtrl(this.getContext(), shapeTypeArray);
        stc.setSelectedEmojiCtrl(filledShapeButton);

        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.setContentView(stc);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.gravity = Gravity.TOP | Gravity.START;

        layoutParams.x = shapePopUpPt.x - stc.getComputedWidth();
        layoutParams.y = shapePopUpPt.y + stc.getComputedStartDrawHeight();

        alertDialog.getWindow().setAttributes(layoutParams);
        stc.setOnClickListener(v -> openShapePopupReceivedClick(alertDialog));
        alertDialog.show();
    }

    void openShapePopupReceivedClick(Dialog alertDialog) {
        alertDialog.dismiss();
        hvp.setShapeType(filledShapeButton.getShapeType());
    }
}