import entities.Attendance;
import entities.TimeUser;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Stateless
public class TimeTracker {
    private final static Logger L = LoggerFactory.getLogger(TimeTracker.class);
    protected WebDriver driver = null;

    @PersistenceContext
    private EntityManager em;

    public TimeTracker() {
        Capabilities caps = new DesiredCapabilities();
        ((DesiredCapabilities) caps).setCapability(
                PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
                "/usr/bin/phantomjs"
        );
        driver = new PhantomJSDriver(caps);
    }

    private static List<TimeUser> getUsers(EntityManager em) {
        return em.createQuery("select o from TimeUser o", TimeUser.class).getResultList();
    }

    public void iterator() {
        Date date = getBeginDate();
        Date yesterday = getYesterday();
        int counter = 0;
        while (!dateToString(date).equals(dateToString(yesterday))) {
            String url = "http://time.laximo.local/?date=" + dateToString(date);
            L.info("Process: " + url);
            List<Attendance> list = getNames(url, date);


            date = plusOneDay(date);
            counter++;
//            if (counter == 10) {
//                break;
//            }
        }
        finish();

    }

    private Date plusOneDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    private String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public List<Attendance> getNames(String url, Date date) {
        List<TimeUser> users = getUsers(em);
        List<Attendance> attendances = new ArrayList<>();
        driver.get(url);
        List<WebElement> names = driver.findElements(By.xpath("//div/div[2]//tbody/tr/td[1]"));
        List<WebElement> hours = driver.findElements(By.xpath("//div/div[2]//tbody/tr/td[4]"));

        for (int i = 0; i < names.size(); i++) {
            final String name = names.get(i).getText();
            String hour = hours.get(i).getText();
            if (name.trim().length() == 0)
                continue;
//            L.info(name + " " + hour);
            TimeUser user = isNameContains(users, name);
            if (user == null) {
                TimeUser newUser = new TimeUser(name.trim());
                em.persist(newUser);
                users.add(newUser);
                user = newUser;
            }
            Double realHour = hourToDouble(hour);
            if (realHour > 1) {
                Attendance attendance = new Attendance(realHour, user, date.getTime());
                em.persist(attendance);
                attendances.add(attendance);
            }
        }
        return attendances;
    }

    private Double hourToDouble(String string) {
        if (string.contains(":")) {
            Integer hour = Integer.valueOf(string.split(":")[0]);
            Integer min = Integer.valueOf(string.split(":")[1]);
            return (double) (hour * 60 + min);
        } else return 0d;
    }

    private TimeUser isNameContains(List<TimeUser> users, String name) {
        List<TimeUser> collect = users.stream().filter(e -> e.getName().trim().toUpperCase().equals(name.trim().toUpperCase())).collect(toList());
        if (collect.size() > 0) {
            return collect.get(0);
        } else return null;
    }

    private Date getBeginDate() {
        List maxes = em.createNativeQuery("SELECT max(timestamp) FROM attendance").getResultList();
        if (maxes.isEmpty() || maxes.get(0) == null) {
            Calendar c = Calendar.getInstance();
            c.set(2014, 10, 5);
            return c.getTime();
        } else {
            Date date = new Date((Long) ((BigInteger) maxes.get(0)).longValue());
            return plusOneDay(date);
        }

    }

    private Date getYesterday() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }

    public void finish() {
        driver.manage().deleteAllCookies();
        driver.quit();
    }

}


// ++ ghost driver
// ++ data base
// -- iterator to databse
/* Алгоритм
* идем с наибольшей даты до вчера, если наибольшей нету- берем начальную:
* 1) отсылаем запрос , парсим каждую строку
* 1) Если встретили нового пользователя - добавили в базу пользователей
*
* Cron каждые  4 часа выполнять вышестоящее действие.
*
*
* */

// -- wildfly project