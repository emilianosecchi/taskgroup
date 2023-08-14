package com.emsh.taskgroup.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class UserGroupId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private long userId;
    @Column(name = "group_id", nullable = false)
    private long groupId;

}
