package com.example.heartsvalentine.frameShapes

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.heartsvalentine.*
import com.example.heartsvalentine.billing.NewFeaturesRepository
import com.example.heartsvalentine.viewModels.HeartValParametersViewModel
import top.defaults.colorpicker.ColorPickerPopup
import top.defaults.colorpicker.ColorPickerPopup.ColorPickerObserver

class FrameShapes : Fragment() {
    private var fragmentActivityContext: FragmentActivity? = null
    var hvp: HeartValParameters? = null
    private var emojiButton: EmojiCellCtrl? = null
    private var filledShapeButton: ShapeCellCtrl? = null
    private var unfilledShapeButton: MainShapeCellCtrl? = null
    private var emojiPopUpPt: Point? = null
    private var shapePopUpPt: Point? = null
    private var mainShapePopupPt: Point? = null
    private var popUpPointsInitialized = false
    private var useEmojiSwitch = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentActivityContext = context as FragmentActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val heartValParametersViewModel = ViewModelProvider(requireActivity()).get(
            HeartValParametersViewModel::class.java
        )
        hvp = heartValParametersViewModel.selectedItem.value
        var purchasedEmoji = false
        var purchasedSymbolsAndColours = false
        var purchasedMainFrameShapes = false

        val act = activity

        if (act != null) {
            val nfr = (act.application as HeartsValentineApplication).appContainer.newFeaturesRepository
            val skuEmoji = nfr.isPurchased(NewFeaturesRepository.SKU_EMOJI).asLiveData()
            purchasedEmoji = skuEmoji.value == true
            val skuSymbolsAndColours = nfr.isPurchased(NewFeaturesRepository.SKU_SYMBOLS_AND_COLOURS).asLiveData()
            purchasedSymbolsAndColours = skuSymbolsAndColours.value == true
            val skuMainFrameShapes = nfr.isPurchased(NewFeaturesRepository.SKU_MAINFRAME_SHAPES).asLiveData()
            purchasedMainFrameShapes = skuMainFrameShapes.value == true
        }

