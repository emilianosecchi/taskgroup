package com.emsh.taskgroup.dto.response;

import com.emsh.taskgroup.model.Group;
import com.emsh.taskgroup.model.GroupCategory;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupResponse {
    private Long id;
    private String name;
    private LocalDate creationDate;
    private String description;
    private GroupCategory category;
    private List<UserDTO> participants;
    private List<UserDTO> admins;
    private String invitationLink;

    public record UserDTO(Long userId, String firstName, String lastName) {}

    public static GroupResponse mapGroupToDto(Group g) {
        return GroupResponse.builder()
                .id(g.getId())
                .name(g.getName())
                .description(g.getDescription())
                .creationDate(g.getCreationDate())
                .category(g.getCategory())
                .participants(new ArrayList<>())
                .admins(new ArrayList<>())
                .build();
    }

}
