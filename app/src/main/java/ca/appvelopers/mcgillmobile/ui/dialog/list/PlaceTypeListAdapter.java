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

import com.guerinet.utils.dialog.ListDialogInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.PlaceType;

/**
 * Displays a list of place types to choose from in the maps section
 * @author Julien Guerinet
 * @since 2.1.0
 */
public abstract class PlaceTypeListAdapter implements ListDialogInterface {
    /**
     * List of place types
     */
    private List<PlaceType> mTypes;
    /**
     * The current choice
     */
    private int mCurrentChoice;

    /**
     * Default Constructor
     *
     * @param mCurrentType The currently selected type
     */
    public PlaceTypeListAdapter(PlaceType mCurrentType) {
        mTypes = new ArrayList<>();
        mTypes.addAll(App.getPlaceTypes());

        //Sort them
        Collections.sort(mTypes, new Comparator<PlaceType>() {
            @Override
            public int compare(PlaceType type, PlaceType type2) {
                return type.toString().compareToIgnoreCase(type2.toString());
            }
        });

        //Add the favorites option
        mTypes.add(0, new PlaceType(true));

        //Add the All option
        mTypes.add(0, new PlaceType(false));

        mCurrentChoice = mTypes.indexOf(mCurrentType);
    }

    @Override
    public int getCurrentChoice() {
        return mCurrentChoice;
    }

    @Override
    public CharSequence[] getChoices() {
        CharSequence[] titles = new CharSequence[mTypes.size()];

        //Go through the types
        for (int i = 0; i < mTypes.size(); i ++) {
            //Add its title to the list
            titles[i] = mTypes.get(i).toString();
        }

        return titles;
    }

    @Override
    public void onChoiceSelected(int position) {
       onPlaceTypeSelected(mTypes.get(position));
    }

    /**
     * Called when a faculty is selected
     *
     * @param type The {@link PlaceType} selected
     */
    public abstract void onPlaceTypeSelected(PlaceType type);
}
