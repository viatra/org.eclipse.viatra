/*******************************************************************************
 * Copyright (c) 2010-2013, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Istvan Rath - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.addon.viewers.tooling.ui.zest.views.tabs;

import org.eclipse.gef.layout.algorithms.SpaceTreeLayoutAlgorithm;

public class CustomSpaceTreeLayoutAlgorithm extends SpaceTreeLayoutAlgorithm {

	public CustomSpaceTreeLayoutAlgorithm(int bottomUp, boolean b) {
		super(bottomUp);
		customStuff();
	}

	public CustomSpaceTreeLayoutAlgorithm() {
		super();
		customStuff();
	}
	
	void customStuff() {
		setLeafGap(50);
		setBranchGap(50 + 15);
		setLayerGap(70);
	}
	
}
