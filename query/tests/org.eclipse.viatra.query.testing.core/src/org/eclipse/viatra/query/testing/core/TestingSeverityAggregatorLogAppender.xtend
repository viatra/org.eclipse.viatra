/** 
 * Copyright (c) 2010-2015, Balázs Grill, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
    val Level minReportSeverity = Level.ERROR
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
    
    /**
     * @since 2.0
     */
    def String getOutput() {
        return if (event.getLevel.isGreaterOrEqual(minReportSeverity))  "" else layout.format(event);
    }
    
    def getSeverity(){
        severity
    }
}