package test.xmlconf;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Provider {

    public static void testxml() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("provider.xml");
        context.start();
        System.out.println("Provider start");
    }

    public static void main(String[] args) throws InterruptedException {
        testxml();
    }
}
