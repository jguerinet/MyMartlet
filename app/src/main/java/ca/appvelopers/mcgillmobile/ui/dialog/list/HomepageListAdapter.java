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
import ca.appvelopers.mcgillmobile.model.Homepage;

/**
 * @author Julien Guerinet
 * @since 2.0.0
 */
@SuppressWarnings("ResourceType")
public abstract class HomepageListAdapter implements ListDialogInterface {
    /**
     * The list of homepages
     */
    private List<Integer> mHomepages;

    /**
     * Default Constructor
     */
    public HomepageListAdapter() {
        //Set up the list of homepages
        mHomepages = new ArrayList<>();
        mHomepages.add(Homepage.SCHEDULE);
        mHomepages.add(Homepage.TRANSCRIPT);
        mHomepages.add(Homepage.MY_COURSES);
        mHomepages.add(Homepage.COURSES);
        mHomepages.add(Homepage.WISHLIST);
        mHomepages.add(Homepage.SEARCH_COURSES);
        mHomepages.add(Homepage.EBILL);
        mHomepages.add(Homepage.MAP);
        mHomepages.add(Homepage.DESKTOP);
        mHomepages.add(Homepage.SETTINGS);

        //Sort them alphabetically
        Collections.sort(mHomepages, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return Homepage.getString(lhs).compareToIgnoreCase(Homepage.getString(rhs));
            }
        });
    }

    @Override
    public int getCurrentChoice() {
        return mHomepages.indexOf(App.getHomepage());
    }

    @Override
    public CharSequence[] getChoices() {
        CharSequence[] titles = new CharSequence[mHomepages.size()];
        for (int i = 0; i < mHomepages.size(); i ++) {
            titles[i] = Homepage.getString(mHomepages.get(i));
        }

        return titles;
    }

    @Override
    public void onChoiceSelected(int position) {
        onHomepageSelected(mHomepages.get(position));
    }

    /**
     * Called when a homepage has been selected
     *
     * @param homepage The selected homepage
     */
    public abstract void onHomepageSelected(@Homepage.Type int homepage);
}
