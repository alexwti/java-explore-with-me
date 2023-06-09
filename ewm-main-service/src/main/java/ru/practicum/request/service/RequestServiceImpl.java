package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.RequestStatusUpdateDto;
import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.dto.RequestsDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;


    @Transactional
    public RequestsDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User not found");
        });
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Event not found");
        });
        if (event.getEventState() != EventState.PUBLISHED) {
            throw new ConflictException("Wrong event status (must be published). Request rejected");
        }
        if (Objects.equals(event.getInitiator().getId(), userId)) {
            throw new ConflictException("You can't send request to your own event");
        }
        int confirmedRequests = requestRepository.findByEventIdConfirmed(eventId).size();

        if (confirmedRequests >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ConflictException("Participants limit exceeded");
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        log.info("Request created");
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    @Transactional
    @Override
    public RequestUpdateDto updateRequests(Long userId, Long eventId, RequestStatusUpdateDto requestStatusUpdateDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            throw new NotFoundException("Event not found");
        });
        if (!event.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("You don't have event with id " + eventId);
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Confirmation is not required");
        }
        RequestUpdateDto requestUpdateDto = new RequestUpdateDto(new ArrayList<>(), new ArrayList<>());
        Integer confirmedRequests = requestRepository.findByEventIdConfirmed(eventId).size();
        List<Request> requests = requestRepository.findByEventIdAndRequestsIds(eventId,
                requestStatusUpdateDto.getRequestIds());

        if (confirmedRequests + requests.size() > event.getParticipantLimit() &&
                requestStatusUpdateDto.getStatus().name().equalsIgnoreCase(RequestStatus.CONFIRMED.name())) {
            throw new ConflictException("Requests limit exceeded");
        }

        if (requestStatusUpdateDto.getStatus().name().equalsIgnoreCase(RequestStatus.REJECTED.name())) {
            requests.forEach(request -> {
                if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                    throw new ConflictException("You can't reject confirmed request");
                }
                request.setStatus(RequestStatus.REJECTED);
            });
            List<RequestsDto> requestsDto = requests.stream()
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
            requestUpdateDto.setRejectedRequests(requestsDto);
            requestRepository.saveAll(requests);
        } else if (requestStatusUpdateDto.getStatus().name().equalsIgnoreCase(RequestStatus.CONFIRMED.name())
                && requestStatusUpdateDto.getRequestIds().size() <= event.getParticipantLimit() - confirmedRequests) {
            requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
            List<RequestsDto> requestsDto = requests.stream()
                    .map(requestMapper::toRequestDto)
                    .collect(Collectors.toList());
            requestUpdateDto.setConfirmedRequests(requestsDto);
            requestRepository.saveAll(requests);
        }
        return requestUpdateDto;

    }

    @Transactional
    public RequestsDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> {
                            throw new NotFoundException("Request not found");
                        }
                );
        request.setStatus(RequestStatus.CANCELED);
        log.info("Request canceled");
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    public List<RequestsDto> findByRequesterId(Long userId) {
        log.info("Request sent");
        return requestRepository.findByRequesterId(userId).stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList()
                );
    }

    public List<RequestsDto> findByEventIdAndInitiatorId(Long userId, Long eventId) {
        log.info("Requests sent");
        return requestRepository.findByEventIdAndInitiatorId(eventId, userId).stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}
