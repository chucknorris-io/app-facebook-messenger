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

package integration;

import com.restfb.types.send.ButtonTemplatePayload;
import com.restfb.types.send.PostbackButton;
import com.restfb.types.send.QuickReply;
import io.chucknorris.app.facebook.messenger.callback.ChuckNorrisCallbackHandler;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;

/**
 * Integration tests for {@link ChuckNorrisCallbackHandler}.
 *
 * @author Marcel Overdijk
 */
public class PostbackIntegrationTests extends AbstractChuckNorrisCallbackHandlerIntegrationTests {

    @Test
    public void testGetStartedPostback() {
        onPostback("GET_STARTED");
        List<QuickReply> quickReplies = Arrays.asList(
                new QuickReply("Random Joke", "RANDOM_JOKE"),
                new QuickReply("Categories", "CATEGORIES")
        );
        verifyQuickReplies("Hi, what would you like to hear?", is(quickReplies));
    }

    @Test
    public void testHelpPostback() {
        onPostback("HELP");
        ButtonTemplatePayload buttonTemplate = new ButtonTemplatePayload(
                "Hi there. I can tell you random Chuck Norris jokes. Ask me things like the following:" +
                        "\n" +
                        "\n  - Tell me a joke" +
                        "\n  - Tell me a joke with category movie" +
                        "\n  - Search joke containing steroids" +
                        "\n  - List available categories" +
                        "\n" +
                        "\nOr choose a command below.");
        buttonTemplate.addButton(new PostbackButton("Random Joke", "RANDOM_JOKE"));
        buttonTemplate.addButton(new PostbackButton("Categories", "CATEGORIES"));
        verifyButtonTemplate(is(buttonTemplate));
    }

    @Test
    public void testCategoriesPostback() {
        onPostback("CATEGORIES");
        List<QuickReply> quickReplies = Arrays.asList(
                new QuickReply("category1", "RANDOM_JOKE_WITH_CATEGORY_category1"),
                new QuickReply("category2", "RANDOM_JOKE_WITH_CATEGORY_category2"),
                new QuickReply("category3", "RANDOM_JOKE_WITH_CATEGORY_category3"),
                new QuickReply("category4", "RANDOM_JOKE_WITH_CATEGORY_category4"),
                new QuickReply("category5", "RANDOM_JOKE_WITH_CATEGORY_category5"),
                new QuickReply("category6", "RANDOM_JOKE_WITH_CATEGORY_category6"),
                new QuickReply("More...", "CATEGORIES_MORE_2")

        );
        verifyQuickReplies("Choose a category:", is(quickReplies));
    }

    @Test
    public void testCategoriesMorePostback() {
        onPostback("CATEGORIES_MORE_2");
        List<QuickReply> quickReplies = Arrays.asList(
                new QuickReply("category7", "RANDOM_JOKE_WITH_CATEGORY_category7"),
                new QuickReply("category8", "RANDOM_JOKE_WITH_CATEGORY_category8"),
                new QuickReply("category9", "RANDOM_JOKE_WITH_CATEGORY_category9"),
                new QuickReply("category10", "RANDOM_JOKE_WITH_CATEGORY_category10"),
                new QuickReply("category11", "RANDOM_JOKE_WITH_CATEGORY_category11"),
                new QuickReply("category12", "RANDOM_JOKE_WITH_CATEGORY_category12"),
                new QuickReply("More...", "CATEGORIES_MORE_3")

        );
        verifyQuickReplies("Choose a category:", is(quickReplies));
    }

    @Test
    public void testRandomJokePostback() {
        onPostback("RANDOM_JOKE");
        verifyTextMessage(is("Chuck Norris once accidentally broke steel by touching it."));
    }

    @Test
    public void testRandomJokeWithCategoryPostback() {
        onPostback("RANDOM_JOKE_WITH_CATEGORY_category1");
        verifyTextMessage(is("When Chuck Norris wants an egg, he cracks open a chicken."));
    }
}
