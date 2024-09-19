package com.emsh.taskgroup.service;

import com.emsh.taskgroup.dto.request.CreateGroupRequest;
import com.emsh.taskgroup.dto.response.GroupResponse;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.Group;
import com.emsh.taskgroup.model.MembershipRequest;
import com.emsh.taskgroup.model.User;
import com.emsh.taskgroup.util.StringEncryptor;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.Long.parseLong;

@Service
public class GroupManagementService {

    private final GroupService groupService;
    private final UserGroupService userGroupService;
    private final MembershipRequestService membershipRequestService;
    private final UserService userService;
    private final StringEncryptor stringEncryptor;

    @Autowired
    public GroupManagementService(GroupService groupService, UserGroupService userGroupService, MembershipRequestService membershipRequestService, UserService userService, StringEncryptor stringEncryptor) {
        this.groupService = groupService;
        this.userGroupService = userGroupService;
        this.membershipRequestService = membershipRequestService;
        this.userService = userService;
        this.stringEncryptor = stringEncryptor;
    }

    public List<GroupResponse> getAllGroupsForUser(Long userId) throws CustomApiException {
        User user = userService.findUserById(userId);
        List<GroupResponse> response = groupService.getAllGroupsForUser(user);
        for (GroupResponse groupResponse : response) {
            groupResponse.setGroupIdHash(this.generateGroupIdHash(groupResponse.getId()));
        }
        return response;
    }

    public void deleteGroup(Long userId, Long groupId) throws CustomApiException {
        User user = userService.findUserById(userId);
        groupService.deleteGroup(user, groupId);
    }

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) throws CustomApiException {
        User user = userService.findUserById(request.getUserId());
        Group group = groupService.createGroup(request, user);
        userGroupService.addParticipantToGroup(user, group, true);
        var groupCreator = new GroupResponse.UserDTO(user.getId(), user.getFirstName(), user.getLastName());
        var gr = GroupResponse.mapGroupToDto(group);
        gr.getParticipants().add(groupCreator);
        gr.getAdmins().add(groupCreator);
        gr.setGroupIdHash(generateGroupIdHash(group.getId()));
        return gr;
    }

    public void createMembershipRequest(Long userId, Long groupId) throws CustomApiException {
        Group group = groupService.findGroupById(groupId);
        if (group.checkIfUserIsParticipant(userId))
            throw new CustomApiException("El usuario ya es participante del grupo.", HttpStatus.BAD_REQUEST);
        if (membershipRequestService.requestAlreadyExists(userId, groupId))
            throw new CustomApiException("Ya existe una solicitud pendiente para unirte a este grupo", HttpStatus.CONFLICT);
        User user = userService.findUserById(userId);
        membershipRequestService.createRequest(user, group);
    }

    @Transactional
    public void acceptMembershipRequest(Long groupAdminId, Long membershipRequestId) throws CustomApiException {
        MembershipRequest membershipRequest = membershipRequestService.acceptRequest(groupAdminId, membershipRequestId);
        userGroupService.addParticipantToGroup(membershipRequest.getRequester(), membershipRequest.getGroup(), false);
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

    public String generateGroupIdHash(Long groupId) throws CustomApiException {
        try {
            return stringEncryptor.encrypt(groupId.toString());
        } catch (Exception e) {
            throw new CustomApiException("Hubo un error al generar el código de invitación al grupo", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
