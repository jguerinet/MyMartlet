/*
 * Copyright 2014-2016 Julien Guerinet
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
import android.support.v4.util.Pair;

import com.guerinet.utils.dialog.ListDialogInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.LanguagePreference;
import ca.appvelopers.mcgillmobile.util.manager.PlacesManager;

/**
 * Displays a list of place types to choose from in the maps section
 * @author Julien Guerinet
 * @since 2.1.0
 */
public abstract class PlaceTypeListAdapter implements ListDialogInterface {
    /**
     * List of place types with their associated String
     */
    private List<Pair<PlaceType, String>> types;
    /**
     * The current choice
     */
    private int currentChoice;
    /**
     * The {@link LanguagePreference} instance
     */
    @Inject
    protected LanguagePreference languagePreference;
    /**
     * {@link PlacesManager} instance
     */
    @Inject
    protected PlacesManager placesManager;

    /**
     * Default Constructor
     *
     * @param context     App context
     * @param currentType Currently selected type
     */
    public PlaceTypeListAdapter(final Context context, PlaceType currentType) {
        App.component(context).inject(this);
        types = new ArrayList<>();

        for (PlaceType type : placesManager.getPlaceTypes()) {
            types.add(new Pair<>(type, type.getString(context, languagePreference.get())));
        }

        //Sort them
        Collections.sort(types, new Comparator<Pair<PlaceType, String>>() {
            @Override
            public int compare(Pair<PlaceType, String> lhs, Pair<PlaceType, String> rhs) {
                return lhs.second.compareToIgnoreCase(rhs.second);
            }
        });

        //Add the favorites option
        PlaceType type = new PlaceType(true);
        types.add(0, new Pair<>(type, type.getString(context, languagePreference.get())));

        //Add the All option
        type = new PlaceType(false);
        types.add(0, new Pair<>(type, type.getString(context, languagePreference.get())));

        //Find the index of the current choice
        for (int i = 0; i < types.size(); i ++) {
            if (types.get(i).first.equals(currentType)) {
                currentChoice = i;
                break;
            }
        }
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
            titles[i] = types.get(i).second;
        }

        return titles;
    }

    @Override
    public void onChoiceSelected(int position) {
       onPlaceTypeSelected(types.get(position).first);
    }

    /**
     * Called when a faculty is selected
     *
     * @param type The {@link PlaceType} selected
     */
    public abstract void onPlaceTypeSelected(PlaceType type);
}
