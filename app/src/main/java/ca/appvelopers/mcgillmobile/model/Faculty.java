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

import android.content.Context;

import ca.appvelopers.mcgillmobile.R;

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

    public String toString(Context context) {
        switch (this) {
            case ENVIRONMENTAL_SCIENCES:
                return context.getResources().getString(R.string.faculty_enviro);
            case ARTS:
                return context.getResources().getString(R.string.faculty_arts);
            case CONTINUING_STUDIES:
                return context.getResources().getString(R.string.faculty_continuing_studies);
            case DENTISTRY:
                return context.getResources().getString(R.string.faculty_dentistry);
            case EDUCATION:
                return context.getResources().getString(R.string.faculty_education);
            case ENGINEERING:
                return context.getResources().getString(R.string.faculty_engineering);
            case GRADUATE:
                return context.getResources().getString(R.string.faculty_graduate);
            case LAW:
                return context.getResources().getString(R.string.faculty_law);
            case MANAGEMENT:
                return context.getResources().getString(R.string.faculty_management);
            case MEDICINE:
                return context.getResources().getString(R.string.faculty_medecine);
            case MUSIC:
                return context.getResources().getString(R.string.faculty_music);
            case RELIGIOUS_STUDIES:
                return context.getResources().getString(R.string.faculty_religion);
            case SCIENCE:
                return context.getResources().getString(R.string.faculty_science);
            default:
                return "";
        }
    }
}
