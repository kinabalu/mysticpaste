package com.mysticcoders.mysticpaste.web.components.blueprint;

import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: 1/2/11
 * Time: 9:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class BluePrintCSS extends AbstractBehavior {

    private final CompressedResourceReference IE_CSS = new CompressedResourceReference(BluePrintCSS.class, "ie.css");
    private final CompressedResourceReference PRINT_CSS = new CompressedResourceReference(BluePrintCSS.class, "print.css");
    private final CompressedResourceReference SCREEN_CSS = new CompressedResourceReference(BluePrintCSS.class, "screen.css");
    private final CompressedResourceReference TABS_CSS = new CompressedResourceReference(BluePrintCSS.class, "tabs.css");
    private final CompressedResourceReference TYPOGRAPHY_CSS = new CompressedResourceReference(BluePrintCSS.class, "typography.css");

	/**
	 * @see org.apache.wicket.markup.html.IHeaderContributor#renderHead(org.apache.wicket.markup.html.IHeaderResponse)
	 */
    @Override
	public void renderHead(IHeaderResponse response) {
//        response.renderJavascriptReference();

        response.renderCSSReference(PRINT_CSS, "print");
        response.renderCSSReference(SCREEN_CSS, "screen, projection");

        response.renderString("<!--[if lt IE 8]>");
        response.renderCSSReference(IE_CSS, "screen, projection");
        response.renderString("<![endif]-->\n");

        response.renderCSSReference(TABS_CSS, "screen, projection");
        response.renderCSSReference(TYPOGRAPHY_CSS, "screen, projection");
	}
}