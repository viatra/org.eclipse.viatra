package service;

import org.eclipse.incquery.validation.runtime.ui.ValidationInitUtil;
import org.eclipse.papyrus.infra.core.editor.IMultiDiagramEditor;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.services.IService;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;

public class InitializeValidationService implements IService {

	public InitializeValidationService() {
	}

	private ServicesRegistry servicesRegistry;

	@Override
	public void init(ServicesRegistry servicesRegistry) {
		this.servicesRegistry = servicesRegistry;		
	}

	@Override
	public void startService() throws ServiceException {
		ModelSet modelSet = servicesRegistry.getService(ModelSet.class);
		IMultiDiagramEditor diagramEditor = servicesRegistry.getService(IMultiDiagramEditor.class);
		ValidationInitUtil.initializeAdapters(diagramEditor, modelSet);
	}

	@Override
	public void disposeService() throws ServiceException {
	}

}
