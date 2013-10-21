package library.viewers.views;

import java.util.Collection;

import library.util.WriterQuerySpecification;

import org.eclipse.gef4.zest.core.viewers.AbstractZoomableViewer;
import org.eclipse.gef4.zest.core.viewers.GraphViewer;
import org.eclipse.gef4.zest.core.viewers.IZoomableWorkbenchPart;
import org.eclipse.incquery.runtime.api.IModelConnectorTypeEnum;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.viewers.runtime.extensions.ViewersComponentConfiguration;
import org.eclipse.incquery.viewers.runtime.zest.extensions.IncQueryViewersZestViewSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

public class SampleZestView extends ViewPart implements IZoomableWorkbenchPart {
	private GraphViewer graphViewer;

	private IncQueryViewersZestViewSupport viewSupport;

	public void createPartControl(final Composite parent) {
		try {
			this.graphViewer = new GraphViewer(parent, SWT.NONE);
			final Collection<String> queries = Sets.newHashSet();
			queries.add(WriterQuerySpecification.instance().getPatternFullyQualifiedName());
			this.viewSupport = new IncQueryViewersZestViewSupport(
					this, 
					ViewersComponentConfiguration.fromQuerySpecFQNs( queries ),
					IModelConnectorTypeEnum.RESOURCESET,
					graphViewer);
			this.viewSupport.init();
			this.viewSupport.createPartControl(parent,this.graphViewer.getGraphControl());
			this.viewSupport.createToolbar();
		} catch (IncQueryException e) {
			e.printStackTrace();
		}
	}

	public void setFocus() {
		boolean _notEquals = (!Objects.equal(this.graphViewer, null));
		if (_notEquals) {
			Control _control = this.graphViewer.getControl();
			_control.setFocus();
		}
	}

	public void dispose() {
		this.viewSupport.dispose();
		super.dispose();
	}

	public AbstractZoomableViewer getZoomableViewer() {
		return this.graphViewer;
	}

}
