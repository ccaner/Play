package play.remotemock;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

public class Main {

    public static void main(String[] args) throws IOException {
        ClassLoader cl = Main.class.getClassLoader();
        Enumeration<URL> rEnum = cl.getResources("applicationContext.xml");
        ArrayList<Resource> resources = new ArrayList<Resource>();
        while (rEnum.hasMoreElements()) {
            URL url = rEnum.nextElement();
            resources.add(new UrlResource(url));
        }

        GenericXmlApplicationContext context = new GenericXmlApplicationContext(resources.toArray(new Resource[]{}));
    }
}
