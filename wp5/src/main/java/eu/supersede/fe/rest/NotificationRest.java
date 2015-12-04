package eu.supersede.fe.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.fe.exception.UnauthorizedException;
import eu.supersede.fe.jpa.NotificationsJpa;
import eu.supersede.fe.jpa.UsersJpa;
import eu.supersede.fe.model.Notification;
import eu.supersede.fe.model.User;
import eu.supersede.fe.security.DatabaseUser;

@RestController
@RequestMapping("/notification")
public class NotificationRest {

	@Autowired
    private UsersJpa users;

	@Autowired
    private NotificationsJpa notifications;
	
	@RequestMapping("")
	public List<Notification> getByUserId(Authentication authentication, @RequestParam(defaultValue="true") Boolean toRead)
	{
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User u = users.getOne(currentUser.getUserId());
		List<Notification> ns;
		if(toRead)
		{
			ns = notifications.findByUserAndReadOrderByCreationTimeDesc(u, !toRead);
		}
		else
		{
			ns = notifications.findByUserOrderByCreationTimeDesc(u);
		}
		
		return ns;
	}
	
	@RequestMapping("/count")
	public Long countByUserId(Authentication authentication, @RequestParam(defaultValue="true") Boolean toRead)
	{
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User u = users.getOne(currentUser.getUserId());
		Long c;
		if(toRead)
		{
			c = notifications.countByUserAndRead(u, !toRead);
		}
		else
		{
			c = notifications.countByUser(u);
		}
		
		return c;
	}
	
	@RequestMapping(method = RequestMethod.PUT, value="/{notificationId}/read")
	public void setRead(Authentication authentication, @PathVariable Long notificationId) {
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User u = users.getOne(currentUser.getUserId());
		Notification n = notifications.findOne(notificationId);
		
		if(n.getUser().equals(u))
		{
			n.setRead(true);
			notifications.save(n);
		}
		else
		{
			throw new UnauthorizedException();
		}
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/{notificationId}")
	public void delete(Authentication authentication, @PathVariable Long notificationId)
	{
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User u = users.getOne(currentUser.getUserId());
		Notification n = notifications.findOne(notificationId);
		
		if(n.getUser().equals(u))
		{
			notifications.delete(notificationId);
		}
		else
		{
			throw new UnauthorizedException();
		}
	}
}
