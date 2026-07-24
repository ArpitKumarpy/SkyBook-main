package com.example.SkyBook.mapper;

import com.example.SkyBook.dto.request.PassengerRequestDto;
import com.example.SkyBook.dto.response.PassengerResponseDto;
import com.example.SkyBook.entity.Passenger;

public class PassengerMapper {

    private PassengerMapper() {
    }

    public static Passenger fromRequestDto(PassengerRequestDto dto) {

        return Passenger.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .gender(dto.gender())
                .dateOfBirth(dto.dateOfBirth())
                .passportNumber(dto.passportNumber())
                .nationality(dto.nationality())
                .build();

    }

    public static PassengerResponseDto toResponseDto(Passenger passenger) {

        return new PassengerResponseDto(

                passenger.getId(),
                passenger.getFirstName(),
                passenger.getLastName(),
                passenger.getGender(),
                passenger.getDateOfBirth(),
                passenger.getPassportNumber(),
                passenger.getNationality(),
                passenger.getBooking().getId(),
                passenger.getCreatedAt(),
                passenger.getUpdatedAt()

        );

    }

}