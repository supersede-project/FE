package demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.jpa.RequirementsJpa;
import demo.model.Requirement;
import demo.utility.RequirementUtil;
import eu.supersede.fe.exception.NotFoundException;

@RestController
@RequestMapping("/requirement")
public class RequirementRest {

	@Autowired
    private RequirementUtil requirementUtil;
	
	@Autowired
    private RequirementsJpa requirements;
	
	// all the requirements
	@RequestMapping("")
	public List<Requirement> getRequirements() {
		return requirements.findAll();
	}
	
	// get a specific requirement 
	@RequestMapping("/{requirementId}")
	public Requirement getRequirement(@PathVariable Long requirementId)
	{
		Requirement c = requirements.findOne(requirementId);
		if(c == null)
		{
			throw new NotFoundException();
		}
		
		return c;
	}
	
	// get number of requirements
	@RequestMapping("/count")
	public Long count() {
		return requirements.count();
	}
	
	// create a requirement with a specific content
	@RequestMapping("/create")
	public void createRequirement(@RequestParam(required=true) String content) 
	{		
		requirementUtil.createRequirement(content);
	}
	
	// populate with 10 random requirements
	@RequestMapping("/populate")
	public void populateRequirements() 
	{
		requirementUtil.populateRequirements();
	}
}
