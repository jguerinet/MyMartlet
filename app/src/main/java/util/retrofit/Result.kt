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

package com.guerinet.mymartlet.util.retrofit

import java.io.IOException

/**
 * Represents the different results of an HTTP call
 * @author Julien Guerinet
 * @since 1.0.0
 */
sealed class Result {

    /**
     * A successful request
     *  TODO: Do we need this?
     *
     * @property result Resulting object [T] of the HTTP call
     */
    open class Success<T>(val result: T?) : Result()

    /**
     * A successful request, with no explicit resulting object
     */
    class EmptySuccess : Success<Nothing>(null)

    /**
     * A failed request
     *
     * @property exception Exception returned when making the request
     */
    class Failure(val exception: IOException) : Result()
}
