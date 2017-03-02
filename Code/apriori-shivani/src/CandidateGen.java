import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates potential candidate Ck for each level of the iterative apriori algorithm
 * @author Shivani Sharma
 *
 */
public class CandidateGen {
	
	static Map<Set<Integer>, Integer>  infrequentItemSets = new HashMap<Set<Integer>, Integer>();
	
	public static Map<Set<Integer>, Integer> generateCandidate(Map<Set<Integer>, Integer>  previousL, int k, List<Set<Integer>> D){
		System.out.println("Entering generateCandidate method to generate Ck. k = "+k);
		
		
		Long startTime = System.nanoTime();
		Map<Set<Integer>, Integer> Ck = new HashMap<Set<Integer>, Integer>();
		for (Set<Integer> l1 : previousL.keySet()) {
			List l1List = new ArrayList(l1);
			for (Set<Integer> l2 : previousL.keySet()) {
				List l2List = new ArrayList(l2);
				Set<Integer> c = null;
				if(!l1.equals(l2)){
					//first k-1 items should be same
					boolean initalItemsSame = true;
					for(int counter = 0; counter < k-2; counter++){
						if(l1List.get(counter).equals(l2List.get(counter))){
							continue;
						} else{
							initalItemsSame = false;
						}
					}
					if(initalItemsSame && !(l1List.get(k-2).equals(l2List.get(k-2)))){
						c = new HashSet<Integer>();
						c.addAll(l1);
						c.addAll(l2);
						if(hasInfrequentSubset(c, previousL, k)){
							Map<Set<Integer>, Integer>  localInfrequentItemSets = new HashMap<Set<Integer>, Integer>();
							//delete c. prune step: remove unfruitful candidate
							//Add it into the infrequent.txt
							 for (Set<Integer> t : D) {
								 //Get the subsets of t that are candidates. Means get the subsets of t that are present in the keyset of Ck
								
								if(t.containsAll(c)){
									if(localInfrequentItemSets.get(c) == null){
										localInfrequentItemSets.put(c, 1);
									} else {
										localInfrequentItemSets.put(c, localInfrequentItemSets.get(c) + 1);
									}
								}					
										
							}	
							infrequentItemSets.putAll(localInfrequentItemSets);
							
						} else{
							Ck.put(c, 0);
						}
					}
				}
			}
		}
		
		
		
		Long endTime = System.nanoTime();
		Long timeTaken = endTime - startTime;
		System.out.println("Generated "+Ck.size() +" Candidates.");
		System.out.println("Computation Time = "+timeTaken);
		System.out.println("Generated following candidates = "+Ck.keySet().toString());
		
		return Ck;
		
	}
	
	/**
	 * Checks if
	 * @param c - candidate k-itemset
	 * @param previousL - frequent (k-1)-itemsets
	 * @param k
	 * @return
	 */
	public static boolean hasInfrequentSubset(Set<Integer> c,  Map<Set<Integer>, Integer> previousL, int k){
		Set<Set<Integer>> subsetsset = generateAllSubsets(c, k-1);
		for (Set<Integer> s : subsetsset) {
			if(!previousL.containsKey(s)){
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Utility method to create all subsets of a set
	 * @param original
	 * @return Set of subsets
	 */
	public static Set<Set<Integer>> generateAllSubsets(Set<Integer> originalSet, int subsetSize) {
	    Set<Set<Integer>> allSubsets = new HashSet<Set<Integer>>();

	    allSubsets.add(new HashSet<Integer>()); //Add empty set.

	    for (Integer element : originalSet) {
	        // Copy subsets so we can iterate over them without ConcurrentModificationException
	        Set<Set<Integer>> tempClone = new HashSet<Set<Integer>>(allSubsets);

	        // All element to all subsets of the current power set.
	        for (Set<Integer> subset : tempClone) {
	            Set<Integer> extended = new HashSet<Integer>(subset);
	            extended.add(element);
	           
	            	allSubsets.add(extended);
	                 
	        }
	    }
	    Set<Set<Integer>> validSubsets = new HashSet<Set<Integer>>();
	    for (Set<Integer> set : allSubsets) {
	    	if(set.size() == subsetSize){
	    		validSubsets.add(set);
	    	}
		}
	    
	    return validSubsets;
	}
}
