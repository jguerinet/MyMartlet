/*
 * Copyright 2014-2018 Julien Guerinet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.guerinet.mymartlet.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

/**
 * Base [ViewModel] with some common observables
 * @author Julien Guerinet
 * @since 2.0.0
 */
open class BaseViewModel : ViewModel() {

    /** Tells the view whether the toolbar progress bar should be visible or not */
    val isToolbarProgressVisible: MutableLiveData<Boolean> = MutableLiveData()

    /**
     * Starts an update by showing the progress bar, running the [block], hiding the
     *  progress bar, and returning the eventual [Exception] from the block
     */
    suspend fun update(block: () -> Exception?): Exception? {
        isToolbarProgressVisible.postValue(true)
        val exception = withContext(CommonPool) {
            block()
        }
        isToolbarProgressVisible.postValue(false)
        return exception
    }
}