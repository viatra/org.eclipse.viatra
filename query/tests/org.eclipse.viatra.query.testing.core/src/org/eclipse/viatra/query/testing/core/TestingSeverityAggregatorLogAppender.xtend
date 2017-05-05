/** 
 * Copyright (c) 2010-2015, Balázs Grill, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 * Balázs Grill - initial API and implementation
 */
package org.eclipse.viatra.query.testing.core

import org.apache.log4j.AppenderSkeleton
import org.apache.log4j.Level
import org.apache.log4j.spi.LoggingEvent

/** 
 * @author Grill Balázs
 */
class TestingSeverityAggregatorLogAppender extends AppenderSkeleton {
    
    var severity=Level.INFO
    var LoggingEvent event;
    
    override void close() {
    }
    
    def void clear(){
        severity = Level.INFO
    }
    
    override boolean requiresLayout() {
        return false 
    }
    override protected void append(LoggingEvent event) {
       val eventLevel = event.getLevel
       if (severity.toInt < eventLevel.toInt){
           severity = eventLevel
           this.event = event
       }
    }
    
    def getEvent(){
        event
    }
    
    def getSeverity(){
        severity
    }
}