/*******************************************************************************
 * Copyright (c) 2010-2018, Gabor Bergmann, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.runtime.matchers.scopes.tables;

import org.eclipse.viatra.query.runtime.matchers.util.Direction;

/**
 * Modifies the contents of a binary {@link IIndexTable}.
 * 
 * @since 2.0
 * @author Gabor Bergmann
 */
public interface ITableWriterBinary<Source, Target> {
    /**
     * Adds/removes a row to/from the table.
     * 
     * @param direction
     *            tells whether putting a row into the table or deleting
     * 
     *            TODO: store as multiset, return bool?
     */
    void write(Direction direction, Source source, Target target);

    /**
     * Intersection type for writers that are also tables
     */
    interface Table<Source, Target> extends ITableWriterBinary<Source, Target>, IIndexTable {
    }

    /**
     * /dev/null implementation
     * 
     * @author Gabor Bergmann
     */
    static class Nop<Source, Target> implements ITableWriterBinary<Source, Target> {
        @Override
        public void write(Direction direction, Source source, Target target) {
            // NO-OP
        }

    }
}
