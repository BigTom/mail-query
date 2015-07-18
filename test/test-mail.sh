#! /bin/bash
#Send a test e-mail for mailgun event log

# Update EMAIL to an email address that works e.g. EMAIL='xxx.yyy@zzz.com'
# MAILGUN_DOMAIN & MAILGUN_API_KEY should have the same values as for the webapp

curl --verbose -s --user api:${MAILGUN_API_KEY} \
	https://api.mailgun.net/v3/${MAILGUN_DOMAIN}/messages \
	-F from='postmaster@'${MAILGUN_DOMAIN} \
	-F to=${EMAIL} \
	-F subject='Hello' \
	-F text='Testing Mailgun'


