package com.mysticcoders.mysticpaste.web.pages.history;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;

/**
 * Created with IntelliJ IDEA.
 * User: kinabalu
 * Date: 11/12/12
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class PastePagingNavigator extends AjaxPagingNavigator {
    public PastePagingNavigator(String id, IPageable pageable) {
        super(id, pageable);
    }

    public PastePagingNavigator(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        super(id, pageable, labelProvider);
    }

    private PastePagingNavigator dependentNavigator;
    public void setDependentNavigator(PastePagingNavigator dependentNavigator) {
        this.dependentNavigator = dependentNavigator;
    }

    protected void onAjaxEvent(AjaxRequestTarget target) {
        super.onAjaxEvent(target);

        if(dependentNavigator!=null) {
            target.add(dependentNavigator);
        }
   	}

/*
    protected PagingNavigation newNavigation(final String id, final IPageable pageable, final IPagingLabelProvider labelProvider) {
        PagingNavigation navigation = super.newNavigation(id, pageable, labelProvider);
        navigation.setRenderBodyOnly(true);
        navigation.setOutputMarkupPlaceholderTag(false);
        return navigation;
   	}
*/
}
