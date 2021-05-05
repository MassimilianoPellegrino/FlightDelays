package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {

	private Graph<Airport, DefaultWeightedEdge> grafo;
	private ExtFlightDelaysDAO dao;
	private Map<Integer, Airport> idMap;
	private List<Rotta> rotte;
	private Map<Airport, Airport> visita;
	
	public Model() {
		dao = new ExtFlightDelaysDAO();
		idMap = new HashMap<>();
		dao.loadAllAirports(idMap);
		rotte = dao.getRotte(idMap);
	}
	
	public void creaGrafo(int x) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, dao.getVertici(idMap, x));
		
		for(Rotta r: rotte) {
			if(grafo.containsVertex(r.getA1()) && grafo.containsVertex(r.getA2())) {
				DefaultWeightedEdge e = grafo.getEdge(r.getA1(), r.getA2());
				if(e==null) {
					Graphs.addEdgeWithVertices(grafo, r.getA1()	, r.getA2(), r.getN());
				}else {
					double pesoVecchio = this.grafo.getEdgeWeight(e);
					double pesoNuovo = pesoVecchio+r.getN();
					this.grafo.setEdgeWeight(e, pesoNuovo);
				}
			}
		}	
		
		System.out.println("Grafo creato\n"
				+ "#Vertici: "+grafo.vertexSet().size()+"\n"
						+ "#Archi: "+grafo.edgeSet().size());
	}

	public Set<Airport> getVertici() {
		// TODO Auto-generated method stub
		return this.grafo.vertexSet();
	}
	
	public List<Airport> trovaPercorso(Airport a1, Airport a2){
		List<Airport> percorso = new LinkedList<>();
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(grafo, a1);
		visita = new HashMap<>();
		visita.put(a1, null);
		
		it.addTraversalListener(new TraversalListener<Airport, DefaultWeightedEdge>() {
			
			@Override
			public void vertexTraversed(VertexTraversalEvent<Airport> e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void vertexFinished(VertexTraversalEvent<Airport> e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> e) {
				// TODO Auto-generated method stub
				Airport ap1 = grafo.getEdgeSource(e.getEdge());
				Airport ap2 = grafo.getEdgeTarget(e.getEdge());
				if(visita.containsKey(ap1) && !visita.containsKey(ap2)) {
					visita.put(ap2, ap1);
				}else if(visita.containsKey(ap2) && !visita.containsKey(ap1)) {
					visita.put(ap1, ap2);
				}
				
			}
			
			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		while(it.hasNext()) {
			it.next();
		}
		
		if(!visita.containsKey(a1) || !visita.containsKey(a2))
			return null;
		
		percorso.add(a2);
		Airport step = a2;
		
		while(visita.get(step)!=null) {
			step = visita.get(step);
			percorso.add(0, step);
		}
		
		
		return percorso;
	}



}
