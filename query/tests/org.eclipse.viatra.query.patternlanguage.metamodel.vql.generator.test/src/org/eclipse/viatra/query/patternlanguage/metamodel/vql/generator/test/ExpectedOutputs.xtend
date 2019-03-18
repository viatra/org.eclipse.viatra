/**
 * Copyright (c) 2010-2018, Mocsai Krisztain, IncQuery Labs Ltd.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
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
        ai: java ^java.lang.Integer
    ) {
        HostInstance(hi);
        java ^java.lang.Integer(ai);
        HostInstance.applications(hi, ai);
    }
    '''
    
    public static String patternWithStringLiteral = '''
    package vgql.test
    
    import "http://org.eclipse.viatra/model/cps"
    
    pattern test3(
        hi: HostInstance
    ) {
        HostInstance(hi);
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
        ai: <<<<<<<Unexpected type declaration.>>>>>>>
    ) {
        HostInstance(hi);
        HostInstance.applications(hi, ai);
    }
    
    pattern test3(
        hi: HostInstance
    ) {
        HostInstance(hi);
        Identifiable.identifier(hi, "abc");
    }
    
    pattern test4(
        hi: HostInstance
    ) {
        HostInstance(hi);
        Identifiable.identifier(hi, name);
        check(name != name.toLowerCase);
    }
    
    pattern test5(
        hi: HostInstance,
        out name: <<<<<<<Unexpected type declaration.>>>>>>>
    ) {
        HostInstance(hi);
        Identifiable.identifier(hi, n);
        expression2 == name;
        expression2 == eval(n.toLowerCase);
    }
    
    pattern test6(
        hi: HostInstance
    ) {
        HostInstance(hi);
        find test4(hi);
    }
    
    pattern test7(
        hi: HostInstance
    ) {
        HostInstance(hi);
        neg find test4(hi);
    }
    
    pattern test8(
        hi: HostInstance,
        ai: ApplicationInstance
    ) {
        HostInstance(hi);
        ApplicationInstance(ai);
        <<<<<<<PathExpressionConstraint should have one edgeType at least.>>>>>>>;
    }
    
    pattern test9(
        hi: HostInstance,
        c: <<<<<<<Unexpected type declaration.>>>>>>>
    ) {
        HostInstance(hi);
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