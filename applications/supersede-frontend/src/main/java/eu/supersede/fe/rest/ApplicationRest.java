/*
   (C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package eu.supersede.fe.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.fe.application.Application;
import eu.supersede.fe.application.ApplicationPage;
import eu.supersede.fe.application.ApplicationUtil;
import eu.supersede.fe.jpa.ProfilesJpa;
import eu.supersede.fe.model.Profile;

@RestController
@RequestMapping("/application")
public class ApplicationRest
{
    @Autowired
    private ApplicationUtil applicationUtil;

    @Autowired
    private ProfilesJpa profiles;

    @RequestMapping("/page")
    public List<ApplicationGrouped> getUserAuthenticatedApplicationsPage(Authentication auth, Locale locale)
    {
        String lang = locale.getLanguage();

        Map<String, ApplicationGrouped> appsMap = new HashMap<>();
        Map<String, Map<String, Page>> appsPagesMap = new HashMap<>();
        List<ApplicationGrouped> applications = new ArrayList<>();

        List<String> authNames = new ArrayList<>();
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

        for (GrantedAuthority ga : authorities)
        {
            authNames.add(ga.getAuthority().substring(5));
        }

        List<Profile> profList = profiles.findByNameIn(authNames);

        // make data nicer for frontend
        for (Profile p : profList)
        {
            Set<ApplicationPage> apps = applicationUtil.getApplicationsPagesByProfileName(p.getName());

            for (ApplicationPage app : apps)
            {
                ApplicationGrouped ag;

                if (!appsMap.containsKey(app.getApplicationName()))
                {
                    Application a = applicationUtil.getApplication(app.getApplicationName());
                    String appLabel = a.getLocalizedApplicationLabel(lang);
                    ag = new ApplicationGrouped(app.getApplicationName(), appLabel);
                    ag.setHomePage(a.getHomePage());
                    applications.add(ag);
                    appsMap.put(app.getApplicationName(), ag);
                    appsPagesMap.put(app.getApplicationName(), new HashMap<String, Page>());
                }
                else
                {
                    ag = appsMap.get(app.getApplicationName());
                }

                Page page;

                if (!appsPagesMap.get(app.getApplicationName()).containsKey(app.getApplicationPage()))
                {
                    page = new Page(app.getApplicationPage(), app.getLocalizedApplicationPageLabel(lang));
                    ag.getPages().add(page);
                    appsPagesMap.get(app.getApplicationName()).put(app.getApplicationPage(), page);
                }
            }
        }

        return applications;
    }

    static class ApplicationGrouped
    {
        private String applicationName;
        private String applicationLabel;
        private String homePage;
        private List<Page> pages;

        public ApplicationGrouped(String applicationName, String applicationLabel)
        {
            this.applicationName = applicationName;
            this.applicationLabel = applicationLabel;
            pages = new ArrayList<>();
        }

        public String getApplicationName()
        {
            return applicationName;
        }

        public void setApplicationName(String applicationName)
        {
            this.applicationName = applicationName;
        }

        public String getApplicationLabel()
        {
            return applicationLabel;
        }

        public void setApplicationLabel(String applicationLabel)
        {
            this.applicationLabel = applicationLabel;
        }

        public List<Page> getPages()
        {
            return pages;
        }

        public void setPages(List<Page> pages)
        {
            this.pages = pages;
        }

        public String getHomePage()
        {
            return homePage;
        }

        public void setHomePage(String homePage)
        {
            this.homePage = homePage;
        }
    }

    static class Page
    {
        private String name;
        private String label;

        Page(String name, String label)
        {
            this.setName(name);
            this.setLabel(label);
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getLabel()
        {
            return label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }
    }
}