package com.example.heartsvalentine

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.heartsvalentine.billing.NewFeaturesRepository
import com.example.heartsvalentine.databinding.ListNewFeatureItemsBinding
import com.example.heartsvalentine.viewModels.NewFeaturesViewModel

class NewFeatureListAdapter internal constructor(
    context: Context,
    sKUList: ArrayList<String>?,
    newFeaturesViewModel: NewFeaturesViewModel,
    newFeaturesFragment: NewFeatures,
    nfr: NewFeaturesRepository,
    activity: Activity
) :
    RecyclerView.Adapter<NewFeatureListAdapter.ViewHolder>() {
    var newFeaturesInflater: LayoutInflater
    var sKUList: ArrayList<String>?
    var context: Context
    val newFeaturesViewModel: NewFeaturesViewModel
    val newFeaturesFragment: NewFeatures
    var nfr: NewFeaturesRepository
    var activity: Activity

    inner class ViewHolder(private val binding: ListNewFeatureItemsBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: String,
            newFeaturesViewModel: NewFeaturesViewModel,
            newFeaturesFragment: NewFeatures
        ) {

            val str: String? = newFeaturesViewModel.getSkuDetails(item).title.value
            binding.featureName.setText(str)

            // What about can purchase? Consider this in code
            if (newFeaturesViewModel.isPurchased(item).value == true) {
                setButtonToPurchasedStatus(binding.buyPurchasedButton)
            }
            else if (newFeaturesViewModel.canBuySku(item).value == true) {
                setButtonToBuyStatus(binding.buyPurchasedButton)
            }
            else {
                setButtonToUnknownStatus(binding.buyPurchasedButton)
            }

            binding.setLifecycleOwner(newFeaturesFragment)
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListNewFeatureItemsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NewFeatureListAdapter.ViewHolder,
        position: Int
    ) {
        sKUList?.let { holder.bind( it.get(position), this.newFeaturesViewModel, newFeaturesFragment) }
    }

    override fun getItemCount(): Int {
        return if (sKUList != null) sKUList!!.size else 0
    }

    fun showInfo(i: Int) {
    }

    fun buySKU(i: Int, purchasedEmoji: Boolean) {
        if (purchasedEmoji) {
            // pop up already bought
        } else {
            nfr.buySku(activity, sKUList!![i])
            //   nfr.re.refreshPurchases();
        }
    }

    fun setButtonToBuyStatus(buyPurchasedButton: AppCompatButton) {
        buyPurchasedButton.visibility = View.VISIBLE
        buyPurchasedButton.setBackgroundColor(ContextCompat.getColor(context, R.color.pinkMagenta))
        buyPurchasedButton.setText(context.getResources().getString(R.string.buy))
        buyPurchasedButton.setTypeface(null, Typeface.NORMAL)
    }

    fun setButtonToPurchasedStatus(buyPurchasedButton: AppCompatButton) {
        buyPurchasedButton.visibility = View.VISIBLE
        buyPurchasedButton.setBackgroundColor(ContextCompat.getColor(context, R.color.navyBlue))
        buyPurchasedButton.setText(context.getResources().getString(R.string.already_purchased))
        buyPurchasedButton.setTypeface(null, Typeface.ITALIC)
    }

    fun setButtonToUnknownStatus(buyPurchasedButton: AppCompatButton) {
        buyPurchasedButton.visibility = View.GONE
    }

    init {
        newFeaturesInflater = LayoutInflater.from(context)
        this.context = context
        this.sKUList = sKUList
        this.newFeaturesViewModel = newFeaturesViewModel
        this.newFeaturesFragment = newFeaturesFragment
        this.nfr = nfr
        this.activity = activity
    }
}
