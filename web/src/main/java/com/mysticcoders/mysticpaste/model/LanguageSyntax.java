/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Jul 4, 2009
 * Time: 10:06:29 AM
 */
package com.mysticcoders.mysticpaste.model;

import java.io.Serializable;

public class LanguageSyntax implements Serializable {
    private static final long serialVersionUID = 1L;

    private String language;
    private String name;
    private String script;

    public LanguageSyntax(String language, String name, String script) {
        this.language = language;
        this.name = name;
        this.script = script;
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public String getScript() {
        return script;
    }
}