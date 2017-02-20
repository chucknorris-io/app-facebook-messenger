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

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.rivescript.Config;
import com.rivescript.RiveScript;
import com.rivescript.session.NoOpSessionManager;
import io.chucknorris.app.facebook.messenger.cache.CategoriesCache;
import io.chucknorris.app.facebook.messenger.rivescript.macro.CategoriesMacro;
import io.chucknorris.app.facebook.messenger.rivescript.macro.RandomJokeMacro;
import io.chucknorris.app.facebook.messenger.rivescript.macro.SearchJokeMacro;
import io.chucknorris.client.ChuckNorrisClient;

import java.io.File;

/**
 * The {@link RiveScript} instance provider.
 *
 * @author Marcel Overdijk
 */
public class RiveScriptProvider implements Provider<RiveScript> {

    private ChuckNorrisClient chuckNorrisClient;
    private CategoriesCache categoriesCache;

    @Inject
    public RiveScriptProvider(ChuckNorrisClient chuckNorrisClient, CategoriesCache categoriesCache) {
        this.chuckNorrisClient = chuckNorrisClient;
        this.categoriesCache = categoriesCache;
    }

    @Override
    public RiveScript get() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("rivescript/chuck-norris.rive").getFile());
        RiveScript bot = new RiveScript(Config.Builder
                .utf8()
                .forceCase(true)
                .sessionManager(new NoOpSessionManager())
                .build());
        bot.setSubroutine("categories", new CategoriesMacro(categoriesCache, 6));
        bot.setSubroutine("randomjoke", new RandomJokeMacro(chuckNorrisClient, categoriesCache));
        bot.setSubroutine("searchjoke", new SearchJokeMacro(chuckNorrisClient));
        bot.loadFile(file);
        bot.sortReplies();
        return bot;
    }
}
