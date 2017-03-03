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

package ca.appvelopers.mcgillmobile.util.dagger;

import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.ui.BaseActivity;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.MapActivity;
import ca.appvelopers.mcgillmobile.ui.ScheduleActivity;
import ca.appvelopers.mcgillmobile.ui.SplashActivity;
import ca.appvelopers.mcgillmobile.ui.courses.CoursesActivity;
import ca.appvelopers.mcgillmobile.ui.dialog.list.CategoryListAdapter;
import ca.appvelopers.mcgillmobile.ui.dialog.list.HomepagesAdapter;
import ca.appvelopers.mcgillmobile.ui.dialog.list.LanguagesAdapter;
import ca.appvelopers.mcgillmobile.ui.dialog.list.TermDialogHelper;
import ca.appvelopers.mcgillmobile.ui.settings.AgreementActivity;
import ca.appvelopers.mcgillmobile.ui.settings.SettingsActivity;
import ca.appvelopers.mcgillmobile.ui.transcript.TranscriptActivity;
import ca.appvelopers.mcgillmobile.ui.transcript.TranscriptAdapter;
import ca.appvelopers.mcgillmobile.ui.walkthrough.WalkthroughAdapter;
import ca.appvelopers.mcgillmobile.ui.web.DesktopActivity;
import ca.appvelopers.mcgillmobile.ui.web.MyCoursesActivity;
import ca.appvelopers.mcgillmobile.ui.wishlist.WishlistActivity;
import ca.appvelopers.mcgillmobile.util.background.BootReceiver;
import ca.appvelopers.mcgillmobile.util.dagger.prefs.PrefsModule;
import ca.appvelopers.mcgillmobile.util.manager.McGillManager;
import ca.appvelopers.mcgillmobile.util.thread.ConfigDownloader;
import ca.appvelopers.mcgillmobile.util.thread.UserDownloader;
import dagger.Component;

/**
 * Dagger Base Component
 * @author Julien Guerinet
 * @since 2.0.4
 */
@Singleton @Component(modules = {AppModule.class, NetworkModule.class, PrefsModule.class})
public interface BaseComponent {
    void inject(App app);

    void inject(BaseActivity activity);
    void inject(DrawerActivity activity);
    void inject(SplashActivity activity);
    void inject(AgreementActivity activity);
    void inject(ScheduleActivity activity);
    void inject(TranscriptActivity activity);
    void inject(CoursesActivity activity);
    void inject(WishlistActivity activity);
    void inject(MapActivity activity);
    void inject(MyCoursesActivity activity);
    void inject(DesktopActivity activity);
    void inject(SettingsActivity activity);

    void inject(McGillManager mcGillManager);
    void inject(BootReceiver receiver);

    /* Adapters */
    void inject(WalkthroughAdapter adapter);
    void inject(HomepagesAdapter adapter);
    void inject(LanguagesAdapter adapter);
    void inject(CategoryListAdapter adapter);
    void inject(TermDialogHelper helper);
    void inject(TranscriptAdapter adapter);

    void inject(ConfigDownloader downloader);
    void inject(UserDownloader downloader);
}
