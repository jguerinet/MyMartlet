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

package ca.appvelopers.mcgillmobile.util.dbflow.converters;

import com.raizlabs.android.dbflow.converter.TypeConverter;

import ca.appvelopers.mcgillmobile.model.Term;

/**
 * Converts a {@link Term} to a String for a DB and vice-versa
 * @author Julien Guerinet
 * @since 2.4.0
 */
@com.raizlabs.android.dbflow.annotation.TypeConverter
public class TermTypeConverter extends TypeConverter<String, Term> {

    @Override
    public String getDBValue(Term model) {
        return model.getId();
    }

    @Override
    public Term getModelValue(String data) {
        return Term.parseTerm(data);
    }
}
