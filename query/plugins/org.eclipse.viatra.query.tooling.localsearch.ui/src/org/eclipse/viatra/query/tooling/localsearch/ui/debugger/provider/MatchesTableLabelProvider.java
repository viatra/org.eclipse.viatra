/*******************************************************************************
 * Copyright (c) 2010-2015, Marton Bur, Akos Horvath, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Marton Bur - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra.query.tooling.localsearch.ui.debugger.provider;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.viatra.query.runtime.localsearch.MatchingFrame;

/**
 * 
 * @author Marton Bur
 *
 */
public class MatchesTableLabelProvider extends ColumnLabelProvider {
	private int columnIndex;
	private Font boldFont = null;
	private TableViewer viewer;

	public MatchesTableLabelProvider(int i, boolean parameter, TableViewer viewer) {
		this.columnIndex = i;
		this.viewer = viewer;
		if (parameter) {
			FontRegistry fregistry = JFaceResources.getFontRegistry();
			boldFont = fregistry.getBold(JFaceResources.DEFAULT_FONT);
		}
	}

	@Override
	public Font getFont(Object element) {
		if (boldFont != null) {
			return boldFont;
		}
		return super.getFont(element);
	}

	@Override
	public Color getBackground(final Object element) {

		MatchingFrame currentFrame = (MatchingFrame) element;
		
		@SuppressWarnings("unchecked")
		List<MatchingFrame> input = (List<MatchingFrame>) viewer.getInput();
		MatchingFrame lastFrame = input.get(input.size() - 1);
		if (currentFrame.equals(lastFrame)) {
			return new Color(Display.getDefault(), 0xFF, 0, 0);
		}
		return super.getBackground(element);
	}

	@Override
	public String getText(Object inputElement) {

		MatchingFrame frame = (MatchingFrame) inputElement;
		Object element = frame.get(columnIndex);

		if (element == null) {
			return "null";
		}
		if (element instanceof EObject) {
			EObject eObject = ((EObject) element);

			EStructuralFeature feature = eObject.eClass().getEStructuralFeature("name");
			if (feature != null) {
				if (!feature.isMany()) {
					return eObject.eGet(feature).toString();
				}
			} else {
				feature = eObject.eClass().getEStructuralFeature(0);
				if (!feature.isMany()) {
					return eObject.eGet(feature).toString();
				}
			}
		}
		return element.toString();
	}

}
