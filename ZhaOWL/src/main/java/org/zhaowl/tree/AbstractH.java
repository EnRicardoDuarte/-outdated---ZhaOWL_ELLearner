package org.zhaowl.tree;

import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.semanticweb.owlapi.model.OWLObject;
//import org.semanticweb.owlapi.owllink.builtin.response.Hierarchy;

import com.google.common.collect.Sets;

public abstract class AbstractH<T  extends OWLObject>   {
	private SortedMap<T, SortedSet<T>> hierarchyUp;
	private SortedMap<T, SortedSet<T>> hierarchyDown;
	
	private SortedSet<T> rootEntities = new TreeSet<T>();
	private SortedSet<T> leafEntities = new TreeSet<T>();
	

	public AbstractH(SortedMap<T, SortedSet<T>> hierarchyUp, SortedMap<T, SortedSet<T>> hierarchyDown) {
		this.hierarchyUp = hierarchyUp;
		this.hierarchyDown = hierarchyDown;
		
		// find most general and most special entities
		for (T entity : Sets.union(hierarchyUp.keySet(), hierarchyDown.keySet())) {
			SortedSet<T> moreGen = getParents(entity);
			SortedSet<T> moreSpec = getChildren(entity);

			if (moreGen.size() == 0 || (moreGen.size() == 1 && moreGen.first().isTopEntity()))
				rootEntities.add(entity);

			if (moreSpec.size() == 0 || (moreSpec.size() == 1 && moreSpec.first().isBottomEntity()))
				leafEntities.add(entity);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Hierarchy#getChildren(org.semanticweb.owlapi.model.OWLEntity)
	 */ 
	public SortedSet<T> getChildren(T entity) {
		return getChildren(entity, true);
	}

	/**
	 * @return all entities in this hierarchy
	 */
	public Set<T> getEntities() {
		return Sets.union(hierarchyDown.keySet(), hierarchyUp.keySet());
	}

	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Hierarchy#getChildren(org.semanticweb.owlapi.model.OWLEntity, boolean)
	 */ 
	public SortedSet<T> getChildren(T entity, boolean direct) {
		SortedSet<T> result =  hierarchyDown.get(entity);
		
		if(result == null) {
			System.out.println("Query for " + entity + " in hierarchy, but the entity is not contained in the (downward) hierarchy, e.g. because the entity does not exist or is ignored. Returning empty result instead.");
			return new TreeSet<T>();
		}
		
		result.remove(entity);
		if(!direct) { // get transitive children
			SortedSet<T> tmp = new TreeSet<T>();
			for(T child : result){
				tmp.addAll(getChildren(child, direct));
			}
			result.addAll(tmp);
		}
		return new TreeSet<T>(result);
	}

	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Hierarchy#getParents(org.semanticweb.owlapi.model.OWLEntity)
	 */ 
	public SortedSet<T> getParents(T entity) {
		return getParents(entity, true);
	}

	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Hierarchy#getParents(org.semanticweb.owlapi.model.OWLEntity, boolean)
	 */ 
	public SortedSet<T> getParents(T entity, boolean direct) {
		SortedSet<T> result =  hierarchyUp.get(entity);
		
		if(result == null) {
			System.out.println("Query for " + entity + " in hierarchy, but the entity is not contained in the (upward) hierarchy, e.g. because the entity does not exist or is ignored. Returning empty result instead.");
			return new TreeSet<T>();
		}
		
		result.remove(entity);
		if(!direct) {
			SortedSet<T> tmp = new TreeSet<T>();
			for(T parent : result){
				tmp.addAll(getParents(parent, direct));
			}
			result.addAll(tmp);
		}
		
		return new TreeSet<T>(result);
	}

	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Hierarchy#getSiblings(org.semanticweb.owlapi.model.OWLEntity)
	 */ 
	public SortedSet<T> getSiblings(T entity) {
		SortedSet<T> siblings = new TreeSet<T>();
		
		Set<T> parents = getParents(entity);
		for(T parent : parents) {
			siblings.addAll(getChildren(parent));
		}
		
		siblings.remove(entity);
		return siblings;
	}

	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Hierarchy#isChildOf(org.semanticweb.owlapi.model.OWLEntity, org.semanticweb.owlapi.model.OWLEntity)
	 */ 
	public boolean isChildOf(T entity1, T entity2) {
		if (entity1.equals(entity2)) {
			return true;
		} else {
			SortedSet<T> parents = getParents(entity1);
			
			if(parents != null){
				// search the upper classes of the subclass
				for (T parent : parents) {
					if (isChildOf(parent, entity2)) {
						return true;
					}
				}
			}
			// we cannot reach the class via any of the upper classes,
			// so it is not a super class
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Hierarchy#isParentOf(org.semanticweb.owlapi.model.OWLClassExpression, org.semanticweb.owlapi.model.OWLClassExpression)
	 */ 
	public boolean isParentOf(T entity1, T entity2) {
		return isChildOf(entity2, entity1);
	}

	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Hierarchy#getRoots()
	 */ 
	public SortedSet<T> getRoots() {
		SortedSet<T> roots = new TreeSet<T>();
		
		for(T child : getChildren(getTopConcept())){
			SortedSet<T> parents = getParents(child);
			parents.remove(getTopConcept());
			
			if(parents.isEmpty()){
				roots.add(child);
			}
		}
		return roots;
	}
	
	/**
	 * @return The most general entites.
	 */
	public SortedSet<T> getMostGeneralEntities() {
		return rootEntities;
	}

	/**
	 * @return The most special roles.
	 */
	public SortedSet<T> getMostSpecialEntities() {
		return leafEntities;
	}
	
	/* (non-Javadoc)
	 * @see org.dllearner.core.owl.Hierarchy#contains(org.semanticweb.owlapi.model.OWLObject)
	 */ 
	public boolean contains(T entity) {
		return hierarchyUp.containsKey(entity);
	}
	
	/**
	 * This method modifies the subsumption hierarchy such that for each class,
	 * there is only a single path to reach it via upward and downward
	 * refinement respectively.
	 */
	public void thinOutSubsumptionHierarchy() {
		SortedMap<T, SortedSet<T>> hierarchyDownNew = new TreeMap<T, SortedSet<T>>();
		SortedMap<T, SortedSet<T>> hierarchyUpNew = new TreeMap<T, SortedSet<T>>();

		Set<T> conceptsInSubsumptionHierarchy = new TreeSet<T>();
		conceptsInSubsumptionHierarchy.addAll(hierarchyUp.keySet());
		conceptsInSubsumptionHierarchy.addAll(hierarchyDown.keySet());
		
		// add empty sets for each concept
		for (T c : conceptsInSubsumptionHierarchy) {
			hierarchyDownNew.put(c, new TreeSet<T>());
			hierarchyUpNew.put(c, new TreeSet<T>());
		}

		for (T c : conceptsInSubsumptionHierarchy) {
			// look whether there are more general concepts
			// (if yes, pick the first one)
			SortedSet<T> moreGeneral = getParents(c);
			if (moreGeneral != null && !moreGeneral.isEmpty()) {
				T chosenParent = moreGeneral.first();
				hierarchyDownNew.get(chosenParent).add(c);
			}
		}

		for (T c : conceptsInSubsumptionHierarchy) {
			SortedSet<T> moreSpecial = getChildren(c);
			if (moreSpecial != null && !moreSpecial.isEmpty()) {
				T chosenChild = moreSpecial.first();
				hierarchyUpNew.get(chosenChild).add(c);
			}
		}
		
		// top node
		hierarchyDownNew.put(getTopConcept(), getChildren(getTopConcept()));
		
		// bottom node
		hierarchyUpNew.put(getBottomConcept(), getParents(getBottomConcept()));
		
		setHierarchyDown(hierarchyDownNew);
		setHierarchyUp(hierarchyUpNew);
	}
	
	/**
	 * The method computes a new subsumption hierarchy, which is a copy of this
	 * one, but only the specified entities are allowed to occur. For instance,
	 * if we have subclass relationships between 1sYearStudent, Student, and
	 * Person, but Student is not allowed, then there a is a subclass relationship
	 * between 1stYearStudent and Person.
	 * Currently, owl:Thing and owl:Nothing are always allowed for technical
	 * reasons.
	 * @param allowedEntities The entities, which are allowed to occur in the new
	 * subsumption hierarchy.
	 * @return A copy of this hierarchy, which is restricted to a certain set
	 * of entities.
	 */
	public AbstractH<T> cloneAndRestrict(Set<? extends T> allowedEntities) {
		// currently TOP and BOTTOM are always allowed
		// (TODO would be easier if Thing/Nothing were declared as named classes)
		Set<T> allowed = new TreeSet<T>();
		allowed.addAll(allowedEntities);
		allowed.add(getTopConcept());
		allowed.add(getBottomConcept());
		
		// create new maps
		SortedMap<T, SortedSet<T>> subsumptionHierarchyUpNew = new TreeMap<T, SortedSet<T>>();
		SortedMap<T, SortedSet<T>> subsumptionHierarchyDownNew = new TreeMap<T, SortedSet<T>>();
		
		for(Entry<T, SortedSet<T>> entry : hierarchyUp.entrySet()) {
			T key = entry.getKey();
			// we only store mappings for allowed entities
			if(allowed.contains(key)) {
				// copy the set of all parents (we consume them until
				// they are empty)
				TreeSet<T> parents = new TreeSet<T>(entry.getValue());
				// storage for new parents
				TreeSet<T> newParents = new TreeSet<T>();
				
				while(!parents.isEmpty()) {
					// pick and remove the first element
					T d = parents.pollFirst();
					// case 1: it is allowed, so we add it
					if(allowed.contains(d)) {
						newParents.add(d);
					// case 2: it is not allowed, so we try its super classes
					} else {
						Set<T> tmp = hierarchyUp.get(d);
						if(tmp != null){
							parents.addAll(tmp);
						}
					}
				}
				
				subsumptionHierarchyUpNew.put(key, newParents);
			}
		}
		
		// downward case is analogous
		for(Entry<T, SortedSet<T>> entry : hierarchyDown.entrySet()) {
			T key = entry.getKey();
			if(allowed.contains(key)) {
				TreeSet<T> children = new TreeSet<T>(entry.getValue());
				TreeSet<T> newChildren = new TreeSet<T>();
				
				while(!children.isEmpty()) {
					T d = children.pollFirst();
					if(allowed.contains(d)) {
						newChildren.add(d);
					} else {
						SortedSet<T> tmp = hierarchyDown.get(d);
						if(tmp != null) {
							children.addAll(tmp);
						}
					}
				}
				
				subsumptionHierarchyDownNew.put(key, newChildren);
			}
		}		

		try {
			return this.getClass().getConstructor(
					SortedMap.class, SortedMap.class).newInstance(subsumptionHierarchyUpNew, subsumptionHierarchyDownNew);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param hierarchyUp the hierarchyUp to set
	 */
	public void setHierarchyUp(SortedMap<T, SortedSet<T>> hierarchyUp) {
		this.hierarchyUp = hierarchyUp;
	}
	
	/**
	 * @return the hierarchyUp
	 */
	public SortedMap<T, SortedSet<T>> getHierarchyUp() {
		return hierarchyUp;
	}
	
	/**
	 * @param hierarchyDown the hierarchyDown to set
	 */
	public void setHierarchyDown(SortedMap<T, SortedSet<T>> hierarchyDown) {
		this.hierarchyDown = hierarchyDown;
	}
	
	/**
	 * @return the hierarchyDown
	 */
	public SortedMap<T, SortedSet<T>> getHierarchyDown() {
		return hierarchyDown;
	}
	
	public void precompute() {
		
	}
	
	@Override
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean showUpwardHierarchy) {
		if (showUpwardHierarchy) {
			String str = "downward subsumption:\n";
			str += toString(hierarchyDown, getTopConcept(), 0);
			str += "upward subsumption:\n";
			str += toString(hierarchyUp, getBottomConcept(), 0);
			return str;
		} else {
			return toString(hierarchyDown, getTopConcept(), 0);
		}
	}
	
	protected String toString(SortedMap<T, SortedSet<T>> hierarchy, T concept, int depth) {
		String str = "";
		for (int i = 0; i < depth; i++)
			str += "  ";
		str += concept.toString() + "\n";
		Set<T> tmp = hierarchy.get(concept);
		if (tmp != null) {
			for (T c : tmp)
				str += toString(hierarchy, c, depth + 1);
		}
		return str;
	}
	
	public abstract T getTopConcept();
	public abstract T getBottomConcept();

}
