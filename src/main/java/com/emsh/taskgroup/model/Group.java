package com.emsh.taskgroup.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "_Group")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @Column(updatable = false)
    private LocalDate creationDate;

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    private List<UserGroup> participants;

    private String description;

    @Enumerated(EnumType.STRING)
    private GroupCategory category;

    public List<User> getAdmins() {
        return participants
                .stream()
                .filter(UserGroup::getIsAdmin)
                .map(UserGroup::getUser)
                .toList();
    }

    public List<User> getAllParticipants() {
        return participants
                .stream()
                .map(UserGroup::getUser)
                .toList();
    }

    /**
     * Verifica si un usuario es administrador del grupo
     * @param userId: id del usuario que se quiere verificar
     * @return true: si el userId proporcionado corresponde a un usuario administrador del grupo, false caso contrario
     */
    public boolean checkIfUserIsAdmin(Long userId) {
        return getAdmins()
                .stream()
                .map(User::getId)
                .toList()
                .contains(userId);
    }

    /**
     * Verifica si un usuario es participante del grupo
     * @param userId: id del usuario que se quiere verificar
     * @return true: si el userId proporcionado corresponde a un usuario participante del grupo, false caso contrario
     */
    public boolean checkIfUserIsParticipant(Long userId) {
        return getAllParticipants()
                .stream()
                .map(User::getId)
                .toList()
                .contains(userId);
    }

}
