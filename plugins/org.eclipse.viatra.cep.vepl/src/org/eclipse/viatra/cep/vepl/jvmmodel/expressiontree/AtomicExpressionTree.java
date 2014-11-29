package org.eclipse.viatra.cep.vepl.jvmmodel.expressiontree;

import java.util.Collections;
import java.util.List;

import org.eclipse.viatra.cep.vepl.vepl.Atom;

public class AtomicExpressionTree extends ExpressionTree {

    public AtomicExpressionTree(Atom atom) {
        super();

        Node root = new Node(null);
        Leaf leaf = new Leaf(atom);
        root.addChild(leaf);
        super.setRoot(root);
    }

    @Override
    public List<Leaf> getComplexLeaves() {
        return Collections.emptyList();
    }
}
