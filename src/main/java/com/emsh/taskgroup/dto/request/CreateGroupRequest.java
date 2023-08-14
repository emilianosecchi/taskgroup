package com.emsh.taskgroup.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupRequest {

    @NotBlank
    private Long userId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;

}
