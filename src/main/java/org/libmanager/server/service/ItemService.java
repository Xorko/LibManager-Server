package org.libmanager.server.service;

import java.util.Optional;

import org.libmanager.server.entity.Item;
import org.libmanager.server.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    /**
     * Delete an item
     * @param id    The id of the item
     * @return      True if the item was found and deleted, false otherwise
     */
    public boolean delete(long id) {
        Optional<Item> foundItem = itemRepository.findById(id);
        if (foundItem.isEmpty())
            return false;
        Item itemToDelete = foundItem.get();
        itemRepository.delete(itemToDelete);
        return true;
    }

}
