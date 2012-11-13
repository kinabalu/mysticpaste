package com.mysticcoders.mysticpaste.web.components;

import org.apache.wicket.request.resource.CssResourceReference;

/**
 * Created with IntelliJ IDEA.
 * User: kinabalu
 * Date: 11/11/12
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class FixBootstrapStylesCssResourceReference extends CssResourceReference {

    public static final FixBootstrapStylesCssResourceReference INSTANCE = new FixBootstrapStylesCssResourceReference();

    /**
     * Construct.
     */
    public FixBootstrapStylesCssResourceReference() {
        super(FixBootstrapStylesCssResourceReference.class, "fix.css");
    }
}