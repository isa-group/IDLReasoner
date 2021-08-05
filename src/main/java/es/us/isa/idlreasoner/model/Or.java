
package es.us.isa.idlreasoner.model;

import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "terms",
    "negated"
})
@Generated("jsonschema2pojo")
public class Or {

    @JsonProperty("terms")
    private List<Term> terms = null;
    @JsonProperty("negated")
    private Boolean negated;

    @JsonProperty("terms")
    public List<Term> getTerms() {
        return terms;
    }

    @JsonProperty("terms")
    public void setTerms(List<Term> terms) {
        this.terms = terms;
    }

    @JsonProperty("negated")
    public Boolean getNegated() {
        return negated;
    }

    @JsonProperty("negated")
    public void setNegated(Boolean negated) {
        this.negated = negated;
    }

	@Override
	public String toString() {
		return "Or [terms=" + terms + ", negated=" + negated + "]";
	}

    
}
