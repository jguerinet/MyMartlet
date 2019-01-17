/*
 * Copyright 2014-2019 Julien Guerinet
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

package com.guerinet.mymartlet.util.extensions

import android.app.Activity
import com.guerinet.suitcase.log.TimberTag

/**
 * Activity extensions
 * @author Julien Guerinet
 * @since 2.0.0
 */

/**
 * Asserts that the [obj] is not null, and finishes the activity, shows an error toast, and logs
 *  and exception using the object [name] (if supplied, defaults to null) if it is null.
 */
fun <T : Any?, A> A.assertNotNull(
    obj: T?,
    name: String? = null
): T? where A : Activity, A : TimberTag {
    if (obj == null) {
        errorToast()
        name?.apply { timber.e(IllegalArgumentException("$this was null")) }
        finish()
    }
    return obj
}
