package nl.helixsoft.graph;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import nl.helixsoft.recordstream.DefaultRecord;
import nl.helixsoft.recordstream.DefaultRecordMetaData;
import nl.helixsoft.recordstream.MemoryRecordStream;
import nl.helixsoft.recordstream.Predicate;
import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.RecordMetaData;
import nl.helixsoft.recordstream.RecordStream;
import nl.helixsoft.util.AttributesTable;
import nl.helixsoft.util.StringUtils;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

public class GraphHelper 
{
	private DirectedGraph<String, Edge> net = new DirectedSparseGraph<String, Edge>();
	private AttributesTable<String> nodeAttr = new AttributesTable<String>();
	private AttributesTable<Edge> edgeAttr = new AttributesTable<Edge>();
	
	//TODO: new logic, somehow also put in Cytoscape plugin
	public void appendJoin(RecordStream rs, String sep)
	{
		for (Record r : rs)
		{
			String src = r.get ("src").toString();
			net.addVertex(src);
			nodeAttr.put (src, "label", src);
			
			for (int i = 0; i < r.getMetaData().getNumCols(); ++i)
			{
				String key = r.getMetaData().getColumnName(i);
				if (key.equals ("src")) continue;
				
				Object val = nodeAttr.get(src, key);
				if (val == null)
				{
					nodeAttr.put (src, key, r.get(i));
				}
				else
				{
					nodeAttr.put (src, key, val + sep + r.get(i));
				}
			}
		}		
	}
	
	//TODO: merge logic with MarrsProject
	public void appendNodeAttributes(RecordStream rs)
	{
		for (Record r : rs)
		{
			// src must be defined
			String src = r.get("src").toString();
			net.addVertex(src);
			nodeAttr.put (src, "label", src);
			
			for (int i = 0; i < r.getMetaData().getNumCols(); ++i)
			{
				String key = r.getMetaData().getColumnName(i);
				if (key.equals ("src")) continue;
				nodeAttr.put (src, key, r.get(i));
			}
		}
	}

	public void appendBackbone(RecordStream rs)
	{
		appendBackbone (rs, false);
	}
	
	//TODO: merge logic with MarrsProject
	public void appendBackbone(RecordStream rs, boolean mergeEdges)
	{
		for (Record r : rs)
		{
			// src must be defined
			String src = r.get("src").toString();
			// dest must be defined
			String dest = r.get("dest").toString();
			
			net.addVertex(src);
			
			//TODO: ID gets lost upon import from GML format... 
			nodeAttr.put (src, "label", src);
			
			//TODO: ID gets lost upon import from GML format... 
			net.addVertex(dest);
			nodeAttr.put (dest, "label", dest);
			
			Edge e = net.findEdge(src, dest);
			if (e == null || !mergeEdges)
			{
				e = new EdgeImpl(null);
				net.addEdge(e, src, dest);
			}
			
			for (int i = 0; i < r.getMetaData().getNumCols(); ++i)
			{
				String key = r.getMetaData().getColumnName(i);
				if (key.equals ("src") || key.equals ("dest")) continue;
				
				if (key.startsWith ("src_"))
				{
					nodeAttr.put (src, key.substring("src_".length()), r.get(i));
				}
				else if (key.startsWith ("dest_"))
				{
					nodeAttr.put (dest, key.substring("dest_".length()), r.get(i));
				}
				else
				{
					edgeAttr.put (e, key, r.get(i));
				}
			}
		}
	}

	public void toGml(OutputStream os)
	{
		GmlFormat.writeGml(os, net, nodeAttr, edgeAttr);
	}
	
	NodeSelection selectAllNodes()
	{
		return new NodeSelection(net.getVertices());
	}
	
	public class NodeSelection 
	{
		private List<String> selection;
		
		@Override public String toString()
		{
			return selection.toString();
		}
		
		private NodeSelection (Collection<String> value)
		{
			selection = new ArrayList<String>(value);
		}
		
		public List<String> attribute(String key)
		{
			List<String> result = new ArrayList<String>();
			for (String node : selection)
			{
				result.add (nodeAttr.get(node, key).toString());
			}
			return result;
		}
		
		public RecordStream getRecords(String[] fields)
		{
			List<Record> result = new ArrayList<Record>();
			RecordMetaData rmd = new DefaultRecordMetaData(fields);
			
			for (String node : selection)
			{
				String[] values = new String[fields.length];
				for (int i = 0; i < fields.length; ++i)
				{
					values[i] = StringUtils.safeToString(nodeAttr.get(node, fields[i]));
				}
				Record x = new DefaultRecord(rmd, values);
				result.add (x);
			}
			return new MemoryRecordStream(result);
		}
		
	}
	
	public class EdgeSelection 
	{
		private List<Edge> selection;

		@Override public String toString()
		{
			return selection.toString();
		}
		
		private EdgeSelection (Collection<Edge> value)
		{
			selection = new ArrayList<Edge>(value);
		}

		public List<String> attribute(String key)
		{
			List<String> result = new ArrayList<String>();
			for (Edge edge : selection)
			{
				result.add (StringUtils.safeToString(edgeAttr.get(edge, key)));
			}
			return result;
		}

		public RecordStream getRecords(List<String> srcFields, List<String> edgeFields, List<String> destFields)
		{
			List<Record> result = new ArrayList<Record>();
			
			List<String> allFields = new ArrayList<String>();
			allFields.addAll (srcFields);
			allFields.addAll (edgeFields);
			allFields.addAll (destFields);
			
			RecordMetaData rmd = new DefaultRecordMetaData(allFields);
			
			for (Edge e : selection)
			{
				String[] values = new String[allFields.size()];
				int col = 0;
				String src = net.getSource(e);
				String dest = net.getDest(e);
				for (int i = 0; i < srcFields.size(); ++i, ++col)
				{
					values[col] = StringUtils.safeToString(nodeAttr.get(src, srcFields.get(i)));
				}
				for (int i = 0; i < edgeFields.size(); ++i, ++col)
				{
					values[col] = StringUtils.safeToString(edgeAttr.get(e, edgeFields.get(i)));
				}
				for (int i = 0; i < destFields.size(); ++i, ++col)
				{
					values[col] = StringUtils.safeToString(nodeAttr.get(dest, destFields.get(i)));
				}				
				
				Record x = new DefaultRecord(rmd, values);
				result.add (x);
			}
			return new MemoryRecordStream(result);
		}
	}
	
	/**
	 * Select nodes, based on their attributes. Pass a Predicate function that examines the attributes for a given node.
	 */
	NodeSelection selectNodes(Predicate<Map<String, Object>> p)
	{
		List<String> selection = new ArrayList<String>(); 
		for (String v : net.getVertices())
		{
			if (p.accept(nodeAttr.getRow(v)))
			{
				selection.add (v);
			}
		}
		return new NodeSelection(selection);
	}

	NodeSelection allNodes()
	{
		return new NodeSelection(net.getVertices());
	}
	
	EdgeSelection selectEdges(Predicate<Map<String, Object>> p)
	{
		List<Edge> selection = new ArrayList<Edge>(); 
		for (Edge e : net.getEdges())
		{
			if (p.accept(edgeAttr.getRow(e)))
			{
				selection.add (e);
			}
		}
		return new EdgeSelection(selection);
	}

}
