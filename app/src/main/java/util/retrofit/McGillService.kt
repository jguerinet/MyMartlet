/*
 * Copyright 2014-2019 Julien Guerinet
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

import com.guerinet.mymartlet.model.*
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Retrieves information from McGill
 * @author Julien Guerinet
 * @since 1.0.0
 */
interface McGillService {

    /**
     * Attempts to log a user in with the [username] and [password]
     */
    @FormUrlEncoded
    @POST("twbkwbis.P_ValLogin")
    fun login(@Field("sid") username: String, @Field("PIN") password: String): Call<ResponseBody>

    /**
     * Retrieves the user's schedule for the [term]
     */
    @GET("bwskfshd.P_CrseSchdDetl")
    fun schedule(@Query("term_in") term: Term): Call<List<Course>>

    /**
     * Retrieves the user's transcript
     */
    @GET("bzsktran.P_Display_Form?user_type=S&tran_type=V")
    fun transcript(): Call<TranscriptConverter.TranscriptResponse>

    /**
     * Retrieves the user's ebill
     */
    @GET("bztkcbil.pm_viewbills")
    fun ebill(): Deferred<List<Statement>>

    /**
     * Registers or unregisters someone to a list of courses with the [url]
     */
    @GET
    fun registration(@Url url: String): Call<List<RegistrationError>>

    /**
     * Searches for a list of classes by [term], [subject], [courseNumber], [title], [minCredits],
     *  [maxCredits], [startHour], [startMinute], [startAM], [endHour], [endMinute], [endAM], and
     *  [days]
     */
    @GET("bwskfcls.P_GetCrse_Advanced?rsts=dummy&crn=dummy&sel_subj=dummy&sel_day=dummy&" +
            "sel_schd=dummy&sel_insm=dummy&sel_camp=dummy&sel_levl=dummy&sel_sess=dummy&" +
            "sel_instr=dummy&sel_ptrm=dummy&sel_instr=%25&sel_attr=dummy&sel_schd=%25&" +
            "sel_levl=%25&sel_ptrm=%25&sel_attr=%25&SUB_BTN=Get+Course+Sections&path=1")
    fun search(@Query("term_in") term: Term, @Query("sel_subj") subject: String,
            @Query("sel_crse") courseNumber: String, @Query("sel_title") title: String,
            @Query("sel_from_cred") minCredits: Int, @Query("sel_to_cred") maxCredits: Int,
            @Query("begin_hh") startHour: Int, @Query("begin_mi") startMinute: Int,
            @Query("begin_ap") startAM: String, @Query("end_hh") endHour: Int,
            @Query("end_mi") endMinute: Int, @Query("end_ap") endAM: String,
            @Query("sel_day") days: List<Char>): Call<List<CourseResult>>
}
