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

import android.text.TextUtils;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import org.threeten.bp.ZonedDateTime;

/**
 * Converts ZonedDateTimes to Strings and vice-versa for DBFlow
 * @author Julien Guerinet
 * @since 3.0.0
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class ZonedDateTimeConverter extends TypeConverter<String, ZonedDateTime> {
    @Override
    public String getDBValue(ZonedDateTime model) {
        if (model == null) {
            return null;
        }
        return model.toString();
    }

    @Override
    public ZonedDateTime getModelValue(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        return ZonedDateTime.parse(data);
    }
}
