package com.emsh.taskgroup.dto.response;

import lombok.Getter;
import lombok.Setter;

public record AuthenticationResponse(String jwtToken, Long userId) {

}
