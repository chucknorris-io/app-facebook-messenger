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
import static io.chucknorris.app.facebook.messenger.MessageTextMatcher.PATTERN_HI;
import static io.chucknorris.app.facebook.messenger.MessageTextMatcher.PATTERN_HOW_ARE_YOU;
import static io.chucknorris.app.facebook.messenger.MessageTextMatcher.PATTERN_RANDOM_JOKE;
import static io.chucknorris.app.facebook.messenger.MessageTextMatcher.PATTERN_RANDOM_JOKE_WITH_CATEGORY;
import static io.chucknorris.app.facebook.messenger.MessageTextMatcher.PATTERN_SEARCH_JOKE;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import com.github.marsbits.restfbmessenger.Messenger;
import com.github.marsbits.restfbmessenger.webhook.AbstractCallbackHandler;
import com.restfb.types.User;
import com.restfb.types.send.ButtonTemplatePayload;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.PostbackButton;
import com.restfb.types.send.QuickReply;
import com.restfb.types.webhook.messaging.MessageItem;
import com.restfb.types.webhook.messaging.MessagingItem;
import com.restfb.types.webhook.messaging.PostbackItem;

import io.chucknorris.client.ChuckNorrisClient;
import io.chucknorris.client.Joke;

/**
 * The Chuck Norris IO {@code CallbackHandler}.
 *
 * @author Marcel Overdijk
 */
public class ChuckNorrisCallbackHandler extends AbstractCallbackHandler {

    private static final Logger logger =
            Logger.getLogger(ChuckNorrisCallbackHandler.class.getName());

    public static final String PAYLOAD_GET_STARTED = "GET_STARTED";
    public static final String PAYLOAD_RANDOM_JOKE = "RANDOM_JOKE";
    public static final String PAYLOAD_RANDOM_JOKE_WITH_CATEGORY = "RANDOM_JOKE_WITH_CATEGORY";
    public static final String PAYLOAD_CATEGORIES = "CATEGORIES";
    public static final String PAYLOAD_CATEGORIES_MORE = "CATEGORIES_MORE";
    public static final String PAYLOAD_HELP = "HELP";
    public static final String PAYLOAD_SEPARATOR = ":";

    public static final int CATEGORIES_PAGE_SIZE = 6;

    private ChuckNorrisClient chuckNorrisClient = new ChuckNorrisClient();

    private CategoriesCache categoriesCache = new CategoriesCache();

    private MessageTextMatcher messageTextMatcher = new MessageTextMatcher();

    @Override
    public void onMessage(Messenger messenger, MessagingItem messaging) {
        String senderId = messaging.getSender().getId();
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        MessageItem message = messaging.getMessage();

        logger.info(format("Message received from %s: %s", senderId, message));

        messenger.send().markSeen(recipient);

        if (message.getQuickReply() != null) {
            String payload = message.getQuickReply().getPayload();
            handlePayload(messenger, senderId, payload);
        } else {
            String messageText = message.getText();
            handleMessageText(messenger, senderId, messageText);
        }
    }

    @Override
    public void onPostback(Messenger messenger, MessagingItem messaging) {
        String senderId = messaging.getSender().getId();
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        PostbackItem postback = messaging.getPostback();

        logger.info(format("Postback received from %s: %s", senderId, postback));

        messenger.send().markSeen(recipient);
        handlePayload(messenger, senderId, postback.getPayload());
    }

    private void handleMessageText(Messenger messenger, String senderId, String messageText) {
        Matcher match = messageTextMatcher.match(messageText);
        if (match == null) {
            sendMisunderstand(messenger, senderId);
        } else {
            Pattern pattern = match.pattern();
            if (pattern == PATTERN_HI) {
                sendHi(messenger, senderId);
            } else if (pattern == PATTERN_HOW_ARE_YOU) {
                sendDoingFine(messenger, senderId);
            } else if (pattern == PATTERN_HELP) {
                sendHelp(messenger, senderId);
            } else if (pattern == PATTERN_RANDOM_JOKE) {
                sendRandomJoke(messenger, senderId);
            } else if (pattern == PATTERN_RANDOM_JOKE_WITH_CATEGORY) {
                String category = match.group("category");
                sendRandomJoke(messenger, senderId, category);
            } else if (pattern == PATTERN_SEARCH_JOKE) {
                String query = match.group("query");
                sendSearchJoke(messenger, senderId, query);
            } else if (pattern == PATTERN_CATEGORIES) {
                sendCategories(messenger, senderId);
            }
        }
    }

    private void handlePayload(Messenger messenger, String senderId, String payload) {
        switch (payload) {

            // first handle exact payloads

            case PAYLOAD_GET_STARTED:
                sendGetStarted(messenger, senderId);
                break;

            case PAYLOAD_RANDOM_JOKE:
                sendRandomJoke(messenger, senderId);
                break;

            case PAYLOAD_CATEGORIES:
                sendCategories(messenger, senderId);
                break;

            case PAYLOAD_HELP:
                sendHelp(messenger, senderId);
                break;

            default:

                // handle dynamic payloads

                if (payload.startsWith(PAYLOAD_RANDOM_JOKE_WITH_CATEGORY + PAYLOAD_SEPARATOR)) {

                    String category = StringUtils.replaceOnce(payload,
                            PAYLOAD_RANDOM_JOKE_WITH_CATEGORY + PAYLOAD_SEPARATOR, EMPTY);
                    sendRandomJoke(messenger, senderId, category);

                } else if (payload.startsWith(PAYLOAD_CATEGORIES_MORE + PAYLOAD_SEPARATOR)) {

                    int pageNumber = 1;
                    try {
                        pageNumber = Integer.parseInt(StringUtils.replaceOnce(payload,
                                PAYLOAD_CATEGORIES_MORE + PAYLOAD_SEPARATOR, EMPTY));
                    } catch (Exception e) {
                        logger.severe(
                                format("Unexpected exception parsing categories page number: ",
                                        e.getMessage()));
                    }
                    sendCategories(messenger, senderId, pageNumber);

                } else {

                    logger.warning(format("Unknown payload received: %s", payload));
                    sendError(messenger, senderId);
                }
                break;
        }
    }

