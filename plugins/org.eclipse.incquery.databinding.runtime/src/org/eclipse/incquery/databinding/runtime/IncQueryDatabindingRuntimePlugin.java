package org.eclipse.incquery.databinding.runtime;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class IncQueryDatabindingRuntimePlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "org.eclipse.incquery.databinding.runtime";
    
    // The shared instance
    private static IncQueryDatabindingRuntimePlugin plugin;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
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
