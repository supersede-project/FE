package demo.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import demo.exception.NotFoundException;
import demo.jpa.ValutationCriteriaJpa;
import demo.model.ValutationCriteria;

@RestController
@RequestMapping("/criteria")
public class ValutationCriteriaRest {
	

	@Autowired
    private ValutationCriteriaJpa valutationCriterias;
		
	//get a specific ValutationCriteria
	@RequestMapping("/{criteriaId}")
	public ValutationCriteria getCriteria(@PathVariable Long criteriaId)
	{
		ValutationCriteria c = valutationCriterias.findOne(criteriaId);
		if(c == null)
		{
			throw new NotFoundException();
		}
		
		return c;
	}
	
	// get all the ValutationCriterias
	@RequestMapping("")
	public List<ValutationCriteria> getCriterias() 
	{
		return valutationCriterias.findAll();
	}
	
	// get number of ValutationCriterias
	@RequestMapping("/count")
	public Long count() {
		return valutationCriterias.count();
	}
	
	// create a ValutationCriteria with a specific content
	@RequestMapping("/create")
	public void createCriteria(@RequestParam(required=true) String name)
	{
		ValutationCriteria v = new ValutationCriteria(name);
		valutationCriterias.save(v);
	}
}