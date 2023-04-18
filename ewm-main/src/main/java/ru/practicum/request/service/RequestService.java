package ru.practicum.request.service;


import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestStatusUpdateDto;
import ru.practicum.request.dto.RequestStatusUpdateResultDto;

import java.util.List;

public interface RequestService {
    RequestDto createRequest(Long userId, Long eventId);

    RequestStatusUpdateResultDto updateRequests(Long userId, Long eventId, RequestStatusUpdateDto requestStatusUpdateDto);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> findByEventIdAndInitiatorId(Long eventId, Long userId);

    List<RequestDto> findByRequesterId(Long userId);

    List<RequestDto> findAll();
}
