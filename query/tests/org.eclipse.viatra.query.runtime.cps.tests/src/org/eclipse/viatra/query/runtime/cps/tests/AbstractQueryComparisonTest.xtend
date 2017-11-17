/*******************************************************************************
 * Copyright (c) 2014-2016 Zoltan Ujhelyi, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.runtime.cps.tests

import java.io.IOException
import java.util.Collection
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.viatra.query.runtime.emf.EMFScope
import org.eclipse.viatra.query.testing.snapshot.QuerySnapshot
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

/**
 * This class can be used to compare the results of various query backends to a given snapshot. This approach provides better reports in case of errors in a single backend as it describes the problematic case automatically. 
 */
@RunWith(Parameterized)
abstract class AbstractQueryComparisonTest {

    protected var QuerySnapshot snapshot
    protected var EMFScope scope

    def abstract String getSnapshotUri()

    @Parameters(name="{index} : {0}")
    static def Collection<Object[]> data() {
        return #[
            #[BackendType.Rete],
            #[BackendType.LocalSearch],
            #[BackendType.LocalSearch_Flat],
            #[BackendType.LocalSearch_NoBase],
            #[BackendType.LocalSearch_Generic]
        ]
    }

    @Parameter(0)
    public var BackendType type

    @Before
    def void before() {
        val rs = new ResourceSetImpl
        scope = new EMFScope(rs)
        snapshot = rs.loadSnapshotFromUri(URI.createPlatformPluginURI(snapshotUri, true))
    }
    
    /**
     * Loads a query snapshot from a given uri in a selected resource set.
     * @throws IOException if the file cannot be opened or does not contain a query snapshot at its root
     */
    private def QuerySnapshot loadSnapshotFromUri(ResourceSet set, URI uri) throws IOException {
        val res = set.getResource(uri, true);
        if (!res.loaded) {
            res.load(newHashMap)
        }
        val _snapshot = res.getContents()?.findFirst[it instanceof QuerySnapshot]  
        if (_snapshot instanceof QuerySnapshot) {
            return _snapshot as QuerySnapshot
        } else {
            throw new IOException(String.format("Resource at uri %S does not contain a query snapshot.", uri.toString()));
        }
    }
}
