package ru.practicum.ewm.ewmService.model.invitation;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import ru.practicum.ewm.ewmService.model.request.StateRequest;

import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
public class InvitationDto {
    private Long id;
    private Long event;
    private Long recipient;
    private Long sender;
    private StateRequest status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private StateInvitation stateInvitation;
}
