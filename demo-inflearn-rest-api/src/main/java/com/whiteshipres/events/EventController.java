package com.whiteshipres.events;

import com.whiteshipres.accounts.Account;
import com.whiteshipres.accounts.CurrentUser;
import com.whiteshipres.common.ErrorResource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@Valid @RequestBody EventDto eventDto, Errors errors,
                                      @CurrentUser Account account){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (errors.hasErrors()) return badRequest(errors);

        eventValidator.validate(eventDto, errors);

        if (errors.hasErrors()) return badRequest(errors);

        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        event.setManager(account);
        Event newEvent = this.eventRepository.save(event);

        ControllerLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> assembler,
                                     @CurrentUser Account account){
        Page<Event> queryEventPage = this.eventRepository.findAll(pageable);
        PagedResources<Resource<Event>> pagedResources = assembler.toResource(queryEventPage, e -> new EventResource(e));

        if (account != null){
            pagedResources.add(linkTo(EventController.class).withRel("create-event"));
            pagedResources.add(new Link("/docs/index.html#authorized-resources-events-list").withRel("profile"));
        }
        else {
            pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        }

        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id, @CurrentUser Account account){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (!optionalEvent.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if (event.getManager().equals(account)){
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));
        }
        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable("id") Integer id,
                                      @Valid @RequestBody EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser){
        Optional<Event> eventById = eventRepository.findById(id);
        if (!eventById.isPresent()){
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()) return badRequest(errors);
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) return badRequest(errors);

        Event existingEvent = eventById.get();
        if (!existingEvent.getManager().equals(currentUser)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        this.modelMapper.map(eventDto, existingEvent);
        Event savedEvent = this.eventRepository.save(existingEvent);

        EventResource eventResource = new EventResource(savedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        return ResponseEntity.ok(eventResource);
    }


    private ResponseEntity badRequest(Errors errors){
        return ResponseEntity.badRequest().body(new ErrorResource(errors));
    }

}
