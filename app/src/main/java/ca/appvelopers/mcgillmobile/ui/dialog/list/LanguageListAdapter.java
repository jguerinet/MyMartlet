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
import ca.appvelopers.mcgillmobile.util.manager.LanguageManager;

/**
 * Displays the list of languages the app is available in
 * @author Julien Guerinet
 * @since 2.0.0
 */
@SuppressWarnings("ResourceType")
public abstract class LanguageListAdapter implements ListDialogInterface {
    /**
     * The {@link LanguageManager} instance
     */
    @Inject
    protected LanguageManager languageManager;
    /**
     * List of languages and their String equivalents
     */
    private List<Integer> languages;

    /**
     * Default Constructor
     */
    public LanguageListAdapter(Context context) {
        App.component(context).inject(this);
        languages = new ArrayList<>();
        languages.add(LanguageManager.ENGLISH);
        languages.add(LanguageManager.FRENCH);

        //Sort them alphabetically
        Collections.sort(languages, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return languageManager.getString(lhs)
                        .compareToIgnoreCase(languageManager.getString(rhs));
            }
        });
    }

    @Override
    public int getCurrentChoice() {
        return languages.indexOf(languageManager.get());
    }

    @Override
    public CharSequence[] getChoices() {
        CharSequence[] titles = new CharSequence[languages.size()];
        for (int i = 0; i < languages.size(); i ++) {
            titles[i] = languageManager.getString(languages.get(i));
        }

        return titles;
    }

    @Override
    public void onChoiceSelected(int position) {
        onLanguageSelected(languages.get(position));
    }

    /**
     * Called when a language has been selected
     *
     * @param language The selected language
     */
    public abstract void onLanguageSelected(@LanguageManager.Language int language);
}
