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

package com.guerinet.mymartlet.util.retrofit;

import com.guerinet.mymartlet.model.Season;
import com.guerinet.mymartlet.model.Semester;
import com.guerinet.mymartlet.model.Term;
import com.guerinet.mymartlet.model.Transcript;
import com.guerinet.mymartlet.model.transcript.TranscriptCourse;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Retrofit converter to parse the user's transcript
 * @author Julien Guerinet
 * @since 2.2.0
 */
public class TranscriptConverter extends Converter.Factory
        implements Converter<ResponseBody, TranscriptConverter.TranscriptResponse> {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
            Annotation[] annotations, Retrofit retrofit) {
        if (!type.equals(TranscriptResponse.class)) {
            // This can only convert transcripts
            return null;
        }
        return new TranscriptConverter();
    }

    @Override
    public TranscriptResponse convert(ResponseBody value) throws IOException {
        //Parse ResponseBody HTML String into a document
        Elements rows = Jsoup.parse(value.string()).getElementsByClass("fieldmediumtext");

        /*
         * Main loop:
         * This will iterate through every row of the transcript data and check for various tokens
         * Once a match is found, the value in the appropriate row will be saved to a variable
         */
        List<Semester> semesters = new ArrayList<>();
        List<TranscriptCourse> courses = new ArrayList<>();
        double cgpa = -1;
        double totalCredits = -1;
        int semesterId = 0;

        for (int index = 0; index < rows.size(); index ++) {
            String text = rows.get(index).text();
            //Check the text at the start of the row
            //If it matches one of the tokens, take the corresponding data out
            //of one of the following rows, depending on the HTML layout

            if (text.startsWith("CUM GPA")) {
                //CGPA
                try {
                    cgpa = Double.parseDouble(rows.get(index + 1).text());
                } catch (NumberFormatException e) {
                    Timber.e(e, "Transcript Parse Error: CGPA");
                }
            } else if(text.startsWith("TOTAL CREDITS:")) {
                //Credits
                try {
                    totalCredits = Double.parseDouble(rows.get(index + 1).text());
                } catch (NumberFormatException e) {
                    Timber.e(e, "Transcript Parse Error: Total Credits");
                }
            } else if (isSemesterStart(text)) {
                //Semester

                //Divide the semester info into separate items
                String[] semesterItems = text.trim().split("\\s+");

                @Season.Type String season;
                String yearString;

                //Find the right season and year, making sure to get the right array index
                if (text.startsWith(Season.FALL) || text.startsWith(Season.WINTER) ||
                        text.startsWith(Season.SUMMER) ) {
                    //Normal Semester that starts with a season name
                    season = Season.getSeason(semesterItems[0]);
                    yearString = semesterItems[1];
                } else if (text.startsWith("Change")) {
                    //Change Semester
                    season = Season.getSeason(semesterItems[3]);
                    yearString = semesterItems[4];
                } else {
                    //Readmitted
                    season = Season.getSeason(semesterItems[1]);
                    yearString = semesterItems[2];
                }

                int year = -1;
                try {
                    //Parse the semester year
                    year = Integer.valueOf(yearString);
                } catch (NumberFormatException e) {
                    Timber.e(e, "Transcript Parse Error: Semester Year");
                }

                String program = "";
                String bachelor = "";
                double termCredits = 0;
                double termGPA = 0.0;
                boolean fullTime = false;
                boolean hasCourse = false;

                //Increment the index before starting the semester loop
                int semesterIndex = index + 1;

                //Search rows until the end of the semester is reached
                //Conditions for end of semester:
                //1. End of transcript is reached
                //2. The words "Fall" "Summer" or "Winter" appear
                while (true) {
                    //Get the current data row text
                    text = rows.get(semesterIndex).text();

                    if (text.contains("Granted")) {
                        //Student has graduated
                        break;
                    } else if (text.startsWith("Dip") || text.startsWith("Bachelor") ||
                            text.startsWith("Master") || text.startsWith("Doctor")) {
                        //Semester Info

                        //Example string:
                        //"Bachelor&nbps;of&nbsp;Engineering"<br>
                        //"Full-time&nbsp;Year&nbsp;0"<br>
                        //"Electrical&nbsp;Engineering"

                        String[] degreeDetails = text.split(" ");
                        bachelor = degreeDetails[0];

                        //Check if student is full time
                        if (degreeDetails[1].startsWith("Full-time")) {
                            fullTime = true;
                        }

                        //Skip first two lines: these are for bachelor and full time/part time
                        for (int i = 2; i < degreeDetails.length; i ++) {
                            program += degreeDetails[i] + " ";
                        }
                    } else if (text.startsWith("TERM GPA")) {
                        //Term GPA
                        try {
                            termGPA = Double.parseDouble(rows.get(semesterIndex + 1).text());
                        } catch (NumberFormatException e) {
                            Timber.e(e, "Transcript Parse Error: Term GPA");
                        }
                    } else if (text.startsWith("TERM TOTALS:")) {
                        //Term Credits
                        try {
                            termCredits = Double.parseDouble(rows.get(semesterIndex + 2).text());
                        } catch (NumberFormatException e) {
                            Timber.e(e, "Transcript Parse Error: Term Credits");
                        }
                    } else if(text.matches("[A-Za-z]{4} [0-9]{3}.*") ||
                            text.matches("[A-Za-z]{3}[0-9] [0-9]{3}") ||
                            text.startsWith("Credits/Exemptions")) {
                        //Course Info
                        String title = "";
                        String code = "";
                        String grade = "N/A";
                        String averageGrade = "";
                        double credits = -1;

                        //Extract course information if row contains a course code
                        //Regex looks for a string in the form "ABCD ###"
                        if (text.matches("[A-Za-z]{4} [0-9]{3}.*") ||
                                text.matches("[A-Za-z]{3}[0-9] [0-9]{3}")) {
                            if (text.matches("[A-Za-z]{4} [0-9]{3}")) {
                                //One semester courses are in the form ABCD ###
                                code = text;
                            } else if (text.matches("[A-Za-z]{3}[0-9] [0-9]{3}")) {
                                //Some courses have the form ABC#
                                code = text;
                            } else {
                                //Multi semester courses are in the form ABCD ###D#
                                try {
                                    //Extract first seven characters from string
                                    code = text.substring(0, 10);
                                } catch (Exception e) {
                                    Timber.e(e, "Transcript Parse Error: Course Code");
                                }
                            }

                            title = rows.get(semesterIndex + 2).text();

                            //Failed courses are missing the earned credits row
                            //Check row to see if earned credit exists
                            try {
                                credits = Double.parseDouble(rows.get(semesterIndex + 6).text());
                            } catch (Exception ignored) {}

                            //Obtain user's grade
                            grade = rows.get(semesterIndex + 4).text();

                            //Check for deferred classes
                            if (grade.equals("L")) {
                                grade = rows.get(semesterIndex + 13).text();
                            }

                            //If average grades haven't been released on minerva, index will be null
                            averageGrade = "";
                            try {
                                if (rows.get(semesterIndex + 7).text()
                                        .matches("[ABCDF].|[ABCDF]")) {
                                    //Regex looks for a letter grade
                                    averageGrade = rows.get(semesterIndex+ 7).text();
                                } else if (rows.get(semesterIndex+ 6).text()
                                        .matches("[ABCDF].|[ABCDF]")) {
                                    //Failed course, average grade appears one row earlier
                                    averageGrade = rows.get(semesterIndex + 6).text();
                                }
                            } catch (IndexOutOfBoundsException ignored) {}
                        } else {
                            //Extract transfer credit information
                            if (!rows.get(semesterIndex + 3).text().matches("[A-Za-z]{4}.*")) {
                                //Individual transferred courses not listed
                                code = rows.get(semesterIndex + 2).text();

                                //Extract the number of credits granted
                                try {
                                    credits = getCredits(code);
                                } catch (Exception e) {
                                    Timber.e(e, "Transcript Parse Error: Credits");
                                }
                            } else {
                                //Individual transferred courses listed
                                try {
                                    //Try checking for the number of credits transferred per course
                                    code = rows.get(semesterIndex + 2).text();
                                    title = rows.get(semesterIndex + 3).text() + " " +
                                            rows.get(semesterIndex + 4).text();
                                    credits = Double.parseDouble(rows.get(semesterIndex + 5)
                                            .text());
                                } catch (NumberFormatException e) {
                                    //Number of credits per course not listed
                                    try {
                                        code = rows.get(semesterIndex+ 2).text();
                                        title = "";

                                        credits = getCredits(code);

                                        //Add the course codes for transferred courses
                                        int addedIndex = 3;
                                        boolean first = true;
                                        while (rows.get(semesterIndex +
                                                addedIndex).text().matches("[A-Za-z]{4}.*")) {
                                            if (!first) {
                                                title += "\n";
                                            }
                                            first = false;
                                            title = title + rows.get(semesterIndex +
                                                    addedIndex).text() + " " +
                                                    rows.get(semesterIndex+ addedIndex + 1).text();
                                            addedIndex += 2;
                                        }
                                    } catch (Exception e2) {
                                        Timber.e(e, "Transcript Parse Error: Credits");
                                    }
                                }
                            }

                            termCredits = credits;
                        }

                        // There is at least one course
                        hasCourse = true;
                        courses.add(new TranscriptCourse(semesterId, new Term(season, year), code,
                                title, credits, grade, averageGrade));
                    }

                    //Breaks the loop if the next semester is reached
                    if (isSemesterStart(text)) {
                        break;
                    }

                    //Increment the index
                    semesterIndex ++;

                    //Reached the end of the transcript, break loop
                    try {
                        rows.get(semesterIndex);
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                }

                // Check if there are any courses associated with the semester
                //  If not, don't add the semester to the list of semesters
                if (hasCourse) {
                    semesters.add(new Semester(semesterId, new Term(season, year), program,
                            bachelor, termCredits, termGPA, fullTime));
                    semesterId ++;
                }

            }
        }

        return new TranscriptResponse(new Transcript(cgpa, totalCredits), semesters, courses);
    }

    /**
     * @param text The current text of the data row
     * @return True if the data row is the start of the semester, false otherwise
     */
    private boolean isSemesterStart(String text) {
        return text.startsWith(Season.FALL) || text.startsWith(Season.WINTER) ||
                text.startsWith(Season.SUMMER) || text.startsWith("Readmitted Fall") ||
                text.startsWith("Readmitted Winter") || text.startsWith("Readmitted Summer") ||
                text.startsWith("Change");
    }

    /**
     * Extracts the number of credits
     *
     * @param credits String to extract the credits from
     * @return Number of credits
     * @throws Exception
     */
    private double getCredits(String credits) throws Exception {
        credits = credits.replaceAll("\\s", "");
        String[] creditArray = credits.split("-");
        creditArray = creditArray[1].split("credits");
        return Double.parseDouble(creditArray[0]);
    }

    /**
     * Response object with all of the parsed info
     */
    public static class TranscriptResponse {
        public final Transcript transcript;
        public final List<Semester> semesters;
        public final List<TranscriptCourse> courses;

        private TranscriptResponse(Transcript transcript, List<Semester> semesters,
                List<TranscriptCourse> courses) {
            this.transcript = transcript;
            this.semesters = semesters;
            this.courses = courses;

            // Inverse the semesters to get them in reverse chronological order
            Collections.reverse(this.semesters);
        }
    }
}
