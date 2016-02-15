package org.eclipse.viatra.query.application.queries.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.viatra.query.application.queries.ClassesInPackageHierarchyMatch;
import org.eclipse.viatra.query.runtime.api.IMatchProcessor;

/**
 * A match processor tailored for the org.eclipse.viatra.query.application.queries.classesInPackageHierarchy pattern.
 * 
 * Clients should derive an (anonymous) class that implements the abstract process().
 * 
 */
@SuppressWarnings("all")
public abstract class ClassesInPackageHierarchyProcessor implements IMatchProcessor<ClassesInPackageHierarchyMatch> {
  /**
   * Defines the action that is to be executed on each match.
   * @param pRootP the value of pattern parameter rootP in the currently processed match
   * @param pContainedClass the value of pattern parameter containedClass in the currently processed match
   * 
   */
  public abstract void process(final EPackage pRootP, final EClass pContainedClass);
  
  @Override
  public void process(final ClassesInPackageHierarchyMatch match) {
    process(match.getRootP(), match.getContainedClass());
  }
}
