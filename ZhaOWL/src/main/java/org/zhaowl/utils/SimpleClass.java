package org.zhaowl.utils;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

public class SimpleClass {
	public ManchesterOWLSyntaxOWLObjectRendererImpl rendering;
	
	public SimpleClass(ManchesterOWLSyntaxOWLObjectRendererImpl rendering)
	{
		this.rendering = rendering;
	}
	
	public int[] showCISizes(Set<OWLAxiom> axSet)
	{
		int[] returns = new int[2];
		
		int sumSize = 0;
		
		OWLAxiom smallestOne = null;
		int smallestSize = 0;
		int totalSize = 0;
		for (OWLAxiom axe : axSet) {
			String inclusion = rendering.render(axe);
			inclusion = inclusion.replaceAll(" and ", " ");
			inclusion = inclusion.replaceAll(" some ", " ");
			
			if(axe.toString().contains("SubClassOf"))
				inclusion = inclusion.replaceAll("SubClassOf", "");
			else
				inclusion = inclusion.replaceAll("EquivalentTo", ""); 
			//System.out.println(inclusion);
			String[] arrIncl = inclusion.split(" ");
			totalSize = 0;
			
			for(int i = 0; i < arrIncl.length; i++)
				if(arrIncl[i].length() > 0 &&  !arrIncl[i].equals("some"))
					totalSize++;
			
			//for(int i = 0; i < arrIncl.length; i++)
			//	System.out.println(arrIncl[i] + "=====" +arrIncl[i].length());
			
			//System.out.println(totalSize);
			if(smallestOne == null) {
				smallestOne = axe;
				smallestSize = totalSize;
			}
			else
			{
				if(smallestSize > totalSize)
				{
					smallestOne = axe;
					smallestSize = totalSize;
				}
			}
				
			sumSize += totalSize;
			//System.out.println("Size of : " + rendering.render(axe) + "." + totalSize);
		}
		System.out.println("Smallest logical axiom: " + rendering.render(smallestOne));
		System.out.println("Size is: " + smallestSize);
		returns[0] = smallestSize;
		returns[1] = sumSize / axSet.size();
		return returns;
	}
}
