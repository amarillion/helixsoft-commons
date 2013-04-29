package nl.helixsoft.graph;

import java.io.OutputStream;
import java.util.Map;

import nl.helixsoft.util.AttributesTable;

import org.apache.commons.collections15.BidiMap;

import edu.uci.ics.jung.algorithms.util.Indexer;
import edu.uci.ics.jung.graph.DirectedGraph;

public class GmlFormat 
{
	public static <V, E> void writeGml(OutputStream fos, DirectedGraph<V, E> graph, AttributesTable<V> nodeAttr, AttributesTable<E> edgeAttr)
	{
		// GML uses integers as node ids, so start by creating integer inex.
		BidiMap<V, Integer> idx = Indexer.create(graph.getVertices());
		
		GmlEmitter gml = new GmlEmitter(fos);
		gml.startList("graph");

		for (V v : graph.getVertices())
		{
			gml.startList("node");
			gml.intLiteral("id", idx.get(v));
			for (Map.Entry<String, Object> entry : nodeAttr.getAttributes(v))
			{
				Object val = entry.getValue();
				if (val instanceof Number)
				{
					gml.numberLiteral(entry.getKey(), (Number)val);
				}
				else
				{
					gml.stringLiteral(entry.getKey(), "" + val);
				}
			}
			gml.closeList();
		}

		for (E e : graph.getEdges())
		{
			V s = graph.getSource(e);
			V t = graph.getDest(e);
			gml.startList("edge");
			gml.intLiteral("source", idx.get(s));
			gml.intLiteral("target", idx.get(t));
			for (Map.Entry<String, Object> entry : edgeAttr.getAttributes(e))
			{
				Object val = entry.getValue();
				if (val instanceof Number)
				{
					gml.numberLiteral(entry.getKey(), (Number)val);
				}
				else
				{
					gml.stringLiteral(entry.getKey(), "" + val);
				}
			}
			gml.closeList();
		}

		gml.closeList();
	};
}
