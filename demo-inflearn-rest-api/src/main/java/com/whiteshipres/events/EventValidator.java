package com.whiteshipres.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors){
        if (eventDto.getMaxPrice() < eventDto.getBasePrice() && eventDto.getMaxPrice() > 0){
            errors.rejectValue("basePrice", "wrongValue", "Base Price is wrong value");
            errors.rejectValue("maxPrice", "wrongValue", "Max Price is wrong value");
        }

        LocalDateTime endEventTime = eventDto.getEndEventDateTime();
        if (endEventTime.isBefore(eventDto.getBeginEventDateTime())
        || endEventTime.isBefore(eventDto.getCloseEnrollmentDateTime())
        || endEventTime.isBefore(eventDto.getBeginEnrollmentDateTime())){
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventTime is wrong value");
        }

        // TODO beginEventDateTime

        // TODO closeEnrollmentTime
    }
}
