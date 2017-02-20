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

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * RiveScript {@link Subroutine} for retrieving categories.
 *
 * @author Marcel Overdijk
 */
public class CategoriesMacro implements Subroutine {

    private final CategoriesCache categoriesCache;
    private final int categoriesPageSize;

    public CategoriesMacro(final CategoriesCache categoriesCache, final int categoriesPageSize) {
        this.categoriesCache = requireNonNull(categoriesCache, "'categoriesCache' must not be null");
        this.categoriesPageSize = categoriesPageSize;
    }

    @Override
    public String call(RiveScript rivescript, String[] args) {
        int pageNumber = 1;
        try {
            pageNumber = Integer.parseInt(args[0]);
        } catch (NumberFormatException ignore) {
        }
        List<List<String>> categoriesPaged = categoriesCache.getCategoriesPaged(categoriesPageSize);
        if (categoriesPaged.size() < pageNumber) {
            pageNumber = 1;
        }
        List<String> categoriesPage = categoriesPaged.get(pageNumber - 1);
        // Categories need to be returned as <title>=<payload> pairs. For example: Title 1=PAYLOAD_1, Title 2=PAYLOAD_2
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < categoriesPage.size(); i++) {
            String title = categoriesPage.get(i);
            String payload = "RANDOM_JOKE_WITH_CATEGORY_" + title;
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(title).append("=").append(payload);
        }
        if (pageNumber < categoriesPaged.size()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("More...").append("=").append("CATEGORIES_MORE_" + (pageNumber + 1));
        }
        return sb.toString();
    }
}
