package org.eclipse.viatra.query.tooling.ui.queryregistry.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.viatra.query.runtime.registry.IQuerySpecificationRegistryEntry;
import org.eclipse.viatra.query.tooling.ui.queryregistry.QueryRegistryTreeEntry;

public class ShowLocationHandler extends ShowPatternLocationHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
        QueryRegistryTreeEntry entry = (QueryRegistryTreeEntry) selection.getFirstElement();
        IQuerySpecificationRegistryEntry registryEntry = entry.getEntry();
        showPatternLocation(event, registryEntry);   
        return null;
    }

}
