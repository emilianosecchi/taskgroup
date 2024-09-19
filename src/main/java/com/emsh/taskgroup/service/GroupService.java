package com.emsh.taskgroup.service;

import com.emsh.taskgroup.dto.request.CreateGroupRequest;
import com.emsh.taskgroup.dto.response.GroupResponse;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.*;
import com.emsh.taskgroup.repository.GroupRepository;
import com.emsh.taskgroup.util.StringEncryptor;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Group findGroupById(Long groupId) throws CustomApiException {
        var group = groupRepository.findById(groupId);
        if (group.isEmpty())
            throw new CustomApiException("El grupo no existe.", HttpStatus.BAD_REQUEST);
        return group.get();
    }

    /**
     * Valida que el parámetro recibido pertenezca al enum declarado correspondiente a las categorías
     * @param groupCategoryStr
     * @return GroupCategory
     */
    private GroupCategory validateGroupCategory(String groupCategoryStr) throws CustomApiException {
        try {
            return EnumUtils.findEnumInsensitiveCase(GroupCategory.class, groupCategoryStr);
        } catch (IllegalArgumentException e) {
            throw new CustomApiException("La categoría seleccionada no es válida.", HttpStatus.BAD_REQUEST);
        }
    }

    public Group createGroup(CreateGroupRequest request, User user) throws CustomApiException {
        GroupCategory category = validateGroupCategory(request.getCategory());
        var group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .creationDate(LocalDate.now())
                .build();
        return groupRepository.save(group);
    }

    public void deleteGroup(User user, Long groupId) throws CustomApiException {
        Group group = findGroupById(groupId);
        if (!group.checkIfUserIsAdmin(user.getId()))
            throw new CustomApiException("El usuario no posee los permisos para borrar el grupo.", HttpStatus.FORBIDDEN);
        groupRepository.delete(group);
    }

    public List<GroupResponse> getAllGroupsForUser(User user) throws CustomApiException {
        List<GroupResponse> response = new ArrayList<>();
        for (Group group : user.getAllGroups()) {
            var gr = GroupResponse.mapGroupToDto(group);
            for (UserGroup userGroup : group.getParticipants()) {
                var userDTO = new GroupResponse.UserDTO(
                        userGroup.getUser().getId(),
                        userGroup.getUser().getFirstName(),
                        userGroup.getUser().getLastName()
                );
                gr.getParticipants().add(userDTO);
                if (userGroup.getIsAdmin())
                    gr.getAdmins().add(userDTO);
            }
            response.add(gr);
        }
        return response;
    }

    public GroupResponse joinGroup(Long groupId, Long userId) throws CustomApiException {
        Group group = findGroupById(groupId);
        if (group.checkIfUserIsParticipant(userId)) {
            throw new CustomApiException("Ya formas parte de este grupo.", HttpStatus.BAD_REQUEST);
        }
        var gr = GroupResponse.mapGroupToDto(group);
        gr.setGroupSize(group.getParticipants().size());
        return gr;
    }

}