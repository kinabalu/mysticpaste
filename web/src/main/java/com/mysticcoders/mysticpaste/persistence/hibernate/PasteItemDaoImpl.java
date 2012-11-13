package com.mysticcoders.mysticpaste.persistence.hibernate;

import com.mysticcoders.mysticpaste.model.PasteItem;
import com.mysticcoders.mysticpaste.persistence.PasteItemDao;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
//@Repository("pasteItemDao")
public class PasteItemDaoImpl extends AbstractDaoHibernate<PasteItem> implements PasteItemDao {


    protected PasteItemDaoImpl() {
        super(PasteItem.class);
    }

    public String create(PasteItem item) {
        return null;
//        save(item);
//        return item.getItemId();
    }

    public void update(PasteItem item) {
        save(item);
    }

    public PasteItem get(String id) {
        return null;
//        return (PasteItem) getEntityManager().createNamedQuery("item.getById")
//                .setParameter("id", id).setMaxResults(1)
//                .getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<PasteItem> find(int count, int startIndex) {
        return getEntityManager().createNamedQuery("item.find")
                .setMaxResults(count).setFirstResult(startIndex).getResultList();
    }

    public PasteItem findByToken(String privateToken) {
        return (PasteItem) getEntityManager()
                .createNamedQuery("item.findByToken")
                .setParameter("token", privateToken)
                .getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<PasteItem> findByUser(String userToken) {
        return getEntityManager()
                .createNamedQuery("item.findByUser")
                .setParameter("token", userToken).getResultList();
    }


    public long count() {
        Integer count = getEntityManager()
                .createNamedQuery("item.count")
                .getFirstResult();

        System.out.println("pasteCount:"+count);
        return null == count ? 0 : count;
    }


    public void markAbuse(PasteItem pasteItem) {
        PasteItem item = get(pasteItem.getItemId());
        item.markAbuse();
        save(item);
    }

    public List<PasteItem> getChildren(PasteItem pasteItem) {
        return null;
//        return (List<PasteItem>)getEntityManager()
//        .createNamedQuery("item.children")
//                .setParameter("pasteItem", pasteItem)
//                .getResultList();
//        return new ArrayList<PasteItem>();
    }

}
