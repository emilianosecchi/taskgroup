package com.emsh.taskgroup.dto.response;

import com.emsh.taskgroup.model.MembershipRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PendingGroupRequestsResponse {
    private record PendingRequestDTO(Long membershipRequestId, GroupResponse.UserDTO userDTO){ }
    private List<PendingRequestDTO> pendingRequests = new ArrayList<>();

    public PendingGroupRequestsResponse(List<MembershipRequest> membershipRequests) {
        for (MembershipRequest mRequest : membershipRequests) {
            this.pendingRequests.add(
                    new PendingRequestDTO(
                            mRequest.getId(),
                            new GroupResponse.UserDTO(mRequest.getRequester().getId(), mRequest.getRequester().getFirstName(), mRequest.getRequester().getLastName()))
            );
        }
    }

}
