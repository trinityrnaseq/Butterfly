import java.util.*;


public class PasaVertex {

	final PairPath pp;
	float readSupport = 0;
	
	int vertex_score = 0;
	
	public static int max_top_paths_to_store = 1;
	
	List<ScoredPath> fromPaths;
	List<ScoredPath> toPaths;
	
	List<PasaVertex> contained_PasaVertices;
	
	HashSet<PasaVertex> overlapping_compatible_PasaVertices;
	
	boolean used = false;
	
	private Comparator<ScoredPath> sp_comparator = new Comparator<ScoredPath>() {
		
		public int compare (ScoredPath spA, ScoredPath spB) {
			
			// want it to sort descendingly on score
			
			if (spA.score < spB.score) {
				return(1);
			}
			else if (spA.score > spB.score) {
				return(-1);
			}
			else {
				return(0);
			}
		}
		
	};
	
	
	public PasaVertex (final PairPath p, int readSupport) {
		
		this.pp = p;
		this.readSupport = readSupport;	
	
		this.contained_PasaVertices = new ArrayList<PasaVertex>();
		this.overlapping_compatible_PasaVertices = new HashSet<PasaVertex>();
	}
	
	
	public void init_PasaVertex_to_and_from_paths() {
		List<PasaVertex> path = new ArrayList<PasaVertex>();
		path.add(this);
		
		fromPaths = new ArrayList<ScoredPath>();
		toPaths = new ArrayList<ScoredPath>();
		
		ScoredPath sp = new ScoredPath(path, this.readSupport + this.containment_support());
		
		this.fromPaths.add(sp);
		this.toPaths.add(sp);
		
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
			this.readSupport /= 1e6;
		}
		for (PasaVertex pv : this.contained_PasaVertices) {
			pv.set_used();
		}
	}
	
	
	public boolean is_used() {
			return(used);
	}
	
	public final List<ScoredPath> get_toPaths () {
		return(toPaths);
	}
	
	public final List<ScoredPath> get_fromPaths() {
		return(fromPaths);
	}
	
	
	public void push_toPaths(ScoredPath sp) {
		
		push_path_list(this.toPaths, sp);
		
		
	}
	
	public void push_fromPaths(ScoredPath sp) {
		
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
	
	
	public final ScoredPath get_highest_scoring_toPath () {
		
		ScoredPath best = null;
		for (ScoredPath sp : this.get_toPaths()) {
			if (best == null || best.score < sp.score)
				best = sp;
		}
		return(best);
	}
	
	
public final List<ScoredPath> get_all_highest_scoring_toPath () {
		
		List<ScoredPath> best_paths = new ArrayList<ScoredPath>();
		
		for (ScoredPath sp : this.get_toPaths()) {
			if (best_paths.isEmpty()) {
				best_paths.add(sp);
			}
			else if (sp.score == best_paths.get(0).score) {
				best_paths.add(sp);
			}
			else if (sp.score > best_paths.get(0).score) {
				best_paths.clear();
				best_paths.add(sp);
			}
			
		}
		return(best_paths);
	}
	
	
	
	public final ScoredPath get_highest_scoring_fromPath () {
		ScoredPath best = null;
		for (ScoredPath sp : this.get_fromPaths()) {
			if (best == null || best.score < sp.score)
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
			else if (sp.score == best_paths.get(0).score) {
				best_paths.add(sp);
			}
			else if (sp.score > best_paths.get(0).score) {
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
	
	
}
