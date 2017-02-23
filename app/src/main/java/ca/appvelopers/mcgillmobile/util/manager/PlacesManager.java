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

package ca.appvelopers.mcgillmobile.util.manager;

import android.content.Context;

import com.guerinet.utils.StorageUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.model.place.Category;
import ca.appvelopers.mcgillmobile.model.place.Place;

/**
 * Entirely manages the {@link Place}s and {@link Category}s lifecycles
 * @author Julien Guerinet
 * @since 2.2.0
 */
@SuppressWarnings("unchecked")
@Singleton
public class PlacesManager {
    /**
     * {@link Context} instance
     */
    private final Context context;

    /**
     * Default Injectable Constructor
     *
     * @param context App Context
     */
    @Inject
    protected PlacesManager(Context context) {
        this.context = context;
    }

    /* GETTERS */

}
