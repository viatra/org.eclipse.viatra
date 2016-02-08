/*******************************************************************************
 * Copyright (c) 2010-2014, Miklos Foldenyi, Andras Szabolcs Nagy, Abel Hegedus, Akos Horvath, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *   Miklos Foldenyi - initial API and implementation
 *   Andras Szabolcs Nagy - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.dse.visualizer;

public class DesignSpaceVisualizerOptions {

    public boolean showExplorationTrace = true;
    public boolean showStateCodes = true;
    public boolean showTransitionCodes = true;

    public DesignSpaceVisualizerOptions withOutExploraionTrace() {
        showExplorationTrace = false;
        return this;
    }

    public DesignSpaceVisualizerOptions withOutstateCodes() {
        showStateCodes = false;
        return this;
    }

    public DesignSpaceVisualizerOptions withOutTransitionCodes() {
        showTransitionCodes = false;
        return this;
    }

    public boolean isShowExplorationTrace() {
        return showExplorationTrace;
    }

    public void setShowExplorationTrace(boolean showExplorationTrace) {
        this.showExplorationTrace = showExplorationTrace;
    }

    public boolean isShowStateCodes() {
        return showStateCodes;
    }

    public void setShowStateCodes(boolean showStateCodes) {
        this.showStateCodes = showStateCodes;
    }

    public boolean isShowTransitionCodes() {
        return showTransitionCodes;
    }

    public void setShowTransitionCodes(boolean showTransitionCodes) {
        this.showTransitionCodes = showTransitionCodes;
    }

}
