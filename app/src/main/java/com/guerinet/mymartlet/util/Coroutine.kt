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

package com.guerinet.mymartlet.util

import com.guerinet.mymartlet.util.retrofit.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.experimental.suspendCoroutine

/**
 *
 * @author Julien Guerinet
 * @since 1.0.0
 */

suspend fun <T> getResult(call: Call<T>): Result =
        suspendCoroutine {
            call.enqueue(object : Callback<T> {

                override fun onResponse(call: Call<T>?, response: Response<T>?) {

                    TODO("Not Implemented")
                }

                override fun onFailure(call: Call<T>?, t: Throwable?) {
                    TODO("Not Implemented")
                }

            })

            val data = call.execute()?.body()
            if (data != null)
        }