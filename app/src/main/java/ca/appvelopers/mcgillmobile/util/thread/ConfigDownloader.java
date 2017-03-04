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

package ca.appvelopers.mcgillmobile.util.thread;

import android.content.Context;
import android.net.ConnectivityManager;

import com.guerinet.utils.DateUtils;
import com.guerinet.utils.Utils;
import com.guerinet.utils.prefs.DatePreference;
import com.guerinet.utils.prefs.IntPreference;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.FastStoreModelTransaction;

import org.threeten.bp.ZonedDateTime;

import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.place.Category;
import ca.appvelopers.mcgillmobile.model.place.Place;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.RegisterTermPreference;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.PlaceCategoriesDB;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.PlacesDB;
import ca.appvelopers.mcgillmobile.util.retrofit.ConfigService;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Downloads the config variables and the list of places from the web server
 * @author Julien Guerinet
 * @since 1.0.0
 */
public class ConfigDownloader extends Thread {
    /**
     * {@link ConnectivityManager} instance
     */
    @Inject
    protected Context context;
    /**
     * Retrofit {@link ConfigService} instance
     */
    @Inject
    protected ConfigService configService;
    /**
     * The Config If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_CONFIG)
    protected DatePreference imsConfigPref;
    /**
     * The Places If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_PLACES)
    protected DatePreference imsPlacesPref;
    /**
     * The Categories If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_CATEGORIES)
    protected DatePreference imsCategoriesPref;
    /**
     * The Registration Semesters If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_REGISTRATION)
    protected DatePreference imsRegistrationPref;
    /**
     * The min version {@link IntPreference}
     */
    @Inject
    @Named(PrefsModule.MIN_VERSION)
    protected IntPreference minVersionPref;
    /**
     * {@link RegisterTermPreference} instance
     */
    RegisterTermPreference registerTermPref;

    /**
     * Default Constructor
     */
    public ConfigDownloader(Context context) {
        App.component(context).inject(this);
    }

    @Override
    public void run() {
        //If we're not connected to the internet, don't continue
        if (!Utils.isConnected(context)) {
            return;
        }

        //Config
        try {
            Response<Config> response = configService
                    .config(DateUtils.getRFC1123String(imsConfigPref.getDate()))
                    .execute();

            if (response.isSuccessful()) {
                minVersionPref.set(response.body().androidMinVersion);
                imsConfigPref.set(ZonedDateTime.now());
            }
        } catch (Exception e) {
            if (!(e instanceof UnknownHostException)) {
                // Don't report UnknownHosts since we know the host is verified
                Timber.e(e, "Error downloading config");
            }
        }

        // Places
        try {
            Response<List<Place>> response = configService
                    .places(DateUtils.getRFC1123String(imsPlacesPref.getDate()))
                    .execute();

            if (response.isSuccessful()) {
                updateDB(Place.class, response.body(), PlacesDB.class, imsPlacesPref,
                        (object, oldObject) -> {
                            // Set whether the place was a favorite or not
                            //  This will automatically save the new place
                            object.setFavorite(oldObject.isFavorite());
                        });
            }
        } catch (Exception e) {
            if (!(e instanceof UnknownHostException)) {
                // Don't report UnknownHosts since we know the host is verified
                Timber.e(new Exception("Error downloading places", e));
            }
        }

        // Categories
        try {
            Response<List<Category>> response = configService
                    .categories(DateUtils.getRFC1123String(imsCategoriesPref.getDate()))
                    .execute();

            if (response.isSuccessful()) {
                updateDB(Category.class, response.body(), PlaceCategoriesDB.class,
                        imsCategoriesPref, null);
            }
        } catch (Exception e) {
            if (!(e instanceof UnknownHostException)) {
                //Don't report UnknownHosts since we know the host is verified
                Timber.e(e, "Error downloading place categories");
            }
        }

        //Registration Semesters
        try {
            Response<List<Term>> response = configService
                    .registrationTerms(DateUtils.getRFC1123String(imsRegistrationPref.getDate()))
                    .execute();

            if (response.isSuccessful()) {
                registerTermPref.setTerms(response.body());
                imsRegistrationPref.set(ZonedDateTime.now());
            }
        } catch (Exception e) {
            if (!(e instanceof UnknownHostException)) {
                //Don't report UnknownHosts since we know the host is verified
                Timber.e(e, "Error downloading registration terms");
            }
        }
    }

    /**
     * Updates the objects in a DB by updating existing objects, removing old objects, and inserting
     *  new ones. Will also update the IMS pref
     *
     * @param type       Object type
     * @param newObjects List of new objects/objects to update
     * @param dbClass    Class of the DB these will be stored in
     * @param imsPref    IMS pref to update
     * @param callback   Optional callback to run any update code. If not, save() will be called
     * @param <T>        Object Type
     */
    private <T extends BaseModel> void updateDB(Class<T> type, List<T> newObjects, Class dbClass,
            DatePreference imsPref, UpdateCallback<T> callback) {
        SQLite
                .select()
                .from(type)
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    if (tResult == null) {
                        return;
                    }

                    // Go through the existing objects
                    for (T oldObject : tResult) {
                        // Check if the object still exists in the received objects
                        int index = newObjects.indexOf(oldObject);
                        if (index != -1) {
                            // Update it
                            T newObject = newObjects.get(index);

                            // If there's a callback, use it
                            if (callback != null) {
                                callback.update(newObject, oldObject);
                            } else {
                                // If not, call save
                                newObject.save();
                            }

                            // Delete that place from the body since we've dealt with it
                            newObjects.remove(newObject);
                        } else {
                            // Delete the old place
                            oldObject.delete();
                        }
                    }

                    // Save any new objects
                    FastStoreModelTransaction<? extends BaseModel> newObjectsTransaction =
                            FastStoreModelTransaction.saveBuilder(
                                    FlowManager.getModelAdapter(type))
                                    .addAll(newObjects)
                                    .build();
                    FlowManager.getDatabase(dbClass)
                            .beginTransactionAsync(newObjectsTransaction)
                            .build()
                            .execute();
                })
                .execute();

        imsPref.set(ZonedDateTime.now());
    }

    /**
     * Callback used to run update code whenever a DB is updated
     *
     * @param <T> Object type
     */
    interface UpdateCallback<T extends BaseModel> {
        /**
         * Called when update code needs to be run
         *
         * @param object    Object to update
         * @param oldObject Object we are updating from
         */
        void update(T object, T oldObject);
    }

    /**
     * Config skeleton class
     */
    public static class Config {
        /**
         * Minimum version of the app that the user needs
         */
        protected int androidMinVersion = -1;
    }
}

