package play.baseline;

import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

public class Main {

    public static void main(String[] args) throws IOException {
        String serverType = "simpleHttp";
        if (args.length == 1 && args[0].equalsIgnoreCase("jetty")) {
            serverType = "jetty";
        }

        ClassLoader cl = Main.class.getClassLoader();
        Enumeration<URL> rEnum = cl.getResources("applicationContext.xml");
        ArrayList<Resource> resources = new ArrayList<Resource>();
        while (rEnum.hasMoreElements()) {
            URL url = rEnum.nextElement();
            resources.add(new UrlResource(url));
        }
        resources.add(new ClassPathResource(serverType + "-config.xml"));

        new GenericXmlApplicationContext(resources.toArray(new Resource[resources.size()]));
    }
}
