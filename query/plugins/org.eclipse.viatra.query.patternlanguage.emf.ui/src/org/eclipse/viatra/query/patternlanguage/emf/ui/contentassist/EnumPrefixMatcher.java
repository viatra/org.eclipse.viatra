/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.patternlanguage.emf.ui.contentassist;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.xtext.ui.editor.contentassist.PrefixMatcher;

/**
 * A Prefix Matcher for {@link EEnum} types.
 * 
 * @author Tamas Szabo
 * 
 */
public class EnumPrefixMatcher extends PrefixMatcher {
   
	private String typeName;
	
	public EnumPrefixMatcher(String typeName) {
        this.typeName = typeName;
    }
    
    @Override
    public boolean isCandidateMatchingPrefix(String name, String prefix) {
        // candidate is matching the prefix if:
    	// (1) EEnum's type name starts with the prefix 
    	// (2) the candidate name starts with the prefix
    	// The candidate name contains two ':' chars in the beginning, the written prefix may not
    	
    	String fPrefix =     	
    			(prefix.length() >= 2 && prefix.charAt(0) != ':' && prefix.charAt(1) != ':') ?
    					"::"+prefix :
    					prefix
    			;
        return (typeName.startsWith(prefix) || name.startsWith(fPrefix));
    }
}
