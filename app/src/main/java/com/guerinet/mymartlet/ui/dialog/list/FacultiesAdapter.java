/*
 * Copyright 2014-2017 Julien Guerinet
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

package com.guerinet.mymartlet.ui.dialog.list;

import android.content.Context;

import com.guerinet.mymartlet.R;
import com.guerinet.utils.dialog.ListDialogInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Displays a list of faculties to choose from in the walkthrough
 * @author Julien Guerinet
 * @since 2.1.0
 */
public abstract class FacultiesAdapter implements ListDialogInterface {
    /**
     * List of faculties
     */
    private final List<String> faculties;
    /**
     * Current choice
     */
    private final int currentChoice;

    /**
     * Default Constructor
     *
     * @param context        App context
     * @param currentFaculty Currently selected faculty, an empty String if none
     */
    protected FacultiesAdapter(Context context, String currentFaculty) {
        faculties = new ArrayList<>();
        addFaculty(context, R.string.faculty_enviro);
        addFaculty(context, R.string.faculty_arts);
        addFaculty(context, R.string.faculty_continuing_studies);
        addFaculty(context, R.string.faculty_dentistry);
        addFaculty(context, R.string.faculty_education);
        addFaculty(context, R.string.faculty_engineering);
        addFaculty(context, R.string.faculty_graduate);
        addFaculty(context, R.string.faculty_law);
        addFaculty(context, R.string.faculty_management);
        addFaculty(context, R.string.faculty_medicine);
        addFaculty(context, R.string.faculty_music);
        addFaculty(context, R.string.faculty_religion);
        addFaculty(context, R.string.faculty_science);

        // Sort them alphabetically
        Collections.sort(faculties, String::compareToIgnoreCase);

        // Add undefined to the top of the list
        addFaculty(context, R.string.faculty_none);

        // Get the current choice index
        currentChoice = faculties.indexOf(currentFaculty);
    }

    @Override
    public int getCurrentChoice() {
        return currentChoice;
    }

    @Override
    public CharSequence[] getChoices() {
        return faculties.toArray(new CharSequence[faculties.size()]);
    }

    @Override
    public void onChoiceSelected(int position) {
       onFacultySelected(faculties.get(position));
    }

    /**
     * Adds a faculty String to the list
     *
     * @param context  App context
     * @param stringId Id of the Faculty String
     */
    private void addFaculty(Context context, int stringId) {
        faculties.add(context.getString(stringId));
    }

    /**
     * Called when a faculty is selected
     *
     * @param faculty The faculty selected, empty if none
     */
    public abstract void onFacultySelected(String faculty);
}
