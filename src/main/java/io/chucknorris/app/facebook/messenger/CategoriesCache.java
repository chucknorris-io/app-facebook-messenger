/*
 * Copyright 2015-2016 the original author or authors.
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

package io.chucknorris.app.facebook.messenger;

import static java.lang.String.format;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.collections4.ListUtils;

import io.chucknorris.client.ChuckNorrisClient;

/**
 * The Chuck Norris IO Categories Cache.
 *
 * @author Marcel Overdijk
 */
public class CategoriesCache {

    private static final Logger logger = Logger.getLogger(CategoriesCache.class.getName());

    private static final long DEFAULR_REFRESH_INTERVAL = TimeUnit.DAYS.toMillis(1);

    private ChuckNorrisClient chuckNorrisClient = new ChuckNorrisClient();

    private List<String> categories;
    private long refreshInterval;
    private long refreshTimestamp = 0;

    public CategoriesCache() {
        this(DEFAULR_REFRESH_INTERVAL);
    }

    public CategoriesCache(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public List<String> getCategories() {
        long currentTimeMillis = System.currentTimeMillis();
        if (categories == null || refreshTimestamp < currentTimeMillis) {
            if (categories == null) {
                logger.info("Categories cache not yet initialized");
            } else if (refreshTimestamp < currentTimeMillis) {
                logger.info("Categories cache expired");
            }
            synchronized (CategoriesCache.class) {
                logger.info("Retrieving categories");
                if (categories == null || refreshTimestamp < System.currentTimeMillis()) {
                    categories = chuckNorrisClient.getCategories();
                    refreshTimestamp = System.currentTimeMillis() + refreshInterval;
                }
            }
        } else {
            logger.fine(format("Categories cache not expired yet (%d millis until refresh)", (refreshTimestamp - currentTimeMillis)));
        }
        return categories;
    }

    public List<List<String>> getCategoriesPaged(int pageSize) {
        return ListUtils.partition(getCategories(), pageSize);
    }

    public boolean containsIgnoreCase(String category) {
        for (String cat : getCategories()) {
            if (cat.equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    public String getCategory(String category) {
        for (String cat : getCategories()) {
            if (cat.equalsIgnoreCase(category)) {
                return cat;
            }
        }
        return null;
    }
}
