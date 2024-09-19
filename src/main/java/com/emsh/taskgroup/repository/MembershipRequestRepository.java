package com.emsh.taskgroup.repository;

import com.emsh.taskgroup.model.MembershipRequest;
import com.emsh.taskgroup.model.MembershipRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public interface MembershipRequestRepository extends JpaRepository<MembershipRequest, Long> {

    Optional<MembershipRequest> findByRequesterIdAndGroupIdAndStatusNot(Long userId, Long GroupId, MembershipRequestStatus status);

    Optional<List<MembershipRequest>> findByGroupIdAndStatusEquals(Long groupId, MembershipRequestStatus status);

    void deleteByRequesterIdAndGroupId(Long userId, Long GroupId);

    default Optional<List<MembershipRequest>> findAllPendingRequestsForGroup(Long groupId) {
        return findByGroupIdAndStatusEquals(groupId, MembershipRequestStatus.PENDING);
    }

    default Boolean requestAlreadyExists(Long userId, Long GroupId) {
        return findByRequesterIdAndGroupIdAndStatusNot(userId, GroupId, MembershipRequestStatus.REJECTED).isPresent();
    }


}
