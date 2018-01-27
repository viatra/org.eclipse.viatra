/**
 * Copyright (c) 2004-2015, Marton Bur, Peter Lunk, Zoltan Ujhelyi and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Marton Bur, Peter Lunk - initial API and implementation
 */
package org.eclipse.viatra.transformation.runtime.emf.changemonitor;

import com.google.common.collect.Multimap;
import org.eclipse.viatra.query.runtime.api.IPatternMatch;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryMatcher;
import org.eclipse.xtend.lib.annotations.Data;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Class representing the changes in a given instance model since the last checkpoint.
 * It contains three MultiMaps which contain the changed elements sorted
 * by the detecting QuerySpecifications.
 * 
 * @author Lunk Péter
 */
@Data
@SuppressWarnings("all")
public class ChangeDelta {
  public final Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> appeared;
  
  public final Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> updated;
  
  public final Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> disappeared;
  
  public ChangeDelta(final Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> appeared, final Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> updated, final Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> disappeared) {
    super();
    this.appeared = appeared;
    this.updated = updated;
    this.disappeared = disappeared;
  }
  
  @Override
  @Pure
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.appeared== null) ? 0 : this.appeared.hashCode());
    result = prime * result + ((this.updated== null) ? 0 : this.updated.hashCode());
    result = prime * result + ((this.disappeared== null) ? 0 : this.disappeared.hashCode());
    return result;
  }
  
  @Override
  @Pure
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ChangeDelta other = (ChangeDelta) obj;
    if (this.appeared == null) {
      if (other.appeared != null)
        return false;
    } else if (!this.appeared.equals(other.appeared))
      return false;
    if (this.updated == null) {
      if (other.updated != null)
        return false;
    } else if (!this.updated.equals(other.updated))
      return false;
    if (this.disappeared == null) {
      if (other.disappeared != null)
        return false;
    } else if (!this.disappeared.equals(other.disappeared))
      return false;
    return true;
  }
  
  @Override
  @Pure
  public String toString() {
    ToStringBuilder b = new ToStringBuilder(this);
    b.add("appeared", this.appeared);
    b.add("updated", this.updated);
    b.add("disappeared", this.disappeared);
    return b.toString();
  }
  
  @Pure
  public Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> getAppeared() {
    return this.appeared;
  }
  
  @Pure
  public Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> getUpdated() {
    return this.updated;
  }
  
  @Pure
  public Multimap<IQuerySpecification<? extends ViatraQueryMatcher<IPatternMatch>>, IPatternMatch> getDisappeared() {
    return this.disappeared;
  }
}
