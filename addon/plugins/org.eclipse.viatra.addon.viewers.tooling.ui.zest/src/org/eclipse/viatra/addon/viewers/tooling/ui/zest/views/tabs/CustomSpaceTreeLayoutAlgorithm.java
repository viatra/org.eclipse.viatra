package org.eclipse.viatra.addon.viewers.tooling.ui.zest.views.tabs;

import org.eclipse.gef4.layout.algorithms.SpaceTreeLayoutAlgorithm;

public class CustomSpaceTreeLayoutAlgorithm extends SpaceTreeLayoutAlgorithm {

	public CustomSpaceTreeLayoutAlgorithm(int bottomUp, boolean b) {
		super(bottomUp, b);
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
