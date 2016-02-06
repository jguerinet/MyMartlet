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

package ca.appvelopers.mcgillmobile.ui.dialog.list;

import android.content.Context;

import com.guerinet.utils.dialog.ListDialogInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.util.manager.LanguageManager;

/**
 * Displays a list of place types to choose from in the maps section
 * @author Julien Guerinet
 * @since 2.1.0
 */
public abstract class PlaceTypeListAdapter implements ListDialogInterface {
    /**
     * List of place types
     */
    private List<PlaceType> types;
    /**
     * The current choice
     */
    private int currentChoice;
    /**
     * The {@link LanguageManager} instance
     */
    @Inject
    protected LanguageManager languageManager;

    /**
     * Default Constructor
     *
     * @param context      App context
     * @param mCurrentType The currently selected type
     */
    public PlaceTypeListAdapter(final Context context, PlaceType mCurrentType) {
        App.component(context).inject(this);
        types = new ArrayList<>();
        types.addAll(App.getPlaceTypes());

        //Sort them
        Collections.sort(types, new Comparator<PlaceType>() {
            @Override
            public int compare(PlaceType type, PlaceType type2) {
                return type.getString(context, languageManager.get())
                        .compareToIgnoreCase(type2.getString(context, languageManager.get()));
            }
        });

        //Add the favorites option
        types.add(0, new PlaceType(true));

        //Add the All option
        types.add(0, new PlaceType(false));

        currentChoice = types.indexOf(mCurrentType);
    }

    @Override
    public int getCurrentChoice() {
        return currentChoice;
    }

    @Override
    public CharSequence[] getChoices() {
        CharSequence[] titles = new CharSequence[types.size()];

        //Go through the types
        for (int i = 0; i < types.size(); i ++) {
            //Add its title to the list
            titles[i] = types.get(i).getString(App.getContext(), languageManager.get());
        }

        return titles;
    }

    @Override
    public void onChoiceSelected(int position) {
       onPlaceTypeSelected(types.get(position));
    }

    /**
     * Called when a faculty is selected
     *
     * @param type The {@link PlaceType} selected
     */
    public abstract void onPlaceTypeSelected(PlaceType type);
}
