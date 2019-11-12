import java.util.*;


public class PasaVertex {

	final PairPath pp;
	Float readSupport = 0f; // original read support, also used in path scoring.
	Float adjustedReadSupport = 0f; // this gets adjusted after path extractions
	
	
	public static int max_top_paths_to_store = 1;
	
	List<ScoredPath> fromPaths;
	//List<ScoredPath> toPaths;
	
	List<PasaVertex> contained_PasaVertices;
	
	HashSet<PasaVertex> overlapping_compatible_PasaVertices;
	
	boolean used = false;
	
	private Comparator<ScoredPath> sp_comparator = new Comparator<ScoredPath>() {
		
		public int compare (ScoredPath spA, ScoredPath spB) {
			
			// want it to sort descendingly on score
			
			if (spA.pv_path_score < spB.pv_path_score) {
				return(1);
			}
			else if (spA.pv_path_score > spB.pv_path_score) {
				return(-1);
			}
			else {
				return(0);
			}
		}
		
	};
	
	
	public PasaVertex (final PairPath p, float readSupport) {
		
		this.pp = p;
		this.readSupport = readSupport;	
		this.adjustedReadSupport = readSupport;
	
		this.contained_PasaVertices = new ArrayList<PasaVertex>();
		this.overlapping_compatible_PasaVertices = new HashSet<PasaVertex>();
		
		this.init_PasaVertex_to_and_from_paths();
	}
	
	
	
	public void reset_score_to_adjusted_score() {
		this.readSupport = this.adjustedReadSupport; // reset
	}
	
	
	public void init_PasaVertex_to_and_from_paths() {
		List<PasaVertex> pv_path = new ArrayList<PasaVertex>();
		pv_path.add(this);
		
		this.fromPaths = new ArrayList<ScoredPath>();
		//toPaths = new ArrayList<ScoredPath>();
		
		ScoredPath sp = new ScoredPath(pv_path, this.readSupport + this.containment_support());
		
		if (BFLY_GLOBALS.VERBOSE_LEVEL >= 20) {
			System.err.println("PasaVertex score initialization: " + this.pp + ", readSupport: " + this.readSupport +
					", containment_support: " + this.containment_support() + 
					", pv_path_score: " + sp.pv_path_score + "\n\tdescription:" );
			System.err.println(sp.describe_score_calculation());
		}
		
		
		this.fromPaths.add(sp);
		//this.toPaths.add(sp);
		
	}
	
	
	public float containment_support() {
		
		float sum_containment_read_support = 0;
		for (PasaVertex pv : this.contained_PasaVertices) {
			sum_containment_read_support += pv.readSupport;
		}
		return(sum_containment_read_support);
	}
	
	
	public void set_used() {
		if (! this.used) {
			this.used = true;
		}
		
		HashSet<PasaVertex> seen = new HashSet<PasaVertex>();
		seen.add(this);
		
		for (PasaVertex pv : this.contained_PasaVertices) {
			
			pv.set_used(seen);
		}
	}
	
	public void set_used(HashSet<PasaVertex> seen) {
		if (! this.used) {
			this.used = true;
		}
		seen.add(this);
		
		
		for (PasaVertex pv : this.contained_PasaVertices) {
			if (! seen.contains(pv)) {
				pv.set_used(seen);
			}
		}
	}
	
	
	
	public boolean is_used() {
			return(used);
	}
	
	/*
	public final List<ScoredPath> get_toPaths () {
		return(toPaths);
	} */
	
	public final List<ScoredPath> get_fromPaths() {
		return(fromPaths);
	}
	
	/*
	public void push_toPaths(ScoredPath sp) {
		
		push_path_list(this.toPaths, sp);
		
		
	} */
	
