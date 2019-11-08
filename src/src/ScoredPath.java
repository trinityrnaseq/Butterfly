import java.util.*;



public class ScoredPath {


	List<PasaVertex> pv_path;
	float score = 0;
	boolean path_extended = false;

	public ScoredPath (List<PasaVertex> pv_path, float score) {
		this.pv_path = pv_path;
		this.score = score;
	}
	
	
	public String toString() {
		String ret = "Score: " + score + " PV Path: " + pv_path;
		return(ret);
	}


	public float compute_extension_score(PasaVertex pv) {
		
		HashSet<PasaVertex> pv_seen = new HashSet<PasaVertex>();
		List<PasaVertex> pv_list = new ArrayList<PasaVertex>(pv_path);
		
		pv_list.add(pv);
		
		float score = 0;
		
		for (PasaVertex v : pv_list) {
			score += v.readSupport;
			if (pv_seen.contains(v)) {
				throw new RuntimeException("Error, pasa vertex " + v + " shows up in a path containment list and it should not!");
			}
			for (PasaVertex cv : v.contained_PasaVertices) {
				if (! pv_seen.contains(cv)) {
					score += cv.readSupport;
					pv_seen.add(cv);
				}
			}
		}
		
		return(score);
		
	}
	
	
	public String describe_score_calculation() {
		
		HashSet<PasaVertex> pv_seen = new HashSet<PasaVertex>();
		
		String ret_text = "Backtracking score calculation:\n";
		float score = 0;
		
		for (PasaVertex v : pv_path) {
			score += v.readSupport;
			ret_text += "pv: " + v + " tally: " + score + "\n";
			
			if (pv_seen.contains(v)) {
				throw new RuntimeException("Error, pasa vertex " + v + " shows up in a path containment list and it should not!");
			}
			for (PasaVertex cv : v.contained_PasaVertices) {
				if (pv_seen.contains(cv)) {
					ret_text += "\tcontains: " + cv + "\t (already accounted for)\n";
				} else {
					score += cv.readSupport;
					ret_text += "\tcontains: " + cv + "\t tally: " + score + "\n";
					
					pv_seen.add(cv);
				}
			}
		}
		
		ret_text += "  done backtracking.\n";
		
		return(ret_text);
		
	}
	


	public List<PairPath> get_pp_list() {
		List<PairPath> pp_list = new ArrayList<PairPath>();
		for (PasaVertex pv : this.pv_path) {
			pp_list.add(pv.pp);
		}
		
		return(pp_list);
	}
	
}



