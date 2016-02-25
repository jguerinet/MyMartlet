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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.threeten.bp.LocalDate;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import ca.appvelopers.mcgillmobile.model.Statement;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Retrofit converter to parse the user's transcript
 * @author Julien Guerinet
 * @since 2.2.0
 */
public class EbillConverter extends Converter.Factory
        implements Converter<ResponseBody, List<Statement>> {
    /**
     * {@link ParameterizedType} representing a list of statements
     */
    private final ParameterizedType type = Types.newParameterizedType(List.class, Statement.class);

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
            Annotation[] annotations, Retrofit retrofit) {
        if (!type.equals(this.type)) {
            //This can only convert transcripts
            return null;
        }
        return new EbillConverter();
    }

    @Override
    public List<Statement> convert(ResponseBody value) throws IOException {
        List<Statement> statements = new ArrayList<>();

        //Get the table
        Element table = Jsoup.parse(value.string()).getElementsByClass("datadisplaytable").first();

        if (table == null) {
            //If there is no table (no statements, return an empty list
            return statements;
        }

        //Go through the rows and extract the necessary information
        Elements rows = table.getElementsByTag("tr");
        for (int i = 2; i < rows.size(); i += 2) {
            //Get the cells for the current row
            Elements cells = rows.get(i).getElementsByTag("td");

            //Parse the statement and due dates
            LocalDate date = ScheduleConverter.parseDate(cells.get(0).text().trim());
            LocalDate dueDate = ScheduleConverter.parseDate(cells.get(3).text().trim());

            //Get the amount String without the $ sign at the beginning
            String amountString = cells.get(5).text().trim().substring(1);

            double amount = -1;
            try {
                if (amountString.endsWith("-")) {
                    //If the String ends with a dash (Mcgill owes the student),
                    //  remove it and parse the resulting amount
                    amount = NumberFormat.getNumberInstance(java.util.Locale.US)
                            .parse(amountString.substring(0, amountString.length() - 1))
                            .doubleValue();
                    //Negate the amount
                    amount *= -1;
                } else {
                    //If not, just parse the amount
                    amount = NumberFormat.getNumberInstance(java.util.Locale.US)
                            .parse(amountString)
                            .doubleValue();
                }
            } catch (ParseException e) {
                Timber.e(e, "Ebill Parser Error: Amount");
            }

            //Add the new statement
            statements.add(new Statement(date, dueDate, amount));
        }

        return statements;
    }
}
