/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.incquery.runtime.rete.construction;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class OperationCompilerException extends Exception {

    private static final long serialVersionUID = -8272290113656867086L;
    protected Object patternDescription;
    protected String templateMessage;
    protected String[] templateContext;
    protected String shortMessage;

    /**
     * Binding the '{n}' (n = 1..N) strings to contextual conditions in 'context'
     * 
     * @param context
     *            : array of context-sensitive Strings
     */
    protected static String bind(String message, String[] context) {
        if (context == null) 
            return message;
    
        String internal = message;
        for (int i = 0; i < context.length; i++) {
            internal = internal.replace("{" + (i + 1) + "}", context[i] != null ? context[i] : "<<null>>");
        }
        return internal;
    }

    /**
     * 
     */
    public OperationCompilerException() {
        super();
    }

    /**
     * @param message
     */
    public OperationCompilerException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public OperationCompilerException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public OperationCompilerException(String message, Throwable cause) {
        super(message, cause);
    }

    public Object getPatternDescription() {
        return patternDescription;
    }

    public void setPatternDescription(Object patternDescription) {
        this.patternDescription = patternDescription;
    }

    public String getTemplateMessage() {
        return templateMessage;
    }

    public String[] getTemplateContext() {
        return templateContext;
    }

    /**
     * @param message
     *            The template of the exception message
     * @param context
     *            The data elements to be used to instantiate the template. Can be null if no context parameter is
     *            defined
     * @param patternDescription
     *            the PatternDescription where the exception occurred
     */
    public OperationCompilerException(String message, String[] context, String shortMessage, Object patternDescription) {
        super(bind(message, context));
        this.patternDescription = patternDescription;
        this.templateMessage = message;
        this.templateContext = context;
        this.shortMessage = shortMessage;
    }

    /**
     * @param message
     *            The template of the exception message
     * @param context
     *            The data elements to be used to instantiate the template. Can be null if no context parameter is
     *            defined
     * @param patternDescription
     *            the PatternDescription where the exception occurred
     */
    public OperationCompilerException(String message, String[] context, String shortMessage, Object patternDescription,
            Throwable cause) {
        super(bind(message, context), cause);
        this.patternDescription = patternDescription;
        this.templateMessage = message;
        this.templateContext = context;
        this.shortMessage = shortMessage;
    }
    
    public String getShortMessage() {
        return shortMessage;
    }

}