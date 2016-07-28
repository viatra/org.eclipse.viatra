package org.eclipse.viatra.transformation.debug.model.transformationstate;

public class ActivationParameter {
    private final Object value;
    private final String name;
    
    
    public ActivationParameter(Object value, String name) {
        super();
        this.value = value;
        this.name = name;
    }


    public Object getValue() {
        return value;
    }


    public String getName() {
        return name;
    };
    
    
}
