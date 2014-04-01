package org.eclipse.incquery.testing.queries.util;

import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.incquery.runtime.api.IncQueryEngine;
import org.eclipse.incquery.runtime.api.impl.BaseGeneratedQuerySpecification;
import org.eclipse.incquery.runtime.context.EMFPatternMatcherContext;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.runtime.extensibility.IQuerySpecificationProvider;
import org.eclipse.incquery.runtime.matchers.psystem.PBody;
import org.eclipse.incquery.runtime.matchers.psystem.PParameter;
import org.eclipse.incquery.runtime.matchers.psystem.PQuery;
import org.eclipse.incquery.runtime.matchers.psystem.PVariable;
import org.eclipse.incquery.runtime.matchers.psystem.annotations.PAnnotation;
import org.eclipse.incquery.runtime.matchers.psystem.basicdeferred.ExportedParameter;
import org.eclipse.incquery.runtime.matchers.psystem.basicenumerables.TypeBinary;
import org.eclipse.incquery.testing.queries.SubstitutionValueMatcher;

/**
 * A pattern-specific query specification that can instantiate SubstitutionValueMatcher in a type-safe way.
 * 
 * @see SubstitutionValueMatcher
 * @see SubstitutionValueMatch
 * 
 */
@SuppressWarnings("all")
public final class SubstitutionValueQuerySpecification extends BaseGeneratedQuerySpecification<SubstitutionValueMatcher> {
  /**
   * @return the singleton instance of the query specification
   * @throws IncQueryException if the pattern definition could not be loaded
   * 
   */
  public static SubstitutionValueQuerySpecification instance() throws IncQueryException {
    try {
    	return LazyHolder.INSTANCE;
    } catch (ExceptionInInitializerError err) {
    	processInitializerError(err);
    	throw err;
    }
    
  }
  
  @Override
  protected SubstitutionValueMatcher instantiate(final IncQueryEngine engine) throws IncQueryException {
    return SubstitutionValueMatcher.on(engine);
  }
  
  @Override
  public String getFullyQualifiedName() {
    return "org.eclipse.incquery.testing.queries.SubstitutionValue";
    
  }
  
  @Override
  public List<String> getParameterNames() {
    return Arrays.asList("Substitution","Value");
  }
  
  @Override
  public List<PParameter> getParameters() {
    return Arrays.asList(new PParameter("Substitution", "org.eclipse.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord"),new PParameter("Value", "java.lang.Object"));
  }
  
