package org.eclipse.incquery.querybasedui.runtime.sources;

import org.eclipse.jface.internal.databinding.viewers.ViewerUpdater;
import org.eclipse.jface.viewers.AbstractListViewer;

/**
 * A {@link ViewerUpdater} that updates {@link AbstractListViewer} instances. Copied from the
 * org.eclipse.databinding.viewers plug-in.
 * 
 * @since 1.2
 */
@SuppressWarnings("restriction")
public class ListViewerUpdater extends ViewerUpdater {
	private AbstractListViewer viewer;

	ListViewerUpdater(AbstractListViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	public void insert(Object element, int position) {
		viewer.insert(element, position);
	}

	public void remove(Object element, int position) {
		viewer.remove(element);
	}

	public void add(Object[] elements) {
		viewer.add(elements);
	}

	public void remove(Object[] elements) {
		viewer.remove(elements);
	}
}
