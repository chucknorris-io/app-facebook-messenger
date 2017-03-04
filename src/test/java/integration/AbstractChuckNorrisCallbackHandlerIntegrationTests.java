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

import com.github.marsbits.restfbmessenger.Messenger;
import com.github.marsbits.restfbmessenger.send.SendOperations;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.restfb.types.send.ButtonTemplatePayload;
import com.restfb.types.send.IdMessageRecipient;
import com.restfb.types.send.QuickReply;
import com.restfb.types.webhook.messaging.MessageItem;
import com.restfb.types.webhook.messaging.MessagingItem;
import com.restfb.types.webhook.messaging.MessagingParticipant;
import com.restfb.types.webhook.messaging.PostbackItem;
import io.chucknorris.app.facebook.messenger.cache.CategoriesCache;
import io.chucknorris.app.facebook.messenger.callback.ChuckNorrisCallbackHandler;
import io.chucknorris.app.facebook.messenger.inject.AppModule;
import io.chucknorris.client.ChuckNorrisClient;
import io.chucknorris.client.Joke;
import org.apache.commons.collections4.ListUtils;
import org.hamcrest.Matcher;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

/**
 * Abstract helper class for {@link ChuckNorrisCallbackHandler} integration tests.
 *
 * @author Marcel Overdijk
 */
public abstract class AbstractChuckNorrisCallbackHandlerIntegrationTests {

	protected CategoriesCache categoriesCache;
	protected ChuckNorrisClient chuckNorrisClient;
	protected Messenger messenger;
	protected SendOperations sendOperations;

	protected ChuckNorrisCallbackHandler callbackHandler;

	protected String senderId;
	protected IdMessageRecipient recipient;

	@Before
	public void setUp() {

		this.senderId = "12345";
		this.recipient = new IdMessageRecipient(senderId);

		this.categoriesCache = mock(CategoriesCache.class);
		this.chuckNorrisClient = mock(ChuckNorrisClient.class);
		this.messenger = mock(Messenger.class);
		this.sendOperations = mock(SendOperations.class);

		Injector injector = Guice.createInjector(Modules
				.override(new AppModule())
				.with(new AbstractModule() {

					@Override
					protected void configure() {
						bind(CategoriesCache.class).toInstance(categoriesCache);
						bind(ChuckNorrisClient.class).toInstance(chuckNorrisClient);
						bind(Messenger.class).toInstance(messenger);
					}
				}));
		this.callbackHandler = injector.getInstance(ChuckNorrisCallbackHandler.class);

		when(messenger.send()).thenReturn(sendOperations);

		List<String> categories;
		categories = new ArrayList<>();
		for (int i = 1; i <= 20; i++) {
			categories.add("category" + i);
		}
		when(categoriesCache.getCategories()).thenReturn(categories);

		when(categoriesCache.getCategoriesPaged(6)).thenReturn(
				ListUtils.partition(categories, 6));

		Joke joke = new Joke();
		joke.setValue("Chuck Norris once accidentally broke steel by touching it.");
		when(chuckNorrisClient.getRandomJoke()).thenReturn(joke);

		Joke jokeWithCategory = new Joke();
		jokeWithCategory.setValue("When Chuck Norris wants an egg, he cracks open a chicken.");
		when(chuckNorrisClient.getRandomJoke("category1")).thenReturn(jokeWithCategory);

		Joke jokeFromSearch = new Joke();
		jokeFromSearch.setValue("Putin: We have the best nuclear weapons Obama: We have Chuck Norris");
		when(chuckNorrisClient.searchJokes("obama")).thenReturn(Arrays.asList(jokeFromSearch));
	}

	protected void onMessage(String message) {
		MessagingItem messaging = createMessagingItemWithText(senderId, message);
		callbackHandler.onMessage(messenger, messaging);
	}

	protected void onPostback(String payload) {
		MessagingItem messaging = createMessagingItemWithPostback(senderId, payload);
		callbackHandler.onPostback(messenger, messaging);
	}

	protected MessagingItem createMessagingItemWithPostback(String senderId, String payload) {
		MessagingParticipant sender = new MessagingParticipant();
		sender.setId(senderId);
		PostbackItem postback = new PostbackItem();
		postback.setPayload(payload);
		MessagingItem messagingItem = new MessagingItem();
		messagingItem.setSender(sender);
		messagingItem.setPostback(postback);
		return messagingItem;
	}

	protected MessagingItem createMessagingItemWithText(String senderId, String text) {
		MessagingParticipant sender = new MessagingParticipant();
		sender.setId(senderId);
		MessageItem message = new MessageItem();
		message.setText(text);
		MessagingItem messagingItem = new MessagingItem();
		messagingItem.setSender(sender);
		messagingItem.setMessage(message);
		return messagingItem;
	}

	protected void verifyButtonTemplate(Matcher<ButtonTemplatePayload> matcher) {
		verify(sendOperations).buttonTemplate(eq(recipient), argThat(matcher));
	}

	protected void verifyQuickReplies(String text, Matcher<List<QuickReply>> matcher) {
		verify(sendOperations).quickReplies(eq(recipient), eq(text), argThat(matcher));
	}

	protected void verifyTextMessage(Matcher<String> matcher) {
		verify(sendOperations).textMessage(eq(recipient), argThat(matcher));
	}
}
