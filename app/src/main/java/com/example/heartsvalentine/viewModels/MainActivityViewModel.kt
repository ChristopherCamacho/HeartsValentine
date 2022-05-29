
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
 * Modified by Hogsmill Software Ltd, May 2022
 */

package com.example.heartsvalentine.viewModels

import android.graphics.Color
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.heartsvalentine.HeartValParameters
import com.example.heartsvalentine.billing.NewFeaturesRepository
import com.example.heartsvalentine.frameShapes.ShapeType

/*
   This is used for any business logic, as well as to echo LiveData from the BillingRepository.
*/
class MainActivityViewModel(private val nfr: NewFeaturesRepository) : ViewModel() {

    val billingLifecycleObserver: LifecycleObserver
        get() = nfr.billingLifecycleObserver

    fun correctHeartValParams(hvp: HeartValParameters) {
        // If purchasedEmoji is false, it may have been set to true in past - case of transaction reversed.
        // If transaction has been reversed, we have to insure emoji switch is off.
        if (!(nfr.isPurchased(NewFeaturesRepository.SKU_EMOJI).asLiveData().value == true)) {
            hvp.setUseEmoji(false)
        }
        if (!(nfr.isPurchased(NewFeaturesRepository.SKU_SYMBOLS_AND_COLOURS).asLiveData().value == true)) {
            hvp.setShapeType(ShapeType.StraightHeart)
            hvp.setSymbol(null)
            hvp.setHeartsColor(Color.RED)
        }
        if (!(nfr.isPurchased(NewFeaturesRepository.SKU_MAINFRAME_SHAPES).asLiveData().value == true)) {
            hvp.setMainShape(ShapeType.StraightHeart)
        }
    }

    companion object {
        val TAG = "NewFeaturesRepository:" + MainActivityViewModel::class.simpleName
    }

    class MainActivityViewModelFactory(private val newFeaturesRepository: NewFeaturesRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                return MainActivityViewModel(newFeaturesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
