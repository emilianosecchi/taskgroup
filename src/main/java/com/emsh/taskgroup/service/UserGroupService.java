package com.emsh.taskgroup.service;

import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.Group;
import com.emsh.taskgroup.model.User;
import com.emsh.taskgroup.model.UserGroup;
import com.emsh.taskgroup.model.UserGroupId;
import com.emsh.taskgroup.repository.UserGroupRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;

    @Autowired
    public UserGroupService(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    /**
     * Agrega un nuevo participante a un grupo.
     * @param user: User que se desea agregar como participante al grupo.
     * @param group: Group al que se desea agregar el participante.
     * @param isAdmin: true si el usuario a agregar va a ser administrador del grupo, false caso contrario.
     */
    public void addParticipantToGroup(User user, Group group, Boolean isAdmin)  {
        var userGroup = UserGroup.builder()
                .id(new UserGroupId(user.getId(), group.getId()))
                .user(user)
                .group(group)
                .isAdmin(isAdmin)
                .build();
        userGroupRepository.save(userGroup);
    }

    @Transactional
    public void removeParticipantFromGroup(Long groupId, Long userId) throws CustomApiException {
        userGroupRepository.deleteById_UserIdAndId_GroupId(userId, groupId);
    }

}
