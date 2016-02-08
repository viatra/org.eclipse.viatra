package org.eclipse.incquery.patternlanguage;

import org.eclipse.incquery.patternlanguage.validation.whitelist.PureWhitelistExtensionLoader;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class PatternLanguagePlugin implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        PureWhitelistExtensionLoader.load();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

}
