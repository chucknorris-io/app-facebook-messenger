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

package io.chucknorris.app.facebook.messenger.rivescript.macro;

import com.rivescript.RiveScript;
import com.rivescript.macro.Subroutine;
import io.chucknorris.app.facebook.messenger.cache.CategoriesCache;
import io.chucknorris.client.ChuckNorrisClient;

import java.util.List;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * RiveScript {@link Subroutine} for retrieving a random joke.
 *
 * @author Marcel Overdijk
 */
public class RandomJokeMacro implements Subroutine {

    private static final Logger logger = Logger.getLogger(RandomJokeMacro.class.getName());

    private final ChuckNorrisClient chuckNorrisClient;
    private final CategoriesCache categoriesCache;

    public RandomJokeMacro(final ChuckNorrisClient chuckNorrisClient, final CategoriesCache categoriesCache) {
        this.chuckNorrisClient = requireNonNull(chuckNorrisClient, "'chuckNorrisClient' must not be null");
        this.categoriesCache = requireNonNull(categoriesCache, "'categoriesCache' must not be null");
    }

    @Override
    public String call(RiveScript rivescript, String[] args) {
        if (args.length == 1) {
            String category = args[0];
            List<String> categories = categoriesCache.getCategories();
            if (categories.contains(category)) {
                logger.info("Retrieving random joke with category: " + category);
                return chuckNorrisClient.getRandomJoke(category).getValue();
            } else {
                return format("Sorry dude, I've found no jokes for the given category ('%s'). Type 'categories' to see available categories.", category);
            }
        } else {
            logger.info("Retrieving random joke");
            return chuckNorrisClient.getRandomJoke().getValue();
        }
    }
}
