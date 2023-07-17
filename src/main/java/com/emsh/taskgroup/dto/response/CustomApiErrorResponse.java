package com.emsh.taskgroup.dto.response;

import java.time.ZonedDateTime;

public record CustomApiErrorResponse(ZonedDateTime timestamp, String message) {
}
