package org.eclipse.viatra.integration.xcore.modeleditor;

import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageExecutableExtensionFactory;
import org.osgi.framework.Bundle;

public class XcoreVqlEditorExtensionFactory extends EMFPatternLanguageExecutableExtensionFactory {

    @Override
    protected Bundle getBundle() {
        return Activator.getDefault().getBundle();
    }
}
