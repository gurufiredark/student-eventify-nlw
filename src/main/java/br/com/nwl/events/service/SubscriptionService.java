package br.com.nwl.events.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nwl.events.exception.EventNotFoundExeption;
import br.com.nwl.events.exception.SubscriptionConflictException;
import br.com.nwl.events.exception.UserIndicadorNotFoundException;
import br.com.nwl.events.dto.SubscriptionResponse;
import br.com.nwl.events.model.Subscription;
import br.com.nwl.events.model.User;
import br.com.nwl.events.repo.EventRepo;
import br.com.nwl.events.repo.SubscriptionRepo;
import br.com.nwl.events.repo.UserRepo;
import br.com.nwl.events.model.Event;

@Service
public class SubscriptionService {

    @Autowired
    private EventRepo evtRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubscriptionRepo subsRepo;

    public SubscriptionResponse creatNewSubscription(String eventName, User user, Integer userId) {
        
        // recupera o evento pelo nome
        Event evt = evtRepo.findByPrettyName(eventName);
        if(evt == null) {
            throw new EventNotFoundExeption("Evento " + eventName + " não encontrado");
        }

        User userRec = userRepo.findByEmail(user.getEmail());
        if (userRec == null) {
            userRec = userRepo.save(user);
        }

        User indicador = userRepo.findById(userId).orElse(null);
        if (indicador != null) {
            throw new UserIndicadorNotFoundException("Usuario " + userId + " indicador não existe");
        }

        user = userRepo.save(user);
        
        Subscription subs = new Subscription();
        subs.setEvent(evt);
        subs.setSubscriber(userRec);
        subs.setIndication(indicador);

        Subscription tmpSub = subsRepo.findByEventAndSubscriber(evt, userRec);
        if (tmpSub != null) {
            throw new SubscriptionConflictException("Ja existe inscrição para o usuário " + userRec.getName() + " no evento " + evt.getTitle());
        }

        Subscription res = subsRepo.save(subs);
        
        return new SubscriptionResponse(res.getSubscriptionNumber(), "http://codecraft.com/subscription" + res.getEvent().getPrettyName()+ "/" + res.getSubscriber().getId());

    }
}
