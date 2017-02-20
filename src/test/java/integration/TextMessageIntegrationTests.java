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
import static org.hamcrest.collection.IsIn.isOneOf;

/**
 * Integration tests for {@link ChuckNorrisCallbackHandler}.
 *
 * @author Marcel Overdijk
 */
public class TextMessageIntegrationTests extends AbstractChuckNorrisCallbackHandlerIntegrationTests {

    @Test
    public void testHiTextMessages() {
        onMessage("hi");
        verifyTextMessage(is("Hi there!"));
    }

    @Test
    public void testHeyTextMessages() {
        onMessage("hey");
        verifyTextMessage(is("Hi there!"));
    }

    @Test
    public void testWhatIsYourNameTextMessages() {
        onMessage("what is your name");
        verifyTextMessage(is("My name is Chuck Bot!"));
    }

    @Test
    public void testHowAreYouTextMessages() {
        onMessage("how are you");
        verifyTextMessage(isOneOf("I'm doing fine!", "I'm doing great!"));
    }

    @Test
    public void testHowAreYouDoingTextMessages() {
        onMessage("how are you doing?");
        verifyTextMessage(isOneOf("I'm doing fine!", "I'm doing great!"));
    }

    @Test
    public void testTellMeAJokeTextMessages() {
        onMessage("tell me a joke");
        verifyTextMessage(is("Chuck Norris once accidentally broke steel by touching it."));
    }

    @Test
    public void testJokeTextMessages() {
        onMessage("joke");
        verifyTextMessage(is("Chuck Norris once accidentally broke steel by touching it."));
    }

    @Test
    public void testTellMeAnotherTextMessages() {
        onMessage("tell me another");
        verifyTextMessage(is("Chuck Norris once accidentally broke steel by touching it."));
    }

    @Test
    public void testAnotherTextMessages() {
        onMessage("another");
        verifyTextMessage(is("Chuck Norris once accidentally broke steel by touching it."));
    }

    @Test
    public void testAgainTextMessages() {
        onMessage("again");
        verifyTextMessage(is("Chuck Norris once accidentally broke steel by touching it."));
    }

    @Test
    public void testTellMeAJokeWithCategoryTextMessages() {
        onMessage("tell me a joke with category category1");
        verifyTextMessage(is("When Chuck Norris wants an egg, he cracks open a chicken."));
    }

    @Test
    public void testJokeWithCategoryTextMessages() {
        onMessage("joke with category category1");
        verifyTextMessage(is("When Chuck Norris wants an egg, he cracks open a chicken."));
    }

    @Test
    public void testJokeCategoryTextMessages() {
        onMessage("joke category category1");
        verifyTextMessage(is("When Chuck Norris wants an egg, he cracks open a chicken."));
    }

    @Test
    public void testSearchJokeWithQueryTextMessages() {
        onMessage("search joke with obama");
        verifyTextMessage(is("Putin: We have the best nuclear weapons Obama: We have Chuck Norris"));
    }

    @Test
    public void testSearchMeAJokeWithQueryTextMessages() {
        onMessage("search me a joke with obama");
        verifyTextMessage(is("Putin: We have the best nuclear weapons Obama: We have Chuck Norris"));
    }

    @Test
    public void testSearchAJokeWithQueryTextMessages() {
        onMessage("search a joke with obama");
        verifyTextMessage(is("Putin: We have the best nuclear weapons Obama: We have Chuck Norris"));
    }

    @Test
    public void testFindJokeContainingQueryTextMessages() {
        onMessage("find joke containing obama");
        verifyTextMessage(is("Putin: We have the best nuclear weapons Obama: We have Chuck Norris"));
    }

    @Test
    public void testLolTextMessages() {
        onMessage("lol");
        verifyTextMessage(is("Yeah that was funny!"));
    }

    @Test
    public void testFunnyTextMessages() {
        onMessage("funny");
        verifyTextMessage(is("Yeah that was funny!"));
    }

    @Test
    public void testLaughingOutLoudFunnyTextMessages() {
        onMessage("laughing out loud");
        verifyTextMessage(is("Yeah that was funny!"));
    }

    @Test
    public void testHelpTextMessage() {
        onMessage("help");
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
    public void testCategorieTextMessagess() {
        onMessage("categories");
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
    public void testListCategorieTextMessagess() {
        onMessage("list categories");
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
    public void testGobbledygookTextMessage() {
        onMessage("gobbledygook");
        verifyTextMessage(is("OK! Ask me something else or type 'help'."));
    }
}
