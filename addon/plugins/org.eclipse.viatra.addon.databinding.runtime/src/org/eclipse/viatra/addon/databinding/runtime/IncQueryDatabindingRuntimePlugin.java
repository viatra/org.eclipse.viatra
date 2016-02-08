package org.eclipse.viatra.addon.databinding.runtime;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class IncQueryDatabindingRuntimePlugin extends Plugin {

    public static final String PLUGIN_ID = "org.eclipse.viatra.addon.databinding.runtime";
    
    // The shared instance
    private static IncQueryDatabindingRuntimePlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static IncQueryDatabindingRuntimePlugin getDefault() {
        return plugin;
    }
    
}
