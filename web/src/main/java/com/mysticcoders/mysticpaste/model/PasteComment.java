package com.mysticcoders.mysticpaste.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:andrew@mysticcoders.com">Andrew Lombardi</a>
 * @version $Revision$ $Date$
 */
@Entity
/*
@Table(name = "PASTE_COMMENTS")
@NamedQueries({@NamedQuery(name = "comment.getById",
                query = "from PasteComment comment where comment.id = :id"),
        @NamedQuery(name = "comment.getByItemId",
                query = "from PasteComment comment where comment.item.id = :id"),
        @NamedQuery(name = "comment.countByItemId",
                query = "select count(comment) from PasteComment comment where comment.item.id = :id")
})
*/
public class PasteComment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "COMMENT_ID")
    protected long id;

    @Lob
    @Column(name = "NAME")
    protected String name;

    @Lob
    @Column(name = "EMAIL")
    protected String email;

    @Basic
    @Column(name = "IP_ADDRESS")
    protected String ipAddress;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TS")
    protected Date timestamp;

    @Basic
    @Column(name = "comment")
    protected String comment;

    @Basic
    @Column(name = "ABUSE_FLAG")
    protected boolean abuseFlag;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "ITEM_ID", nullable = true)
    protected PasteItem item;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isAbuseFlag() {
        return abuseFlag;
    }

    public void setAbuseFlag(boolean abuseFlag) {
        this.abuseFlag = abuseFlag;
    }

    public PasteItem getItem() {
        return item;
    }

    public void setItem(PasteItem item) {
        this.item = item;
    }
}
