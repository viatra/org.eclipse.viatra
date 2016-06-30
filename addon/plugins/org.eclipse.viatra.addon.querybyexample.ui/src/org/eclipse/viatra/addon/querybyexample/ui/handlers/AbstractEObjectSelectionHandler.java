package org.eclipse.viatra.addon.querybyexample.ui.handlers;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.viatra.addon.querybyexample.ui.QBEViewUtils;
import org.eclipse.viatra.addon.querybyexample.ui.ui.QBEView;
import org.eclipse.viatra.query.tooling.ui.queryexplorer.IModelConnector;

public abstract class AbstractEObjectSelectionHandler extends AbstractHandler {

    public static final String QBE_COMMON_ALERT_TITLE = "Error";

    protected abstract void handleSelection(QBEView qbeView, Collection<EObject> selectedEObjects);

    private static final String QBE_NO_EOBJECTS_IN_SELECTION_ERROR = "There are no EObjects in the active selection!";

    public AbstractEObjectSelectionHandler() {
        super();
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
    
        IEditorPart editor = HandlerUtil.getActiveEditor(event);
        QBEView qbeView = QBEViewUtils.getQBEView(event);
    
        // getting the selection, using the service to construct the pattern
        // validating the active selection
        ISelection selection = editor.getEditorSite().getSelectionProvider().getSelection();
        if (selection instanceof IStructuredSelection)
            qbeView.setSelection((IStructuredSelection) selection);
        IModelConnector selectionProvider = getSelectionProviderFromIEditorPart(editor);
        Collection<EObject> selectedEObjects = selectionProvider.getSelectedEObjects();
        if (selectedEObjects == null || selectedEObjects.isEmpty()) {
            MessageDialog.openError(HandlerUtil.getActiveShell(event), 
                    QBE_COMMON_ALERT_TITLE,
                    QBE_NO_EOBJECTS_IN_SELECTION_ERROR);
            return null;
        }
        handleSelection(qbeView, selectedEObjects);
        QBEViewUtils.getMainSourceProvider(event).setModelLoadedState();
    
        return null;
    }

    private IModelConnector getSelectionProviderFromIEditorPart(IEditorPart editorPart) {
        if (editorPart != null) {
            Object adaptedObject = editorPart.getAdapter(IModelConnector.class);
            if (adaptedObject != null) {
                return (IModelConnector) adaptedObject;
            }
    
            Platform.getAdapterManager().loadAdapter(editorPart, IModelConnector.class.getName());
            adaptedObject = editorPart.getAdapter(IModelConnector.class);
            if (adaptedObject != null) {
                return (IModelConnector) adaptedObject;
            } else {
                StatusManager.getManager()
                        .handle(new Status(IStatus.ERROR, QBEViewUtils.PLUGIN_ID, IStatus.ERROR,
                                "Adapted object (Model Connector instance) not found.", new IllegalStateException()));
            }
        }
        return null;
    }

}