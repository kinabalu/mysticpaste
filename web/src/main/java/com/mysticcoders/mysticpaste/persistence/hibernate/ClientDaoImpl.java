package com.mysticcoders.mysticpaste.persistence.hibernate;

import com.mysticcoders.mysticpaste.model.Client;
import com.mysticcoders.mysticpaste.persistence.ClientDao;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
public class ClientDaoImpl extends AbstractDaoHibernate<Client> implements ClientDao {
    protected ClientDaoImpl(Class dataClass) {
        super(dataClass);
    }

    public Client get(long id) {
        return (Client) getSession().getNamedQuery("client.id").setLong("id", id).setMaxResults(1).uniqueResult();
    }

    public Client findByToken(String clientToken) {
        return (Client) getSession().getNamedQuery("client.findByToken").setString("token", clientToken)
                .setMaxResults(1).uniqueResult();
    }
}
