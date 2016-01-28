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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.guerinet.utils.Util;

import java.io.IOException;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;
import okio.BufferedSource;
import okio.Okio;

/**
 * Contains various useful static help methods
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class Help {

    /**
     * Displays a toast with a generic error message
     */
    public static void error() {
        Util.toast(App.getContext(), R.string.error_other);
    }

    /**
     * Gets the Docuum link for a course
     *
     * @param courseName The 4-letter name of the code
     * @param courseCode The course code number
     * @return The Docuum URL
     */
    public static String getDocuumLink(String courseName, String courseCode){
        return "http://www.docuum.com/mcgill/" + courseName.toLowerCase() + "/" + courseCode;
    }

    /* OTHER */

    /**
     * Reads a String from a local file
     *
     * @param context      The app context
     * @param fileResource The resource of the file to read
     * @return The file in String format
     */
    public static String readFromFile(Context context, int fileResource) {
        try{
            BufferedSource fileSource =
                    Okio.buffer(Okio.source(context.getResources().openRawResource(fileResource)));

            return fileSource.readUtf8();
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return True if the user is connected to the internet, false otherwise
     */
    public static boolean isConnected() {
        ConnectivityManager connectManager = (ConnectivityManager)
                App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }
}
