package com.emsh.taskgroup.service;

import com.emsh.taskgroup.dto.request.CreateGroupRequest;
import com.emsh.taskgroup.dto.response.GroupResponse;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.*;
import com.emsh.taskgroup.repository.GroupRepository;
import com.emsh.taskgroup.repository.UserGroupRepository;
import com.emsh.taskgroup.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public GroupResponse createGroup(CreateGroupRequest request) throws CustomApiException {
        var user = userRepository.findById(request.getUserId());
        if (user.isEmpty())
            throw new CustomApiException("El usuario que desea realizar la acción no existe.", HttpStatus.BAD_REQUEST);

        GroupCategory category;
        try {
            category = EnumUtils.findEnumInsensitiveCase(GroupCategory.class, request.getCategory());
        } catch (IllegalArgumentException e) {
            throw new CustomApiException("La categoría seleccionada no es válida.", HttpStatus.BAD_REQUEST);
        }

        var group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .creationDate(LocalDate.now())
                .build();
        groupRepository.save(group);
        var userGroup = UserGroup.builder()
                .id(
                    new UserGroupId(user.get().getId(), group.getId())
                )
                .user(user.get())
                .group(group)
                .isAdmin(true)
                .build();
        userGroupRepository.save(userGroup);
        var groupCreator = new GroupResponse.UserDTO(
                user.get().getId(),
                user.get().getFirstName(),
                user.get().getLastName()
        );
        var gr = GroupResponse.mapGroupToDto(group);
        gr.getParticipants().add(groupCreator);
        gr.getAdmins().add(groupCreator);
        return gr;
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

    public List<GroupResponse> getAllGroupsForUser(Long userId) throws CustomApiException {
        var user = userRepository.findById(userId);
        if (user.isEmpty())
            throw new CustomApiException("El usuario que desea realizar la acción no existe", HttpStatus.BAD_REQUEST);

        List<GroupResponse> response = new ArrayList<>();

        for (Group group : user.get().getAllGroups()) {

            var gr = GroupResponse.mapGroupToDto(group);

            group.getParticipants().forEach(
                    userGroup -> {
                         var userDTO = new GroupResponse.UserDTO(
                                 userGroup.getUser().getId(),
                                 userGroup.getUser().getFirstName(),
                                 userGroup.getUser().getLastName()
                         );
                         gr.getParticipants().add(userDTO);
                         if (userGroup.getIsAdmin())
                             gr.getAdmins().add(userDTO);
                    }
            );
            response.add(gr);
        }

        return response;
    }

}
