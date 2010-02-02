/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Jul 5, 2009
 * Time: 3:25:33 PM
 */
package com.mysticcoders.mysticpaste.persistence.hibernate;

import com.mysticcoders.mysticpaste.model.PasteComment;
import com.mysticcoders.mysticpaste.persistence.PasteCommentDao;

import java.util.List;

public class PasteCommentDaoImpl extends AbstractDaoHibernate<PasteComment> implements PasteCommentDao {

    public PasteCommentDaoImpl() {
        super(PasteComment.class);
    }

    @SuppressWarnings("unchecked")
    public int countComments(Long pasteItemId) {

        Long count = (Long) getSession().getNamedQuery("comment.countByItemId")
                .setLong("id", pasteItemId)
                .iterate()
                .next();

        return count.intValue();
    }

    @SuppressWarnings("unchecked")
    public List<PasteComment> findByPasteId(Long pasteItemId) {
        return (List<PasteComment>) getSession().getNamedQuery("comment.getByItemId")
                .setLong("id", pasteItemId)
                .list();
    }
}