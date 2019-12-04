package es.us.isa.idlreasoner.pojos;

public class Variable {
    private String name;
    private String type;
    private Boolean required;

    public Variable(String name, String type, Boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
        if (required == null) {
            this.required = false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
        if (required == null) {
            this.required = false;
        }
    }
}
