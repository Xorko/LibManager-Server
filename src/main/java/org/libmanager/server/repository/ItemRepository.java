package org.libmanager.server.repository;

import org.libmanager.server.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT SUM(e.totalCopies) FROM Item e")
    Long sumTotalCopies();

}
