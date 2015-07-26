# mailgun query app

This is a basic app to search a mailgun event log for a specific target e-mail address.

It uses [**http-kit-server**](http://www.http-kit.org/) as its server and uses 
[**http-kit-client**](http://www.http-kit.org/) to access the 
[**mailgun** event log](https://documentation.mailgun.com/api-events.html)

## Usage

The app requires two environment variables to be set:

1. **MAILGUN_DOMAIN** looks like sandbox1dbb60fblahblablad481.mailgun.org

2. **MAILGUN_API_KEY** looks like key-4e895blahblahblahe27dca51824c84

3. **MAILGUN_ADMIN_PWD** a cleartext password for the "admin" user to be used by basic_auth (yes I know its 
   unsafe and ugly but its better than nothing)

What they are is described [here](https://documentation.mailgun.com/api-intro.html#authentication)

## License

Copyright © 2015 Tom Ayerst

Distributed under the Eclipse Public License either version 1.0 or later.
