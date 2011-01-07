package com.mysticcoders.mysticpaste.web.components.highlighter;

import com.mysticcoders.mysticpaste.model.LanguageSyntax;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.markup.html.resources.JavaScriptReference;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.springframework.web.util.JavaScriptUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * HighlighterTextAreaPanel
 * <p/>
 * Created by: Andrew Lombardi
 * Copyright 2006 Mystic Coders, LLC
 */
public class HighlighterPanel extends Panel {
    private static final long serialVersionUID = 1L;


    private static List<LanguageSyntax> types
            = new ArrayList<LanguageSyntax>();

    static {
        types.add(new LanguageSyntax("as3", "Applescript", "shBrushAS3.js"));
        types.add(new LanguageSyntax("bash", "Bash", "shBrushBash.js"));
        types.add(new LanguageSyntax("csharp", "C#", "shBrushCSharp.js"));
        types.add(new LanguageSyntax("cpp", "C / C++", "shBrushCpp.js"));
        types.add(new LanguageSyntax("css", "CSS", "shBrushCss.js"));
        types.add(new LanguageSyntax("delphi", "Delphi", "shBrushDelphi.js"));
        types.add(new LanguageSyntax("diff", "Diff", "shBrushDiff.js"));
        types.add(new LanguageSyntax("groovy", "Groovy", "shBrushGroovy.js"));
        types.add(new LanguageSyntax("java", "Java", "shBrushJava.js"));
        types.add(new LanguageSyntax("js", "JavaScript", "shBrushJScript.js"));
        types.add(new LanguageSyntax("javafx", "JavaFX", "shBrushJavaFX.js"));
        types.add(new LanguageSyntax("perl", "Perl", "shBrushPerl.js"));
        types.add(new LanguageSyntax("php", "PHP", "shBrushPhp.js"));
        types.add(new LanguageSyntax("text", "Plain Text", "shBrushPlain.js"));
        types.add(new LanguageSyntax("powershell", "PowerShell", "shBrushPowerShell.js"));
        types.add(new LanguageSyntax("python", "Python", "shBrushPython.js"));
        types.add(new LanguageSyntax("ruby", "Ruby", "shBrushRuby.js"));
        types.add(new LanguageSyntax("scala", "Scala", "shBrushScala.js"));
        types.add(new LanguageSyntax("sql", "SQL", "shBrushSql.js"));
        types.add(new LanguageSyntax("vb", "VB / VB.NET", "shBrushVb.js"));
        types.add(new LanguageSyntax("xml", "XML / XHTML", "shBrushXml.js"));
    }


    public static List<LanguageSyntax> getLanguageSyntaxList() {
        return types;
    }

    public static LanguageSyntax getLanguageSyntax(String language) {
        for (LanguageSyntax syntax : types) {
            if (syntax.getLanguage().equals(language)) {
                return syntax;
            }
        }

        return null;
    }

    public static String getLanguageScript(String language) {
        for (LanguageSyntax syntax : types) {
            if (syntax.getLanguage().equals(language)) {
                return syntax.getScript();
            }
        }

        return null;
    }

    public HighlighterPanel(String id, IModel model) {
        this(id, model, null);
    }

    public HighlighterPanel(String id, IModel model, String language) {
        this(id, model, language, false, null);
    }

    public HighlighterPanel(String id, IModel model, String language, boolean excludeExternalResources) {
        this(id, model, language, excludeExternalResources, null);
    }

    public HighlighterPanel(String id, IModel model, String language, final boolean excludeExternalResources, String highlightLines) {
        super(id);

        if(!excludeExternalResources) {
            add(CSSPackageResource.getHeaderContribution(HighlighterPanel.class, "shCore.css"));
    //        add(CSSPackageResource.getHeaderContribution(HighlighterPanel.class, "shCoreDjango.css"));
            add(CSSPackageResource.getHeaderContribution(HighlighterPanel.class, "shThemeDefault.css"));
            add(JavascriptPackageResource.getHeaderContribution(HighlighterPanel.class, "jquery-1.4.4.min.js"));

        }

        add(new JavaScriptReference("highlighterCore", HighlighterPanel.class, "shCore.js") {
            @Override
            public boolean isVisible() {
                return !excludeExternalResources;
            }
        });

        if (language == null || getLanguageScript(language) == null) language = "text";

        // TODO we need to find out how to do smart header contribution so only the brushes we need get loaded, not multiple times
        add(new JavaScriptReference("highlighterLanguage", HighlighterPanel.class,
                getLanguageScript(language)));

        Label codePanel = new Label("code", model);
        add(codePanel);


        StringBuffer brushConfig = new StringBuffer("brush: ");
        brushConfig.append(language);
        brushConfig.append("; toolbar: false");
        if (highlightLines != null)
            brushConfig.append("; highlight: [").append(highlightLines).append("]");

        codePanel.add(new AttributeModifier("class", true, new Model<String>(brushConfig.toString())));

    }

    public static HeaderContributor getHeaderContribution(final Class<?> scope, final String path, final String media, final String rel, final String title) {
        return new HeaderContributor(new IHeaderContributor() {
            private static final long serialVersionUID = 1L;

            public void renderHead(IHeaderResponse response) {
                ResourceReference reference = new CompressedResourceReference(scope, path);

                CharSequence url = RequestCycle.get().urlFor(reference);

                if (Strings.isEmpty(url)) {
                    throw new IllegalArgumentException("url cannot be empty or null");
                }
                List<Object> token = Arrays.asList(new Object[]{"css", url, media});

                response.getResponse().write("<link rel=\"" + (rel != null ? rel : "stylesheet") + " type=\"text/css\" href=\"");
                response.getResponse().write(url);
                response.getResponse().write("\"");
                if (media != null)
                {
                    response.getResponse().write(" media=\"");
                    response.getResponse().write(media);
                    response.getResponse().write("\"");
                }
                if(title!=null) {
                    response.getResponse().write(" title=\"");
                    response.getResponse().write(title);
                    response.getResponse().write("\"");
                }
                response.getResponse().println(" />");
            }
        });
    }
}