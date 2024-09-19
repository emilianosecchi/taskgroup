package com.emsh.taskgroup.repository;

import com.emsh.taskgroup.model.UserGroup;
import com.emsh.taskgroup.model.UserGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {

    void deleteById_UserIdAndId_GroupId(Long userId, Long groupId);

}