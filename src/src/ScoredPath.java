import java.util.*;



public class ScoredPath {


	List<PasaVertex> pv_path;
	Float pv_path_score = 0f;
	Boolean path_extended = false;
	HashSet<PasaVertex> sp_contained_pasa_vertices;

	public ScoredPath (List<PasaVertex> pv_path, float score) {
		this.pv_path = pv_path;
		this.pv_path_score = score;
		sp_contained_pasa_vertices = new HashSet<PasaVertex>();
		for (PasaVertex pv : pv_path) {
			for (PasaVertex pv_contained : pv.contained_PasaVertices) {
				sp_contained_pasa_vertices.add(pv_contained);
			}
		}
	}
	
	public ScoredPath (ScoredPath sp, PasaVertex pv, float score) {
		
		// create an extended PV path list
		this.pv_path = new ArrayList<PasaVertex>();
		this.pv_path.addAll(sp.pv_path);
		this.pv_path.add(pv);
		
		// incorporate all contained vertices
		this.sp_contained_pasa_vertices = new HashSet<PasaVertex>();
		this.sp_contained_pasa_vertices.addAll(sp.sp_contained_pasa_vertices);
		this.sp_contained_pasa_vertices.addAll(pv.contained_PasaVertices);
		
		this.pv_path_score = score;
		
		// ensure score is as expected:

		if (Math.abs(this.pv_path_score - this.tally_score()) / this.tally_score() > 0.01) {
			throw new RuntimeException("Error, tallied score doesn't match stored score.");
		}
		
	}
	
	
	
	public String toString() {
		String ret = "Score: " + pv_path_score + " PV Path: " + pv_path + "\n";
		for (PasaVertex pv : this.sp_contained_pasa_vertices) {
			ret += "\tcontains: " + pv.pp + "\n";
		}
		return(ret);
	}

	
	public float compute_extension_score(PasaVertex pv) {
		
		
		// score pv chain
		List<PasaVertex> pv_list = new ArrayList<PasaVertex>(pv_path);
		pv_list.add(pv);
		
		float ext_score = 0;
		
		for (PasaVertex v : pv_list) {
			ext_score += v.readSupport;
			if (sp_contained_pasa_vertices.contains(v)) {
				throw new RuntimeException("Error, pasa vertex " + v + " shows up in a path containment list and it should not!");
			}
		}
		
		
		// score containments
		HashSet<PasaVertex> all_contained_pasa_vertices = new HashSet<PasaVertex>();
		all_contained_pasa_vertices.addAll(this.sp_contained_pasa_vertices);
		all_contained_pasa_vertices.addAll(pv.contained_PasaVertices);
		
		for (PasaVertex cv : all_contained_pasa_vertices) {

			ext_score += cv.readSupport;

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
				throw new RuntimeException("Error, pasa vertex " + v + " shows up in a path containment list and it should not!" 
						+ "\n backtrack so far: " + ret_text);
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
	
		if (Math.abs(this.pv_path_score - this.tally_score()) / this.tally_score() > 0.01) {
			throw new RuntimeException(ret_text + "\nError, tallied score doesn't match stored score.");
		}
		
		return(ret_text);
		
	}
	

	public float tally_score() {
		
		float loc_score = 0;
		
		for (PasaVertex v : pv_path) {
			if (this.sp_contained_pasa_vertices.contains(v)) {
				throw new RuntimeException("Error, pasa vertex " + v + " shows up in a path containment list and it should not!" +
			"\n" + this);
			}
			loc_score += v.readSupport;
		}
		for (PasaVertex cv : this.sp_contained_pasa_vertices) {
			loc_score += cv.readSupport;
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


	public void rescore() {
		this.pv_path_score = this.tally_score();
		
	}
	
	
	public static Comparator<ScoredPath> ScoredPathComparer = new Comparator<ScoredPath>() { // sort by first node depth in graph
		
		public int compare(ScoredPath a, ScoredPath b) {

			if (a.pv_path_score < b.pv_path_score) {
				return(-1);
			} else if (a.pv_path_score.equals(b.pv_path_score)) {
				return(0);
			}
			else if (a.pv_path_score > b.pv_path_score) {
				return(1);
			}
			else {
				throw new RuntimeException("Error, couldn't do a proper comparison between scored paths"); // shouldn't ever get here.
			}
		}
	};

	public String unique_nodeset_description() {
		
		String ret_string = "path_score: " + this.pv_path_score + " "  + this.get_unique_nodeset();
		
		return(ret_string);
		
	}
	
	
	public HashSet<Integer> get_unique_nodeset() {
		
		HashSet<Integer> all_node_ids = new HashSet<Integer>();
		
		for (PasaVertex pv : this.pv_path) {
			all_node_ids.addAll(pv.pp.get_all_node_ids());
		}
		
		return(all_node_ids);
		
	}
	
}



