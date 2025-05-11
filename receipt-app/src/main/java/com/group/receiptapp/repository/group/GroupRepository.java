package com.group.receiptapp.repository.group;
import com.group.receiptapp.domain.group.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByName(String name);

    Optional<Group> findByNameAndPreventDuplicateReceipt(String name, Boolean preventDuplicateReceipt);

}