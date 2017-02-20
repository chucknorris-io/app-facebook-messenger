/*
 * Copyright 2015-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.chucknorris.app.facebook.messenger.inject;

import com.github.marsbits.restfbmessenger.DefaultMessenger;
import com.github.marsbits.restfbmessenger.Messenger;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.rivescript.RiveScript;
import io.chucknorris.app.facebook.messenger.cache.CategoriesCache;
import io.chucknorris.app.facebook.messenger.callback.ChuckNorrisCallbackHandler;
import io.chucknorris.client.ChuckNorrisClient;

/**
 * The application's main {@link Module}.
 *
 * @author Marcel Overdijk
 */
public class AppModule extends AbstractModule {

    @Override
    protected void configure() {

        // Chuck Norris Client
        bind(ChuckNorrisClient.class)
                .in(Scopes.SINGLETON);

        // RiveScript
        bind(RiveScript.class)
                .toProvider(RiveScriptProvider.class);
    }

    @Provides
    @Singleton
    protected CategoriesCache provideCategoriesCache(ChuckNorrisClient chuckNorrisClient) {
        return new CategoriesCache(chuckNorrisClient);
    }

    @Provides
    @Singleton
    protected ChuckNorrisCallbackHandler provideChuckNorrisCallbackHandler(ChuckNorrisClient chuckNorrisClient, RiveScript rivescript) {
        return new ChuckNorrisCallbackHandler(chuckNorrisClient, rivescript);
    }

    @Provides
    @Singleton
    protected Messenger provideMessenger(ChuckNorrisCallbackHandler callbackHandler) {
        String verifyToken = System.getProperty("facebook.verify_token");
        String accessToken = System.getProperty("facebook.access_token");
        String appSecret = System.getProperty("facebook.app_secret");
        return new DefaultMessenger(verifyToken, accessToken, appSecret, callbackHandler);
    }
}
