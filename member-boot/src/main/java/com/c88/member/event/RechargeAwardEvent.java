package com.c88.member.event;

import com.c88.member.event.model.RechargeAwardEventModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RechargeAwardEvent {

    @EventListener
    public void listen(PayloadApplicationEvent<RechargeAwardEventModel> event) {
        RechargeAwardEventModel payload = event.getPayload();
    }


}
