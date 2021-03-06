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

package eu.supersede.fe.notifier;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eu.supersede.fe.jpa.NotificationsJpa;
import eu.supersede.fe.jpa.ProfilesJpa;
import eu.supersede.fe.jpa.UsersJpa;
import eu.supersede.fe.mail.SupersedeMailSender;
import eu.supersede.fe.model.Notification;
import eu.supersede.fe.model.Profile;
import eu.supersede.fe.model.User;
import eu.supersede.fe.multitenant.MultiJpaProvider;

@Component
@PropertySource("classpath:wp5.properties")
public class Notifier
{
    @SuppressWarnings("unused")
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SupersedeMailSender supersedeMailSender;

    @Autowired
    private UsersJpa users;

    @Autowired
    private ProfilesJpa profiles;

    @Autowired
    private NotificationsJpa notifications;

    @Autowired
    private MultiJpaProvider multiJpaProvider;

    @Value("${notifier.mail.sender.delay}")
    private int SENDER_DELAY;

    private Map<String, NotificationsJpa> notificationsJpa;

    @PostConstruct
    private void init()
    {
        notificationsJpa = multiJpaProvider.getRepositories(NotificationsJpa.class);
    }

    public void createForUsers(List<String> usersEmail, String message)
    {
        for (String email : usersEmail)
        {
            User u = users.findByEmail(email);

            if (u != null)
            {
                Notification n = new Notification(message, u);
                notifications.save(n);
            }
        }
    }

    public void createForProfile(String profile, String message)
    {
        Profile p = profiles.findByName(profile);
        List<User> us = p.getUsers();

        for (User u : us)
        {
            Notification n = new Notification(message, u);
            notifications.save(n);
        }
    }

    @Scheduled(fixedRateString = "${notifier.mail.sender.checkRate}")
    public void checkNotifications()
    {
        // now
        Date now = new Date();
        Date limit = new Date(now.getTime() - SENDER_DELAY);

        for (NotificationsJpa nJpa : notificationsJpa.values())
        {
            // get all notifications not read and not sent via email and created before
            List<Notification> ns = nJpa.findByReadAndEmailSentAndCreationTimeLessThan(false, false, limit);

            for (Notification n : ns)
            {
                sendEmail(n);
                n.setEmailSent(true);
                nJpa.save(n);
            }
        }
    }

    private static final String subject = "Supersede notification";
    private static final String emailTemplate = "Hi %s, \nyou have just received a supersede notification.";

    private void sendEmail(Notification n)
    {
        supersedeMailSender.sendEmail(subject,
                String.format(emailTemplate, n.getUser().getFirstName() + " " + n.getUser().getLastName()),
                n.getUser().getEmail());
    }
}