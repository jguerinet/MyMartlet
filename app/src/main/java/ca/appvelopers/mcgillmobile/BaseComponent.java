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

package ca.appvelopers.mcgillmobile;

import javax.inject.Singleton;

import ca.appvelopers.mcgillmobile.model.PrefsModule;
import ca.appvelopers.mcgillmobile.ui.DrawerActivity;
import ca.appvelopers.mcgillmobile.ui.SplashActivity;
import ca.appvelopers.mcgillmobile.ui.schedule.ScheduleActivity;
import ca.appvelopers.mcgillmobile.ui.settings.SettingsActivity;
import dagger.Component;

/**
 * Dagger Base Component
 * @author Julien Guerinet
 * @since 2.0.4
 */
@Singleton @Component(modules = {AppModule.class, PrefsModule.class})
public interface BaseComponent {
    void inject(App app);
    void inject(DrawerActivity activity);
    void inject(SplashActivity activity);
    void inject(ScheduleActivity activity);
    void inject(SettingsActivity activity);
}
