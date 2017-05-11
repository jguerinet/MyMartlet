/*
 * Copyright 2014-2017 Julien Guerinet
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

package com.guerinet.mymartlet.util.dbflow.converters;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.threeten.bp.LocalDate;

/**
 * Converts LocalDates to a String for DBFlow and vice-versa
 * @author Julien Guerinet
 * @since 2.4.0
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class LocalDateConverter extends TypeConverter<String, LocalDate> {

    @Override
    public String getDBValue(LocalDate model) {
        if (model == null) {
            return null;
        }
        return model.toString();
    }

    @Override
    public LocalDate getModelValue(String data) {
        if (data == null) {
            return null;
        }
        return LocalDate.parse(data);
    }
}
