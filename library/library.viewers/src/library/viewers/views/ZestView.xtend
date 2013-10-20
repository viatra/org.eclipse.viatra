package library.viewers.views

import com.google.common.collect.Sets
import library.util.BookOfWriterQuerySpecification
import library.util.BookQuerySpecification
import library.util.WriterQuerySpecification
import org.eclipse.gef4.zest.core.viewers.GraphViewer
import org.eclipse.gef4.zest.core.viewers.IZoomableWorkbenchPart
import org.eclipse.incquery.viewers.runtime.extensions.ViewersComponentConfiguration
import org.eclipse.incquery.viewers.runtime.zest.extensions.IncQueryViewersZestViewSupport
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.ui.part.ViewPart

class ZestView extends ViewPart implements IZoomableWorkbenchPart {
	
	GraphViewer graphViewer;
	
	extension IncQueryViewersZestViewSupport viewSupport
	
	override createPartControl(Composite parent) {
		// initialize Zest viewer first
		graphViewer = new GraphViewer(parent, SWT.NONE)
		
		// initialize support object
		val queries = Sets.newHashSet
		queries.add( WriterQuerySpecification.instance.patternFullyQualifiedName )
		queries.add( BookQuerySpecification.instance.patternFullyQualifiedName )
		queries.add( BookOfWriterQuerySpecification.instance.patternFullyQualifiedName )
		val config = ViewersComponentConfiguration.fromQuerySpecFQNs( queries )
		viewSupport = new IncQueryViewersZestViewSupport(this,config,graphViewer)
		
		// initialization with the help of support object
		init
		createPartControl(parent,graphViewer.graphControl)
		createToolbar
	}
	
	override setFocus() {
		if (graphViewer!=null) {
			graphViewer.control.setFocus
		}
	}
	
	override dispose() {
		viewSupport.dispose
		super.dispose()
	}
	
	override getZoomableViewer() {
		return graphViewer
	}
	
}