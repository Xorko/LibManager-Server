package org.libmanager.server.service;

public interface ItemService {

    /**
     * Delete an item
     * @param id    The id of the item
     * @return      True if the item was found and deleted, false otherwise
     */
    boolean delete(long id);

}
