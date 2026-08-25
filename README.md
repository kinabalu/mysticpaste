# Overview

This pastebin was built using Apache Wicket. The original code was built by
several folks as a tutorial for learning Wicket named 5 Days of Wicket.
There's a link in the navigation to the source code which you can peruse at
your leisure.

The idea of a pastebin is simple, copy all or a fragment of code you need
help with into the content box on the page. Select which language the code
is in for nice syntax highlighting provided by the SyntaxHighlighter JavaScript
library. You will receive a small URL to paste into an email, IRC, mailing list,
or instant message to receive help on your issue.

We have plugins for 4 different development environments so you can paste
directly from the editor.

## Requirements

* JDK 21 or newer
* Maven 3.9 or newer
* MongoDB and Redis for anything that reads or writes pastes

The stack is Apache Wicket 10, Spring Framework 6.2, Morphia 2 on the MongoDB
sync driver 5, Jedis for Redis, and Jakarta EE 10 (`jakarta.servlet`). Embedded
Jetty 12 is used for local development.

## Environment Configuration

MongoDB and Redis connection settings live in
`web/src/main/resources/application.properties` and can be overridden per
deployment in `web/src/main/resources/application-override.properties` or in
`/etc/mysticpaste/application.properties`:

```
mongo.uri=mongodb://localhost:27017
mongo.database=mysticpaste
redis.host=localhost
redis.port=6379
```

### The Mystic bits

Mystic Paste is set up with Maven, so to build a war and run the tests:

`mvn clean verify`

To skip the tests:

`mvn package -DskipTests`

Pull it into any IDE and find the `Start.java` in `web/src/test/java/com/mysticcoders`.
Execute the main and you should have a pastebin running on
[http://localhost:8080](http://localhost:8080). Pass `-Djetty.port=<port>` to use
a different port.

## Deploying

Deployment should be as simple as adding your own `filters-DEV.properties` and
then typing:

`mvn package -DskipTests -PDEV`

The resulting `web/target/mysticpaste.war` is a Jakarta EE 10 web application and
needs a Servlet 6.0 container (Jetty 12 `ee10`, Tomcat 10.1+, or similar).
