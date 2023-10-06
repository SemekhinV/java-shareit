package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
	private static final String USER_ID = "X-Sharer-User-Id";

	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(
			@RequestHeader(value = USER_ID) long userId,
			@RequestBody @Valid BookItemRequestDto requestDto
	) {

		log.info("Creating booking {}, userId={}", requestDto, userId);

		return bookingClient.createBooking(userId, requestDto);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(
			@RequestHeader(value = USER_ID) long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size
	) {

		BookingState state = BookingState
				.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));

		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);

		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(
			@RequestHeader("X-Sharer-User-Id") long userId,
			@PathVariable Long bookingId
	) {

		log.info("Get booking {}, userId={}", bookingId, userId);

		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getOwnerBookings(
			@RequestHeader(value = USER_ID) Long userId,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
			@RequestParam(name = "state", defaultValue = "all") String stateParam
	) {

		BookingState state = BookingState.from(stateParam).orElseThrow(
				() -> new IllegalArgumentException("Unknown state: " + stateParam));

		return bookingClient.getUserBookings(userId, state, from, size);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(
			@RequestHeader(value = USER_ID) Long userId,
			@RequestParam(required = false) Boolean approved,
			@PathVariable Integer bookingId
	) {

		return bookingClient.approveBooking(bookingId, approved, userId);
	}
}
