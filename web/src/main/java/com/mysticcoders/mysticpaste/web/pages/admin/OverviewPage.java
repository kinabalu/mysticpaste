package com.mysticcoders.mysticpaste.web.pages.admin;

import com.mysticcoders.mysticpaste.web.pages.BasePage;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: 1/20/12
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
@AuthorizeInstantiation(Roles.ADMIN)
public class OverviewPage extends BasePage {
    
    public OverviewPage() {
        
    }
}
