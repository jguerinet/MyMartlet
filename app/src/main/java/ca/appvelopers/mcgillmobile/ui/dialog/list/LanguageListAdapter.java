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

package ca.appvelopers.mcgillmobile.ui.dialog.list;

import android.content.Context;
import android.util.Pair;

import com.guerinet.utils.dialog.ListDialogInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.LanguagePreference;

/**
 * Displays the list of languages the app is available in
 * @author Julien Guerinet
 * @since 2.0.0
 */
public abstract class LanguageListAdapter implements ListDialogInterface {
    /**
     * {@link LanguagePreference} instance
     */
    @Inject
    LanguagePreference languagePref;
    /**
     * List of languages and their String equivalents
     */
    private final List<Pair<String, String>> languages;

    /**
     * Default Constructor
     *
     * @param context App context
     */
    protected LanguageListAdapter(Context context) {
        App.component(context).inject(this);
        languages = new ArrayList<>();
        addLanguage(LanguagePreference.ENGLISH);
        addLanguage(LanguagePreference.FRENCH);

        // Sort them alphabetically
        Collections.sort(languages, (o1, o2) -> o1.second.compareToIgnoreCase(o2.second));
    }

    @Override
    public int getCurrentChoice() {
        for (int i = 0; i < languages.size(); i ++) {
            if (languages.get(i).first.equals(languagePref.get())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public CharSequence[] getChoices() {
        CharSequence[] titles = new CharSequence[languages.size()];
        for (int i = 0; i < languages.size(); i ++) {
            titles[i] = languages.get(i).second;
        }
        return titles;
    }

    @Override
    public void onChoiceSelected(int position) {
        onLanguageSelected(languages.get(position).first);
    }

    /**
     * @param language Language to add to the list
     */
    private void addLanguage(String language) {
        languages.add(new Pair<>(language, languagePref.getString(language)));
    }

    /**
     * Called when a language has been selected
     *
     * @param language Selected language
     */
    public abstract void onLanguageSelected(String language);
}
