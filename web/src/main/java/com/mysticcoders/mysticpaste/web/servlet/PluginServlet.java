package com.mysticcoders.mysticpaste.web.servlet;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.services.InvalidClientException;
import com.mysticcoders.mysticpaste.services.PasteService;

import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class PluginServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_LANG = "text";

    private static PasteService pasteService;

    private static PasteService getPasteService(ServletContext sContext) {
        if (pasteService == null) {
            ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(sContext);
            pasteService = (PasteService) ctx.getBean("pasteService");
        }
        return pasteService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String type = req.getParameter("type");
        String content = req.getParameter("content");
        String fileExtension = req.getParameter("fileExt");
        String redirect = req.getParameter("redirect");

        PasteItem item = new PasteItem();
        item.setContent(content);
        if (Strings.isEmpty(fileExtension)) {
            item.setType(Strings.isEmpty(type) ? DEFAULT_LANG : type);
        } else {
            item.setType(mapFileType(fileExtension));
        }

        try {
            getPasteService(getServletContext()).createItem("plugin", item);
        } catch (InvalidClientException e) {
            e.printStackTrace();
        }


        StringBuilder sb = new StringBuilder("http://");
        sb.append("mysticpaste.com");
/*
        sb.append(req.getServerName());
        if (req.getServerPort() != 80) {
            sb.append(":").append(req.getServerPort());
        }
*/

        sb.append("/view/").append(item.getId());

        if (redirect != null && redirect.equalsIgnoreCase("true")) {
            resp.sendRedirect(sb.toString());

        } else {
            resp.setHeader("X-Paste-Identifier", "" + item.getId());  // Added to allow pastebin integration with the VIM plugin

            resp.getWriter().print(item.getId());
            resp.flushBuffer();
        }

    }

    /**
     * Map file extensions (i.e. java, cpp, sql, h, js) to types recognized by
     * our system (jashi types)
     *
     * @param fileExtension
     * @return
     */
    private String mapFileType(String fileExtension) {
        String ret = DEFAULT_LANG;
        ResourceBundle bundle = ResourceBundle.getBundle("com.mysticcoders.mysticpaste.config.syntax");
        try {
            //lookup the file extension from our properties file to find the SyntaxHighlighter type
            ret = bundle.getString(fileExtension.toLowerCase());
        } catch (MissingResourceException mre) {
            //do nothing, the default will be text
            logger.warn("Could not find a SyntaxHighlighter mapping for the file extension: " + fileExtension);
        }
        return ret;
    }
}
