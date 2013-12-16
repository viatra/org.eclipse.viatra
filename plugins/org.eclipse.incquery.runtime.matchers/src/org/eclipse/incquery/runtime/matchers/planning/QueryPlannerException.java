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
package org.eclipse.incquery.runtime.matchers.planning;

import java.util.Arrays;

/**
 * @author Zoltan Ujhelyi
 *
 */
public class QueryPlannerException extends Exception {

    private static final long serialVersionUID = -8272290113656867086L;
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
    
    protected Object patternDescription;
    protected String templateMessage;
    protected String[] templateContext;
    
    protected String shortMessage;
    
    /**
     * @param message
     *            The template of the exception message
     * @param context
     *            The data elements to be used to instantiate the template. Can be null if no context parameter is
     *            defined
     * @param patternDescription
     *            the PatternDescription where the exception occurred
     */
    public QueryPlannerException(String message, String[] context, String shortMessage, Object patternDescription) {
        super(bind(message, context));
        initializeFields(message, context, shortMessage, patternDescription);
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
    public QueryPlannerException(String message, String[] context, String shortMessage, Object patternDescription,
            Throwable cause) {
        super(bind(message, context), cause);
        initializeFields(message, context, shortMessage, patternDescription);
    }
    
    public Object getPatternDescription() {
        return patternDescription;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String[] getTemplateContext() {
        return Arrays.copyOf(templateContext, templateContext.length);
    }

    public String getTemplateMessage() {
        return templateMessage;
    }

    private void initializeFields(String message, String[] context, String shortMessage, Object patternDescription) {
        this.patternDescription = patternDescription;
        this.templateMessage = message;
        this.templateContext = Arrays.copyOf(context, context.length);
        this.shortMessage = shortMessage;
    }

    
    public void setPatternDescription(Object patternDescription) {
        this.patternDescription = patternDescription;
    }

}