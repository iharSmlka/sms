package smolka.smsapi.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smolka.smsapi.service.qiwi_service.BillService;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class BillCloser extends ThreadService {

    @Autowired
    private final BillService billService;

    public BillCloser(@Value(value = "${sms.api.bill-closer-delay}") Integer delaySec, BillService billService) {
        super(delaySec);
        this.billService = billService;
    }

    protected void step() {
        billService.closeAllStaleBills();
    }
}
