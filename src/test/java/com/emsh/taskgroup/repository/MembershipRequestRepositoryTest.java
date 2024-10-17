package com.emsh.taskgroup.repository;

import com.emsh.taskgroup.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MembershipRequestRepositoryTest {

    @Autowired
    private MembershipRequestRepository underTest;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldFindAllPendingRequestsForGroup() {
        // given
        var group = Group.builder()
                .name("los amigos")
                .description("este es un grupo de prueba")
                .category(GroupCategory.AMIGOS)
                .creationDate(LocalDate.now())
                .build();
        var user = User.builder()
                .email("user@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .role(Role.USER)
                .build();
        var user2 = User.builder()
                .email("user2@gmail.com")
                .firstName("Joe")
                .lastName("Duff")
                .password("password")
                .role(Role.USER)
                .build();
        groupRepository.save(group);
        userRepository.save(user);
        userRepository.save(user2);
        var pendingRequest = MembershipRequest.builder()
                .group(group)
                .requester(user)
                .status(MembershipRequestStatus.PENDING)
                .build();
        var acceptedRequest = MembershipRequest.builder()
                .group(group)
                .requester(user2)
                .status(MembershipRequestStatus.ACCEPTED)
                .build();

        underTest.save(pendingRequest);
        underTest.save(acceptedRequest);

        // when
        var resultList = underTest.findAllPendingRequestsForGroup(group.getId());

        // then
        assertThat(resultList).isPresent();
        assertThat(resultList.get().size()).isEqualTo(1);

    }

    @Test
    void itShouldNotFindAnyPendingRequestsForGroup() {
        // given
        var group = Group.builder()
                .name("los amigos")
                .description("este es un grupo de prueba")
                .category(GroupCategory.AMIGOS)
                .creationDate(LocalDate.now())
                .build();
        var user = User.builder()
                .email("user@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .role(Role.USER)
                .build();
        groupRepository.save(group);
        userRepository.save(user);
        var rejectedRequest = MembershipRequest.builder()
                .group(group)
                .requester(user)
                .status(MembershipRequestStatus.REJECTED)
                .build();
        underTest.save(rejectedRequest);

        // when
        var resultList = underTest.findAllPendingRequestsForGroup(group.getId());

        // then
        assertThat(resultList.get().isEmpty()).isTrue();
    }

}