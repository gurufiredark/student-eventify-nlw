package br.com.nwl.events.repo;

import org.springframework.data.repository.CrudRepository;
import br.com.nwl.events.model.Subscription;
import br.com.nwl.events.model.User;
import br.com.nwl.events.model.Event;

public interface SubscriptionRepo extends CrudRepository<Subscription, Integer> {
    public Subscription findByEventAndSubscriber(Event evt, User user);
    
}
