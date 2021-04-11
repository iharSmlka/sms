package smolka.smsapi.threads;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public abstract class ThreadService extends Thread {

    private Integer delaySec;

    @PostConstruct
    public void startAfterInit() {
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(delaySec * 1000);
                step();
            } catch (Throwable e) {
                log.error("Ошибка в MessageUpdater ", e);
            }
        }
    }

    protected abstract void step() throws Throwable;
}
