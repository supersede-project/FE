package demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name="requirements")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Requirement {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long requirementId;
    private String name;
    private String description;
    
    public Requirement() {
    }
 
    public Requirement(String name, String description) {
        this.name = name;
        this.setDescription(description);
    }
 
    public Long getRequirementId() {
        return requirementId;
    }
 
    public void setRequirementId(Long requirementId) {
        this.requirementId = requirementId;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}