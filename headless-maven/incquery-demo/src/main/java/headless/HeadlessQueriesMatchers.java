package headless;

import headless.ClassesInPackageHierarchyMatcher;
import headless.ClassesInPackageMatcher;
import headless.EClassMatcher;
import headless.EClassNamesKeywordMatcher;
import headless.EClassNamesMatcher;
import headless.EObjectMatcher;
import headless.EPackageMatcher;
import headless.SubPackageMatcher;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.exception.IncQueryException;

@SuppressWarnings("all")
public final class HeadlessQueriesMatchers {
  private IncQueryEngine engine;
  
  public HeadlessQueriesMatchers(final IncQueryEngine engine) {
    this.engine = engine;
    
  }
  
  public ClassesInPackageMatcher getClassesInPackageMatcher() throws IncQueryException {
    return ClassesInPackageMatcher.on(engine);
  }
  
  public EClassNamesKeywordMatcher getEClassNamesKeywordMatcher() throws IncQueryException {
    return EClassNamesKeywordMatcher.on(engine);
  }
  
  public EPackageMatcher getEPackageMatcher() throws IncQueryException {
    return EPackageMatcher.on(engine);
  }
  
  public SubPackageMatcher getSubPackageMatcher() throws IncQueryException {
    return SubPackageMatcher.on(engine);
  }
  
  public EClassMatcher getEClassMatcher() throws IncQueryException {
    return EClassMatcher.on(engine);
  }
  
  public EObjectMatcher getEObjectMatcher() throws IncQueryException {
    return EObjectMatcher.on(engine);
  }
  
  public EClassNamesMatcher getEClassNamesMatcher() throws IncQueryException {
    return EClassNamesMatcher.on(engine);
  }
  
  public ClassesInPackageHierarchyMatcher getClassesInPackageHierarchyMatcher() throws IncQueryException {
    return ClassesInPackageHierarchyMatcher.on(engine);
  }
}
