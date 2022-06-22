package com.example.heartsvalentine.viewModels

import androidx.lifecycle.*
import com.example.heartsvalentine.R
import com.example.heartsvalentine.billing.SKU_EMOJI
import com.example.heartsvalentine.billing.SKU_MAINFRAME_SHAPES
import com.example.heartsvalentine.billing.SKU_SYMBOLS_AND_COLOURS
import com.example.heartsvalentine.billing.StoreManager
import java.util.HashMap

class NewFeaturesViewModel(private val storeManager: StoreManager) : ViewModel() {
    companion object {
        val sKUToResourceIdMap: MutableMap<String, Int> = HashMap()

        init {
            sKUToResourceIdMap[SKU_EMOJI] = R.drawable.sku_emoji
            sKUToResourceIdMap[SKU_MAINFRAME_SHAPES] = R.drawable.sku_frame
            sKUToResourceIdMap[SKU_SYMBOLS_AND_COLOURS] = R.drawable.sku_symbols
        }
    }

    class SkuDetails internal constructor(val sku: String, storeManager: StoreManager) {
        val title = storeManager.getSkuTitle(sku).asLiveData()
      //  val description = storeManager.getSkuDescription(sku).asLiveData()
        val price = storeManager.getSkuPrice(sku).asLiveData()
    }

    fun getSkuDetails(sku: String): SkuDetails {
        return SkuDetails(sku, storeManager)
   }

    fun canBuySku(sku: String): LiveData<Boolean> {
       return storeManager.canPurchase(sku).asLiveData()
    }

    fun isPurchased(sku: String): LiveData<Boolean> {
        return storeManager.isPurchased(sku).asLiveData()
    }

    class NewFeaturesViewModelFactory(private val storeManager: StoreManager) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewFeaturesViewModel::class.java)) {
                return NewFeaturesViewModel(storeManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
