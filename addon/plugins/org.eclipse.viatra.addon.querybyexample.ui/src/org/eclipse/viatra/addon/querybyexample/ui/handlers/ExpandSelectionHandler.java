package org.eclipse.viatra.addon.querybyexample.ui.handlers;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;

public class ExpandSelectionHandler extends AbstractEObjectSelectionHandler {

    @Override
    protected void handleSelection(QBEView qbeView, Collection<EObject> selectedEObjects) {
        qbeView.expand(selectedEObjects);
    }

}
