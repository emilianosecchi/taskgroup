package com.emsh.taskgroup.service;

import com.emsh.taskgroup.dto.request.CreateGroupRequest;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.Group;
import com.emsh.taskgroup.model.User;
import com.emsh.taskgroup.model.UserGroup;
import com.emsh.taskgroup.model.UserGroupId;
import com.emsh.taskgroup.repository.GroupRepository;
import com.emsh.taskgroup.repository.UserGroupRepository;
import com.emsh.taskgroup.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository, UserGroupRepository userGroupRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.userGroupRepository = userGroupRepository;
    }

    @Transactional
    public void createGroup(CreateGroupRequest request) throws CustomApiException {
        Optional<User> user = userRepository.findById(request.getUserId());
        if (user.isEmpty())
            throw new CustomApiException("El usuario que desea realizar la acción no existe.", HttpStatus.BAD_REQUEST);
        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .creationDate(LocalDate.now())
                .build();
        groupRepository.save(group);
        UserGroup userGroup = UserGroup.builder()
                .id(
                    new UserGroupId(user.get().getId(), group.getId())
                )
                .user(user.get())
                .group(group)
                .isAdmin(true)
                .build();
        userGroupRepository.save(userGroup);
    }

    public void deleteGroup(Long userId, Long groupId) throws CustomApiException {
        var group = groupRepository.findById(groupId);
        if (group.isEmpty())
            throw new CustomApiException("El grupo que se desea borrar no existe.", HttpStatus.BAD_REQUEST);

        if (!userRepository.existsById(userId))
            throw new CustomApiException("El usuario que desea realizar la acción no existe.", HttpStatus.BAD_REQUEST);

        if (!group.get().checkIfUserIsAdmin(userId))
            throw new CustomApiException("El usuario no posee los permisos para borrar el grupo.", HttpStatus.FORBIDDEN);

        groupRepository.delete(group.get());

    }

}
