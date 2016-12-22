package eu.supersede.fe.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import eu.supersede.fe.model.Application;

@Component
@PropertySource("classpath:wp5.properties")
public class ApplicationsLoader
{
    private final String APPLICATION_NAMES = "configuration.tools.applications";
    private final String APPLICATION_PROPERTY = "configuration.tools.application.";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private ArrayList<Application> applications;

    @Autowired
    Environment env;

    @PostConstruct
    public void load()
    {
        applications = new ArrayList<>();

        String applicationsList = env.getProperty(APPLICATION_NAMES);

        if (applicationsList == null)
        {
            log.error("Missing required property: " + APPLICATION_NAMES);
            return;
        }

        String[] applicationNames = applicationsList.split(",");

        for (String applicationName : applicationNames)
        {
            String url_property = APPLICATION_PROPERTY + applicationName + ".url";
            String url = env.getProperty(url_property);

            if (url == null)
            {
                log.error("Missing required property: " + url_property);
                return;
            }

            Application application = new Application();
            application.setName(applicationName);
            application.setUrl(url);
            applications.add(application);
        }
    }

    public List<Application> getApplications()
    {
        return applications;
    }
}