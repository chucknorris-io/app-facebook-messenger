# Official chucknorris.io app for Facebook Messenger

[![Apache 2.0 License](https://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)


[chucknorris.io][] is a free JSON API for hand curated Chuck Norris facts.

Chuck Norris facts are satirical factoids about martial artist and actor Chuck Norris that have become an Internet
phenomenon and as a result have become widespread in popular culture. The 'facts' are normally absurd hyperbolic claims
about Norris' toughness, attitude, virility, sophistication, and masculinity.

Chuck Norris facts have spread around the world, leading not only to translated versions, but also spawning localized
versions mentioning country-specific advertisements and other Internet phenomena. Allusions are also sometimes made to
his use of roundhouse kicks to perform seemingly any task, his large amount of body hair with specific regard to his
beard, and his role in the action television series Walker, Texas Ranger.

## Deployment

First make sure to store the encrypted tokens and app secret as we don't want to store them GitHub.

    ./gradlew addCredentials --key chucknorrisIoVerifyToken --value the-verify-token
    ./gradlew addCredentials --key chucknorrisIoAccessToken --value the-access-token
    ./gradlew addCredentials --key chucknorrisIoAppSecret --value the-app-secret

After the tokens and app secret we can deploy the app to Google App Engine.

    ./gradlew clean appengineUpdate

## License

This software is released under version 2.0 of the [Apache License][].


[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
[chucknorris.io]: https://api.chucknorris.io
