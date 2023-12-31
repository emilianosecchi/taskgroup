package com.emsh.taskgroup.service;

import com.emsh.taskgroup.dto.request.CreateGroupRequest;
import com.emsh.taskgroup.dto.response.GroupResponse;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.*;
import com.emsh.taskgroup.repository.GroupRepository;
import com.emsh.taskgroup.repository.UserGroupRepository;
import com.emsh.taskgroup.repository.UserRepository;
import com.emsh.taskgroup.util.StringEncryptor;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Long.parseLong;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserService userService;
    private final UserGroupRepository userGroupRepository;

    private final StringEncryptor stringEncryptor;

    @Autowired
    public GroupService(GroupRepository groupRepository, UserService userService, UserGroupRepository userGroupRepository, StringEncryptor stringEncryptor) {
        this.groupRepository = groupRepository;
        this.userService = userService;
        this.userGroupRepository = userGroupRepository;
        this.stringEncryptor = stringEncryptor;
    }

    public Group findGroupById(Long groupId) throws CustomApiException {
        var group = groupRepository.findById(groupId);
        if (group.isEmpty())
            throw new CustomApiException("El grupo no existe.", HttpStatus.BAD_REQUEST);
        return group.get();
    }

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) throws CustomApiException {

        User user = userService.findUserById(request.getUserId());

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
        addParticipant(user, group, true);
        var groupCreator = new GroupResponse.UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName()
        );
        var gr = GroupResponse.mapGroupToDto(group);
        gr.getParticipants().add(groupCreator);
        gr.getAdmins().add(groupCreator);
        return gr;
    }

    /**
     * Elimina el grupo
     * @param userId: id del User que desea borrar el grupo.
     * @param groupId: id del Group que se desea borrar.
     * @throws CustomApiException
     */
    public void deleteGroup(Long userId, Long groupId) throws CustomApiException {

        Group group = findGroupById(groupId);

        userService.findUserById(userId);

        if (!group.checkIfUserIsAdmin(userId))
            throw new CustomApiException("El usuario no posee los permisos para borrar el grupo.", HttpStatus.FORBIDDEN);

        groupRepository.delete(group);
    }

    public List<GroupResponse> getAllGroupsForUser(Long userId) throws CustomApiException {
        User user = userService.findUserById(userId);
        List<GroupResponse> response = new ArrayList<>();
        for (Group group : user.getAllGroups()) {

            var gr = GroupResponse.mapGroupToDto(group);

            group.getParticipants().forEach(
                userGroup -> {
                     var userDTO = new GroupResponse.UserDTO(
                             userGroup.getUser().getId(),
                             userGroup.getUser().getFirstName(),
                             userGroup.getUser().getLastName()
                     );
                     gr.getParticipants().add(userDTO);
                     if (userGroup.getIsAdmin()) {
                         gr.getAdmins().add(userDTO);
                         try {
                             gr.setInvitationLink(stringEncryptor.encrypt(group.getId().toString()));
                         } catch (Exception ignored) {
                             ;
                         }
                     }
                }
            );
            response.add(gr);
        }

        return response;
    }

    /**
     * Agrega un nuevo participante al grupo.
     * @param user: User que se desea agregar como participante al grupo.
     * @param group: Group al que se desea agregar el participante.
     * @param isAdmin: true si el usuario a agregar va a ser administrador del grupo, false caso contrario.
     */
    public void addParticipant(User user, Group group, Boolean isAdmin)  {
        var userGroup = UserGroup.builder()
                .id(new UserGroupId(user.getId(), group.getId()))
                .user(user)
                .group(group)
                .isAdmin(isAdmin)
                .build();
        userGroupRepository.save(userGroup);
    }

    public GroupResponse joinGroup(String encryptedGroupId, Long userId) throws CustomApiException {
        Long groupId = decryptGroupId(encryptedGroupId);

        Group group = findGroupById(groupId);

        if (group.checkIfUserIsParticipant(userId)) {
            throw new CustomApiException("Ya formas parte de este grupo.", HttpStatus.BAD_REQUEST);
        }

        var gr = GroupResponse.mapGroupToDto(group);
        gr.setGroupSize(group.getParticipants().size());
        return gr;
    }

    public Long decryptGroupId(String encryptedGroupId) throws CustomApiException {
        String groupId;
        try {
            groupId = stringEncryptor.decrypt(encryptedGroupId);
        } catch (Exception e) {
            throw new CustomApiException("Hubo un error al procesar la solicitud de ingreso al grupo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (groupId.isBlank() || groupId.isEmpty()) {
            throw new CustomApiException("El grupo al que desea unirse no es válido.", HttpStatus.BAD_REQUEST);
        }
        return parseLong(groupId);
    }

}
