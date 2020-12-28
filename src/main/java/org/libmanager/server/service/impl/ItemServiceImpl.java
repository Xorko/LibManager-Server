package org.libmanager.server.service.impl;

import java.util.Optional;

import org.libmanager.server.entity.Item;
import org.libmanager.server.repository.ItemRepository;
import org.libmanager.server.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    /**
     * {@inheritDoc}
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
