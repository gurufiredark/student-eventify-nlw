package br.com.nwl.events.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nwl.events.repo.EventRepo;
import br.com.nwl.events.model.Event;
import java.util.List;


@Service
public class EventService {
    
    @Autowired
    private EventRepo eventRepo;

    public Event addNewEvent(Event event) {
        // GERANDO O PREETY NAME
        event.setPrettyName(event.getTitle().toLowerCase().replaceAll(" ", "-"));
        return eventRepo.save(event);
    }

    public List<Event> getAllEvents() {
        return (List<Event>)eventRepo.findAll();
    }

    public Event getByPrettyName(String prettyName) {
        return eventRepo.findByPrettyName(prettyName);
    }

}
