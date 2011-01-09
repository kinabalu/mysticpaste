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

## Environment Configuration

Mystic Paste is setup with Maven so to build a war, just type

`mvn package -Dmaven.test.skip=true`

Pull it into any IDE and find the Start.java in `web/src/test/java/com/mysticcoders`

Execute the main and you should have a pastebin running.

## Deploying

Deployment should be as simple as adding your own `filters-DEV.properties` and
then typing :

`mvn package -Dmaven.test.skip=true -PDEV`