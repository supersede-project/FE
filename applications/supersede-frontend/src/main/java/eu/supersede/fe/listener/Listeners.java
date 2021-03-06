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

package eu.supersede.fe.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * Container of listeners for profiles and notifications.
 */
@Component
public class Listeners
{
    @Autowired
    private ProfileListener profileListener;
    @Autowired
    private NotificationListener notificationListener;

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory)
    {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        MessageListenerAdapter profileAdapter = new MessageListenerAdapter(profileListener, "receiveMessage");
        profileAdapter.afterPropertiesSet();
        container.addMessageListener(profileAdapter, new PatternTopic("profile"));

        MessageListenerAdapter notificationAdapter = new MessageListenerAdapter(notificationListener, "receiveMessage");
        notificationAdapter.afterPropertiesSet();
        container.addMessageListener(notificationAdapter, new PatternTopic("notification"));

        return container;
    }
}