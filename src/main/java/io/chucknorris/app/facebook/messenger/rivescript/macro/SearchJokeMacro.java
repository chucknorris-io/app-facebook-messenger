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
import io.chucknorris.client.ChuckNorrisClient;
import io.chucknorris.client.Joke;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * RiveScript {@link Subroutine} for searching a joke.
 *
 * @author Marcel Overdijk
 */
public class SearchJokeMacro implements Subroutine {

    private static final Logger logger = Logger.getLogger(SearchJokeMacro.class.getName());

    private final ChuckNorrisClient chuckNorrisClient;

    public SearchJokeMacro(final ChuckNorrisClient chuckNorrisClient) {
        this.chuckNorrisClient = requireNonNull(chuckNorrisClient, "'chuckNorrisClient' must not be null");
    }

    @Override
    public String call(RiveScript rivescript, String[] args) {
        String query = args[0];
        logger.info("Searching jokes with query: " + query);
        List<Joke> jokes = chuckNorrisClient.searchJokes(query);
        if (jokes != null && jokes.size() > 0) {
            int i = new Random().nextInt(jokes.size());
            return jokes.get(i).getValue();
        } else {
            return format("Your search for '%s' did not match any joke. Make sure that all words are spelled correctly. Try different keywords. Try more general keywords.", query);
        }
    }
}
