package com.example.heartsvalentine.viewModels

import android.app.Activity
import androidx.lifecycle.*
import com.example.heartsvalentine.R
import com.example.heartsvalentine.billing.NewFeaturesRepository
import kotlinx.coroutines.launch
import java.util.HashMap

class NewFeaturesViewModel(private val nfr: NewFeaturesRepository) : ViewModel() {
    companion object {
        val TAG = "HeartsValentine:" + NewFeaturesViewModel::class.java.simpleName
        private val sKUToResourceIdMap: MutableMap<String, Int> = HashMap()

        init {
            sKUToResourceIdMap[NewFeaturesRepository.SKU_EMOJI] = R.drawable.sku_emoji
            sKUToResourceIdMap[NewFeaturesRepository.SKU_MAINFRAME_SHAPES] = R.drawable.sku_symbols
            sKUToResourceIdMap[NewFeaturesRepository.SKU_SYMBOLS_AND_COLOURS] = R.drawable.sku_frame
        }
    }

    class SkuDetails internal constructor(val sku: String, nfr: NewFeaturesRepository) {
        val title = nfr.getSkuTitle(sku).asLiveData()
        val description = nfr.getSkuDescription(sku).asLiveData()
        val price = nfr.getSkuPrice(sku).asLiveData()
        val iconDrawableId = sKUToResourceIdMap[sku]!!
    }

    fun getSkuDetails(sku: String): SkuDetails {
        return SkuDetails(sku, nfr)
    }

    fun canBuySku(sku: String): LiveData<Boolean> {
        return nfr.canPurchase(sku).asLiveData()
    }

    fun isPurchased(sku: String): LiveData<Boolean> {
        return nfr.isPurchased(sku).asLiveData()
    }

    /**
     * Starts a billing flow for purchasing gas.
     * @param activity
     * @return whether or not we were able to start the flow
     */
    fun buySku(activity: Activity, sku: String) {
        nfr.buySku(activity, sku)
    }

    val billingFlowInProcess: LiveData<Boolean>
        get() = nfr.billingFlowInProcess.asLiveData()

    fun sendMessage(message: Int) {
        viewModelScope.launch {
            //nfr.sendMessage(message)
        }
    }

    operator fun invoke(newFeaturesViewModel: NewFeaturesViewModel) {

    }

    class NewFeaturesViewModelFactory(private val newFeaturesRepository: NewFeaturesRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewFeaturesViewModel::class.java)) {
                return NewFeaturesViewModel(newFeaturesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
