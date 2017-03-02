import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Contains the main method and calls other components of apriori algorithm
 * @author Shivani Sharma
 *
 */
public class Main {
	static int MIN_SUPPORT = 5;
	static String DATASET_LOCATION = "C://A3/Package/Datasets/dataset2.txt";
	static String OUTOUT_LOCATION = "C://A3/Output/B/minSup5";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Map<Set<Integer>, Integer> C1 = new HashMap<Set<Integer>, Integer>();
		List<Set<Integer>> D = new ArrayList<Set<Integer>>();
		
		try{
			File file = new File(DATASET_LOCATION);
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));				
			
			
	    	while (bufferedReader.ready()) {    		
	    		String line=bufferedReader.readLine();
	    		StringTokenizer tokenizer = new StringTokenizer(line," ");
	    		Set<Integer> tx = new HashSet<Integer>();
	    		while (tokenizer.hasMoreTokens()) {
	    			int item = Integer.parseInt(tokenizer.nextToken());  
	    			//Add into the 
	    			tx.add(item);
	    		}  
	    		D.add(tx);
	    	}
	    	
	    	bufferedReader.close();	    	
	    	
		} catch(Exception e){
			throw new RuntimeException("Exception Occured in reading file.", e);
		} 
		try{
			SupportCounter.generateFrequentItemsets(D, MIN_SUPPORT);
		} catch(Exception e){
			throw new RuntimeException("Exception Occured while generating frequent item sets.", e);
		}
		

	}

}
