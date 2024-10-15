package com.emsh.taskgroup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSubElement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Task task;
    private String description;
    private Boolean done;
}
