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

import static io.chucknorris.app.facebook.messenger.MessageTextMatcher.PATTERN_CATEGORIES;
import static io.chucknorris.app.facebook.messenger.MessageTextMatcher.PATTERN_HELP;
import static io.chucknorris.app.facebook.messenger.MessageTextMatcher.PATTERN_RANDOM_JOKE;
import static io.chucknorris.app.facebook.messenger.MessageTextMatcher.PATTERN_RANDOM_JOKE_WITH_CATEGORY;
import static io.chucknorris.app.facebook.messenger.MessageTextMatcher.PATTERN_SEARCH_JOKE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link MessageTextMatcher}.
 *
 * @author Marcel Overdijk
 */
public class MessageTextMatcherTests {

    private MessageTextMatcher matcher;

    @Before
    public void setUp() {
        matcher = new MessageTextMatcher();
    }

    @Test
    public void testPatternHelp() {
        assertThat(matcher.match("help").pattern(), is(sameInstance(PATTERN_HELP)));
        assertThat(matcher.match("hi, I need help").pattern(), is(sameInstance(PATTERN_HELP)));
        assertThat(matcher.match("hi, I need help!").pattern(), is(sameInstance(PATTERN_HELP)));
    }

    @Test
    public void testPatternRandomJoke() {
        assertThat(matcher.match("joke").pattern(), is(sameInstance(PATTERN_RANDOM_JOKE)));
        assertThat(matcher.match("random joke").pattern(), is(sameInstance(PATTERN_RANDOM_JOKE)));
        assertThat(matcher.match("tell joke").pattern(), is(sameInstance(PATTERN_RANDOM_JOKE)));
        assertThat(matcher.match("tell me a joke").pattern(),
                is(sameInstance(PATTERN_RANDOM_JOKE)));
        assertThat(matcher.match("tell me a joke!").pattern(),
                is(sameInstance(PATTERN_RANDOM_JOKE)));
        assertThat(matcher.match("tell me a joke bot").pattern(),
                is(sameInstance(PATTERN_RANDOM_JOKE)));
        assertThat(matcher.match("hi bot, tell me a joke").pattern(),
                is(sameInstance(PATTERN_RANDOM_JOKE)));
    }

    @Test
    public void testPatternRandomJokeWithCateogry() {
        assertThat(matcher.match("joke category food").pattern(),
                is(sameInstance(PATTERN_RANDOM_JOKE_WITH_CATEGORY)));
        assertThat(matcher.match("joke category food").group("category"),
                is(equalTo("food")));
        assertThat(matcher.match("joke with category fashion").pattern(),
                is(sameInstance(PATTERN_RANDOM_JOKE_WITH_CATEGORY)));
        assertThat(matcher.match("joke with category fashion").group("category"),
                is(equalTo("fashion")));
        assertThat(matcher.match("hi, tell me a joke with category religion").pattern(),
                is(sameInstance(PATTERN_RANDOM_JOKE_WITH_CATEGORY)));
        assertThat(matcher.match("hi, tell me a joke with category religion").group("category"),
                is(equalTo("religion")));
    }

    @Test
    public void testPatternSearchJoke() {
        assertThat(matcher.match("search joke containing food").pattern(),
                is(sameInstance(PATTERN_SEARCH_JOKE)));
        assertThat(matcher.match("search joke containing food").group("query"),
                is(equalTo("food")));
        assertThat(matcher.match("search joke with fashion").pattern(),
                is(sameInstance(PATTERN_SEARCH_JOKE)));
        assertThat(matcher.match("search joke with fashion").group("query"),
                is(equalTo("fashion")));
        assertThat(matcher.match("hi, find me a joke with religion").pattern(),
                is(sameInstance(PATTERN_SEARCH_JOKE)));
        assertThat(matcher.match("hi, find me a joke with religion").group("query"),
                is(equalTo("religion")));
    }

    @Test
    public void testPatternCategories() {
        assertThat(matcher.match("categories").pattern(), is(sameInstance(PATTERN_CATEGORIES)));
        assertThat(matcher.match("list categories").pattern(),
                is(sameInstance(PATTERN_CATEGORIES)));
        assertThat(matcher.match("hi, show me the categories!").pattern(),
                is(sameInstance(PATTERN_CATEGORIES)));
    }
}
