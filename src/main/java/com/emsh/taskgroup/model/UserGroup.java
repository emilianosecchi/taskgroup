package com.emsh.taskgroup.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class UserGroup {

    @EmbeddedId
    private UserGroupId id;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false)
    @MapsId("user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", updatable = false)
    @MapsId("group_id")
    private Group group;

    private Boolean isAdmin;

}
