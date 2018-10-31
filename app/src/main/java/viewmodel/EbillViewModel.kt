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

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.guerinet.mymartlet.model.Semester
import com.guerinet.mymartlet.model.Statement
import com.guerinet.mymartlet.util.retrofit.McGillService
import com.guerinet.mymartlet.util.room.daos.StatementDao

/**
 * [ViewModel] for the [Semester]
 * @author Julien Guerinet
 * @since 2.0.0
 */
class EbillViewModel(private val statementDao: StatementDao, private val mcGillService: McGillService)
    : BaseViewModel() {

    val statements: LiveData<List<Statement>> by lazy { statementDao.getAll() }

    suspend fun refresh(): Exception? = update {
        try {
            val response = mcGillService.ebill().execute().body()
                    ?: return@update Exception("Body was null")

            // Save the response
            statementDao.update(response)
            return@update null
        } catch (e: Exception) {
            return@update e
        }
    }
}