package org.eclipse.viatra.cep.vepl.ui.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.builder.BuilderParticipant;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.resource.IResourceDescription.Delta;

public class VeplBuilderParticipant extends BuilderParticipant {

    @Override
    public void build(final IBuildContext context, IProgressMonitor monitor) throws CoreException {
        super.build(context, monitor);

    }

    @Override
    protected void handleChangedContents(Delta delta, IBuildContext context,
            EclipseResourceFileSystemAccess2 fileSystemAccess) throws CoreException {
        super.handleChangedContents(delta, context, fileSystemAccess);
    }

    @Override
    protected boolean shouldGenerate(Resource resource, IBuildContext context) {
        return super.shouldGenerate(resource, context);
    }
}
