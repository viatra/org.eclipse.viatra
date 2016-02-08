/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.wizards.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.ui.PlatformUI;
import org.eclipse.viatra.query.tooling.core.targetplatform.ITargetPlatformMetamodelLoader;

/**
 * An {@link IListAdapter} implementation for importing {@link EPackage}s.
 * 
 * @author Tamas Szabo
 * 
 */
@SuppressWarnings("restriction")
public class ImportListAdapter implements IListAdapter<String> {

	private ITargetPlatformMetamodelLoader metamodelLoader;

	public ImportListAdapter(ITargetPlatformMetamodelLoader metamodelLoader) {
		this.metamodelLoader = metamodelLoader;
	}

	@Override
	public void customButtonPressed(ListDialogField<String> field, int index) {
		// if Add button is pressed
		if (index == 0) {
			ElementSelectionDialog listDialog = new ElementSelectionDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new ImportListLabelProvider(),
					"EPackage");
			listDialog.setTitle("Select packages to import");
			listDialog.setMessage("Select one or more package(s) (* = any string, ? = any char):");
			Object[] input = getElements(field);
			listDialog.setElements(input);
			listDialog.open();
			Object[] result = listDialog.getResult();
			if (result != null && result.length > 0) {
				for (Object obj : result) {
					field.addElement((String) obj);
				}
			}
		}
	}

	/**
	 * Returns the available {@link EPackage}s.
	 * 
	 * @param field
	 *            the {@link ListDialogField} instance to avoid duplicate
	 *            importing
	 * @return the array of {@link EPackage}s
	 */
	private Object[] getElements(ListDialogField<String> field) {
		List<String> result = new ArrayList<String>();

		Collection<String> packages = metamodelLoader.listEPackages();
		for (String ePackage : packages) {
			if (!fieldContains(field, ePackage)) {
				result.add(ePackage);
			}
		}

		return result.toArray();
	}

	private boolean fieldContains(ListDialogField<String> field, String _package) {
		for (String _p : field.getElements()) {
			if (_p.matches(_package)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void selectionChanged(ListDialogField<String> field) {
	}

	@Override
	public void doubleClicked(ListDialogField<String> field) {
	}
}
