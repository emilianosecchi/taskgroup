package com.emsh.taskgroup.service;

import com.emsh.taskgroup.dto.response.PendingGroupRequestsResponse;
import com.emsh.taskgroup.event.events.MembershipRequestCompletedEvent;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.Group;
import com.emsh.taskgroup.model.MembershipRequest;
import com.emsh.taskgroup.model.MembershipRequestStatus;
import com.emsh.taskgroup.model.User;
import com.emsh.taskgroup.repository.MembershipRequestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MembershipRequestService {

    private final MembershipRequestRepository membershipRequestRepository;
    private final GroupService groupService;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public MembershipRequestService(MembershipRequestRepository membershipRequestRepository, GroupService groupService, UserService userService, ApplicationEventPublisher applicationEventPublisher) {
        this.membershipRequestRepository = membershipRequestRepository;
        this.groupService = groupService;
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public MembershipRequest findMembershipRequestById(Long membershipRequestId) throws CustomApiException {
        var mRequest = membershipRequestRepository.findById(membershipRequestId);
        if (mRequest.isEmpty())
            throw new CustomApiException("La solicitud no existe.", HttpStatus.BAD_REQUEST);
        return mRequest.get();
    }

    public void createRequest(Long userId, String encryptedGroupId) throws CustomApiException {
        Long groupId = groupService.decryptGroupId(encryptedGroupId);
        if (requestAlreadyExists(userId, groupId))
            throw new CustomApiException("Ya existe una solicitud pendiente para unirte a este grupo", HttpStatus.CONFLICT);

        Group group = groupService.findGroupById(groupId);
        if (group.checkIfUserIsParticipant(userId))
            throw new CustomApiException("El usuario ya es participante del grupo.", HttpStatus.BAD_REQUEST);

        User user = userService.findUserById(userId);

        var membershipRequest = MembershipRequest.builder()
                .requester(user)
                .status(MembershipRequestStatus.PENDING)
                .group(group)
                .build();

        membershipRequestRepository.save(membershipRequest);
    }

    private MembershipRequest manageRequest(Long groupAdminId, Long membershipRequestId, MembershipRequestStatus status) throws CustomApiException {
        MembershipRequest membershipRequest = findMembershipRequestById(membershipRequestId);
        if (!membershipRequest.getStatus().equals(MembershipRequestStatus.PENDING))
            throw new CustomApiException("La solicitud ya se encuentra " + membershipRequest.getStatus().name().toLowerCase(), HttpStatus.BAD_REQUEST);
        if (!membershipRequest.getGroup().checkIfUserIsAdmin(groupAdminId))
            throw new CustomApiException("El usuario no posee los permisos para realizar la acción.", HttpStatus.FORBIDDEN);
        membershipRequest.setStatus(status);
        return membershipRequestRepository.save(membershipRequest);
    }

    @Transactional
    public void acceptRequest(Long groupAdminId, Long membershipRequestId) throws CustomApiException {
        MembershipRequest request = manageRequest(groupAdminId, membershipRequestId, MembershipRequestStatus.ACCEPTED);
        groupService.addParticipant(request.getRequester(), request.getGroup(), false);
        applicationEventPublisher.publishEvent(
                new MembershipRequestCompletedEvent(this, request.getGroup(), request.getRequester(), MembershipRequestStatus.ACCEPTED)
        );
    }

    public void rejectRequest(Long groupAdminId, Long membershipRequestId) throws CustomApiException {
        MembershipRequest request = manageRequest(groupAdminId, membershipRequestId, MembershipRequestStatus.REJECTED);
        applicationEventPublisher.publishEvent(
                new MembershipRequestCompletedEvent(this, request.getGroup(), request.getRequester(), MembershipRequestStatus.REJECTED)
        );
    }

    public boolean requestAlreadyExists(Long userId, Long groupId) {
        return membershipRequestRepository.requestAlreadyExists(userId, groupId);
    }

    public PendingGroupRequestsResponse findAllPendingRequestForGroup(Long groupId) {
        var optPendingRequests = membershipRequestRepository.findAllPendingRequestsForGroup(groupId);
        if (optPendingRequests.isPresent()) {
            return new PendingGroupRequestsResponse(optPendingRequests.get());
        } else
            return new PendingGroupRequestsResponse(new ArrayList<>());
    }

}
