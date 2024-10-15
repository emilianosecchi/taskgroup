package com.emsh.taskgroup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "creator_user_id", updatable = false, nullable = false)
    private User creator;
    @ManyToOne
    @JoinColumn(name = "finisher_user_id")
    private User finisher;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "group_id")
    private Group group;
    @Column(nullable = false)
    private String description;
    @Column(updatable = false, nullable = false)
    private LocalDate creationDate;
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TaskSubElement> subElements;
}