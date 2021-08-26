
package es.us.isa.idlreasoner.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "relation",
    "result",
    "operation"
})
@Generated("jsonschema2pojo")
public class ArithmeticDependency {

    @JsonProperty("relation")
    private String relation;
    @JsonProperty("result")
    private String result;
    @JsonProperty("operation")
    private List<Operation> operation = null;

    @JsonProperty("relation")
    public String getRelation() {
        return relation;
    }

    @JsonProperty("relation")
    public void setRelation(String relation) {
        this.relation = relation;
    }

    @JsonProperty("result")
    public String getResult() {
        return result;
    }

    @JsonProperty("result")
    public void setResult(String result) {
        this.result = result;
    }

    @JsonProperty("operation")
    public List<Operation> getOperation() {
        return operation;
    }

    @JsonProperty("operation")
    public void setOperation(List<Operation> operation) {
        this.operation = operation;
    }

}
