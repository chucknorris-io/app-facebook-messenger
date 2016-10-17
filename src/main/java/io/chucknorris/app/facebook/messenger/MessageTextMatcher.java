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

import static java.util.regex.Pattern.CASE_INSENSITIVE;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Basic message text matcher / interpreter.
 *
 * Might reimplement this later using e.g. RiveScript.
 *
 * @author Marcel Overdijk
 */
public class MessageTextMatcher {

    public static final Pattern PATTERN_HI =
            Pattern.compile("(hi|hello)(.*)?", CASE_INSENSITIVE);

    public static final Pattern PATTERN_HOW_ARE_YOU =
            Pattern.compile("(.*)?how are you(.*)?", CASE_INSENSITIVE);

    public static final Pattern PATTERN_HELP =
            Pattern.compile("(.*)?help(.*)?", CASE_INSENSITIVE);

    public static final Pattern PATTERN_RANDOM_JOKE =
            Pattern.compile("(.* )?joke(.*)?", CASE_INSENSITIVE);

    public static final Pattern PATTERN_RANDOM_JOKE_WITH_CATEGORY =
            Pattern.compile("(.* )?joke (.* )?category (?<category>.*)", CASE_INSENSITIVE);

    public static final Pattern PATTERN_SEARCH_JOKE =
            Pattern.compile("(.* )?(search|find) (.* )?joke (with|containing) (?<query>.*)",
                    CASE_INSENSITIVE);

    public static final Pattern PATTERN_CATEGORIES =
            Pattern.compile("(.* )?categories(.*)?", CASE_INSENSITIVE);

    private static final List<Pattern> PATTERNS = Arrays.asList(
            // do not change the order of the patterns
            PATTERN_CATEGORIES,
            PATTERN_SEARCH_JOKE,
            PATTERN_RANDOM_JOKE_WITH_CATEGORY,
            PATTERN_RANDOM_JOKE,
            PATTERN_HELP,
            PATTERN_HI,
            PATTERN_HOW_ARE_YOU
            );

    public Matcher match(String messageText) {
        for (Pattern pattern : PATTERNS) {
            Matcher matcher = pattern.matcher(messageText);
            if (matcher.matches()) {
                return matcher;
            }
        }
        return null;
    }
}
