package org.eclipse.incquery.testing.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchSetRecord;
import org.eclipse.incquery.testing.queries.util.CorrespondingRecordInMatchSetRecordQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.testing.queries.CorrespondingRecordInMatchSetRecord pattern,
 * to be used in conjunction with {@link CorrespondingRecordInMatchSetRecordMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters,
 * usable to represent a match of the pattern in the result of a query,
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see CorrespondingRecordInMatchSetRecordMatcher
 * @see CorrespondingRecordInMatchSetRecordProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class CorrespondingRecordInMatchSetRecordMatch extends BasePatternMatch {
  private MatchRecord fRecord;
  
  private MatchRecord fCorrespodingRecord;
  
  private MatchSetRecord fExpectedSet;
  
  private static List<String> parameterNames = makeImmutableList("Record", "CorrespodingRecord", "ExpectedSet");
  
  private CorrespondingRecordInMatchSetRecordMatch(final MatchRecord pRecord, final MatchRecord pCorrespodingRecord, final MatchSetRecord pExpectedSet) {
    this.fRecord = pRecord;
    this.fCorrespodingRecord = pCorrespodingRecord;
    this.fExpectedSet = pExpectedSet;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Record".equals(parameterName)) return this.fRecord;
    if ("CorrespodingRecord".equals(parameterName)) return this.fCorrespodingRecord;
    if ("ExpectedSet".equals(parameterName)) return this.fExpectedSet;
    return null;
    
  }
  
  public MatchRecord getRecord() {
    return this.fRecord;
    
  }
  
  public MatchRecord getCorrespodingRecord() {
    return this.fCorrespodingRecord;
    
  }
  
  public MatchSetRecord getExpectedSet() {
    return this.fExpectedSet;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Record".equals(parameterName) ) {
    	this.fRecord = (org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord) newValue;
    	return true;
    }
    if ("CorrespodingRecord".equals(parameterName) ) {
    	this.fCorrespodingRecord = (org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord) newValue;
    	return true;
    }
    if ("ExpectedSet".equals(parameterName) ) {
    	this.fExpectedSet = (org.eclipse.incquery.snapshot.EIQSnapshot.MatchSetRecord) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setRecord(final MatchRecord pRecord) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fRecord = pRecord;
    
  }
  
  public void setCorrespodingRecord(final MatchRecord pCorrespodingRecord) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fCorrespodingRecord = pCorrespodingRecord;
    
  }
  
  public void setExpectedSet(final MatchSetRecord pExpectedSet) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fExpectedSet = pExpectedSet;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.testing.queries.CorrespondingRecordInMatchSetRecord";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return CorrespondingRecordInMatchSetRecordMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fRecord, fCorrespodingRecord, fExpectedSet};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Record\"=" + prettyPrintValue(fRecord) + ", ");
    result.append("\"CorrespodingRecord\"=" + prettyPrintValue(fCorrespodingRecord) + ", ");
    result.append("\"ExpectedSet\"=" + prettyPrintValue(fExpectedSet));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fRecord == null) ? 0 : fRecord.hashCode());
    result = prime * result + ((fCorrespodingRecord == null) ? 0 : fCorrespodingRecord.hashCode());
    result = prime * result + ((fExpectedSet == null) ? 0 : fExpectedSet.hashCode());
    return result;
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof CorrespondingRecordInMatchSetRecordMatch)) { // this should be infrequent
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    CorrespondingRecordInMatchSetRecordMatch other = (CorrespondingRecordInMatchSetRecordMatch) obj;
    if (fRecord == null) {if (other.fRecord != null) return false;}
    else if (!fRecord.equals(other.fRecord)) return false;
    if (fCorrespodingRecord == null) {if (other.fCorrespodingRecord != null) return false;}
    else if (!fCorrespodingRecord.equals(other.fCorrespodingRecord)) return false;
    if (fExpectedSet == null) {if (other.fExpectedSet != null) return false;}
    else if (!fExpectedSet.equals(other.fExpectedSet)) return false;
    return true;
  }
  
  @Override
  public CorrespondingRecordInMatchSetRecordQuerySpecification specification() {
    try {
    	return CorrespondingRecordInMatchSetRecordQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends CorrespondingRecordInMatchSetRecordMatch {
    Mutable(final MatchRecord pRecord, final MatchRecord pCorrespodingRecord, final MatchSetRecord pExpectedSet) {
      super(pRecord, pCorrespodingRecord, pExpectedSet);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends CorrespondingRecordInMatchSetRecordMatch {
    Immutable(final MatchRecord pRecord, final MatchRecord pCorrespodingRecord, final MatchSetRecord pExpectedSet) {
      super(pRecord, pCorrespodingRecord, pExpectedSet);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
