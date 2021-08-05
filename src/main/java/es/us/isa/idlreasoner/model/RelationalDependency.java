
package es.us.isa.idlreasoner.model;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "relation",
    "firstTerm",
    "secondTerm"
})
@Generated("jsonschema2pojo")
public class RelationalDependency {

    @JsonProperty("relation")
    private String relation;
    @JsonProperty("firstTerm")
    private String firstTerm;
    @JsonProperty("secondTerm")
    private String secondTerm;

    @JsonProperty("relation")
    public String getRelation() {
        return relation;
    }

    @JsonProperty("relation")
    public void setRelation(String relation) {
        this.relation = relation;
    }

    @JsonProperty("firstTerm")
    public String getFirstTerm() {
        return firstTerm;
    }

    @JsonProperty("firstTerm")
    public void setFirstTerm(String firstTerm) {
        this.firstTerm = firstTerm;
    }

    @JsonProperty("secondTerm")
    public String getSecondTerm() {
        return secondTerm;
    }

    @JsonProperty("secondTerm")
    public void setSecondTerm(String secondTerm) {
        this.secondTerm = secondTerm;
    }

	@Override
	public String toString() {
		return "RelationalDependency [relation=" + relation + ", firstTerm=" + firstTerm + ", secondTerm=" + secondTerm
				+ "]";
	}
    
    
   

}