    private void sendMisunderstand(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        messenger.send().textMessage(recipient, "I don't really understand, try asking me something else or ask 'help'."); // TODO
        messenger.send().typingOff(recipient);
    }

    private void sendGetStarted(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);

        User user = messenger.getUserProfile(senderId);

        Map<String, Object> values = new HashMap<>();
        values.put("user_first_name", user.getFirstName());

        String textMessage = StrSubstitutor
                .replace("Hi ${user_first_name}, what would you like to hear?", values);

        messenger.send().quickReplies(recipient, textMessage, Arrays.asList(
                new QuickReply("Random Joke", PAYLOAD_RANDOM_JOKE),
                new QuickReply("Categories", PAYLOAD_CATEGORIES)));
        messenger.send().typingOff(recipient);
    }

    private void sendRandomJoke(Messenger messenger, String senderId) {
        sendRandomJoke(messenger, senderId, null);
    }

    private void sendHi(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        messenger.send().textMessage(recipient, "Hi there!");
        messenger.send().typingOff(recipient);
    }

    private void sendDoingFine(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        messenger.send().textMessage(recipient, "I'm doing fine!");
        messenger.send().typingOff(recipient);
    }

    private void sendRandomJoke(Messenger messenger, String senderId, String category) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        String textMessage;
        if (category != null) {
            List<String> categories = categoriesCache.getCategories();
            if (categories.contains(category)) {
                textMessage = chuckNorrisClient.getRandomJoke(category).getValue();
            } else {
                textMessage = "I couldn't find the category you are looking for, try another one.";
            }
        } else {
            textMessage = chuckNorrisClient.getRandomJoke().getValue();
        }
        messenger.send().textMessage(recipient, textMessage);
        messenger.send().typingOff(recipient);
    }

    private void sendSearchJoke(Messenger messenger, String senderId, String query) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        String textMessage;
        List<Joke> jokes = chuckNorrisClient.searchJokes(query);
        if (jokes != null && jokes.size() > 0) {
            int i = new Random().nextInt(jokes.size());
            textMessage = jokes.get(i).getValue();
        } else {
            textMessage = format("It seems there are no Chuck Norris jokes containing %s.", query);
        }
        messenger.send().textMessage(recipient, textMessage);
        messenger.send().typingOff(recipient);
    }

    private void sendCategories(Messenger messenger, String senderId) {
        sendCategories(messenger, senderId, 1);
    }

    private void sendCategories(Messenger messenger, String senderId, int pageNumber) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        List<QuickReply> quickReplies = new ArrayList<>();
        List<List<String>> categoriesPaged =
                categoriesCache.getCategoriesPaged(CATEGORIES_PAGE_SIZE);
        if (categoriesPaged.size() < pageNumber) {
            pageNumber = 1;
        }
        List<String> categoriesPage = categoriesPaged.get(pageNumber - 1);
        for (int i = 0; i < categoriesPage.size(); i++) {
            String category = categoriesPage.get(i);
            String title = category;
            String payload = PAYLOAD_RANDOM_JOKE_WITH_CATEGORY + PAYLOAD_SEPARATOR + category;
            quickReplies.add(new QuickReply(title, payload));
        }
        if (pageNumber < categoriesPaged.size()) {
            quickReplies.add(
                    new QuickReply("More...",
                            PAYLOAD_CATEGORIES_MORE + PAYLOAD_SEPARATOR + (pageNumber + 1)));
        }
        messenger.send().quickReplies(recipient, "Choose a category:", quickReplies);
        messenger.send().typingOff(recipient);
    }

    private void sendHelp(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        ButtonTemplatePayload buttonTemplate = new ButtonTemplatePayload(
                "Hi there. I can tell you random Chuck Norris jokes. Ask me things like the following:"
                        + "\n"
                        + "\n  • Tell me a joke"
                        + "\n  • Tell me a joke with category movie"
                        + "\n  • Search joke containing steroids"
                        + "\n  • List available categories"
                        + "\n"
                        + "\nOr choose a command below.");
        buttonTemplate.addButton(new PostbackButton("Random Joke", PAYLOAD_RANDOM_JOKE));
        buttonTemplate.addButton(new PostbackButton("Categories", PAYLOAD_CATEGORIES));
        messenger.send().buttonTemplate(recipient, buttonTemplate);
        messenger.send().typingOff(recipient);
    }

    private void sendError(Messenger messenger, String senderId) {
        IdMessageRecipient recipient = new IdMessageRecipient(senderId);
        messenger.send().typingOn(recipient);
        messenger.send().textMessage(recipient,
                "Something went wrong. If it is your fault, and probably it is, Chuck Norris will find you and roundhouse kick your butt!");
        messenger.send().typingOff(recipient);
    }
}
