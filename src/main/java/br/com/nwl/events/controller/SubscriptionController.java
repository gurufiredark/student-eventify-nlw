package br.com.nwl.events.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import br.com.nwl.events.dto.ErrorMessage;
import br.com.nwl.events.exception.EventNotFoundExeption;
import br.com.nwl.events.exception.SubscriptionConflictException;
import br.com.nwl.events.exception.UserIndicadorNotFoundException;
import br.com.nwl.events.service.SubscriptionService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import br.com.nwl.events.dto.SubscriptionResponse;
import br.com.nwl.events.model.User;


@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionService service;

    @PostMapping({"/subscription/{prettyName}", "/subscription/{prettyName}/{userId}"})
    public ResponseEntity<?> createSubscription(@PathVariable String prettyName, @RequestBody User subscriber, @PathVariable(required = false) Integer userId) {
        try {
        SubscriptionResponse res = service.creatNewSubscription(prettyName, subscriber, userId);
            if (res != null) {
                return ResponseEntity.ok(res);
            }
        
        } catch (EventNotFoundExeption ex) {
        return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));  
        }
        catch (SubscriptionConflictException ex) {
            return ResponseEntity.status(409).body(new ErrorMessage(ex.getMessage()));
        }
        catch (UserIndicadorNotFoundException ex) {
            return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
        }
        return ResponseEntity.badRequest().build();
    }
}

