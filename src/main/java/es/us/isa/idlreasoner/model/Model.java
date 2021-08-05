
package es.us.isa.idlreasoner.model;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "terms"
})
@Generated("jsonschema2pojo")
public class Model {

    @JsonProperty("terms")
    private List<Term> terms = null;

    @JsonProperty("terms")
    public List<Term> getTerms() {
        return terms;
    }

    @JsonProperty("terms")
    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

	@Override
	public String toString() {
		return "Model [terms=" + terms + "]";
	}
    
    

}
