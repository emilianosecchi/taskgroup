package com.emsh.taskgroup.dto.response;

import lombok.*;

import java.time.LocalDate;
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

    private List<UserDTO> participants;
    private List<UserDTO> admins;

    public record UserDTO(Long userId, String firstName, String lastName) {}

}
