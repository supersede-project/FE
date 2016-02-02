package demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.jpa.ProfilesJpa;
import demo.jpa.UserCriteriaPointsJpa;
import demo.jpa.UsersJpa;
import demo.jpa.ValutationCriteriaJpa;
import demo.model.Profile;
import demo.model.User;
import demo.model.ValutationCriteria;
import eu.supersede.fe.exception.NotFoundException;

@RestController
@RequestMapping("/user")
public class UserRest {

	@Autowired
    private UsersJpa users;
	
	@Autowired
    private ProfilesJpa profiles;
	
	@Autowired
    private ValutationCriteriaJpa valutationCriterias;
	
	@Autowired
    private UserCriteriaPointsJpa userCriteriaPoints;
	
	// get a specific user by the Id
	@RequestMapping("/{userId}")
	public User getUser(@PathVariable Long userId)
	{
		User u = users.findOne(userId);
		if(u == null)
		{
			throw new NotFoundException();
		}
		
		return u;
	}
	
	// get all the users
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<User> getUsers(@RequestParam(required = false) String profile) 
	{
		List<User> us = null;
		if(profile != null)
		{
			Profile p = profiles.findByName(profile);
			us = p.getUsers();
			
		}
		else
		{
			us = users.findAll();
		}
		return us;
	}
	
	// Get all users that have a specific ValutationCriteria
	@RequestMapping(value = "/criteria/{criteriaId}", method = RequestMethod.GET)
	public List<User> getCriteriaUsers(@PathVariable Long criteriaId)
	{
		ValutationCriteria v = valutationCriterias.findOne(criteriaId);
		if(v == null){
			throw new NotFoundException();
		}
		
		List<User> userList = userCriteriaPoints.findUsersByValutationCriteria(v);		
		return userList;
	}
}
