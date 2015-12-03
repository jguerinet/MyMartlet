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

package ca.appvelopers.mcgillmobile.model;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;

/**
 * Represents the status of a connection
 * @author Julien Guerinet
 * @since 1.0.0
 */
public enum ConnectionStatus {
    /**
     * Connection was successful
     */
    OK,
    /**
     * The credentials were wrong
     */
    WRONG_INFO,
    /**
     * An unknown error occurred
     */
    ERROR_UNKNOWN,
    /**
     * The user is not connected to internet
     */
    NO_INTERNET,
    /**
     * The user has been logged out of Minerva
     */
    MINERVA_LOGOUT;

    /**
     * @return The status' associated error String
     */
    public String getErrorString(){
        switch(this){
            case OK:
                return null;
            case WRONG_INFO:
                return App.getContext().getString(R.string.login_error_wrong_data);
            case NO_INTERNET:
                return App.getContext().getString(R.string.error_no_internet);
            default:
                return App.getContext().getString(R.string.error_other);
        }
    }

    /**
     * @return The String to use as the GA Label
     */
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
