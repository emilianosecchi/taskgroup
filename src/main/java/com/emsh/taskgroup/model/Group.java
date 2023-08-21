package com.emsh.taskgroup.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "_Group")
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
     * Verifica si un usuario es administrador de un grupo
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

}
