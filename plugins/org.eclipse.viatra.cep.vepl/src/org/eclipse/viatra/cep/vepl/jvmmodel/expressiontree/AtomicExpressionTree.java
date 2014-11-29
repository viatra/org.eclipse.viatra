package org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree;

import java.util.Collections;
import java.util.List;

import org.eclipse.viatra.cep.core.metamodels.events.TimeWindow;
import org.eclipse.viatra.cep.vepl.vepl.Atom;
import org.eclipse.viatra.cep.vepl.vepl.VeplFactory;

public class AtomicExpressionTree extends ExpressionTree {

    public AtomicExpressionTree(Atom atom, int multiplicity, TimeWindow timeWindow) {
        super();

        Node root = new Node(VeplFactory.eINSTANCE.createFollowsOperator(), 1, timeWindow);
        Leaf leaf = new Leaf(atom, multiplicity);
        root.addChild(leaf);
        super.setRoot(root);
    }

    @Override
    public List<Leaf> getComplexLeaves() {
        return Collections.emptyList();
    }
}
