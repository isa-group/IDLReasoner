
package es.us.isa.idlreasoner.model;

import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "IfTheDepedency",
    "OrDepdencny",
    "negated",
    "AllOrNone",
    "ZeroOnOne"
})
@Generated("jsonschema2pojo")
public class Term {
	
    @JsonProperty("negated")
    private Boolean negated;

    @JsonProperty("IfTheDepedency")
    private IfThenDepedency ifTheDepedency;
    @JsonProperty("OrDepdencny")
    private OrDepdencny orDepdencny;
    @JsonProperty("AllOrNone")
    private AllOrNone allOrNone;
    @JsonProperty("ZeroOrOne")
    private ZeroOrOne zeroOrOne;
    @JsonProperty("OnlyOne")
    private OnlyOne onlyOne;
    
    @JsonProperty("relationalDependency")
    private RelationalDependency relationalDependency;
    @JsonProperty("parameter")
    private String parameter;
    @JsonProperty("presence")
    private Boolean presence;
	@JsonProperty("and")
    private And and;
    @JsonProperty("or")
    private Or or;
    @JsonProperty("relation")
    private String relation;
    @JsonProperty("value")
    private String value;
    
    @JsonProperty("OnlyOne")
    public OnlyOne getOnlyOne() {
		return onlyOne;
	}
    @JsonProperty("OnlyOne")
	public void setOnlyOne(OnlyOne onlyOne) {
		this.onlyOne = onlyOne;
	}
	@JsonProperty("relationalDependency")
    public RelationalDependency getRelationalDependency() {
		return relationalDependency;
	}
    @JsonProperty("relationalDependency")
	public void setRelationalDependency(RelationalDependency relationalDependency) {
		this.relationalDependency = relationalDependency;
	}

	@JsonProperty("parameter")
    public String getParameter() {
		return parameter;
	}

    @JsonProperty("parameter")
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

    @JsonProperty("presence")
	public Boolean getPresence() {
		return presence;
	}

    @JsonProperty("presence")
	public void setPresence(Boolean presence) {
		this.presence = presence;
	}

	@JsonProperty("and")
	public And getAnd() {
		return and;
	}

	@JsonProperty("and")
	public void setAnd(And and) {
		this.and = and;
	}
	
    @JsonProperty("or")
	public Or getOr() {
		return or;
	}

    @JsonProperty("or")
	public void setOr(Or or) {
		this.or = or;
	}

    @JsonProperty("relation")
	public String getRelation() {
		return relation;
	}

    @JsonProperty("relation")
	public void setRelation(String relation) {
		this.relation = relation;
	}

    @JsonProperty("value")
	public String getValue() {
		return value;
	}

    @JsonProperty("value")
	public void setValue(String value) {
		this.value = value;
	}


    @JsonProperty("IfTheDepedency")
    public IfThenDepedency getIfTheDepedency() {
        return ifTheDepedency;
    }

    @JsonProperty("IfTheDepedency")
    public void setIfTheDepedency(IfThenDepedency ifTheDepedency) {
        this.ifTheDepedency = ifTheDepedency;
    }

    @JsonProperty("OrDepdencny")
    public OrDepdencny getOrDepdencny() {
        return orDepdencny;
    }

    @JsonProperty("OrDepdencny")
    public void setOrDepdencny(OrDepdencny orDepdencny) {
        this.orDepdencny = orDepdencny;
    }

    @JsonProperty("negated")
    public Boolean getNegated() {
        return negated;
    }

    @JsonProperty("negated")
    public void setNegated(Boolean negated) {
        this.negated = negated;
    }

    @JsonProperty("AllOrNone")
    public AllOrNone getAllOrNone() {
        return allOrNone;
    }

    @JsonProperty("AllOrNone")
    public void setAllOrNone(AllOrNone allOrNone) {
        this.allOrNone = allOrNone;
    }

    @JsonProperty("ZeroOnOne")
    public ZeroOrOne getZeroOrOne() {
        return zeroOrOne;
    }

    @JsonProperty("ZeroOnOne")
    public void setZeroOnOne(ZeroOrOne zeroOnOne) {
        this.zeroOrOne = zeroOnOne;
    }
    
	@Override
	public String toString() {
		return "Term [negated=" + negated + ", ifTheDepedency=" + ifTheDepedency + ", orDepdencny=" + orDepdencny
				+ ", allOrNone=" + allOrNone + ", zeroOrOne=" + zeroOrOne + ", onlyOne=" + onlyOne
				+ ", relationalDependency=" + relationalDependency + ", parameter=" + parameter + ", presence="
				+ presence + ", and=" + and + ", or=" + or + ", relation=" + relation + ", value=" + value + "]";
	}            

}
