package it.polito.tdp.yelp.model;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	private Graph<Review,DefaultWeightedEdge> grafo;
	private YelpDao dao=new YelpDao();
	private int narchi;
	private int nvertici;
	private int max;
	private int giornimax;
	private String stringmax;
	private List<Review> best;
	
	public List<String>	getcitta(){
		return dao.getAllcity();
	}
	
	public List<Business>getbusinesscitta(String citta){
		return dao.getAllcitybusines(citta);
	}
	
	public int getNarchi() {
		return narchi;
	}

	
	public int getNvertici() {
		return nvertici;
	}

	

	public void creagrafo(String nome,String c) {
		grafo=new SimpleDirectedWeightedGraph<Review,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		List<Review> l=new ArrayList<Review>(dao.getAllReviewsbyloc(dao.getidbusinness(nome,c)) );
		Graphs.addAllVertices(grafo,dao.getAllReviewsbyloc(dao.getidbusinness(nome,c)) );
		for(Review r1: l) {
			for(Review r2: l) {
				if(!r1.equals(r2) && r1.getDate().isAfter(r2.getDate()) && ChronoUnit.DAYS.between(r2.getDate(), r1.getDate())>0 ) {
					grafo.addEdge(r1, r2);
					grafo.setEdgeWeight(grafo.getEdge(r1, r2), ChronoUnit.DAYS.between(r2.getDate(), r1.getDate()));
				}
			}
		}
		this.max=0;
		this.stringmax="";
		for(Review r1: l) {
			if(grafo.outgoingEdgesOf(r1).size()>max) {
				max=grafo.outgoingEdgesOf(r1).size();
				this.stringmax=r1.getReviewId();
			}else if(grafo.outgoingEdgesOf(r1).size()==max) {
				this.stringmax=this.stringmax+" "+r1.getReviewId();
			}
		}
		this.narchi=grafo.edgeSet().size();
		this.nvertici=grafo.vertexSet().size();
	}
	
	
	
	public int getMax() {
		return max;
	}



	public String getStringmax() {
		return stringmax;
	}



	public String getid (String name,String c) {
		return dao.getidbusinness(name,c);
	}
	
	public List<Review> getreviewsByloc(String id) {
		return dao.getAllReviewsbyloc(id);
	}
	
	
	public List<Review> getpercorsomax(){
		best=new LinkedList<>();
		
		List<Review> parziale =new LinkedList<>();
		Review rr;
		Review bestr=new Review("", "stringmax", "stringmax", 0, null, 0, 0, 0,"");
		for(Review r: grafo.vertexSet()) {
			bestr=r;
			break;
		}
		for(Review r: grafo.vertexSet()) {
			if(r.getDate().isBefore(bestr.getDate())) {
				bestr=r;
			}
		}
		parziale.add(bestr);
		cerca(parziale);
		return this.best;
	}
	
	public int getGiornimax() {
		return giornimax;
	}

	

	private void cerca(List<Review> parziale) {
		// condizione terminazione
		
			if(parziale.size()>best.size()) {
				best=new ArrayList<>(parziale);
				this.giornimax=(int) ChronoUnit.DAYS.between(parziale.get(0).getDate(), parziale.get(parziale.size()-1).getDate());
			}
			
			
		// scorro i vicini dell'ultimo inserito ed esploro
		 for(Review v :Graphs.neighborListOf(grafo, parziale.get(parziale.size()-1))) {
			 if(!parziale.contains(v) && v.getStars()>=parziale.get(parziale.size()-1).getStars()) {
				 // evito cicli
				 parziale.add(v);
				 
				 cerca(parziale);
				 parziale.remove(parziale.size()-1);
				 }
		 }
		 
		
	}
}
