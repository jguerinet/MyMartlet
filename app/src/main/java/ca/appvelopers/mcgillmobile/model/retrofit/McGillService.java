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

package ca.appvelopers.mcgillmobile.model.retrofit;

import java.util.List;

import ca.appvelopers.mcgillmobile.RegistrationError;
import ca.appvelopers.mcgillmobile.model.Course;
import ca.appvelopers.mcgillmobile.model.CourseResult;
import ca.appvelopers.mcgillmobile.model.Statement;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.Transcript;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Retrofit service to use to get information from McGill
 * @author Julien Guerinet
 * @since 2.2.0
 */
public interface McGillService {

    /**
     * Creates the POST request that logs the user in
     *
     * @param username The user's McGill email
     * @param password The user's password
     * @return The McGill login {@link ResponseBody}
     */
    @FormUrlEncoded
    @POST("twbkwbis.P_ValLogin")
    Call<ResponseBody> login(@Field("sid") String username, @Field("PIN") String password);

    /**
     * Retrieves the user's schedule for a given term
     *
     * @param term The term to retrieve the schedule for, in String format
     * @return The schedule {@link Response}
     */
    @GET("bwskfshd.P_CrseSchdDetl")
    Call<List<Course>> schedule(@Query("term_in") Term term);

    /**
     * Retrieves the user's transcript
     *
     * @return The transcript {@link Response}
     */
    @GET("bzsktran.P_Display_Form?user_type=S&tran_type=V")
    Call<Transcript> transcript();

    /**
     * Retrieves the user's ebill
     *
     * @return The ebill {@link Response}
     */
    @GET("bztkcbil.pm_viewbills")
    Call<List<Statement>> ebill();

    /**
     * Registers or unregisters someone to a list of courses
     *
     * @param url The end of the registration URL
     * @return The registration {@link Response}
     */
    @GET
    Call<List<RegistrationError>> registration(@Url String url);

    /**
     * Searches for a list of classes
     *
     * @param term         Term to search in
     * @param subject      Course subject
     * @param courseNumber Course number
     * @param title        Course title
     * @param minCredits   Course min credits
     * @param maxCredits   Course max credits
     * @param startHour    Course start hour
     * @param startMinute  Course start minute
     * @param startAM      Course start AM/PM
     * @param endHour      Course end hour
     * @param endMinute    Course end minute
     * @param endAM        Course end AM/PM
     * @param days         Course days
     * @return The search {@link Response}
     */
    @GET("bwskfcls.P_GetCrse_Advanced?rsts=dummy&crn=dummy&sel_subj=dummy&sel_day=dummy&" +
            "sel_schd=dummy&sel_insm=dummy&sel_camp=dummy&sel_levl=dummy&sel_sess=dummy&" +
            "sel_instr=dummy&sel_ptrm=dummy&sel_instr=%25&sel_attr=dummy&sel_schd=%25&" +
            "sel_levl=%25&sel_ptrm=%25&sel_attr=%25&SUB_BTN=Get+Course+Sections&path=1")
    Call<List<CourseResult>> search(@Query("term_in") Term term, @Query("sel_subj") String subject,
            @Query("sel_crse") String courseNumber, @Query("sel_title") String title,
            @Query("sel_from_cred") int minCredits, @Query("sel_to_cred") int maxCredits,
            @Query("begin_hh") int startHour, @Query("begin_mi") int startMinute,
            @Query("begin_ap") String startAM, @Query("end_hh") int endHour,
            @Query("end_mi") int endMinute, @Query("end_ap") String endAM,
            @Query("sel_day") List<Character> days);
}
