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

import com.guerinet.mymartlet.model.RegistrationError
import com.squareup.moshi.Types
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Converter
import retrofit2.Retrofit
import timber.log.Timber
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.ArrayList

/**
 * Retrofit converter to parse any registration errors that arise during (un)registration
 * @author Julien Guerinet
 * @since 2.2.0
 */
class RegistrationErrorConverter : Converter.Factory(),
    Converter<ResponseBody, List<RegistrationError>> {

    /** [ParameterizedType] representing a list of [RegistrationError]s */
    private val type = Types.newParameterizedType(List::class.java, RegistrationError::class.java)

    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return if (type!!.toString() != this.type.toString()) {
            //This can only convert a list of registration errors
            null
        } else RegistrationErrorConverter()
    }

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): List<RegistrationError> {
        val errors = ArrayList<RegistrationError>()

        //Parse the document
        val document = Jsoup.parse(value.string(), "UTF-8")

        //Go through the list of relevant rows
        for (row in document.getElementsByClass("plaintable")) {
            //Check if an error exists
            if (row.toString().contains("errortext")) {
                //If so, determine what error is present
                for (link in document.select("a[href]")) {
                    if (link.toString()
                            .contains("http://www.is.mcgill.ca/whelp/sis_help/rg_errors.htm")
                    ) {
                        val crn = Integer.parseInt(link.parent().parent().child(1).text())
                        val error = link.text()
                        errors.add(RegistrationError(crn, error))
                        Timber.e("(Un)registration error for %d: %s", crn, error)
                    }
                }
            }
        }
        return errors
    }
}
