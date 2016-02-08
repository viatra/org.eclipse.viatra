package org.eclipse.incquery.xcore.editor;

import org.eclipse.incquery.patternlanguage.emf.ui.EMFPatternLanguageExecutableExtensionFactory;
import org.osgi.framework.Bundle;

public class XcoreEiqEditorExtensionFactory  extends
	EMFPatternLanguageExecutableExtensionFactory {

@Override
protected Bundle getBundle() {
	return Activator.getDefault().getBundle();
}
}
