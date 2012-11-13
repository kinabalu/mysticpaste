package com.mysticcoders.mysticpaste.web;

import com.mysticcoders.mysticpaste.MysticPasteApplication;
import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.servlet.WicketSessionFilter;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: 1/20/12
 * Time: 6:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class MysticPasteSession extends WebSession {

    private static final Logger log = LoggerFactory.getLogger(MysticPasteSession.class);

    public MysticPasteSession(Request request) {
        super(request);
//        injectDependencies();
    }

    public static MysticPasteSession get() {
        return (MysticPasteSession) Session.get();
    }

/*
    private void injectDependencies() {
        Injector.get().inject(this);
    }
*/
}
