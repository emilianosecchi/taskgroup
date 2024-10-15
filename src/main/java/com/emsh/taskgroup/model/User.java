package com.emsh.taskgroup.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "_User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, updatable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ToString.Exclude
    @OneToMany(mappedBy = "user")
    private List<UserGroup> groups;

    @ToString.Exclude
    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    private List<Task> tasks;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    public List<Group> getAllGroups() {
        return groups.stream().map(UserGroup::getGroup).toList();
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

}
