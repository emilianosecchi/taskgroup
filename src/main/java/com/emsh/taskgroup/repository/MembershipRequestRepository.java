package com.emsh.taskgroup.repository;

import com.emsh.taskgroup.model.MembershipRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRequestRepository extends JpaRepository<MembershipRequest, Long> {
}
