package com.mysticcoders.mysticpaste.model;

import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
@Entity
@Table(name = "PASTE_ITEMS")
@NamedQueries({@NamedQuery(name = "item.getById",
//                query = "from PasteItem item where item.id = :id"),
                query = "from PasteItem item left join fetch item.parent where item.id = :id"),
        @NamedQuery(name = "item.find",
                query = "from PasteItem item where item.isPrivate <> true AND item.abuseFlag <> true and item.content is not null order by item.timestamp desc"),
        @NamedQuery(name = "item.findThreaded",
                query = "from PasteItem item where item.isPrivate <> true AND item.abuseFlag <> true and item.content is not null and item.parent is null order by item.timestamp desc"),
        @NamedQuery(name = "item.findByLanguage",
                query = "from PasteItem item where item.isPrivate <> true AND item.abuseFlag <> true and item.content is not null and item.type = :type order by item.timestamp desc"),
        @NamedQuery(name = "item.findByLanguageThreaded",
                query = "from PasteItem item where item.isPrivate <> true AND item.abuseFlag <> true and item.content is not null and item.parent is null and item.type = :type order by item.timestamp desc"),
        @NamedQuery(name = "item.findByToken",
                query = "from PasteItem item where item.privateToken = :token"),
        @NamedQuery(name = "item.findByUser",
                query = "from PasteItem item where item.isPrivate <> true AND item.abuseFlag <> true and item.content is not null and item.userToken = :token"),
        @NamedQuery(name = "item.count",
                query = "select count(item) from PasteItem item where item.isPrivate <> true AND item.abuseFlag <> true"),
        @NamedQuery(name = "item.children",
                query = "from PasteItem item where item.parent = :pasteItem")
})
public class PasteItem implements Serializable {
    private static final long serialVersionUID = -6467870857777145137L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ITEM_ID")
    protected long id;

    @Lob
    @Column(name = "CONTENT")
    protected String content;

    @Lob
    @Column(name = "HTML_CONTENT")
    protected String formatedContent;

    @Basic
    @Column(name = "LANG_TYPE_CD")
    protected String type;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TS")
    protected Date timestamp;

    @Basic
    @Column(name = "USER_TOKEN")
    protected String userToken;


    @Basic
    @Column(name = "ABUSE_FLAG")
    protected boolean abuseFlag;

//    @Basic
//    @Column(name = "CLIENT_TOKEN")
//    protected String clientToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID")
    protected Client client;

    @Basic
    @Column(name = "PRIVATE_FLG")
    protected boolean isPrivate;

    @Basic
    @Column(name = "PRIVATE_TOKEN", unique = true, updatable = false)
    protected String privateToken;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "PARENT_ITEM_ID", nullable = true)
    protected PasteItem parent;

    @Transient
    protected int viewCount;

    @Basic
    @Column(name = "CLIENT_IP")
    protected String clientIp;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormatedContent() {
        return formatedContent;
    }

    public void setFormatedContent(String formatedContent) {
        this.formatedContent = formatedContent;
    }

    /**
     * Calculate up to 5 lines for a preview and return it to end user
     *
     * @return up to 5 lines of paste, or empty string
     */
    public String getPreviewContent() {
        if(getContent() == null || getContent().length() == 0) return "";

        String[] contentLines = getContent().split("\n");

        if(contentLines==null) return "";

        StringBuffer previewContent = new StringBuffer();

        for(int i=0; i< (contentLines.length < 5 ? contentLines.length : 5); i++) {
            previewContent.append(contentLines[i]).append("\n");
        }
        return previewContent.toString();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

//    public String getClientToken() {
//        return clientToken;
//    }
//
//    public void setClientToken(String clientToken) {
//        this.clientToken = clientToken;
//    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public boolean isAbuseFlag() {
        return abuseFlag;
    }

    public void markAbuse() {
        this.abuseFlag = true;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getPrivateToken() {
        return privateToken;
    }

    public void setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
    }

    public PasteItem getParent() {
        return parent;
    }

    public void setParent(PasteItem parent) {
        this.parent = parent;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public int getContentLineCount() {
        if(getContent() == null || getContent().length() == 0) return 0;

        String[] lines = getContent().split("\n");
        return lines != null ? lines.length : 0;
    }

    /**
     * Return a simplistic diff view of 2 pastes
     *
     * @param originalPaste
     * @param revisedPaste
     * @return
     */
    public static Object[] diffPastes(String originalPaste, String revisedPaste) {
        int new_line_count = 0;
        int old_line_count = 0;

        List<String> original = Arrays.asList(originalPaste.split("\n"));
        List<String> revised = Arrays.asList(revisedPaste.split("\n"));

        List<Integer> changedLines = new ArrayList<Integer>();

        int lineIndex = 0;

        StringBuilder diffText = new StringBuilder();
        List<Difference> diffs  = new Diff<String>(original, revised).diff();
        for (Difference diff : diffs) {
            int        del_start = diff.getDeletedStart();
            int        del_end   = diff.getDeletedEnd();
            int        add_start = diff.getAddedStart();
            int        add_end   = diff.getAddedEnd();

            if (del_end != Difference.NONE) {
                for (; old_line_count < del_start; ++old_line_count, ++new_line_count) {
                    diffText.append(original.get(old_line_count)).append("\n");
                    lineIndex++;
                }
                for (; old_line_count <= del_end; ++old_line_count) {
                    diffText.append("- ").append(original.get(old_line_count)).append("\n");
                    changedLines.add(lineIndex);
                    lineIndex++;
                }
            }

            if (add_end != Difference.NONE) {
                for (; new_line_count < add_start; ++new_line_count, ++old_line_count) {
                    diffText.append(revised.get(new_line_count)).append("\n");
                    lineIndex++;
                }

                for (; new_line_count <= add_end; ++new_line_count) {
                    diffText.append("+ ").append(revised.get(new_line_count)).append("\n");
                    changedLines.add(lineIndex);
                    lineIndex++;
                }

            }
        }

        for (; new_line_count < revised.size(); ++new_line_count, ++old_line_count) {
            diffText.append(revised.get(new_line_count));
        }

        return new Object[]{changedLines, diffText.toString()};        // TODO this is UGGGGGLY

    }
}
