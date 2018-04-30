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

package com.guerinet.mymartlet.util.retrofit

import java.io.IOException

/**
 *
 * @author Julien Guerinet
 * @since 1.0.0
 */
sealed class Result {

    open class Success<T>(val result: T?) : Result()

    class EmptySuccess : Success<Nothing>(null)

    class Failure(val exception: IOException) : Result()
}