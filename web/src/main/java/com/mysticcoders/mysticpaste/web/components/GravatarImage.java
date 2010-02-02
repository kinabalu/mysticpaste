/*
 * Copyright 2009 Mystic Coders, LLC (http://www.mysticcoders.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mysticcoders.mysticpaste.web.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Simple Web Component that must be attached to an HTML tag of type 'img'.
 * <p/>
 * Supply a model which contains the email address and an optional size attribute
 * which is auto-scaled proportional.
 * <p/>
 * See here for more info on setting up Gravatar URL's:
 * http://en.gravatar.com/site/implement/url
 * <p/>
 * <p/>
 * Java:
 * <p/>
 * <pre>
 *      add(new GravatarImage("image", new Model("email@domain.com"));
 * </pre>
 * <p/>
 * HTML:
 * <pre>
 *      &lt;img wicket:id=&quot;image&quot; /&gt;
 * </pre>
 *
 * @author Andrew Lombardi (andrew@mysticcoders.com)
 */
public class GravatarImage extends WebComponent {

    private static final int DEFAULT_HSIZE = 44;

    public GravatarImage(String id, IModel<String> model) {
        this(id, model, DEFAULT_HSIZE);
    }

    public GravatarImage(String id, IModel<String> model, int hsize) {
        super(id);
        setDefaultModel(new GravatarModel(model, hsize));
    }

    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "img");
        tag.put("src", getDefaultModelObjectAsString());
    }

    /**
     * Given an email address this will return a valid Gravatar-based URL which
     * if registered with an image, will show that "gravatar"
     */
    private class GravatarModel extends AbstractReadOnlyModel<String> {

        // Base URL for Gravatar
        private static final String GRAVATAR_URL = "http://www.gravatar.com/avatar/";

        String email;
        String gravatarKey;
        int hsize;

        public GravatarModel(IModel<String> model, int hsize) {
            email = model.getObject();
            gravatarKey = MD5Util.md5Hex(email);
            this.hsize = hsize;
        }

        public String getObject() {
            StringBuilder sb = new StringBuilder();
            sb.append(GRAVATAR_URL);
            sb.append(gravatarKey);
            sb.append("?s=");
            sb.append(hsize);
            return sb.toString();
        }
    }

    /**
     * Copied verbatim from the example code on the gravatar.com site here:
     *
     * http://en.gravatar.com/site/implement/java
     *
     */
    private static class MD5Util {
        public static String hex(byte[] array) {
            StringBuffer sb = new StringBuffer();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray
                        & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        }

        public static String md5Hex(String message) {
            try {
                MessageDigest md =
                        MessageDigest.getInstance("MD5");
                return hex(md.digest(message.getBytes("CP1252")));
            } catch (NoSuchAlgorithmException e) { // do nothing
            } catch (UnsupportedEncodingException e) { // do nothing
            }
            return null;
        }
    }
}