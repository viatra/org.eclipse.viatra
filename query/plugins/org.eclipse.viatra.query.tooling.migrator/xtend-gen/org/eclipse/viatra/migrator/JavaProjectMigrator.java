/**
 * Copyright (c) 2010-2012, Balazs Grill, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Balazs Grill - initial API and implementation
 */
package org.eclipse.viatra.migrator;

import com.google.common.base.Objects;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.viatra.migrator.JavaProjectMigratorData;
import org.eclipse.viatra.query.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.xtext.xbase.lib.CollectionExtensions;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public class JavaProjectMigrator extends JavaProjectMigratorData {
  private final IJavaProject javaProject;
  
  public JavaProjectMigrator(final IProject project) {
    IJavaProject _create = JavaCore.create(project);
    this.javaProject = _create;
  }
  
  public JavaProjectMigrator(final IJavaProject project) {
    this.javaProject = project;
  }
  
  public void migrate(final IProgressMonitor monitor) {
    try {
      final SubMonitor m = SubMonitor.convert(monitor);
      final LinkedList<ICompilationUnit> list = CollectionLiterals.<ICompilationUnit>newLinkedList();
      IPackageFragment[] _packageFragments = this.javaProject.getPackageFragments();
      for (final IPackageFragment p : _packageFragments) {
        int _kind = p.getKind();
        boolean _equals = (_kind == IPackageFragmentRoot.K_SOURCE);
        if (_equals) {
          ICompilationUnit[] _compilationUnits = p.getCompilationUnits();
          CollectionExtensions.<ICompilationUnit>addAll(list, _compilationUnits);
        }
      }
      int _size = list.size();
      int _multiply = (_size * 2);
      int _plus = (_multiply + 1);
      m.beginTask("Migrating project", _plus);
      final IProject project = this.javaProject.getProject();
      boolean _isOpenPDEProject = ProjectGenerationHelper.isOpenPDEProject(project);
      if (_isOpenPDEProject) {
        SubMonitor _newChild = m.newChild(1);
        ProjectGenerationHelper.replaceBundledependencies(project, JavaProjectMigratorData.bundleRenames, JavaProjectMigratorData.bundleVersions, _newChild);
      }
      final ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
      for (final ICompilationUnit unit : list) {
        {
          SubMonitor _newChild_1 = m.newChild(1);
          final ASTNode ast = this.parse(unit, _newChild_1);
          final ASTRewrite rewrite = this.collectChanges(ast);
          m.worked(1);
          final TextEdit textEdit = rewrite.rewriteAST();
          final IPath path = unit.getPath();
          try {
            bufferManager.connect(path, LocationKind.IFILE, null);
            final ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path, LocationKind.IFILE);
            final IDocument document = textFileBuffer.getDocument();
            textEdit.apply(document);
            textFileBuffer.commit(null, false);
          } finally {
            bufferManager.disconnect(path, LocationKind.IFILE, null);
            m.worked(1);
          }
        }
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public ASTNode parse(final ICompilationUnit unit, final IProgressMonitor monitor) {
    final ASTParser parser = ASTParser.newParser(AST.JLS8);
    parser.setKind(ASTParser.K_COMPILATION_UNIT);
    parser.setSource(unit);
    parser.setResolveBindings(true);
    return parser.createAST(monitor);
  }
  
  public String replaceName(final String oldValue, final Map.Entry<String, String> entry) {
    String _key = entry.getKey();
    String _value = entry.getValue();
    return oldValue.replace(_key, _value);
  }
  
  public String getLastSegment(final String fqn) {
    boolean _endsWith = fqn.endsWith(".");
    if (_endsWith) {
      return "";
    }
    final int i = fqn.lastIndexOf(".");
    if ((i >= 0)) {
      return fqn.substring((i + 1));
    }
    return fqn;
  }
  
  public void createChange(final ASTRewrite rewrite, final ImportDeclaration importDeclaration, final Map<String, String> typeRenames) {
    Name _name = importDeclaration.getName();
    final String fullyQualifiedName = _name.getFullyQualifiedName();
    Set<Map.Entry<String, String>> _entrySet = JavaProjectMigrator.qualifiedNameRenames.entrySet();
    for (final Map.Entry<String, String> entry : _entrySet) {
      String _key = entry.getKey();
      boolean _startsWith = fullyQualifiedName.startsWith(_key);
      if (_startsWith) {
        final String newName = this.replaceName(fullyQualifiedName, entry);
        final String tn_old = this.getLastSegment(fullyQualifiedName);
        final String tn_new = this.getLastSegment(newName);
        boolean _notEquals = (!Objects.equal(tn_old, tn_new));
        if (_notEquals) {
          typeRenames.put(tn_old, tn_new);
        }
        AST _aST = importDeclaration.getAST();
        Name _newName = _aST.newName(newName);
        rewrite.set(importDeclaration, ImportDeclaration.NAME_PROPERTY, _newName, null);
        return;
      }
    }
  }
  
  public void createChange(final ASTRewrite rewrite, final SimpleType type, final Map<String, String> typeRenames) {
    final Name name = type.getName();
    if ((name instanceof QualifiedName)) {
      final String fullyQualifiedName = ((QualifiedName)name).getFullyQualifiedName();
      Set<Map.Entry<String, String>> _entrySet = JavaProjectMigrator.qualifiedNameRenames.entrySet();
      for (final Map.Entry<String, String> entry : _entrySet) {
        String _key = entry.getKey();
        boolean _startsWith = fullyQualifiedName.startsWith(_key);
        if (_startsWith) {
          AST _aST = type.getAST();
          String _replaceName = this.replaceName(fullyQualifiedName, entry);
          Name _newName = _aST.newName(_replaceName);
          rewrite.set(type, ImportDeclaration.NAME_PROPERTY, _newName, null);
          return;
        }
      }
    }
    if ((name instanceof SimpleName)) {
      final String n = ((SimpleName)name).getFullyQualifiedName();
      boolean _containsKey = typeRenames.containsKey(n);
      if (_containsKey) {
        AST _aST_1 = type.getAST();
        String _get = typeRenames.get(n);
        Name _newName_1 = _aST_1.newName(_get);
        rewrite.set(type, SimpleType.NAME_PROPERTY, _newName_1, null);
      }
    }
  }
  
  public ASTRewrite collectChanges(final ASTNode node) {
    AST _aST = node.getAST();
    final ASTRewrite rewrite = ASTRewrite.create(_aST);
    final HashMap<String, String> typeRenames = CollectionLiterals.<String, String>newHashMap();
    node.accept(new ASTVisitor() {
      @Override
      public boolean visit(final ImportDeclaration node) {
        boolean _xblockexpression = false;
        {
          JavaProjectMigrator.this.createChange(rewrite, node, typeRenames);
          _xblockexpression = super.visit(node);
        }
        return _xblockexpression;
      }
      
      @Override
      public boolean visit(final SimpleType node) {
        boolean _xblockexpression = false;
        {
          JavaProjectMigrator.this.createChange(rewrite, node, typeRenames);
          _xblockexpression = super.visit(node);
        }
        return _xblockexpression;
      }
    });
    return rewrite;
  }
}
