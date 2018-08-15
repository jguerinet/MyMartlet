/*
 * Copyright 2014-2018 Julien Guerinet
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

package com.guerinet.mymartlet.util.dagger

import com.guerinet.mymartlet.ui.walkthrough.WalkthroughAdapter
import com.guerinet.mymartlet.ui.wishlist.WishlistActivity
import com.guerinet.mymartlet.ui.wishlist.WishlistHelper
import com.guerinet.mymartlet.util.background.BootReceiver
import com.guerinet.mymartlet.util.prefs.PrefsModule
import com.guerinet.mymartlet.util.thread.UserDownloader
import dagger.Component
import javax.inject.Singleton

/**
 * Dagger Base Component
 * @author Julien Guerinet
 * @since 1.0.0
 */
@Singleton
@Component(modules = [AppModule::class, PrefsModule::class])
interface BaseComponent {

    fun inject(activity: WishlistActivity)

    fun inject(adapter: WalkthroughAdapter)

    fun inject(downloader: UserDownloader)

    fun inject(receiver: BootReceiver)
    fun inject(helper: WishlistHelper)
}
