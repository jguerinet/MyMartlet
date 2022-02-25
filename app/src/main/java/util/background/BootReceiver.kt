/*
 * Copyright 2014-2022 Julien Guerinet
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

package com.guerinet.mymartlet.util.background

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.guerinet.mymartlet.util.Prefs
import com.guerinet.mymartlet.util.prefs.UsernamePref
import com.guerinet.suitcase.settings.BooleanSetting
import com.orhanobut.hawk.Hawk
import java.util.Calendar

/**
 * TODO Clean up
 * Automatically (re)starts the alarm if needed when the device is rebooted or the user opts in.
 * @author Shabbir Hussain
 * @author Julien Guerinet
 * @since 2.0.0
 */
class BootReceiver : BroadcastReceiver() {

    var usernamePref: UsernamePref? = null

    /**
     * Seat checker [BooleanSetting]
     */
    var seatCheckerPref: BooleanSetting? = null

    /**
     * Grade checker [BooleanSetting]
     */
    var gradeCheckerPref: BooleanSetting? = null

    override fun onReceive(context: Context, intent: Intent) {
        setAlarm(
            context, usernamePref?.value, Hawk.get<String>(Prefs.PASSWORD),
            seatCheckerPref?.value ?: false, gradeCheckerPref?.value ?: false
        )
    }

    companion object {

        /**
         * Starts the alarm receiver if needed
         *
         * @param context The app context
         */
        fun setAlarm(
            context: Context,
            username: String?,
            password: String?,
            seatChecker: Boolean,
            gradeChecker: Boolean
        ) {
            //If we don't need it, don't start it
            if (username == null || password == null || !seatChecker && !gradeChecker) {
                //Make sure it's cancelled
                cancelAlarm(context)
                return
            }

            //Get the alarm manager
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            //Set the alarm to fire at approximately 8:30 AM  according to the device's clock,
            //  and to repeat once a day.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, 8)
            calendar.set(Calendar.MINUTE, 30)

            manager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, getPendingIntent(context)
            )
        }

        /**
         * Cancels the alarm
         *
         * @param context The app context
         */
        fun cancelAlarm(context: Context) {
            //Get the alarm manager
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            //Cancel the pending intent
            manager.cancel(getPendingIntent(context))
        }

        /**
         * Gets the pending intent for the checker alarm
         *
         * @param context The app context
         * @return The pending intent
         */
        private fun getPendingIntent(context: Context): PendingIntent {
            // TODO
            val intent = Intent() // Intent(context, CheckerService::class.java)
            return PendingIntent.getService(context, 0, intent, 0)
        }
    }
}
