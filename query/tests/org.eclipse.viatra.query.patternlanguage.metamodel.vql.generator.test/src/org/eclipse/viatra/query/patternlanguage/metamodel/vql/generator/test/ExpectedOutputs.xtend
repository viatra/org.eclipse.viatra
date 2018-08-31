/**
 * Copyright (c) 2010-2018, Mocsai Krisztain, IncQuery Labs Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Mocsai Krisztian - initial API and implementation
 *******************************************************************************/
 
 package org.eclipse.viatra.query.patternlanguage.metamodel.vql.generator.test

class ExpectedOutputs {
    
    public static String patternWithoutBody = '''
    package vgql.test
    
    import "http://org.eclipse.viatra/model/cps"
    
    pattern test(
        hi: HostInstance
    ) {
        HostInstance(hi);
    }'''
    
    public static String patternWithPathExpression = '''
    package vgql.test
    
    import "http://org.eclipse.viatra/model/cps"
    
    pattern test2(
        hi: HostInstance,
        ai: java.Integer
    ) {
        HostInstance.applications.totalCpu(hi, ai);
    }
    '''
    
    public static String patternWithStringLiteral = '''
    package vgql.test
    
    import "http://org.eclipse.viatra/model/cps"
    
    pattern test3(
        hi: HostInstance
    ) {
        Identifiable.identifier(hi, "abc");
    }
    
    '''
    
    public static String testAllCases = '''
    package vgql.test
    
    import "http://org.eclipse.viatra/model/cps"
    
    pattern test(
        hi: HostInstance
    ) {
        HostInstance(hi);
    }
    
    pattern test2(
        hi: HostInstance,
        ai: <<<<<<<Type of the parameter should defined.>>>>>>>
    ) {
        HostInstance.applications(hi, ai);
    }
    
    pattern test3(
        hi: HostInstance
    ) {
        Identifiable.identifier(hi, "abc");
    }
    
    pattern test4(
        hi: HostInstance
    ) {
        Identifiable.identifier(hi, name);
        check(name != name.toLowerCase);
    }
    
    pattern test5(
        hi: HostInstance,
        out name: <<<<<<<Type of the parameter should defined.>>>>>>>
    ) {
        Identifiable.identifier(hi, n);
        expression2 == name;
        expression2 == eval(n.toLowerCase);
    }
    
    pattern test6(
        hi: HostInstance
    ) {
        find test4(hi);
    }
    
    pattern test7(
        hi: HostInstance
    ) {
        neg find test4(hi);
    }
    
    pattern test8(
        hi: HostInstance,
        ai: ApplicationInstance
    ) {
        <<<<<<<PathExpressionConstraint should have one edgeType at least.>>>>>>>;
    }
    
    pattern test9(
        hi: HostInstance,
        c: <<<<<<<Type of the parameter should defined.>>>>>>>
    ) {
        expression2 == c;
        expression2 == find test8(hi, _);
    }
    
    pattern pattern10(
    ) {
        expression0 == count find test7(_);
    }
    
    pattern BeforeOrAfter(
        st: State,
        to: State
    ) {
        State(st);
        State(to);
        Transition(tr);
        State.outgoingTransitions(to, tr);
        Transition.targetState(tr, st);
    } or {
        State(st);
        State(to);
        Transition(tr);
        State.outgoingTransitions(st, tr);
        State.outgoingTransitions(tr, to);
    }
    '''
}