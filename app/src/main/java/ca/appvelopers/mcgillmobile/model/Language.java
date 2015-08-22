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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.R;

/**
 * The languages that this app is offered in
 * @author Julien Guerinet
 * @version 2.0.0
 * @since 1.0.0
 */
public enum Language {
    ENGLISH,
    FRENCH;

    /**
     * @return The language String
     */
    public String getString(){
        switch(this){
            case ENGLISH:
                return App.getContext().getString(R.string.english);
            case FRENCH:
                return App.getContext().getString(R.string.french);
            default:
                return null;
        }
    }

    /**
     * @return The Strings of all of the languages available
     */
    public static List<String> getStrings(){
        List<String> languages = new ArrayList<>();

        for(Language language : values()){
            languages.add(language.getString());
        }

        //Sort them alphabetically
        Collections.sort(languages);

        return languages;
    }

    @Override
    public String toString(){
        switch (this){
            case FRENCH:
                return "fr";
            default:
                return "en";
        }
    }
}
