package org.eclipse.incquery.viewers.tooling.ui.zest.views.tabs;

import org.eclipse.gef4.zest.layouts.algorithms.SpaceTreeLayoutAlgorithm;

public class CustomSpaceTreeLayoutAlgorithm extends SpaceTreeLayoutAlgorithm {

	public CustomSpaceTreeLayoutAlgorithm(int bottomUp, boolean b) {
		super(bottomUp, b);
		customStufF();
	}

	public CustomSpaceTreeLayoutAlgorithm() {
		super();
		customStufF();
	}
	
	void customStufF() {
		setLeafGap(50);
		setBranchGap(50 + 15);
		setLayerGap(70);
	}
	
}
