package com.mysticcoders.mysticpaste.persistence.hibernate;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
public class PasteItemDaoImpl extends AbstractDaoHibernate<PasteItem> implements PasteItemDao {


    protected PasteItemDaoImpl() {
        super(PasteItem.class);
    }

    public Long create(PasteItem item) {
        save(item);
        return item.getId();
    }

    public void update(PasteItem item) {
        save(item);
    }

    public PasteItem get(long id) {
        return (PasteItem) getSession().getNamedQuery("item.getById")
                .setLong("id", id).setMaxResults(1)
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<PasteItem> find(int count, int startIndex) {
        return getSession().getNamedQuery("item.find")
                .setMaxResults(count).setFirstResult(startIndex).list();
    }

    @SuppressWarnings("unchecked")
    public List<PasteItem> findThreaded(int count, int startIndex) {
        return getSession()
                .getNamedQuery("item.findThreaded")
                .setMaxResults(count).setFirstResult(startIndex).list();
    }

    public PasteItem findByToken(String privateToken) {
        return (PasteItem) getSession()
                .getNamedQuery("item.findByToken")
                .setParameter("token", privateToken)
                .uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<PasteItem> findByUser(String userToken) {
        return getSession()
                .getNamedQuery("item.findByUser")
                .setParameter("token", userToken).list();
    }


    public long getPasteCount() {
        Long count = (Long) getSession()
                .getNamedQuery("item.count")
                .iterate().next();

        return null == count ? 0 : count;
    }


    public void markAbuse(PasteItem pasteItem) {
        PasteItem item = get(pasteItem.getId());
        item.markAbuse();
        save(item);
    }

    public List<PasteItem> getChildren(PasteItem pasteItem) {
        System.out.println("getChildren(PasteItem pasteItem)");
        return (List<PasteItem>)getSession()
        .getNamedQuery("item.children")
                .setParameter("pasteItem", pasteItem)
                .list();
//        return new ArrayList<PasteItem>();
    }

    public void detachItem(PasteItem pasteItem) {
        getSession().evict(pasteItem);
    }

}
