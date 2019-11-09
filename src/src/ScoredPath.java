import java.util.*;



public class ScoredPath {


	List<PasaVertex> pv_path;
	Float pv_path_score = 0f;
	Boolean path_extended = false;

	public ScoredPath (List<PasaVertex> pv_path, float score) {
		this.pv_path = pv_path;
		this.pv_path_score = score;
	}
	
	
	public String toString() {
		String ret = "Score: " + pv_path_score + " PV Path: " + pv_path;
		return(ret);
	}


	public float compute_extension_score(PasaVertex pv) {
		
		HashSet<PasaVertex> pv_seen = new HashSet<PasaVertex>();
		List<PasaVertex> pv_list = new ArrayList<PasaVertex>(pv_path);
		
		pv_list.add(pv);
		
		float ext_score = 0;
		
		for (PasaVertex v : pv_list) {
			ext_score += v.readSupport;
			if (pv_seen.contains(v)) {
				throw new RuntimeException("Error, pasa vertex " + v + " shows up in a path containment list and it should not!");
			}
			for (PasaVertex cv : v.contained_PasaVertices) {
				if (! pv_seen.contains(cv)) {
					ext_score += cv.readSupport;
					pv_seen.add(cv);
				}
			}
		}
		
		return(ext_score);
		
	}
	
	
	public String describe_score_calculation() {
		
		HashSet<PasaVertex> pv_seen = new HashSet<PasaVertex>();
		
		String ret_text = "Backtracking score calculation. Stored score = " + this.pv_path_score + " :\n";
		float loc_score = 0;
		
		for (PasaVertex v : pv_path) {
			loc_score += v.readSupport;
			ret_text += "pv: " + v + " tally: " + loc_score + "\n";
			
			if (pv_seen.contains(v)) {
				throw new RuntimeException("Error, pasa vertex " + v + " shows up in a path containment list and it should not!");
			}
			for (PasaVertex cv : v.contained_PasaVertices) {
				if (pv_seen.contains(cv)) {
					ret_text += "\tcontains: " + cv + "\t (already accounted for)\n";
				} else {
					loc_score += cv.readSupport;
					ret_text += "\tcontains: " + cv + "\t tally: " + loc_score + "\n";
					
					pv_seen.add(cv);
				}
			}
		}
		
		ret_text += "  done backtracking.\n";
	
		if (Math.abs(this.pv_path_score - this.tally_score()) > 0.0001) {
			throw new RuntimeException(ret_text + "\nError, tallied score doesn't match.");
		}
		
		return(ret_text);
		
	}
	

	public float tally_score() {
		
		HashSet<PasaVertex> pv_seen = new HashSet<PasaVertex>();
		
		
		float loc_score = 0;
		
		for (PasaVertex v : pv_path) {
			loc_score += v.readSupport;
			
			
			if (pv_seen.contains(v)) {
				throw new RuntimeException("Error, pasa vertex " + v + " shows up in a path containment list and it should not!");
			}
			for (PasaVertex cv : v.contained_PasaVertices) {
				if (! pv_seen.contains(cv)) {
					loc_score += cv.readSupport;
					pv_seen.add(cv);
				}
			}
		}
		
		return(loc_score);
		
	}
	
	

	public List<PairPath> get_pp_list() {
		List<PairPath> pp_list = new ArrayList<PairPath>();
		for (PasaVertex pv : this.pv_path) {
			pp_list.add(pv.pp);
		}
		
		return(pp_list);
	}
	
}



