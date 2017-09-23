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
import android.support.annotation.NonNull;
import android.util.Pair;

import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.util.manager.HomepageManager;
import com.guerinet.mymartlet.util.manager.HomepageManager.Homepage;
import com.guerinet.suitcase.dialog.SingleListInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Displays the available homepages
 * @author Julien Guerinet
 * @since 2.0.0
 */
@SuppressWarnings("ResourceType")
public abstract class HomepagesAdapter implements SingleListInterface {
    /**
     * {@link HomepageManager} instance
     */
    @Inject
    HomepageManager homepageManager;

    /**
     * The list of homepages
     */
    private final List<Pair<Integer, String>> homepages;

    /**
     * Default Constructor
     *
     * @param context App context
     */
    protected HomepagesAdapter(Context context) {
        App.Companion.component(context).inject(this);
        homepages = new ArrayList<>();
        addHomepage(HomepageManager.SCHEDULE);
        addHomepage(HomepageManager.TRANSCRIPT);
        addHomepage(HomepageManager.MY_COURSES);
        addHomepage(HomepageManager.COURSES);
        addHomepage(HomepageManager.WISHLIST);
        addHomepage(HomepageManager.SEARCH_COURSES);
        addHomepage(HomepageManager.EBILL);
        addHomepage(HomepageManager.MAP);
        addHomepage(HomepageManager.DESKTOP);
        addHomepage(HomepageManager.SETTINGS);

        // Sort them alphabetically
        Collections.sort(homepages, (lhs, rhs) -> lhs.second.compareToIgnoreCase(rhs.second));
    }

    @Override
    public int getCurrentChoice() {
        for (int i = 0; i < homepages.size(); i ++) {
            if (homepages.get(i).first == homepageManager.get()) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public String[] getChoices() {
        String[] titles = new String[homepages.size()];
        for (int i = 0; i < homepages.size(); i ++) {
            titles[i] = homepages.get(i).second;
        }
        return titles;
    }

    @Override
    public void onChoiceSelected(int position) {
        onHomepageSelected(homepages.get(position).first);
    }

    /**
     * @param homepage {@link Homepage} to add
     */
    private void addHomepage(@Homepage int homepage) {
        homepages.add(new Pair<>(homepage, homepageManager.getString(homepage)));
    }

    /**
     * Called when a homepage has been selected
     *
     * @param homepage The selected {@link Homepage}
     */
    public abstract void onHomepageSelected(@Homepage int homepage);
}
