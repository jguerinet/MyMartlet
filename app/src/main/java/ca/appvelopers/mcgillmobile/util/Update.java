/*
 * Copyright 2014-2015 Appvelopers
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

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.util.storage.Load;
import ca.appvelopers.mcgillmobile.util.storage.Save;

/**
 * TODO
 * Runs any update code
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public class Update {
    public static void update(Context context){
        //Get the version number
        int versionNumber = Help.getVersionCode();

        //Load the current version number from the preferences.
        int storedVersion = Load.versionCode();

        //Stored version is smaller than version number
        while(storedVersion < versionNumber){
            //First time opening the app
            if(storedVersion == -1){
                init();
                //Break out of the loop
                break;
            }
            if(storedVersion == 6){
                update7();
            }

            /* This will be where the version updates would go */

            storedVersion ++;
        }

        //Store the new version in the SharedPrefs
        Save.versionCode(versionNumber);
    }

    /**
     * Run anything that needs to be run the first time the app is opened
     */
    private static void init(){
        //Force the config downloader
        App.forceReload = true;
    }

    private static void update7(){
        //Force the user to re-update all of the information in the app
        App.forceUserReload = true;
    }
}
