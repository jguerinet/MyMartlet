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

package ca.appvelopers.mcgillmobile.object;

import android.content.Context;

import ca.appvelopers.mcgillmobile.R;

/**
 * Author: Julien
 * Date: 09/02/14, 3:02 PM
 */
public enum ConnectionStatus {
    OK,
    WRONG_INFO,
    ERROR_UNKNOWN,
    NO_INTERNET,
    MINERVA_LOGOUT,
    AUTHENTICATING,
    CONNECTION_FIRST_ACCESS;

    public String getErrorString(Context context){
        switch(this){
            case OK:
                return null;
            case WRONG_INFO:
                return context.getResources().getString(R.string.login_error_wrong_data);
            case NO_INTERNET:
                return context.getResources().getString(R.string.error_no_internet);
            default:
                return context.getResources().getString(R.string.error_other);
        }
    }

    public String getGAString(){
        switch (this){
            case WRONG_INFO:
                return "Wrong Info";
            case NO_INTERNET:
                return "No Internet";
            case ERROR_UNKNOWN:
                return "Unknown";
            default:
                return null;
        }
    }
}
