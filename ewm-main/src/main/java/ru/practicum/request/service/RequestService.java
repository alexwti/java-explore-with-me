package ru.practicum.request.service;


import ru.practicum.request.dto.RequestsDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;
import ru.practicum.request.dto.RequestUpdateDto;

import java.util.List;

public interface RequestService {
    RequestsDto createRequest(Long userId, Long eventId);

    RequestUpdateDto updateRequests(Long userId, Long eventId, RequestStatusUpdateDto requestStatusUpdateDto);

    RequestsDto cancelRequest(Long userId, Long requestId);

    List<RequestsDto> findByEventIdAndInitiatorId(Long eventId, Long userId);

    List<RequestsDto> findByRequesterId(Long userId);

}
