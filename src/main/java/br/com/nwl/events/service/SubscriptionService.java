package br.com.nwl.events.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nwl.events.exception.EventNotFoundException;
import br.com.nwl.events.exception.SubscriptionConflictException;
import br.com.nwl.events.exception.UserIndicadorNotFoundException;
import br.com.nwl.events.dto.SubscriptionResponse;
import br.com.nwl.events.model.Subscription;
import br.com.nwl.events.model.User;
import br.com.nwl.events.repo.EventRepo;
import br.com.nwl.events.repo.SubscriptionRepo;
import br.com.nwl.events.repo.UserRepo;
import br.com.nwl.events.model.Event;
import br.com.nwl.events.dto.SubscriptionRankingByUser;
import br.com.nwl.events.dto.SubscriptionRankingItem;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class SubscriptionService {
    
    @Autowired
    private EventRepo evtRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private SubscriptionRepo subRepo;
    public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId){
        Event evt = evtRepo.findByPrettyName(eventName);
        if (evt == null){
            throw new EventNotFoundException("Evento " + eventName+ " não existe");
        }
        User userRec = userRepo.findByEmail(user.getEmail());
        if(userRec == null){
            userRec = userRepo.save(user);
        }
        User indicador = null;
        if(userId != null){
            indicador = userRepo.findById(userId).orElse(null);
            if (indicador == null) {
            throw new UserIndicadorNotFoundException("Usuário " +userId+ " indicador não existe");
            }
        }
        
        Subscription subs = new Subscription();
        subs.setEvent(evt);
        subs.setSubscriber(userRec);
        subs.setIndication(indicador);
        Subscription tmpSub = subRepo.findByEventAndSubscriber(evt, userRec);
        if(tmpSub != null){
            throw new SubscriptionConflictException("Já existe incrição para o usuário " + userRec.getName() + " no evento " + evt.getTitle());
        }
        Subscription res = subRepo.save(subs);
        return new SubscriptionResponse(res.getSubscriptionNumber(), "http://codecraft.com/subscription/"+res.getEvent().getPrettyName()+"/"+res.getSubscriber().getId());
    }

    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName){
        Event evt = evtRepo.findByPrettyName(prettyName);
        if (evt == null){
            throw new EventNotFoundException("Ranking do Evento " + prettyName+ " não existe");
        }
        return subRepo.generateRanking(evt.getEventId());
    }

    public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId){
        List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);

        SubscriptionRankingItem item = ranking.stream().filter(i -> i.userId().equals(userId)).findFirst().orElse(null);
        if(item == null){
            throw new UserIndicadorNotFoundException("Não há inscrições com indicação do usuário " + userId);
        }
        Integer posicao = IntStream.range(0, ranking.size()).filter(pos -> ranking.get(pos).userId().equals(userId)).findFirst().getAsInt();

        return new SubscriptionRankingByUser(item, posicao+1);
    }
}
