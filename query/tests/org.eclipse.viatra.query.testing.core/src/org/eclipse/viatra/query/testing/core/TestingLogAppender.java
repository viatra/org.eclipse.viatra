/*******************************************************************************
 * Copyright (c) 2010-2012, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Abel Hegedus - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.testing.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.varia.LevelRangeFilter;

/**
 * @author Abel Hegedus
 * @deprecated use {@link TestingSeverityAggregatorLogAppender} or {@link ConsoleAppender} instead
 */
@Deprecated
public class TestingLogAppender extends AppenderSkeleton implements Appender {

    private static final String FORMAT_LAYOUT = "%m%n";
    private static final Level LEVEL_MIN = Level.WARN;

    private final Map<String, StringBuilder> messages = new HashMap<>();
    private final StringBuilder output = new StringBuilder();

    public TestingLogAppender() {
        layout = new PatternLayout(FORMAT_LAYOUT);

        LevelRangeFilter newFilter = new LevelRangeFilter();
        newFilter.setAcceptOnMatch(true);
        newFilter.setLevelMin(LEVEL_MIN);
        addFilter(newFilter);
    }

    /**
     * @return the messages
     */
    public Map<String, StringBuilder> getMessages() {
        return messages;
    }

    /**
     * @return the output
     */
    public StringBuilder getOutput() {
        return output;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    @Override
    protected void append(LoggingEvent event) {
        String formatted = layout.format(event);
        String levelName = event.getLevel().toString();

        StringBuilder sb = messages.get(levelName);
        if (sb == null) {
            sb = new StringBuilder();
            messages.put(levelName, sb);
        }
        sb.append(formatted);
        output.append(formatted);

        ThrowableInformation throwInfo = event.getThrowableInformation();
        if (throwInfo != null) {
            String[] lines = throwInfo.getThrowableStrRep();
            for (String line : lines) {
                output.append(line).append("%n");
            }
        }
    }

}
