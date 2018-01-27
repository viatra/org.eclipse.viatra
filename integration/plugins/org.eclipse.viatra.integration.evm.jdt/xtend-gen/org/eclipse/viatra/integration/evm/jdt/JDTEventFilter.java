/**
 * Copyright (c) 2015-2016, IncQuery Labs Ltd. and Ericsson AB
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.integration.evm.jdt;

import com.google.common.base.Objects;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.viatra.integration.evm.jdt.JDTEventAtom;
import org.eclipse.viatra.transformation.evm.api.event.EventFilter;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Pure;

@SuppressWarnings("all")
public class JDTEventFilter implements EventFilter<JDTEventAtom> {
  @Accessors
  private IJavaProject project;
  
  public JDTEventFilter() {
  }
  
  @Override
  public boolean isProcessable(final JDTEventAtom eventAtom) {
    IJavaElement _element = eventAtom.getElement();
    IJavaProject _javaProject = _element.getJavaProject();
    return Objects.equal(_javaProject, this.project);
  }
  
  @Pure
  public IJavaProject getProject() {
    return this.project;
  }
  
  public void setProject(final IJavaProject project) {
    this.project = project;
  }
}
