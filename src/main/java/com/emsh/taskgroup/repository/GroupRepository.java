package com.emsh.taskgroup.repository;

import com.emsh.taskgroup.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
