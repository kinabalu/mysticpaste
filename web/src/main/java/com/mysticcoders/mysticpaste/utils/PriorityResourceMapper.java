package com.mysticcoders.mysticpaste.utils;

import org.apache.wicket.request.Request;
import org.apache.wicket.request.mapper.ResourceMapper;
import org.apache.wicket.request.mapper.parameter.IPageParametersEncoder;
import org.apache.wicket.request.resource.ResourceReference;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: 1/21/11
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class PriorityResourceMapper extends ResourceMapper {

    private String[] segments;

    public PriorityResourceMapper(String path, ResourceReference resourceReference) {
        super(path, resourceReference);

        segments = getMountSegments(path);

    }

    public PriorityResourceMapper(String path, ResourceReference resourceReference, IPageParametersEncoder encoder) {
        super(path, resourceReference, encoder);

        segments = getMountSegments(path);
    }

    @Override
    public int getCompatibilityScore(Request request) {
        if (urlStartsWith(request.getUrl(), segments)) {
            return segments.length;
        } else {
            return 0;
        }
    }

}
