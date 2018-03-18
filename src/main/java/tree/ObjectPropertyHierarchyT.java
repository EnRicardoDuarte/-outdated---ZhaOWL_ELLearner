package tree;

import java.util.Set; 
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.dllearner.core.owl.AbstractHierarchy;
import org.dllearner.core.owl.ObjectPropertyHierarchy;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.owllink.builtin.response.HierarchyPair;
import org.semanticweb.owlapi.owllink.builtin.response.ResponseVisitor;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

public class ObjectPropertyHierarchyT extends AbstractH<OWLObjectProperty> {
	private static final OWLObjectProperty OWL_TOP_OBJECT_PROPERTY = new OWLObjectPropertyImpl(
			OWLRDFVocabulary.OWL_TOP_OBJECT_PROPERTY.getIRI());
	private static final OWLObjectProperty OWL_BOTTOM_OBJECT_PROPERTY = new OWLObjectPropertyImpl(
			OWLRDFVocabulary.OWL_BOTTOM_OBJECT_PROPERTY.getIRI());

	public ObjectPropertyHierarchyT(
			SortedMap<OWLObjectProperty, SortedSet<OWLObjectProperty>> roleHierarchyUp,
			SortedMap<OWLObjectProperty, SortedSet<OWLObjectProperty>> roleHierarchyDown) {
		super(roleHierarchyUp, roleHierarchyDown);
	}
	
	public SortedSet<OWLObjectProperty> getMoreGeneralRoles(OWLObjectProperty role) {
		return new TreeSet<OWLObjectProperty>(getParents(role));
	}
	
	public SortedSet<OWLObjectProperty> getMoreSpecialRoles(OWLObjectProperty role) {
		return new TreeSet<OWLObjectProperty>(getChildren(role));
	}
	
	public boolean isSubpropertyOf(OWLObjectProperty subProperty, OWLObjectProperty superProperty) {
		return isChildOf(subProperty, superProperty);
	}	

	/**
	 * @return The most general roles.
	 */
	public SortedSet<OWLObjectProperty> getMostGeneralRoles() {
		return getMostGeneralEntities();
	}

	/**
	 * @return The most special roles.
	 */
	public SortedSet<OWLObjectProperty> getMostSpecialRoles() {
		return getMostSpecialEntities();
	}

	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.AbstractHierarchy#getTopConcept()
	 */
	@Override
	public OWLObjectProperty getTopConcept() {
		return OWL_TOP_OBJECT_PROPERTY;
	}

	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.AbstractHierarchy#getBottomConcept()
	 */
	@Override
	public OWLObjectProperty getBottomConcept() {
		return OWL_BOTTOM_OBJECT_PROPERTY;
	}
	
	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.AbstractHierarchy#toString(java.util.SortedMap, org.semanticweb.owlapi.model.OWLObject, int)
	 */
	@Override
	protected String toString(SortedMap<OWLObjectProperty, SortedSet<OWLObjectProperty>> hierarchy,
			OWLObjectProperty prop, int depth) {
		String str = "";
		for (int i = 0; i < depth; i++)
			str += "  ";
		str += prop.toString() + "\n";
		Set<OWLObjectProperty> tmp;
		if(prop.isTopEntity()) {
			tmp = getMostGeneralRoles();
		} else {
			tmp  = hierarchy.get(prop);
		}
		
		if (tmp != null) {
			for (OWLObjectProperty c : tmp)
				str += toString(hierarchy, c, depth + 1);
		}
		return str;
	}
	
	@Override
	public ObjectPropertyHierarchy clone() {
		return new ObjectPropertyHierarchy(getHierarchyUp(), getHierarchyDown());		
	}

	public Set<HierarchyPair<OWLObjectProperty>> getPairs() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node<OWLObjectProperty> getUnsatisfiables() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getWarning() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasWarning() {
		// TODO Auto-generated method stub
		return false;
	}

	public <O> O accept(ResponseVisitor<O> arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
