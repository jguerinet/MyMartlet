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

package ca.appvelopers.mcgillmobile.model;

import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import ca.appvelopers.mcgillmobile.R;

/**
 * The faculties present at McGill
 * @author Julien Guerinet 
 * @since 1.0.0
 */
public class Faculty {
    /**
     * The different faculties
     */
    @Retention(RetentionPolicy.CLASS)
    @IntDef({NONE, ENVIRONMENTAL_SCIENCES, ARTS, CONTINUING_STUDIES, DENTISTRY, EDUCATION,
            ENGINEERING, GRADUATE, LAW, MANAGEMENT, MEDICINE, MUSIC, RELIGIOUS_STUDIES, SCIENCE})
    public @interface Type{}
    public static final int NONE = -1;
    public static final int ENVIRONMENTAL_SCIENCES = 0;
    public static final int ARTS = 1;
    public static final int CONTINUING_STUDIES = 2;
    public static final int DENTISTRY = 3;
    public static final int EDUCATION = 4;
    public static final int ENGINEERING = 5;
    public static final int GRADUATE = 6;
    public static final int LAW = 7;
    public static final int MANAGEMENT = 8;
    public static final int MEDICINE = 9;
    public static final int MUSIC = 10;
    public static final int RELIGIOUS_STUDIES = 11;
    public static final int SCIENCE = 12;

    /**
     * @param context App context
     * @param faculty Faculty {@link Type}
     * @return Faculty title
     */
    public static String getString(Context context, @Type int faculty) {
        switch (faculty) {
            case NONE:
                return context.getString(R.string.faculty_none);
            case ENVIRONMENTAL_SCIENCES:
                return context.getString(R.string.faculty_enviro);
            case ARTS:
                return context.getString(R.string.faculty_arts);
            case CONTINUING_STUDIES:
                return context.getString(R.string.faculty_continuing_studies);
            case DENTISTRY:
                return context.getString(R.string.faculty_dentistry);
            case EDUCATION:
                return context.getString(R.string.faculty_education);
            case ENGINEERING:
                return context.getString(R.string.faculty_engineering);
            case GRADUATE:
                return context.getString(R.string.faculty_graduate);
            case LAW:
                return context.getString(R.string.faculty_law);
            case MANAGEMENT:
                return context.getString(R.string.faculty_management);
            case MEDICINE:
                return context.getString(R.string.faculty_medicine);
            case MUSIC:
                return context.getString(R.string.faculty_music);
            case RELIGIOUS_STUDIES:
                return context.getString(R.string.faculty_religion);
            case SCIENCE:
                return context.getString(R.string.faculty_science);
            default:
                throw new IllegalArgumentException("Unknown Faculty");
        }
    }
}
