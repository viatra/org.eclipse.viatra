/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package org.eclipse.viatra.query.tooling.ui.wizards.internal;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.viatra.query.patternlanguage.emf.ui.EMFPatternLanguageUIPlugin;

/**
 * {@link ILabelProvider} implementation for the {@link ImportListAdapter}.
 * 
 * @author Tamas Szabo
 * 
 */
public class ImportListLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

    private ImageRegistry imageRegistry;

    public ImportListLabelProvider() {
        imageRegistry = EMFPatternLanguageUIPlugin.getInstance().getImageRegistry();
    }

    @Override
    public Image getImage(Object element) {
        return imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_EPACKAGE);
    }

    @Override
    public String getText(Object element) {
        return element.toString();
    }

    @Override
    public void update(ViewerCell cell) {
        Object element = cell.getElement();
        StyledString text = new StyledString();
        if (element instanceof String) {
            String nsUri = (String) element;
            text.append(nsUri);
//            if (ePackage.eResource() != null && ePackage.eResource().getURI().isPlatform()) {
//                text.append(String.format(" (%s)", ePackage.eResource().getURI()), StyledString.QUALIFIER_STYLER);
//            }
            cell.setImage(imageRegistry.get(EMFPatternLanguageUIPlugin.ICON_EPACKAGE));
        }
        cell.setText(text.getString());
        cell.setStyleRanges(text.getStyleRanges());
        super.update(cell);
    }


}
