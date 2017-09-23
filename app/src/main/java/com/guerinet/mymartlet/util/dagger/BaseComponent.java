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

package com.guerinet.mymartlet.util.dagger;

import com.guerinet.mymartlet.ui.BaseActivity;
import com.guerinet.mymartlet.ui.DrawerActivity;
import com.guerinet.mymartlet.ui.MapActivity;
import com.guerinet.mymartlet.ui.ScheduleActivity;
import com.guerinet.mymartlet.ui.SplashActivity;
import com.guerinet.mymartlet.ui.courses.CoursesActivity;
import com.guerinet.mymartlet.ui.dialog.list.CategoryListAdapter;
import com.guerinet.mymartlet.ui.dialog.list.HomepagesAdapter;
import com.guerinet.mymartlet.ui.dialog.list.TermDialogHelper;
import com.guerinet.mymartlet.ui.search.SearchActivity;
import com.guerinet.mymartlet.ui.settings.AgreementActivity;
import com.guerinet.mymartlet.ui.settings.SettingsActivity;
import com.guerinet.mymartlet.ui.transcript.TranscriptActivity;
import com.guerinet.mymartlet.ui.walkthrough.WalkthroughAdapter;
import com.guerinet.mymartlet.ui.web.DesktopActivity;
import com.guerinet.mymartlet.ui.web.MyCoursesActivity;
import com.guerinet.mymartlet.ui.wishlist.WishlistActivity;
import com.guerinet.mymartlet.ui.wishlist.WishlistHelper;
import com.guerinet.mymartlet.util.background.BootReceiver;
import com.guerinet.mymartlet.util.dagger.prefs.PrefsModule;
import com.guerinet.mymartlet.util.manager.McGillManager;
import com.guerinet.mymartlet.util.service.ConfigDownloadService;
import com.guerinet.mymartlet.util.thread.UserDownloader;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger Base Component
 * @author Julien Guerinet
 * @since 2.0.4
 */
@Singleton @Component(modules = {AppModule.class, NetworkModule.class, PrefsModule.class})
public interface BaseComponent {
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
    void inject(SearchActivity activity);

    void inject(McGillManager mcGillManager);
    void inject(BootReceiver receiver);

    void inject(WalkthroughAdapter adapter);
    void inject(HomepagesAdapter adapter);
    void inject(CategoryListAdapter adapter);
    void inject(TermDialogHelper helper);

    void inject(ConfigDownloadService service);

    void inject(UserDownloader downloader);

    void inject(WishlistHelper helper);
}
