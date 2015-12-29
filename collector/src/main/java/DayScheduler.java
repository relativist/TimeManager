import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;

@Singleton
@Startup
public class DayScheduler {

    private static final Logger L = LoggerFactory.getLogger(DayScheduler.class);
    private final static ObjectMapper MAPPER = new ObjectMapper();

    @PersistenceContext
    private EntityManager em;

    @Resource
    private TimerService timerService;

    @PostConstruct
    public void init(){
        L.info("init day cache");
        timeout();
        Collection<Timer> timers = timerService.getTimers();
        if (timers.isEmpty()) {
            L.info("Add timer");
            timerService.createTimer(0, 1500000, null);
        } else {
            L.info("Timer already exists");
        }
        L.info("done");
    }

    @Timeout
    @Lock(LockType.WRITE)
    public void timeout() {
        L.info("init day cache: started");
//        new TimeTracker().iterator();
        L.info("init agent cache: done");
    }

}
