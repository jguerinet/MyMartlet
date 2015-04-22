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
 * The faculties present at McGill
 * @author Julien Guerinet 
 * @version 2.0
 * @since 1.0
 */
public enum Faculty {
    ENVIRONMENTAL_SCIENCES,
    ARTS,
    CONTINUING_STUDIES,
    DENTISTRY,
    EDUCATION,
    ENGINEERING,
    GRADUATE,
    LAW,
    MANAGEMENT,
    MEDICINE,
    MUSIC,
    RELIGIOUS_STUDIES,
    SCIENCE;

    @Override
    public String toString() {
        switch (this) {
            case ENVIRONMENTAL_SCIENCES:
                return App.getContext().getString(R.string.faculty_enviro);
            case ARTS:
                return App.getContext().getString(R.string.faculty_arts);
            case CONTINUING_STUDIES:
                return App.getContext().getString(R.string.faculty_continuing_studies);
            case DENTISTRY:
                return App.getContext().getString(R.string.faculty_dentistry);
            case EDUCATION:
                return App.getContext().getString(R.string.faculty_education);
            case ENGINEERING:
                return App.getContext().getString(R.string.faculty_engineering);
            case GRADUATE:
                return App.getContext().getString(R.string.faculty_graduate);
            case LAW:
                return App.getContext().getString(R.string.faculty_law);
            case MANAGEMENT:
                return App.getContext().getString(R.string.faculty_management);
            case MEDICINE:
                return App.getContext().getString(R.string.faculty_medecine);
            case MUSIC:
                return App.getContext().getString(R.string.faculty_music);
            case RELIGIOUS_STUDIES:
                return App.getContext().getString(R.string.faculty_religion);
            case SCIENCE:
                return App.getContext().getString(R.string.faculty_science);
            default:
                return "";
        }
    }
}
