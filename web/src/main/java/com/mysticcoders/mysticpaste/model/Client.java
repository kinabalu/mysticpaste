package com.mysticcoders.mysticpaste.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
@Entity

@Table(name = "CLIENTS")
@NamedQueries({@NamedQuery(name = "client.id", query = "from Client c where c.id = :id"),
        @NamedQuery(name = "client.findByToken", query = "from Client c where c.token = :token")})
public class Client implements Serializable {
    private static final long serialVersionUID = -7477330789867279559L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CLIENT_ID")
    private int id;

    @Basic
    @Column(name = "CLIENT_TOKEN")
    private String token;

    @Basic
    @Column(name = "DESCR", nullable = true)
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATE_TS")
    private Date created;

    @Basic
    @Column(name = "VALID_FLG")
    private boolean isValid;

    public Client() {
    }

    public Client(String token) {
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
