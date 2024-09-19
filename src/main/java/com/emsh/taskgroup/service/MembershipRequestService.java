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
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public MembershipRequestService(MembershipRequestRepository membershipRequestRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.membershipRequestRepository = membershipRequestRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public MembershipRequest findMembershipRequestById(Long membershipRequestId) throws CustomApiException {
        var mRequest = membershipRequestRepository.findById(membershipRequestId);
        if (mRequest.isEmpty())
            throw new CustomApiException("La solicitud no existe.", HttpStatus.BAD_REQUEST);
        return mRequest.get();
    }

    public void createRequest(User user, Group group) throws CustomApiException {
        var membershipRequest = MembershipRequest.builder()
                .requester(user)
                .status(MembershipRequestStatus.PENDING)
                .group(group)
                .build();
        membershipRequestRepository.save(membershipRequest);
    }

    /**
     * Cuando un usuario abandona un grupo también se debe eliminar la solicitud de membresía creada cuando se unió
     * por primera vez al grupo.
     * @param userId
     * @param groupId
     */
    public void deleteRequest(Long userId, Long groupId) {
        membershipRequestRepository.deleteByRequesterIdAndGroupId(userId, groupId);
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

    public MembershipRequest acceptRequest(Long groupAdminId, Long membershipRequestId) throws CustomApiException {
        MembershipRequest request = manageRequest(groupAdminId, membershipRequestId, MembershipRequestStatus.ACCEPTED);
        applicationEventPublisher.publishEvent(
                new MembershipRequestCompletedEvent(this, request.getGroup(), request.getRequester(), MembershipRequestStatus.ACCEPTED)
        );
        return request;
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
