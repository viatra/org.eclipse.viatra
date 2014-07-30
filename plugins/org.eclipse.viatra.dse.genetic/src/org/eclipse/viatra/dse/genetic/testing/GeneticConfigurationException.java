package org.eclipse.viatra.dse.genetic.testing;

import org.eclipse.viatra.dse.api.DSEException;

public class GeneticConfigurationException extends DSEException{

    private static final long serialVersionUID = -1875675292849836784L;

    /**
     * @see RuntimeException#RuntimeException().
     */
    public GeneticConfigurationException() {
        super();
    }

    /**
     * @see RuntimeException#RuntimeException(String)).
     */
    public GeneticConfigurationException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)).
     */
    public GeneticConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see RuntimeException#RuntimeException(Throwable)).
     */
    public GeneticConfigurationException(Throwable cause) {
        super(cause);
    }

}
