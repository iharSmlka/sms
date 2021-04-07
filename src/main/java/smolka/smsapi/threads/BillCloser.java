package smolka.smsapi.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smolka.smsapi.service.qiwi_service.BillService;

import javax.annotation.PostConstruct;

@Service
@Slf4j
public class BillCloser extends Thread {

    @Value(value = "${sms.api.bill-closer-delay}")
    private Integer delay;

    @Autowired
    private BillService billService;

    @PostConstruct
    public void initialize() {
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(delay * 1000);
                step();
            } catch (Exception e) {
                log.error("Ошибка в BillCloser ", e);
                break;
            }
        }
        log.error("Умер BillCloser");
    }

    private void step() {
        billService.closeAllStaleBills();
    }
}
