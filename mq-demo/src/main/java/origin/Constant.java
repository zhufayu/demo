package origin;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.text.SimpleDateFormat;
import java.util.concurrent.*;

public class Constant {
    public static ExecutorService executorService = new ThreadPoolExecutor(1000, 300 * 1000,
            100L, TimeUnit.MICROSECONDS, new LinkedBlockingQueue<Runnable>(500),
            new BasicThreadFactory.Builder().namingPattern("mq-thread-pool-%d").daemon(true).build(), new ThreadPoolExecutor.AbortPolicy());

    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    public static String midServer = "10.248.224.69:8018";
    public static String dfdevServer = "devamp.dmg.inner-dmall.com.hk";
    public static String dftestServer = "dfdevtestamp.dmg.dmall.com:8081";

    public static String headerKey ="%@=!$#^&*[哈欠]╋╬╩═┡┟ぬてそお￥……、·n&D字符\uD83D\uDE04\uD83C\uDF7A\uD83D\uDC4CγΡΕΒŸÕÖωΨΩ♎♌♋♊⊙∪⑮⑱⑥⒄⒊⒖!《ぬてそお·。。...·%@$#^&╋╬╩═┡┟ぬてそお￥……、·Β≤∴∵∷大家●▅※♪♮¶卍♎♌♋♊⊙∪⑮⑱⑥⒄⒊⒖";

    public static ScheduledThreadPoolExecutor scheduledExecutorService = new ScheduledThreadPoolExecutor(4,
            new BasicThreadFactory.Builder().namingPattern("mq-schedule-pool-%d").daemon(true).build());

}