	public void push_fromPaths(ScoredPath sp) {
		
		//System.err.println("SCOREPATH SCORECHECK: pv_path_score: " + sp.pv_path_score + ", tallied: " + sp.tally_score());
		
		if (sp.pv_path_score != sp.tally_score()) {
			throw new RuntimeException("Error, tallied score doesn't match: " + sp.describe_score_calculation());
		}
		
		push_path_list(this.fromPaths, sp);
		
	}
	
	
	private void push_path_list (List<ScoredPath> sp_list, ScoredPath sp) {
	
		//System.out.println("SP_LIST size before: " + sp_list.size());
		
		sp_list.add(sp);
		
		if (sp_list.size() > PasaVertex.max_top_paths_to_store) {
			Collections.sort(sp_list, this.sp_comparator);
		
			sp_list.retainAll(sp_list.subList(0, max_top_paths_to_store));
		}
		
		//System.out.println("SP_LIST size after: " + sp_list.size());
	
	}
	
	
	/*
	public final ScoredPath get_highest_scoring_toPath () {
		
		ScoredPath best = null;
		for (ScoredPath sp : this.get_toPaths()) {
			if (best == null || best.pv_path_score < sp.pv_path_score)
				best = sp;
		}
		return(best);
	} */
	
	/*
	public final List<ScoredPath> get_all_highest_scoring_toPath () {
		
		List<ScoredPath> best_paths = new ArrayList<ScoredPath>();
		
		for (ScoredPath sp : this.get_toPaths()) {
			if (best_paths.isEmpty()) {
				best_paths.add(sp);
			}
			else if (sp.pv_path_score == best_paths.get(0).pv_path_score) {
				best_paths.add(sp);
			}
			else if (sp.pv_path_score > best_paths.get(0).pv_path_score) {
				best_paths.clear();
				best_paths.add(sp);
			}
			
		}
		return(best_paths);
	} */
	
	
	
	public final ScoredPath get_highest_scoring_fromPath () {
		ScoredPath best = null;
		for (ScoredPath sp : this.get_fromPaths()) {
			if (best == null || best.pv_path_score < sp.pv_path_score)
				best = sp;
		}
		return(best);
	}
	
	public final List<ScoredPath> get_all_highest_scoring_fromPath () {

		List<ScoredPath> best_paths = new ArrayList<ScoredPath>();

		for (ScoredPath sp : this.get_fromPaths()) {
			if (best_paths.isEmpty()) {
				best_paths.add(sp);
			}
			else if (sp.pv_path_score == best_paths.get(0).pv_path_score) {
				best_paths.add(sp);
			}
			else if (sp.pv_path_score > best_paths.get(0).pv_path_score) {
				best_paths.clear();
				best_paths.add(sp);
			}

		}
		return(best_paths);
	}
	
	
	
	public String toString () {
		String ret = "PasaVertex.  PairPath: " + this.pp + ", readSupport: " + this.readSupport;
		
		return(ret);
	}


	public void add_containment(PasaVertex pv) {
		this.contained_PasaVertices.add(pv);
		
	}


	public void decrement_read_support(List<Integer> best_path_vertex_list) {
		
		float compat_read_support_CONFLICTS_with_path = 0;
		float compat_read_support_COMPATIBLE_to_path = 0;
		
		for (PasaVertex pv : this.overlapping_compatible_PasaVertices) {
			if (pv.pp.isCompatibleAndContainedBySinglePath(best_path_vertex_list)) {
				compat_read_support_COMPATIBLE_to_path += pv.readSupport;
			} else {
				compat_read_support_CONFLICTS_with_path += pv.readSupport;
			}
		}
	
		float pseudocount = (float) 1e-6;
		
		float fraction_conflict = 
				(compat_read_support_CONFLICTS_with_path + pseudocount) /
				(compat_read_support_CONFLICTS_with_path + compat_read_support_COMPATIBLE_to_path + pseudocount);
		
		float adjusted_support = this.readSupport * fraction_conflict;
		
		if (BFLY_GLOBALS.VERBOSE_LEVEL >= 10) {
			System.err.println("Decrementing read support for: " + this.pp + 
					", read_support_COMPAT: " + compat_read_support_COMPATIBLE_to_path +
					", read_support_CONFLICT: " + compat_read_support_CONFLICTS_with_path +
					", fraction_conflict: " + fraction_conflict +
					"(current_read_support: " + this.readSupport + "  => adjusted_read_support: " + adjusted_support + ")");
			
		}
		
		this.adjustedReadSupport = adjusted_support;
				
		
	}


	public String show_from_paths() {
		
		String ret_text = "From scored paths at PasaVertex node: " + this + "\n";
		
		for (ScoredPath sp : this.fromPaths) {
			ret_text += sp.describe_score_calculation();
		}
		
		return(ret_text);
	}



	public void rescore_from_paths() {
		for (ScoredPath sp : this.fromPaths) {
			sp.rescore();
		}
		
	}
	
	
}
