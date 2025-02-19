package br.com.nwl.events.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import br.com.nwl.events.model.Subscription;
import br.com.nwl.events.model.User;
import br.com.nwl.events.model.Event;
import java.util.List;
import br.com.nwl.events.dto.SubscriptionRankingItem;
import org.springframework.data.repository.query.Param;


public interface SubscriptionRepo extends CrudRepository<Subscription, Integer> {
    public Subscription findByEventAndSubscriber(Event evt, User user);

    @Query(value = "select count(subscription_number) as quantidade, indication_user_id, user_name" + 
                " from tbl_subscription inner join tbl_user" + 
                " on tbl_subscription.indication_user_id = tbl_user.user_id " + 
                " where indication_user_id is not null" + 
                "    and event_id = :eventId" + 
                " group by indication_user_id" + 
                " order by quantidade desc", nativeQuery = true)
    public List<SubscriptionRankingItem> generateRanking(@Param("eventId") Integer eventId);

}