  @Override
  public Set<PBody> doGetContainedBodies() throws IncQueryException {
    EMFPatternMatcherContext context = new EMFPatternMatcherContext();
    Set<PBody> bodies = Sets.newHashSet();
    {
      PBody body = new PBody(this);
      PVariable var_Substitution = body.getOrCreateVariableByName("Substitution");
      PVariable var_Value = body.getOrCreateVariableByName("Value");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Substitution, "Substitution"), 
        new ExportedParameter(body, var_Value, "Value")
      ));
      
      
      new TypeBinary(body, context, var_Substitution, var_Value, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "IntSubstitution", "value"), "http://www.eclipse.org/incquery/snapshot/IntSubstitution.value");
      bodies.add(body);
    }{
      PBody body = new PBody(this);
      PVariable var_Substitution = body.getOrCreateVariableByName("Substitution");
      PVariable var_Value = body.getOrCreateVariableByName("Value");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Substitution, "Substitution"), 
        new ExportedParameter(body, var_Value, "Value")
      ));
      
      
      new TypeBinary(body, context, var_Substitution, var_Value, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "EMFSubstitution", "value"), "http://www.eclipse.org/incquery/snapshot/EMFSubstitution.value");
      bodies.add(body);
    }{
      PBody body = new PBody(this);
      PVariable var_Substitution = body.getOrCreateVariableByName("Substitution");
      PVariable var_Value = body.getOrCreateVariableByName("Value");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Substitution, "Substitution"), 
        new ExportedParameter(body, var_Value, "Value")
      ));
      
      
      new TypeBinary(body, context, var_Substitution, var_Value, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "FloatSubstitution", "value"), "http://www.eclipse.org/incquery/snapshot/FloatSubstitution.value");
      bodies.add(body);
    }{
      PBody body = new PBody(this);
      PVariable var_Substitution = body.getOrCreateVariableByName("Substitution");
      PVariable var_Value = body.getOrCreateVariableByName("Value");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Substitution, "Substitution"), 
        new ExportedParameter(body, var_Value, "Value")
      ));
      
      
      new TypeBinary(body, context, var_Substitution, var_Value, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "MiscellaneousSubstitution", "value"), "http://www.eclipse.org/incquery/snapshot/MiscellaneousSubstitution.value");
      bodies.add(body);
    }{
      PBody body = new PBody(this);
      PVariable var_Substitution = body.getOrCreateVariableByName("Substitution");
      PVariable var_Value = body.getOrCreateVariableByName("Value");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Substitution, "Substitution"), 
        new ExportedParameter(body, var_Value, "Value")
      ));
      
      
      new TypeBinary(body, context, var_Substitution, var_Value, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "DateSubstitution", "value"), "http://www.eclipse.org/incquery/snapshot/DateSubstitution.value");
      bodies.add(body);
    }{
      PBody body = new PBody(this);
      PVariable var_Substitution = body.getOrCreateVariableByName("Substitution");
      PVariable var_Value = body.getOrCreateVariableByName("Value");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Substitution, "Substitution"), 
        new ExportedParameter(body, var_Value, "Value")
      ));
      
      
      new TypeBinary(body, context, var_Substitution, var_Value, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "EnumSubstitution", "valueLiteral"), "http://www.eclipse.org/incquery/snapshot/EnumSubstitution.valueLiteral");
      bodies.add(body);
    }{
      PBody body = new PBody(this);
      PVariable var_Substitution = body.getOrCreateVariableByName("Substitution");
      PVariable var_Value = body.getOrCreateVariableByName("Value");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Substitution, "Substitution"), 
        new ExportedParameter(body, var_Value, "Value")
      ));
      
      
      new TypeBinary(body, context, var_Substitution, var_Value, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "BooleanSubstitution", "value"), "http://www.eclipse.org/incquery/snapshot/BooleanSubstitution.value");
      bodies.add(body);
    }{
      PBody body = new PBody(this);
      PVariable var_Substitution = body.getOrCreateVariableByName("Substitution");
      PVariable var_Value = body.getOrCreateVariableByName("Value");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Substitution, "Substitution"), 
        new ExportedParameter(body, var_Value, "Value")
      ));
      
      
      new TypeBinary(body, context, var_Substitution, var_Value, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "DoubleSubstitution", "value"), "http://www.eclipse.org/incquery/snapshot/DoubleSubstitution.value");
      bodies.add(body);
    }{
      PBody body = new PBody(this);
      PVariable var_Substitution = body.getOrCreateVariableByName("Substitution");
      PVariable var_Value = body.getOrCreateVariableByName("Value");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Substitution, "Substitution"), 
        new ExportedParameter(body, var_Value, "Value")
      ));
      
      
      new TypeBinary(body, context, var_Substitution, var_Value, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "LongSubstitution", "value"), "http://www.eclipse.org/incquery/snapshot/LongSubstitution.value");
      bodies.add(body);
    }{
      PBody body = new PBody(this);
      PVariable var_Substitution = body.getOrCreateVariableByName("Substitution");
      PVariable var_Value = body.getOrCreateVariableByName("Value");
      body.setExportedParameters(Arrays.<ExportedParameter>asList(
        new ExportedParameter(body, var_Substitution, "Substitution"), 
        new ExportedParameter(body, var_Value, "Value")
      ));
      
      
      new TypeBinary(body, context, var_Substitution, var_Value, getFeatureLiteral("http://www.eclipse.org/incquery/snapshot", "StringSubstitution", "value"), "http://www.eclipse.org/incquery/snapshot/StringSubstitution.value");
      bodies.add(body);
    }{
      PAnnotation annotation = new PAnnotation("QueryExplorer");
      annotation.addAttribute("display",false);
      addAnnotation(annotation);
    }
    {
      PAnnotation annotation = new PAnnotation("QueryBasedFeature");
      annotation.addAttribute("feature","derivedValue");
      addAnnotation(annotation);
    }
    
    return bodies;
  }
  
  private SubstitutionValueQuerySpecification() throws IncQueryException {
    super();
    setStatus(PQuery.PQueryStatus.UNINITIALIZED);
  }
  
  @SuppressWarnings("all")
  public static class Provider implements IQuerySpecificationProvider<SubstitutionValueQuerySpecification> {
    @Override
    public SubstitutionValueQuerySpecification get() throws IncQueryException {
      return instance();
    }
  }
  
  
  @SuppressWarnings("all")
  private static class LazyHolder {
    private final static SubstitutionValueQuerySpecification INSTANCE = make();
    
    public static SubstitutionValueQuerySpecification make() {
      try {
      	return new SubstitutionValueQuerySpecification();
      } catch (IncQueryException ex) {
      	throw new RuntimeException	(ex);
      }
      
    }
  }
  
}
