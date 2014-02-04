package org.eclipse.incquery.testing.queries;

import java.util.Arrays;
import java.util.List;
import org.eclipse.incquery.runtime.api.IPatternMatch;
import org.eclipse.incquery.runtime.api.impl.BasePatternMatch;
import org.eclipse.incquery.runtime.exception.IncQueryException;
import org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord;
import org.eclipse.incquery.testing.queries.util.CorrespondingRecordsQuerySpecification;

/**
 * Pattern-specific match representation of the org.eclipse.incquery.testing.queries.CorrespondingRecords pattern, 
 * to be used in conjunction with {@link CorrespondingRecordsMatcher}.
 * 
 * <p>Class fields correspond to parameters of the pattern. Fields with value null are considered unassigned.
 * Each instance is a (possibly partial) substitution of pattern parameters, 
 * usable to represent a match of the pattern in the result of a query, 
 * or to specify the bound (fixed) input parameters when issuing a query.
 * 
 * @see CorrespondingRecordsMatcher
 * @see CorrespondingRecordsProcessor
 * 
 */
@SuppressWarnings("all")
public abstract class CorrespondingRecordsMatch extends BasePatternMatch {
  private MatchRecord fRecord;
  
  private MatchRecord fCorrespondingRecord;
  
  private static List<String> parameterNames = makeImmutableList("Record", "CorrespondingRecord");
  
  private CorrespondingRecordsMatch(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord) {
    this.fRecord = pRecord;
    this.fCorrespondingRecord = pCorrespondingRecord;
    
  }
  
  @Override
  public Object get(final String parameterName) {
    if ("Record".equals(parameterName)) return this.fRecord;
    if ("CorrespondingRecord".equals(parameterName)) return this.fCorrespondingRecord;
    return null;
    
  }
  
  public MatchRecord getRecord() {
    return this.fRecord;
    
  }
  
  public MatchRecord getCorrespondingRecord() {
    return this.fCorrespondingRecord;
    
  }
  
  @Override
  public boolean set(final String parameterName, final Object newValue) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    if ("Record".equals(parameterName) ) {
    	this.fRecord = (org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord) newValue;
    	return true;
    }
    if ("CorrespondingRecord".equals(parameterName) ) {
    	this.fCorrespondingRecord = (org.eclipse.incquery.snapshot.EIQSnapshot.MatchRecord) newValue;
    	return true;
    }
    return false;
    
  }
  
  public void setRecord(final MatchRecord pRecord) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fRecord = pRecord;
    
  }
  
  public void setCorrespondingRecord(final MatchRecord pCorrespondingRecord) {
    if (!isMutable()) throw new java.lang.UnsupportedOperationException();
    this.fCorrespondingRecord = pCorrespondingRecord;
    
  }
  
  @Override
  public String patternName() {
    return "org.eclipse.incquery.testing.queries.CorrespondingRecords";
    
  }
  
  @Override
  public List<String> parameterNames() {
    return CorrespondingRecordsMatch.parameterNames;
    
  }
  
  @Override
  public Object[] toArray() {
    return new Object[]{fRecord, fCorrespondingRecord};
    
  }
  
  @Override
  public String prettyPrint() {
    StringBuilder result = new StringBuilder();
    result.append("\"Record\"=" + prettyPrintValue(fRecord) + ", ");
    result.append("\"CorrespondingRecord\"=" + prettyPrintValue(fCorrespondingRecord));
    return result.toString();
    
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fRecord == null) ? 0 : fRecord.hashCode()); 
    result = prime * result + ((fCorrespondingRecord == null) ? 0 : fCorrespondingRecord.hashCode()); 
    return result; 
    
  }
  
  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
    	return true;
    if (!(obj instanceof CorrespondingRecordsMatch)) { // this should be infrequent				
    	if (obj == null)
    		return false;
    	if (!(obj instanceof IPatternMatch))
    		return false;
    	IPatternMatch otherSig  = (IPatternMatch) obj;
    	if (!specification().equals(otherSig.specification()))
    		return false;
    	return Arrays.deepEquals(toArray(), otherSig.toArray());
    }
    CorrespondingRecordsMatch other = (CorrespondingRecordsMatch) obj;
    if (fRecord == null) {if (other.fRecord != null) return false;}
    else if (!fRecord.equals(other.fRecord)) return false;
    if (fCorrespondingRecord == null) {if (other.fCorrespondingRecord != null) return false;}
    else if (!fCorrespondingRecord.equals(other.fCorrespondingRecord)) return false;
    return true;
  }
  
  @Override
  public CorrespondingRecordsQuerySpecification specification() {
    try {
    	return CorrespondingRecordsQuerySpecification.instance();
    } catch (IncQueryException ex) {
     	// This cannot happen, as the match object can only be instantiated if the query specification exists
     	throw new IllegalStateException	(ex);
    }
    
  }
  
  @SuppressWarnings("all")
  static final class Mutable extends CorrespondingRecordsMatch {
    Mutable(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord) {
      super(pRecord, pCorrespondingRecord);
      
    }
    
    @Override
    public boolean isMutable() {
      return true;
    }
  }
  
  
  @SuppressWarnings("all")
  static final class Immutable extends CorrespondingRecordsMatch {
    Immutable(final MatchRecord pRecord, final MatchRecord pCorrespondingRecord) {
      super(pRecord, pCorrespondingRecord);
      
    }
    
    @Override
    public boolean isMutable() {
      return false;
    }
  }
  
}
