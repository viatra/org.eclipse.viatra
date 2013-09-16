package org.eclipse.viatra2.emf.runtime.modelmanipulation;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.base.Preconditions;

/**
 * Abstract base class for model manipulation implementation. It checks for the
 * preconditions of the operations, and the subclasses should override the do*
 * methods.
 * 
 * @author Zoltan Ujhelyi
 * 
 */
public abstract class AbstractModelManipulations implements IModelManipulations {

	public AbstractModelManipulations() {
		super();
	}

	protected <Type extends EObject> void doMoveTo(Collection<Type> what,
			EList<Type> where) throws ModelManipulationException {
		for (Type obj : what) {
			moveTo(obj, where);
		}
	}

	protected abstract <Type extends EObject> void doMoveTo(Type what,
			EList<Type> where) throws ModelManipulationException;

	/**
	 * Remove a non-containment reference value
	 */
	protected abstract void doRemove(EObject container, EReference reference,
			EObject element) throws ModelManipulationException;

	/**
	 * Removes an element from the containment hierarchy 
	 */
	protected abstract void doRemove(EObject object)
			throws ModelManipulationException;

	protected abstract void doAdd(EObject container, EAttribute attribute,
			Object value) throws ModelManipulationException;

	protected abstract void doAdd(EObject container, EReference reference,
			EObject element) throws ModelManipulationException;

	protected abstract EObject doCreate(EObject container,
			EReference reference, EClass clazz);

	protected abstract EObject doCreate(Resource res, EClass clazz)
			throws ModelManipulationException;

	@Override
	public EObject create(Resource res, EClass clazz)
			throws ModelManipulationException {
		return doCreate(res, clazz);
	}

	@Override
	public EObject create(EObject container, EReference reference) {
		EClass containerClass = container.eClass();
		Preconditions
				.checkArgument(
						containerClass.getEAllReferences().contains(container),
						"The container of EClass %s does neither define or inherit an EReference %s.",
						containerClass.getName(), reference.getName());
		Preconditions
				.checkArgument(reference.isContainment(),
						"Created elements must be inserted directly into the containment hierarchy.");
		EClass clazz = reference.getEReferenceType();

		return doCreate(container, reference, clazz);
	}

	@Override
	public void add(EObject container, EReference reference, EObject element)
			throws ModelManipulationException {
		EClass containerClass = container.eClass();
		Preconditions
				.checkArgument(
						containerClass.getEAllReferences().contains(container),
						"The container of EClass %s does neither define or inherit an EReference %s.",
						containerClass.getName(), reference.getName());
		Preconditions.checkArgument(reference.getUpperBound() > 1,
				"The EAttribute %s must have an upper bound larger than 1.",
				reference.getName());
		Preconditions
				.checkArgument(
						!reference.isContainment(),
						"Adding existing elements into the containment reference %s is not supported.",
						reference.getName());
		doAdd(container, reference, element);
	}

	@Override
	public void add(EObject container, EAttribute attribute, Object value)
			throws ModelManipulationException {
		EClass containerClass = container.eClass();
		Preconditions
				.checkArgument(
						!containerClass.getEAllAttributes().contains(attribute),
						"The container of EClass %s does neither define or inherit an EAttribute %s.",
						containerClass.getName(), attribute.getName());
		Preconditions.checkArgument(attribute.getUpperBound() > 1,
				"The EAttribute %s must have an upper bound larger than 1.",
				attribute.getName());

		doAdd(container, attribute, value);
	}

	@Override
	public void remove(EObject object) throws ModelManipulationException {
		doRemove(object);
	}

	@Override
	public void remove(EObject container, EReference reference, EObject element)
			throws ModelManipulationException {
		Preconditions.checkArgument(reference.isMany(), "Remove only works on references with 'many' multiplicity.");
		if (reference.isContainment()) {
			doRemove(element);
		} else {
			doRemove(container, reference, element);
		}
	}

	@Override
	public <Type extends EObject> void moveTo(Type what, EList<Type> where)
			throws ModelManipulationException {
		doMoveTo(what, where);
	}

	@Override
	public <Type extends EObject> void moveTo(Collection<Type> what,
			EList<Type> where) throws ModelManipulationException {
		doMoveTo(what, where);
	}

}