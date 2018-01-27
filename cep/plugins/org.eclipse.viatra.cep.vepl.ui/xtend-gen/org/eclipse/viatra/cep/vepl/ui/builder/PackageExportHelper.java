/**
 * Copyright (c) 2004-2015, Istvan David, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Istvan David - initial API and implementation
 */
package org.eclipse.viatra.cep.vepl.ui.builder;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.inject.Singleton;
import org.eclipse.viatra.cep.vepl.jvmmodel.NamingProvider;
import org.eclipse.viatra.cep.vepl.vepl.AtomicEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.ComplexEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.EventModel;
import org.eclipse.viatra.cep.vepl.vepl.ModelElement;
import org.eclipse.viatra.cep.vepl.vepl.QueryResultChangeEventPattern;
import org.eclipse.viatra.cep.vepl.vepl.Rule;

@Singleton
@SuppressWarnings("all")
public class PackageExportHelper {
  public ImmutableList<String> getExportablePackages(final EventModel eventModel) {
    String _name = eventModel.getName();
    final String basePackageName = _name.toString();
    ImmutableList.Builder<String> _builder = ImmutableList.<String>builder();
    ImmutableList.Builder<String> _add = _builder.add(basePackageName);
    Joiner _on = Joiner.on(".");
    String _join = _on.join(basePackageName, NamingProvider.EVENTCLASS_PACKAGE_NAME_ELEMENT);
    ImmutableList.Builder<String> _add_1 = _add.add(_join);
    Joiner _on_1 = Joiner.on(".");
    String _join_1 = _on_1.join(basePackageName, NamingProvider.ATOMIC_PATTERN_PACKAGE_NAME_ELEMENT);
    ImmutableList.Builder<String> _add_2 = _add_1.add(_join_1);
    Joiner _on_2 = Joiner.on(".");
    String _join_2 = _on_2.join(basePackageName, NamingProvider.QUERYRESULT_PATTERN_PACKAGE_NAME_ELEMENT);
    ImmutableList.Builder<String> _add_3 = _add_2.add(_join_2);
    Joiner _on_3 = Joiner.on(".");
    String _join_3 = _on_3.join(basePackageName, NamingProvider.COMPLEX_PATTERN_PACKAGE_NAME_ELEMENT);
    ImmutableList.Builder<String> _add_4 = _add_3.add(_join_3);
    Joiner _on_4 = Joiner.on(".");
    String _join_4 = _on_4.join(basePackageName, NamingProvider.RULES_PACKAGE_NAME_ELEMENT);
    ImmutableList.Builder<String> _add_5 = _add_4.add(_join_4);
    Joiner _on_5 = Joiner.on(".");
    String _join_5 = _on_5.join(basePackageName, NamingProvider.JOBS_PACKAGE_NAME_ELEMENT);
    ImmutableList.Builder<String> _add_6 = _add_5.add(_join_5);
    Joiner _on_6 = Joiner.on(".");
    String _join_6 = _on_6.join(basePackageName, NamingProvider.MAPPING_PACKAGE_NAME_ELEMENT);
    ImmutableList.Builder<String> _add_7 = _add_6.add(_join_6);
    final ImmutableList<String> packages = _add_7.build();
    return packages;
  }
  
  public static boolean packageShouldBeExported(final ModelElement modelElement) {
    boolean _or = false;
    if ((((modelElement instanceof AtomicEventPattern) || (modelElement instanceof ComplexEventPattern)) || (modelElement instanceof QueryResultChangeEventPattern))) {
      _or = true;
    } else {
      _or = (modelElement instanceof Rule);
    }
    return _or;
  }
}
