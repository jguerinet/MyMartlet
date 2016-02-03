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

package ca.appvelopers.mcgillmobile.util;

import android.content.Context;

import com.guerinet.utils.Utils;

import ca.appvelopers.mcgillmobile.R;

/**
 * Contains various useful static help methods
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Help {

    /**
     * Displays a toast with a generic error message
     *
     * @param context App context
     */
    public static void error(Context context) {
        Utils.toast(context, R.string.error_other);
    }

    /**
     * Returns the Docuum link for a course
     *
     * @param courseName The 4-letter name of the code
     * @param courseCode The course code number
     * @return The Docuum URL
     */
    public static String getDocuumLink(String courseName, String courseCode) {
        return "http://www.docuum.com/mcgill/" + courseName.toLowerCase() + "/" + courseCode;
    }
}
