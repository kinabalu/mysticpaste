package com.mysticcoders.mysticpaste.model.gae;

import com.google.appengine.api.datastore.Key;
import org.simpleds.annotations.Entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: May 13, 2010
 * Time: 2:05:17 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class SimpleObject implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column
    @GeneratedValue
    private Key id;

    @Column
    private String content;

    public Key getId() {
        return id;
    }

    public void setId(Key id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "SimpleObject{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }
}
