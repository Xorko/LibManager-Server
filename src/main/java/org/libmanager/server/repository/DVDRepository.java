package org.libmanager.server.repository;

import org.libmanager.server.entity.DVD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DVDRepository extends JpaRepository<DVD, Long>, JpaSpecificationExecutor<DVD> {

}
