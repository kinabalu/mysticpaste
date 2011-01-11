package com.mysticcoders.mysticpaste.services;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import com.mysticcoders.mysticpaste.utils.TokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @author <a href="mailto:andrew@mysticcoders.com">Andrew Lombardi</a>
 * @version $Revision$ $Date$
 */
public class PasteServiceImpl implements PasteService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final int DEFAULT_TOKEN_LENGTH = 10;

    public static final int DEFAULT_PREVIEW_LINES = 5;

    private PasteItemDao itemDao;

    private int tokenLength;

    private int previewLines;

    public PasteServiceImpl() {
        this.tokenLength = DEFAULT_TOKEN_LENGTH;
        this.previewLines = DEFAULT_PREVIEW_LINES;
    }

    public PasteServiceImpl(PasteItemDao itemDao, int tokenLength) {
        this.itemDao = itemDao;
        this.tokenLength = tokenLength;
    }

    @Transactional(readOnly = true)
    public List<PasteItem> getLatestItems(String clientToken, int count, int startIndex, boolean threaded)
            throws InvalidClientException {
        logger.trace("Service: getLatestItems. clientToken = {}, count = {}, startIndex = {}, threaded = {}",
                new Object[]{clientToken, count, startIndex, threaded});
        validateClient(clientToken);
        List<PasteItem> results;
        if (threaded) {
            results = itemDao.findThreaded(count, startIndex);
        } else {
            results = itemDao.find(count, startIndex);
        }

        if (null == results) {
            logger.warn("Found no items in database.");
            results = new ArrayList<PasteItem>();
        }
        return results;
    }

    @Transactional(readOnly = true)
    public PasteItem getItem(String clientToken, long id) throws InvalidClientException {
        validateClient(clientToken);

        return itemDao.get(id);
    }

    @Transactional(readOnly = true)
    public PasteItem findPrivateItem(String clientToken, String privateToken) throws InvalidClientException {
        validateClient(clientToken);

        return itemDao.findByToken(privateToken);
    }

    @Transactional(readOnly = true)
    public List<PasteItem> findItemsByLanguage(String clientToken, String languageType, int count,
                                               int startIndex, boolean threaded)
            throws InvalidClientException {
        validateClient(clientToken);

        List<PasteItem> results;
/*
        LanguageEnum type = LanguageEnum.valueOf(languageType);
        if (threaded) {
            results = itemDao.findByLanguageThreaded(languageType, count, startIndex);
        } else {
            results = itemDao.findByLanguage(languageType, count, startIndex);
        }
        if (null == results) {
            results = new ArrayList<PasteItem>();
        }
        return results;
*/
        return null;
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

    @Transactional(rollbackFor = Exception.class)
    public long createItem(String clientToken, PasteItem item) throws InvalidClientException {
        return createItem(clientToken, item, true);
    }

    @Transactional(rollbackFor = Exception.class)
    public long createItem(String clientToken, PasteItem item, boolean twitter) throws InvalidClientException {
        validateClient(clientToken);
        if (null != item && item.isPrivate()) {
            item.setPrivateToken(TokenGenerator.generateToken(getTokenLength()));
        }
        // set created Timestamp
        item.setTimestamp(new Date(System.currentTimeMillis()));

        long id = itemDao.create(item);

/*
        if (!item.isPrivate() && twitter && twitterEnabled() && (twitterUsername != null && twitterPassword != null)) {
            Twitter twitterClient = new Twitter(twitterUsername, twitterPassword);
            twitterClient.setSource("mysticpaste.com - " + clientToken);

            int contentLineCount = item.getContentLineCount();

            StringBuffer sb = new StringBuffer();
            sb.append("#").append(item.getType());
            sb.append(" paste at http://mysticpaste.com/view/");
            sb.append(item.getId());
            sb.append(" posted - ");
            sb.append(contentLineCount).append(" line").append(contentLineCount > 1 ? "s" : "").append(" - can you help?");

            try {
                Status status = twitterClient.updateStatus(sb.toString());
                System.out.println("Successfully updated the status to [" + status.getText() + "].");
            } catch (TwitterException e) { */
/* it's not the end of the world if twitter doesn't update *//*
 }

        }
*/

        return id;
    }


    @Transactional(rollbackFor = Exception.class)
    public long createReplyItem(String clientToken, PasteItem item, long parentId)
            throws InvalidClientException, ParentNotFoundException {
        validateClient(clientToken);
        long id;
        PasteItem parent = itemDao.get(parentId);
        if (null != parent) {
            item.setParent(parent);
            // set created timestamp
            item.setTimestamp(new Date(System.currentTimeMillis()));
            id = itemDao.create(item);
        } else {
            throw new ParentNotFoundException("Parent does not exist");
        }
        return id;
    }

    @Transactional(readOnly = true)
    public List<PasteItem> getItemsForUser(String clientToken, String userToken) throws InvalidClientException {
        validateClient(clientToken);
        return itemDao.findByUser(userToken);
    }

    @Transactional(readOnly = true)
    public long getLatestItemsCount(String clientToken) throws InvalidClientException {
        validateClient(clientToken);
        return itemDao.getPasteCount();
    }

    @Transactional
    public void markAbuse(PasteItem pasteItem) {
        itemDao.markAbuse(pasteItem);
    }

    public List<PasteItem> hasChildren(PasteItem pasteItem) {
        return itemDao.getChildren(pasteItem);
    }

    public void detachItem(PasteItem pasteItem) {
        itemDao.detachItem(pasteItem);
    }

    private void validateClient(String clientToken) throws InvalidClientException {
        // TODO add client validation
    }

    public PasteItemDao getItemDao() {
        return itemDao;
    }

    public void setItemDao(PasteItemDao itemDao) {
        this.itemDao = itemDao;
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
