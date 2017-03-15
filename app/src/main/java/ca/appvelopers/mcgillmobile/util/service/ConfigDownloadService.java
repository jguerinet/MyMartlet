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

package ca.appvelopers.mcgillmobile.util.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.guerinet.utils.DateUtils;
import com.guerinet.utils.Utils;
import com.guerinet.utils.prefs.DatePreference;
import com.guerinet.utils.prefs.IntPreference;

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
import ca.appvelopers.mcgillmobile.util.dbflow.DBUtils;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.PlaceCategoriesDB;
import ca.appvelopers.mcgillmobile.util.dbflow.databases.PlacesDB;
import ca.appvelopers.mcgillmobile.util.retrofit.ConfigService;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Downloads the latest version of the config info from the server
 * @author Julien Guerinet
 * @since 2.4.0
 */
public class ConfigDownloadService extends IntentService {
    /**
     * Retrofit {@link ConfigService} instance
     */
    @Inject
    ConfigService configService;
    /**
     * The Config If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_CONFIG)
    DatePreference imsConfigPref;
    /**
     * The Places If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_PLACES)
    DatePreference imsPlacesPref;
    /**
     * The Categories If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_CATEGORIES)
    DatePreference imsCategoriesPref;
    /**
     * The Registration Semesters If-Modified-Since {@link DatePreference}
     */
    @Inject
    @Named(PrefsModule.IMS_REGISTRATION)
    DatePreference imsRegistrationPref;
    /**
     * The min version {@link IntPreference}
     */
    @Inject
    @Named(PrefsModule.MIN_VERSION)
    IntPreference minVersionPref;
    /**
     * {@link RegisterTermPreference} instance
     */
    @Inject
    RegisterTermPreference registerTermPref;

    public ConfigDownloadService() {
        super("ConfigDownloadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.component(getApplicationContext()).inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!Utils.isConnected(getApplicationContext())) {
            // If we're not connected to the internet, don't continue
            return;
        }

        // Config
        Config config = executeRequest(configService.config(getIMS(imsConfigPref)), imsConfigPref);
        if (config != null) {
            minVersionPref.set(config.androidMinVersion);
        }

        // Places
        List<Place> places = executeRequest(configService.places(getIMS(imsPlacesPref)),
                imsPlacesPref);
        if (places != null) {
            DBUtils.updateDB(Place.class, places, null, PlacesDB.class, (object, oldObject) -> {
                // Set whether the place was a favorite or not
                //  This will automatically save the new place
                object.setFavorite(oldObject.isFavorite());
            });
        }

        // Categories
        List<Category> categories = executeRequest(configService.categories(
                getIMS(imsCategoriesPref)), imsCategoriesPref);
        if (categories != null) {
            DBUtils.updateDB(Category.class, categories, null, PlaceCategoriesDB.class, null);
        }

        // Registration Terms
        List<Term> registerTerms = executeRequest(configService.registrationTerms(
                getIMS(imsRegistrationPref)), imsRegistrationPref);
        if (registerTerms != null) {
            registerTermPref.setTerms(registerTerms);
        }
    }

    /**
     * @param pref {@link DatePreference} instance to get the date from
     * @return IMS String to use for the call
     */
    private String getIMS(DatePreference pref) {
        return DateUtils.getRFC1123String(pref.getDate());
    }

    /**
     * Executes a given call
     *
     * @param call    Call to execute
     * @param imsPref IMS {@link DatePreference} to update after a successful call
     * @param <T>     Class of the response object
     * @return Response object, null if there was an error
     */
    private <T> T executeRequest(Call<T> call, DatePreference imsPref) {
        try {
            Response<T> response = call.execute();

            if (response.isSuccessful()) {
                imsPref.set(ZonedDateTime.now());
            }
            return response.body();
        } catch (Exception e) {
            if (!(e instanceof UnknownHostException)) {
                // Don't report UnknownHosts since we know the host is verified
                Timber.e(e, "Error downloading config section");
            }
            return null;
        }
    }

    /**
     * Config skeleton class
     */
    public static class Config {
        /**
         * Minimum version of the app that the user needs
         */
        int androidMinVersion = -1;
    }
}
