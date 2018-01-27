/**
 * Copyright (c) 2015-2016, IncQuery Labs Ltd., Ericsson AB, CEA LIST
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Abel Hegedus, Daniel Segesdi, Robert Doczi, Zoltan Ujhelyi - initial API and implementation
 */
package org.eclipse.viatra.integration.evm.jdt;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jdt.internal.core.builder.State;
import org.eclipse.viatra.integration.evm.jdt.JDTRealm;
import org.eclipse.viatra.integration.evm.jdt.util.QualifiedName;
import org.eclipse.viatra.integration.evm.jdt.wrappers.JDTBuildState;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class BuildNotifierCompilationParticipant extends CompilationParticipant {
  @Extension
  private final Logger logger = Logger.getLogger(this.getClass());
  
  private final JDTRealm realm;
  
  public BuildNotifierCompilationParticipant() {
    JDTRealm _instance = JDTRealm.getInstance();
    this.realm = _instance;
  }
  
  @Override
  public boolean isActive(final IJavaProject project) {
    return this.realm.isActive();
  }
  
  @Override
  public void buildFinished(final IJavaProject project) {
    boolean _isActive = this.realm.isActive();
    if (_isActive) {
      final IProject iproject = project.getProject();
      JavaModelManager _javaModelManager = JavaModelManager.getJavaModelManager();
      NullProgressMonitor _nullProgressMonitor = new NullProgressMonitor();
      Object _lastBuiltState = _javaModelManager.getLastBuiltState(iproject, _nullProgressMonitor);
      final State lastState = ((State) _lastBuiltState);
      if ((lastState != null)) {
        final JDTBuildState buildState = new JDTBuildState(lastState);
        final Iterable<QualifiedName> affectedFiles = buildState.getAffectedCompilationUnitsInProject();
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("Affected files are ");
        {
          boolean _hasElements = false;
          for(final QualifiedName file : affectedFiles) {
            if (!_hasElements) {
              _hasElements = true;
            } else {
              _builder.appendImmediate(", ", "");
            }
            _builder.append(file, "");
          }
        }
        this.logger.debug(_builder);
        final Function1<QualifiedName, IJavaElement> _function = new Function1<QualifiedName, IJavaElement>() {
          @Override
          public IJavaElement apply(final QualifiedName fqn) {
            try {
              String _string = fqn.toString();
              Path _path = new Path(_string);
              return project.findElement(_path);
            } catch (Throwable _e) {
              throw Exceptions.sneakyThrow(_e);
            }
          }
        };
        final Iterable<IJavaElement> compilationUnits = IterableExtensions.<QualifiedName, IJavaElement>map(affectedFiles, _function);
        StringConcatenation _builder_1 = new StringConcatenation();
        _builder_1.append("Affected compilation units are ");
        {
          boolean _hasElements_1 = false;
          for(final IJavaElement cu : compilationUnits) {
            if (!_hasElements_1) {
              _hasElements_1 = true;
            } else {
              _builder_1.appendImmediate(", ", "");
            }
            _builder_1.append(cu, "");
          }
        }
        this.logger.debug(_builder_1);
        final Procedure1<IJavaElement> _function_1 = new Procedure1<IJavaElement>() {
          @Override
          public void apply(final IJavaElement compilationUnit) {
            BuildNotifierCompilationParticipant.this.realm.notifySources(compilationUnit);
          }
        };
        IterableExtensions.<IJavaElement>forEach(compilationUnits, _function_1);
        this.realm.buildFinishedOnProject(project);
      }
      StringConcatenation _builder_2 = new StringConcatenation();
      _builder_2.append("Build of ");
      String _elementName = project.getElementName();
      _builder_2.append(_elementName, "");
      _builder_2.append(" has finished");
      this.logger.debug(_builder_2);
    }
  }
  
  @Override
  public int aboutToBuild(final IJavaProject project) {
    int _xblockexpression = (int) 0;
    {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("About to build ");
      String _elementName = project.getElementName();
      _builder.append(_elementName, "");
      this.logger.trace(_builder);
      _xblockexpression = super.aboutToBuild(project);
    }
    return _xblockexpression;
  }
  
  @Override
  public void buildStarting(final BuildContext[] files, final boolean isBatch) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Build starting for [");
    {
      boolean _hasElements = false;
      for(final BuildContext file : files) {
        if (!_hasElements) {
          _hasElements = true;
        } else {
          _builder.appendImmediate(", ", "");
        }
        _builder.append(file, "");
      }
    }
    _builder.append("]");
    this.logger.trace(_builder);
    super.buildStarting(files, isBatch);
  }
  
  @Override
  public void cleanStarting(final IJavaProject project) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("Clean starting on ");
    String _elementName = project.getElementName();
    _builder.append(_elementName, "");
    this.logger.trace(_builder);
    super.cleanStarting(project);
  }
}
