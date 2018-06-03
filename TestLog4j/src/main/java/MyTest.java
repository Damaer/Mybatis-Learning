import org.apache.log4j.Logger;

public class MyTest {
    private static Logger logger = Logger.getLogger(MyTest.class.getClass());
    @org.junit.Test
    public void test() {
        // debug级别
        logger.debug("这是一个debug级别的信息");

        // info级别
        logger.info("这是一个info级别的信息");
        // error级别
        logger.error("这是一个error级别的信息");
    }
}
