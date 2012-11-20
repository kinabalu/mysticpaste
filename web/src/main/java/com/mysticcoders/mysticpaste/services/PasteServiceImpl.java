package com.mysticcoders.mysticpaste.services;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @author <a href="mailto:andrew@mysticcoders.com">Andrew Lombardi</a>
 * @version $Revision$ $Date$
 */

@Service("pasteService")
public class PasteServiceImpl implements PasteService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final int DEFAULT_TOKEN_LENGTH = 10;

    public static final int DEFAULT_PREVIEW_LINES = 5;

    @Resource(name = "mongoPasteItemDao")
    private PasteItemDao pasteItemDao;

    public void setPasteItemDao(PasteItemDao pasteItemDao) {
        this.pasteItemDao = pasteItemDao;
    }

    private int tokenLength = 10;

    private int previewLines;

    public PasteServiceImpl() {
        this.tokenLength = DEFAULT_TOKEN_LENGTH;
        this.previewLines = DEFAULT_PREVIEW_LINES;
    }

    public List<PasteItem> getLatestItems(String clientToken, int count, int startIndex) {
        logger.trace("Service: getLatestItems. clientToken = {}, count = {}, startIndex = {}",
                new Object[]{clientToken, count, startIndex});
        List<PasteItem> results;
        results = pasteItemDao.find(count, startIndex);
//        System.out.println("results:"+results);
        if (null == results) {
            logger.warn("Found no items in database.");
            results = new ArrayList<PasteItem>();
        }
        return results;
    }

    public PasteItem getItem(String clientToken, String id) {

        return pasteItemDao.get(id);
    }

    private String twitterUsername;
    private String twitterPassword;
    private String twitterEnabled;

    public String getTwitterUsername() {
        return twitterUsername;
    }

    public void setTwitterUsername(String twitterUsername) {
        this.twitterUsername = twitterUsername;
    }

    public String getTwitterPassword() {
        return twitterPassword;
    }

    public void setTwitterPassword(String twitterPassword) {
        this.twitterPassword = twitterPassword;
    }

    public String getTwitterEnabled() {
        return twitterEnabled;
    }

    public boolean twitterEnabled() {
        return (twitterEnabled != null) && Boolean.valueOf(twitterEnabled);
    }

    public void setTwitterEnabled(String twitterEnabled) {
        this.twitterEnabled = twitterEnabled;
    }

    public String createItem(String clientToken, PasteItem item) {
        return createItem(clientToken, item, true);
    }

    public String createItem(String clientToken, PasteItem item, boolean twitter) {
        // set created Timestamp
        item.setTimestamp(new Date(System.currentTimeMillis()));

        return pasteItemDao.create(item);
    }


    public String createReplyItem(String clientToken, PasteItem item, String parentId)
            throws ParentNotFoundException {
        String id;
        PasteItem parent = pasteItemDao.get(parentId);
        if (null != parent) {
            item.setParent(parent.getItemId());
            // set created timestamp
            item.setTimestamp(new Date(System.currentTimeMillis()));
            id = pasteItemDao.create(item);
        } else {
            throw new ParentNotFoundException("Parent does not exist");
        }
        return id;
    }

    public long getLatestItemsCount(String clientToken) {
        return pasteItemDao.count();
    }

    public int incViewCount(PasteItem pasteItem) {
        return pasteItemDao.incViewCount(pasteItem);
    }

    public void markAbuse(PasteItem pasteItem) {
        pasteItemDao.markAbuse(pasteItem);
    }

    public List<PasteItem> hasChildren(PasteItem pasteItem) {
        return pasteItemDao.getChildren(pasteItem);
    }

    public int getTokenLength() {
        return tokenLength;
    }

    public void setTokenLength(int tokenLength) {
        this.tokenLength = tokenLength;
    }

    public int getPreviewLines() {
        return previewLines;
    }

    public void setPreviewLines(int previewLines) {
        this.previewLines = previewLines;
    }
}
