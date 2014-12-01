package org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree;

import java.util.Collections;
import java.util.List;

import org.eclipse.viatra.cep.core.metamodels.events.TimeWindow;
import org.eclipse.viatra.cep.vepl.vepl.Atom;
import org.eclipse.viatra.cep.vepl.vepl.VeplFactory;

public class AtomicExpressionTree extends ExpressionTree {

    public AtomicExpressionTree(Atom atom, int multiplicity, TimeWindow timewindow) {
        super();

        Node root = new Node(VeplFactory.eINSTANCE.createFollowsOperator(), multiplicity, timewindow);
        Leaf leaf = new Leaf(atom);
        root.addChild(leaf);
        super.setRoot(root);
    }

    @Override
    public List<Leaf> getComplexLeaves() {
        return Collections.emptyList();
    }
}