        val heartsColorButton = view.findViewById<AppCompatButton>(R.id.heartsColorButton)
        heartsColorButton.setBackgroundColor(hvp!!.heartsColor)
        heartsColorButton.setOnClickListener { v: View? ->
            onClickHeartsColorButton(
                v,
                heartsColorButton
            )
        }
        emojiButton = view.findViewById(R.id.emojiButton)
        emojiButton?.emoji = hvp!!.emoji
        emojiButton?.setOnClickListener { _: View? -> openEmojiPopup() }
        filledShapeButton = view.findViewById(R.id.filledShapeButton)
        if (hvp!!.shapeType == ShapeType.None) {
            filledShapeButton?.symbol = hvp!!.symbol
        } else {
            filledShapeButton?.shapeType = hvp!!.shapeType
        }
        filledShapeButton?.setOnClickListener { _: View? -> openShapePopup() }
        activateDeactivateHeartColorButton(view, !hvp!!.useEmoji)
        val useEmojiSwitch = view.findViewById<SwitchCompat>(R.id.useEmojiSwitch)
        // Hide emoji switch if emojis not purchased
        useEmojiSwitch.visibility = if (purchasedEmoji) View.VISIBLE else View.GONE
        useEmojiSwitch.isChecked = hvp!!.useEmoji
        useEmojiSwitch.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            hvp!!.useEmoji = isChecked
            activateDeactivateHeartColorButton(view, !isChecked)
        }

        // Hide emoji button if emojis not purchased
        val emojiLineLayout = view.findViewById<LinearLayout>(R.id.emojiLinearLayout)
        emojiLineLayout.visibility = if (purchasedEmoji) View.VISIBLE else View.GONE

        // Hide symbol button if symbols and colours not purchased
        val symbolLinearLayout = view.findViewById<LinearLayout>(R.id.symbolLinearLayout)
        symbolLinearLayout.visibility = if (purchasedSymbolsAndColours) View.VISIBLE else View.GONE

        // Hide symbol colour button if symbols and colours not purchased
        val symbolColourLinearLayout =
            view.findViewById<LinearLayout>(R.id.symbolColourLinearLayout)
        symbolColourLinearLayout.visibility =
            if (purchasedSymbolsAndColours) View.VISIBLE else View.GONE

        // Hide symbol colour button if symbols and colours not purchased
        val mainShapeLinearLayout = view.findViewById<LinearLayout>(R.id.mainShapeLinearLayout)
        mainShapeLinearLayout.visibility = if (purchasedMainFrameShapes) View.VISIBLE else View.GONE
        unfilledShapeButton = view.findViewById(R.id.unfilledShapeButton)
        unfilledShapeButton?.setFillShape(false)
        unfilledShapeButton?.shapeType = hvp!!.mainShape
        unfilledShapeButton?.setOnClickListener { _: View? -> openMainShapePopup() }

        // If no purchases, show friendly message
        val noPurchaseMessage = view.findViewById<TextView>(R.id.noPurchaseMessage)
        noPurchaseMessage.visibility =
            if (!purchasedEmoji && !purchasedSymbolsAndColours && !purchasedMainFrameShapes) View.VISIBLE else View.GONE
    }

    private fun initializePopUpPoints() {
        if (!popUpPointsInitialized) {
            popUpPointsInitialized = true
            emojiPopUpPt = Point()
            shapePopUpPt = Point()
            mainShapePopupPt = Point()
            val screenSizePt: Point
            val context = context
            screenSizePt = if (context != null) {
                Utilities.getRealScreenSize(context)
            } else {
                Point()
            }
            emojiPopUpPt!!.x = screenSizePt.x
            emojiPopUpPt!!.y = screenSizePt.y

            // now the shape button
            shapePopUpPt!!.x = screenSizePt.x
            shapePopUpPt!!.y = emojiPopUpPt!!.y

            // now the main shape popup button
            mainShapePopupPt!!.x = screenSizePt.x
            mainShapePopupPt!!.y = screenSizePt.y
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_frame_shapes, container, false)
        val button = view.findViewById<View>(R.id.backButton)
        button.setOnClickListener { navigateToSettingsFragment() }
        return view
    }

    private fun navigateToSettingsFragment() {
        val fragment: Fragment = Settings()
        val fragmentManager = fragmentActivityContext!!.supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.settings_frame, fragment)
            .setReorderingAllowed(true)
            .commit()
    }

    private fun onClickHeartsColorButton(v: View?, heartsColorButton: AppCompatButton) {
        if (!hvp!!.useEmoji) {
            ColorPickerPopup.Builder(context).initialColor(hvp!!.heartsColor)
                .enableBrightness(true)
                .enableAlpha(true)
                .okTitle(resources.getString(R.string.choose))
                .cancelTitle(resources.getString(R.string.cancel))
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(v,
                    object : ColorPickerObserver() {
                        override fun onColorPicked(color: Int) {
                            heartsColorButton.setBackgroundColor(color)
                            hvp!!.heartsColor = color
                        }
                    })
        }
    }

    private fun activateDeactivateHeartColorButton(view: View, activate: Boolean) {
        val emojiText = view.findViewById<TextView>(R.id.emojiText)
        emojiText.setTextColor(if (activate) Color.GRAY else Color.BLACK)
        val edgeShapeText = view.findViewById<TextView>(R.id.edgeShapeText)
        edgeShapeText.setTextColor(if (activate) Color.BLACK else Color.GRAY)
        val shapeColorText = view.findViewById<TextView>(R.id.shapeColorText)
        shapeColorText.setTextColor(if (activate) Color.BLACK else Color.GRAY)
        if (context != null && requireContext().resources != null) {
            val emojiButtonFrame = view.findViewById<View>(R.id.emojiButtonFrame)
            emojiButtonFrame.setBackgroundColor(
                if (!activate) ContextCompat.getColor(requireContext(), R.color.highlightBlue) else
                    ContextCompat.getColor(requireContext(), R.color.midDayFog)
            )
            val filledShapeButtonFrame = view.findViewById<View>(R.id.filledShapeButtonFrame)
            filledShapeButtonFrame.setBackgroundColor(
                if (activate) ContextCompat.getColor(requireContext(), R.color.highlightBlue) else
                    ContextCompat.getColor(requireContext(), R.color.midDayFog
                )
            )
            val heartColorButtonFrame = view.findViewById<View>(R.id.shapeColorButtonFrame)
            heartColorButtonFrame.setBackgroundColor(
                if (activate) ContextCompat.getColor(requireContext(), R.color.highlightBlue) else ContextCompat.getColor(requireContext(),
                    R.color.midDayFog
                )
            )
            val unfilledShapeButtonFrame = view.findViewById<View>(R.id.unfilledShapeButtonFrame)
            unfilledShapeButtonFrame.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.highlightBlue))
        }
        emojiButton!!.setIsActive(!activate)
        filledShapeButton!!.setIsActive(activate)
        useEmojiSwitch = !activate
        val heartsColorButton = view.findViewById<AppCompatButton>(R.id.heartsColorButton)
        heartsColorButton.setBackgroundColor(if (activate) hvp!!.heartsColor else hvp!!.heartsColor and -0x77000001)
    }

    private fun openEmojiPopup() {
        if (!useEmojiSwitch) {
            return
        }
        initializePopUpPoints()
        val alertDialog = Dialog(this.requireContext())
        val etc = EmojiTableCtrl(this.context)
        etc.setSelectedEmojiCtrl(emojiButton)
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(etc)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog.window!!.attributes)
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = 0 //emojiPopUpPt.x - etc.getComputedWidth();
        layoutParams.y = 0 //emojiPopUpPt.y + etc.getComputedStartDrawHeight();
        layoutParams.width = mainShapePopupPt!!.x
        layoutParams.height = mainShapePopupPt!!.y
        alertDialog.window!!.attributes = layoutParams
        etc.setOnClickListener {
            emojiPopupReceivedClick(
                alertDialog
            )
        }
        alertDialog.show()
    }

    private fun emojiPopupReceivedClick(alertDialog: Dialog) {
        alertDialog.dismiss()
        hvp!!.emoji = emojiButton!!.emoji
    }

    private fun openShapePopup() {
        if (useEmojiSwitch) {
            return
        }
        initializePopUpPoints()
        val alertDialog = Dialog(this.requireContext())
        //  ([ShapeType, String])[] shapeTypeArray = {[ShapeType.StraightHeart, 0], ShapeType.Circle, ShapeType.Square, ShapeType.Star, ShapeType.Spade, ShapeType.Club, ShapeType.Diamond};
        val stc = ShapeTableCtrl(this.context)
        stc.setSelectedShapeCtrl(filledShapeButton)
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(stc)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog.window!!.attributes)
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = shapePopUpPt!!.x // - stc.getComputedWidth();
        layoutParams.y = shapePopUpPt!!.y // + stc.getComputedStartDrawHeight();
        alertDialog.window!!.attributes = layoutParams
        stc.setOnClickListener {
            openShapePopupReceivedClick(
                alertDialog
            )
        }
        alertDialog.show()
    }

    private fun openShapePopupReceivedClick(alertDialog: Dialog) {
        alertDialog.dismiss()
        hvp!!.shapeType = filledShapeButton!!.shapeType
        hvp!!.symbol = filledShapeButton!!.symbol
    }

    private fun openMainShapePopup() {
        initializePopUpPoints()
        val alertDialog = Dialog(this.requireContext())
        val shapeTypeArray = arrayOf(ShapeType.StraightHeart, ShapeType.Circle, ShapeType.Square)
        val mSTC = MainShapeTableCtrl(this.context, shapeTypeArray, false)
        mSTC.setSelectedEmojiCtrl(unfilledShapeButton)
        alertDialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setContentView(mSTC)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(alertDialog.window!!.attributes)
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = 0
        layoutParams.y = 0
        layoutParams.width = mainShapePopupPt!!.x
        layoutParams.height = mainShapePopupPt!!.y
        alertDialog.window!!.attributes = layoutParams
        mSTC.setOnClickListener {
            openMainShapePopupReceivedClick(
                alertDialog
            )
        }
        alertDialog.show()
    }

    private fun openMainShapePopupReceivedClick(alertDialog: Dialog) {
        alertDialog.dismiss()
        hvp!!.mainShape = unfilledShapeButton!!.shapeType
    }
}