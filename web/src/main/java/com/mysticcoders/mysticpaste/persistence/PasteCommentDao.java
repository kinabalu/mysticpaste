/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Jul 5, 2009
 * Time: 3:24:12 PM
 */
package com.mysticcoders.mysticpaste.persistence;

import com.mysticcoders.mysticpaste.model.PasteComment;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface PasteCommentDao {

    @Transactional
    public void save(PasteComment comment);

    public List<PasteComment> findByPasteId(Long pasteItemId);

    public int countComments(Long pasteItemId);

}