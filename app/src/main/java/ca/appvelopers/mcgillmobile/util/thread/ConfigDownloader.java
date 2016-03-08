/*
 * Copyright 2014-2016 Julien Guerinet
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

import org.threeten.bp.ZonedDateTime;

import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.model.Place;
import ca.appvelopers.mcgillmobile.model.PlaceType;
import ca.appvelopers.mcgillmobile.model.Term;
import ca.appvelopers.mcgillmobile.model.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.model.retrofit.ConfigService;
import ca.appvelopers.mcgillmobile.util.manager.PlacesManager;
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
    protected ConnectivityManager connectivityManager;
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
     * {@link PlacesManager} instance
     */
    @Inject
    protected PlacesManager placesManager;

    /**
     * Default Constructor
     */
    public ConfigDownloader(Context context) {
        App.component(context).inject(this);
    }

    @Override
    public void run() {
        //If we're not connected to the internet, don't continue
        if (!Utils.isConnected(connectivityManager)) {
            return;
        }

        //Config
        try {
            Response<Config> response = configService
                    .config(DateUtils.getRFC1123String(imsConfigPref.getDate()))
                    .execute();

            if (response.isSuccess()) {
                minVersionPref.set(response.body().androidMinVersion);
                imsConfigPref.set(ZonedDateTime.now());
            }
        } catch (Exception e) {
            if (!(e instanceof UnknownHostException)) {
                //Don't report UnknownHosts since we know the host is verified
                Timber.e(e, "Error downloading config");
            }
        }

        //Places
        try {
            Response<List<Place>> response = configService
                    .places(DateUtils.getRFC1123String(imsPlacesPref.getDate()))
                    .execute();

            if (response.isSuccess()) {
                placesManager.setPlaces(response.body());
                imsPlacesPref.set(ZonedDateTime.now());
            }
        } catch (Exception e) {
            if (!(e instanceof UnknownHostException)) {
                //Don't report UnknownHosts since we know the host is verified
                Timber.e(e, "Error downloading places");
            }
        }

        //Place Categories
        try {
            Response<List<PlaceType>> response = configService
                    .categories(DateUtils.getRFC1123String(imsCategoriesPref.getDate()))
                    .execute();

            if (response.isSuccess()) {
                placesManager.setPlaceTypes(response.body());
                imsCategoriesPref.set(ZonedDateTime.now());
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

            if (response.isSuccess()) {
                App.setRegisterTerms(response.body());
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
     * Config skeleton class
     */
    public static class Config {
        /**
         * Minimum version of the app that the user needs
         */
        protected int androidMinVersion = -1;
    }
}

