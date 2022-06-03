/*
 * Copyright (C) 2021 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified by Hogsmill Software Ltd, May 2022 renamed NewFeatureRepository from TrivialDriveRepository
 */


package com.example.heartsvalentine.billing
import android.app.Activity
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NewFeaturesRepository (
    private val storeManager: StoreManager,
    private val defaultScope: CoroutineScope
) {
    init {
        postMessagesFromBillingFlow()
    }

    private fun postMessagesFromBillingFlow() {
        defaultScope.launch {
            try {
                storeManager.getNewPurchases().collect { skuList ->
                    // TODO: Handle multi-line purchases better
                    for ( sku in skuList ) {
                        when (sku) {
                            //  TrivialDriveRepository.SKU_PREMIUM -> gameMessages.emit(R.string.message_premium)
                        }
                    }
                }
            } catch (e: Throwable) {
                Log.d(NewFeaturesRepository.TAG, "Collection complete")
            }
            Log.d(NewFeaturesRepository.TAG, "Collection Coroutine Scope Exited")
        }
    }

    fun buySku(activity: Activity, sku: String) {
        storeManager.launchBillingFlow(activity, sku)
    }

    fun isPurchased(sku: String): Flow<Boolean> {
        return storeManager.isPurchased(sku)
    }

    fun canPurchase(sku: String): Flow<Boolean> {
        return  storeManager.canPurchase(sku)
    }

    suspend fun refreshPurchases() {
        storeManager.refreshPurchases()
    }

    val billingLifecycleObserver: LifecycleObserver
        get() = storeManager

    fun getSkuTitle(sku: String): Flow<String> {
        return storeManager.getSkuTitle(sku)
    }

    fun getSkuPrice(sku: String): Flow<String> {
        return storeManager.getSkuPrice(sku)
    }

    fun getSkuDescription(sku: String): Flow<String> {
        return storeManager.getSkuDescription(sku)
    }

    fun getSKUArray(): Array<String> {
        return INAPP_SKUS
    }

    val billingFlowInProcess: Flow<Boolean>
        get() = storeManager.getBillingFlowInProcess()

    companion object {
        const val SKU_EMOJI = "com.hearts_valentine.emojis"
        const val SKU_MAINFRAME_SHAPES = "com.hearts_valentine.mainframe_shapes"
        const val SKU_SYMBOLS_AND_COLOURS = "com.hearts_valentine.symbols_and_colours"

        val TAG = NewFeaturesRepository::class.simpleName
        val INAPP_SKUS = arrayOf(SKU_EMOJI, SKU_MAINFRAME_SHAPES, SKU_SYMBOLS_AND_COLOURS)
    }
}






