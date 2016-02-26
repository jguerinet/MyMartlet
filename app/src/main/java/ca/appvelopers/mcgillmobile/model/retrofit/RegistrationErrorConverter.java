/*
 * Copyright 2014-2016 Appvelopers
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

package ca.appvelopers.mcgillmobile.model.retrofit;

import com.squareup.moshi.Types;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.RegistrationError;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Retrofit converter to parse any registration errors that arise during (un)registration
 * @author Julien Guerinet
 * @since 2.2.0
 */
public class RegistrationErrorConverter extends Converter.Factory
        implements Converter<ResponseBody, List<RegistrationError>> {
    /**
     * {@link ParameterizedType} representing a list of {@link RegistrationError}s
     */
    private final ParameterizedType type =
            Types.newParameterizedType(List.class, RegistrationError.class);

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
            Annotation[] annotations, Retrofit retrofit) {
        if (!type.equals(this.type)) {
            //This can only convert a list of registration errors
            return null;
        }
        return new RegistrationErrorConverter();
    }

    @Override
    public List<RegistrationError> convert(ResponseBody value) throws IOException {
        List<RegistrationError> errors = new ArrayList<>();

        //Parse the document
        Document document = Jsoup.parse(value.string(), "UTF-8");

        //Go through the list of relevant rows
        for (Element row : document.getElementsByClass("plaintable")) {
            //Check if an error exists
            if (row.toString().contains("errortext")) {
                //If so, determine what error is present
                for (Element link : document.select("a[href]")) {
                    if (link.toString()
                            .contains("http://www.is.mcgill.ca/whelp/sis_help/rg_errors.htm")) {
                        int crn = Integer.parseInt(link.parent().parent().child(1).text());
                        String error = link.text();
                        errors.add(new RegistrationError(crn, error));
                        Timber.e("(Un)registration error for %d: %s", crn, error);
                    }
                }
            }
        }
        return errors;
    }
}
