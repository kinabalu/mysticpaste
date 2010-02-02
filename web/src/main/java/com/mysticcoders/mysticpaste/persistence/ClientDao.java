package com.mysticcoders.mysticpaste.persistence;

import com.mysticcoders.mysticpaste.model.Client;

/**
 * @author <a href="mailto:gcastro@mysticcoders.com">Guillermo Castro</a>
 * @version $Revision$ $Date$
 */
public interface ClientDao {

    Client get(long id);

    Client findByToken(String clientToken);
}
