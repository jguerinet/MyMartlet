/*
 * Copyright 2014-2016 Julien Guerinet
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

package ca.appvelopers.mcgillmobile.util.dbflow.converters;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Converts a list of integers into a CSV String and vice versa
 * @author Julien Guerinet
 * @since 2.4.0
 */
public class IntegerListConverter extends TypeConverter<String, List<Integer>> {
    @Override
    public String getDBValue(List<Integer> model) {
        if (model == null) {
            return null;
        }

        String string = "";

        for (int i = 0; i < model.size(); i ++) {
            string += model.get(i);

            // Add commas for every position except the last one
            if (i != model.size() - 1) {
                string += ",";
            }
        }
        return string;
    }

    @Override
    public List<Integer> getModelValue(String data) {
        if (data == null) {
            return null;
        }

        List<Integer> model = new ArrayList<>();
        for (String character : data.split(",")) {
            try {
                model.add(Integer.valueOf(character));
            } catch (Exception e) {
                Timber.e(e, "Cannot convert into number: %s", character);
            }
        }

        return model;
    }
}
