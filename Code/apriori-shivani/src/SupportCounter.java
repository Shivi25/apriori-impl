import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Computes the support count for generated candidates and generates Lk
 * @author Shivani Sharma
 *
 */
public class SupportCounter {

	public static Map<Set<Integer>, Integer> generateFrequentItemsets(List<Set<Integer>> D, int minSup ){
		System.out.println("Entring generateFrequentItemsets");
		
		Map<Set<Integer>, Integer>  C1 = new HashMap<Set<Integer>, Integer>();
		
		Long totalTimeForCandidateGen = 0l;
		Long totalTimeForSupportCounter = 0l;
	
		
		//Measure time consumed to generate C1
		
		Long startTime = System.nanoTime();
		//Generate C1 by iterating over D and adding all the items in the Map
		for (Set<Integer> tx : D) {
			for(Integer item : tx){
				Set<Integer> key = new HashSet<Integer>();
    			key.add(item);
    			if(C1.containsKey(key)){
    				C1.put(key, C1.get(key) + 1);
    			} else{
    				C1.put(key, 1); 				
    			}
			}
		}
		Long timeConsumed = System.nanoTime() - startTime;
		totalTimeForCandidateGen = totalTimeForCandidateGen + timeConsumed;
		
		List<String> outputContent = new ArrayList<String>();
		outputContent.add("Number of itemsets: "+C1.size());
		outputContent.add("Computation time: "+timeConsumed +" Nano seconds");
		outputContent.add("");
		for(Set<Integer> itemset : C1.keySet()){
			outputContent.add(itemset.toString());
		}
		
		// Write the output in C1.txt
		writeIntoFile("C1.txt", outputContent);

		
		//Generate L1 from C1
		Map<Set<Integer>, Integer>  L1 = new HashMap<Set<Integer>, Integer>();
		
		startTime = System.nanoTime();
		
		for (Set<Integer> entry : C1.keySet()) {
			if(C1.get(entry) >= minSup){
				L1.put(entry, C1.get(entry));
			}			
		}
		timeConsumed = System.nanoTime() - startTime;
		totalTimeForSupportCounter = totalTimeForSupportCounter + timeConsumed;
		
		outputContent = new ArrayList<String>();
		outputContent.add("Number of itemsets: "+L1.size());
		outputContent.add("Computation time: "+timeConsumed +" Nano seconds");
		outputContent.add("");
		for(Set<Integer> itemset : L1.keySet()){
			outputContent.add(itemset.toString() + " : "+L1.get(itemset));
		}
		
		// Write the output in C1.txt
		writeIntoFile("L1.txt", outputContent);

		
		
		// Now generate C2, C3..... and L1, L2.....
		Map<Set<Integer>, Integer> L = null;
		Map<Set<Integer>, Integer> Lk = null;
		Map<Set<Integer>, Integer> previousL = L1;
		Map<Set<Integer>, Integer> Ck = null;
		for(int k = 2; (previousL != null && !previousL.isEmpty()); k++){
			 startTime = System.nanoTime();
			 
			 //!!!!!!!!!!!!******** Generate Ck !!!!!!!!!!!!!******************
			 Ck = CandidateGen.generateCandidate(previousL, k, D);
			 
			 //*********** Just for Output printing **********
			 timeConsumed = System.nanoTime() - startTime;
			 
			 totalTimeForCandidateGen = totalTimeForCandidateGen + timeConsumed;
				
			outputContent = new ArrayList<String>();
			outputContent.add("Number of itemsets: "+Ck.size());
			outputContent.add("Computation time: "+timeConsumed +" Nano seconds");
			outputContent.add("");
			for(Set<Integer> itemset : Ck.keySet()){
				outputContent.add(itemset.toString());
			}
			
			// Write the output in Ck.txt
			writeIntoFile("C"+k +".txt", outputContent);
			
			//***********Output printing End **********
			 
			 startTime = System.nanoTime();
			 
			 Set<Set<Integer>> CkKeySet = Ck.keySet();
			 for (Set<Integer> t : D) {
				 //Get the subsets of t that are candidates. Means get the subsets of t that are present in the keyset of Ck
				for (Set<Integer> candidate : CkKeySet) {
					if(t.containsAll(candidate)){
						Ck.put(candidate, (Ck.get(candidate) + 1));
					}					
				}				
			}
			//Lk = {c in Ck where c.count >= min_sup 
			 Lk = new HashMap<Set<Integer>, Integer>();
			 for (Set<Integer> c : CkKeySet) {
				 if(Ck.get(c) >= minSup){
					 Lk.put(c, Ck.get(c) );
				 } else{
					 // Add into the infrequent txt
					 CandidateGen.infrequentItemSets.put(c, Ck.get(c) );
				 }
			}
			 //TODO: Verify
			 if(Lk.isEmpty()){
				 L = previousL;
			 }
			 //This will become previous L for next iteration
			 previousL = Lk;			
			
			//*********** Just for Output printing **********
			 timeConsumed = System.nanoTime() - startTime;
			 totalTimeForSupportCounter = totalTimeForSupportCounter + timeConsumed;
				
			outputContent = new ArrayList<String>();
			outputContent.add("Number of itemsets: "+Lk.size());
			outputContent.add("Computation time: "+timeConsumed +" Nano seconds");
			outputContent.add("");
			for(Set<Integer> itemset : Lk.keySet()){
				outputContent.add(itemset.toString() + " : "+Lk.get(itemset));
			}
			
			// Write the output in Lk.txt
			writeIntoFile("L"+k +".txt", outputContent);
			
			//***********Output printing End **********
			 
//			 System.out.println("Number of Frequent Candidates in current Iteration = "+previousL.size());
//			 System.out.println("Computation Time = "+timeConsumed);
//			 System.out.println("List of Frequent Candidates = "+previousL.keySet().toString());
//			 
		}	
		//*********** printing frequent.txt**********
		 	
		outputContent = new ArrayList<String>();
		outputContent.add("Number of frequent itemsets: "+L.size());
		outputContent.add("");
		for(Set<Integer> itemset : L.keySet()){
			outputContent.add(itemset.toString() + " : "+L.get(itemset));
		}
		
		// Write the output in Frequent.txt
		writeIntoFile("Frequent.txt", outputContent);
		
		// write infrequent
		outputContent = new ArrayList<String>();
		outputContent.add("Number of Infrequent itemsets: "+CandidateGen.infrequentItemSets.size());
		outputContent.add("");
		for(Set<Integer> itemset : CandidateGen.infrequentItemSets.keySet()){
			outputContent.add(itemset.toString() + " : "+CandidateGen.infrequentItemSets.get(itemset));
		}
		
		// Write the output in Frequent.txt
		writeIntoFile("Infrequent.txt", outputContent);
		
		//***********Output printing End **********
		
		//*************** Build Summary File *************
		outputContent = new ArrayList<String>();
		outputContent.add("MinSupport: "+Main.MIN_SUPPORT);
		outputContent.add("Total T(C):"+totalTimeForCandidateGen +" Nano Seconds");
		outputContent.add("Total T(L):"+totalTimeForSupportCounter +" Nano Seconds");
		outputContent.add("Total time: "+(totalTimeForCandidateGen+totalTimeForSupportCounter) +" Nano Seconds");
		outputContent.add("Frequent itemsets: "+L.size());
		outputContent.add("Infrequent itemsets: "+CandidateGen.infrequentItemSets.size());
		
			
		// Write the output in Summary.txt
		writeIntoFile("Summary.txt", outputContent);
				
		return L;
		
	}
	
	/**
	 * Writes the output contents in the text file
	 * @param fileName
	 * @param outputContent
	 */
	private static void writeIntoFile(String fileName, List<String> outputContent){
		// Write the output in txt file
				File file = new File(Main.OUTOUT_LOCATION+"\\"+fileName);
				Writer fileWriter = null;
				BufferedWriter bufferedWriter = null;
				try{
					fileWriter = new FileWriter(file);
					bufferedWriter = new BufferedWriter(fileWriter);
					for (String row : outputContent) {
						row = row + System.getProperty("line.separator");
						bufferedWriter.write(row);
					}
				} catch(Exception e){
					throw new RuntimeException("Exception Occured while writing into "+fileName, e);
				} finally{
					if (bufferedWriter != null && fileWriter != null) {
						try{
							bufferedWriter.close();
							fileWriter.close();
						} catch(Exception e){
							e.printStackTrace();
						}
					}
				}
	}
}
