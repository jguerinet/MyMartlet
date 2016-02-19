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
import ca.appvelopers.mcgillmobile.util.Analytics;
import ca.appvelopers.mcgillmobile.util.manager.HomepageManager;

/**
 * @author Julien Guerinet
 * @since 2.0.0
 */
@SuppressWarnings("ResourceType")
public abstract class HomepageListAdapter implements ListDialogInterface {
    /**
     * Current {@link HomepageManager} instance
     */
    @Inject
    protected HomepageManager homepageManager;
    /**
     * {@link Analytics} instance
     */
    @Inject
    protected Analytics analytics;
    /**
     * The list of homepages
     */
    private List<Integer> homepages;

    /**
     * Default Constructor
     *
     * @param context App context
     */
    public HomepageListAdapter(Context context) {
        App.component(context).inject(this);

        //Set up the list of homepages
        homepages = new ArrayList<>();
        homepages.add(HomepageManager.SCHEDULE);
        homepages.add(HomepageManager.TRANSCRIPT);
        homepages.add(HomepageManager.MY_COURSES);
        homepages.add(HomepageManager.COURSES);
        homepages.add(HomepageManager.WISHLIST);
        homepages.add(HomepageManager.SEARCH_COURSES);
        homepages.add(HomepageManager.EBILL);
        homepages.add(HomepageManager.MAP);
        homepages.add(HomepageManager.DESKTOP);
        homepages.add(HomepageManager.SETTINGS);

        //Sort them alphabetically
        Collections.sort(homepages, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return homepageManager.getString(lhs)
                        .compareToIgnoreCase(homepageManager.getString(rhs));
            }
        });
    }

    @Override
    public int getCurrentChoice() {
        return homepages.indexOf(homepageManager.get());
    }

    @Override
    public CharSequence[] getChoices() {
        CharSequence[] titles = new CharSequence[homepages.size()];
        for (int i = 0; i < homepages.size(); i ++) {
            titles[i] = homepageManager.getString(homepages.get(i));
        }

        return titles;
    }

    @Override
    public void onChoiceSelected(int position) {
        onHomepageSelected(homepages.get(position));
    }

    /**
     * Called when a homepageManager has been selected
     *
     * @param homepage The selected homepageManager
     */
    public abstract void onHomepageSelected(@HomepageManager.Homepage int homepage);
}
