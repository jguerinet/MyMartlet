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
import android.support.v4.util.Pair;

import com.guerinet.mymartlet.App;
import com.guerinet.mymartlet.model.place.Category;
import com.guerinet.suitcase.dialog.SingleListInterface;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Displays a list of place types to choose from in the maps section
 * @author Julien Guerinet
 * @since 2.1.0
 */
public abstract class CategoryListAdapter implements SingleListInterface {
    /**
     * List of place types with their associated String
     */
    private final List<Pair<Category, String>> categories;
    /**
     * The current choice
     */
    private int currentChoice;

    /**
     * Default Constructor
     *
     * @param context         App context
     * @param currentCategory Currently selected category
     */
    protected CategoryListAdapter(Context context, Category currentCategory) {
        App.component(context).inject(this);
        categories = new ArrayList<>();

        // Get the categories synchronously from the DB
        List<Category> categories = SQLite.select()
                .from(Category.class)
                .queryList();

        // Add them all to the main list
        for (Category category : categories) {
            this.categories.add(new Pair<>(category, category.getString(context)));
        }

        // Sort them
        Collections.sort(this.categories, (lhs, rhs) -> lhs.second.compareToIgnoreCase(rhs.second));

        // Add the favorites option
        Category type = new Category(true);
        this.categories.add(0, new Pair<>(type, type.getString(context)));

        // Add the All option
        type = new Category(false);
        this.categories.add(0, new Pair<>(type, type.getString(context)));

        // Find the index of the current choice
        for (int i = 0; i < this.categories.size(); i ++) {
            if (this.categories.get(i).first.equals(currentCategory)) {
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
    public String[] getChoices() {
        String[] titles = new String[categories.size()];

        // Go through the categories
        for (int i = 0; i < categories.size(); i ++) {
            // Add its title to the list
            titles[i] = categories.get(i).second;
        }

        return titles;
    }

    @Override
    public void onChoiceSelected(int position) {
       onCategorySelected(categories.get(position).first);
    }

    /**
     * Called when a category is selected
     *
     * @param type The {@link Category} selected
     */
    public abstract void onCategorySelected(Category type);
}
