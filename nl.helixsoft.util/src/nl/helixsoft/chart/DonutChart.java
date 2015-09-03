package nl.helixsoft.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nl.helixsoft.recordstream.Record;
import nl.helixsoft.recordstream.ReduceFunctions;
import nl.helixsoft.stats.DataFrame;
import nl.helixsoft.stats.Factor;

public class DonutChart
{
	private final DataFrame dfLong;
	private final int outerColIdx;
	private final int innerColIdx;
	private final int valueColIdx;
	
	DonutChart (DataFrame dfLong, String outerColumn, String innerColumn, String valueColumn)
	{
		this.dfLong = dfLong;
		this.outerColIdx = dfLong.getColumnIndex(outerColumn);
		this.innerColIdx = dfLong.getColumnIndex(innerColumn);
		this.valueColIdx = dfLong.getColumnIndex(valueColumn);
	}
	
	private void drawLabel(Graphics2D g2d, String lab, double lx, double ly)
	{
		String label = lab == null ? "null" : lab;
		g2d.setColor (Color.BLACK);
		
		Rectangle2D tBounds = g2d.getFontMetrics().getStringBounds(label, g2d);
		
		double tx = lx - tBounds.getCenterX();
		double ty = ly - tBounds.getCenterY();
	
		g2d.drawString(label, (int)tx, (int)ty);
	}
	
	private Path2D ringSegment(double cx, double cy, double outerRadius, double innerRadius, double startFraction, double extentFraction)
	{
		assert outerRadius > innerRadius;
		
		Path2D p = new Path2D.Double();
		
		double top = cy - outerRadius;
		double left = cx - outerRadius;
		double width = 2 * outerRadius;
		double height = 2 * outerRadius;
		
		Arc2D arc = new Arc2D.Double (
				left, top, width, height,
				startFraction * 360,
				extentFraction * 360,

				Arc2D.OPEN
				);

		p.append (arc.getPathIterator(null), false);

		top = cy - innerRadius;
		left = cx - innerRadius;
		width = 2 * innerRadius;
		height = 2 * innerRadius;

		arc = new Arc2D.Double (
				left, top, width, height,
				(startFraction + extentFraction) * 360,
				-extentFraction * 360,
				Arc2D.OPEN
				);

		p.append (arc.getPathIterator(null), true);

		p.closePath();
		return p;
	}

	public void draw(Graphics2D g2d, Rectangle2D area)
	{		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// calculate total triples
		double sum = dfLong.getColumn(Long.class, valueColIdx).apply (0L, ReduceFunctions.LONG_SUM);
		
		// draw arcs
		List<Color> cycleColors = Arrays.asList (new Color[] { 
			new Color(0x8080FF), Color.YELLOW, new Color(0x80FFFF), Color.GREEN, Color.CYAN, new Color(0x80FF80), Color.MAGENTA, new Color (0xFF2060), Color.LIGHT_GRAY, new Color (0xFFFF80), Color.PINK, 
			Color.RED, new Color(0xFF80FF), Color.ORANGE, Color.DARK_GRAY, new Color(0x8080FF), new Color(0xFF8080) } );
		
		int outerColor = 0;
		int innerColor = 0;
		
		double outerCumulative = 0;
		double innerCumulative = 0;

		Factor innerFactor = dfLong.getColumnAsFactor(String.class, innerColIdx);
		List<String> sortedCategories = new ArrayList<String>(innerFactor.getFactors());
		Collections.sort (sortedCategories);
		
		double width = area.getWidth();
		double height = area.getHeight();
		
		for (String category : sortedCategories)
		{
			DataFrame slice = innerFactor.getRows(category).sort(outerColIdx);
			double innerTriples = slice.getColumn(Long.class, valueColIdx).apply(0L, ReduceFunctions.LONG_SUM);
			
			g2d.setColor(cycleColors.get ((innerColor++) % cycleColors.size()));
			double cx = area.getCenterX();
			double cy = area.getCenterY();
			
			Path2D p = ringSegment (cx, cy, width / 2 - 120, width / 2 - 220, innerCumulative / sum, innerTriples / sum);
			g2d.fill(p);
			g2d.setColor(Color.BLACK);
			g2d.draw(p);
					
			drawLabel (g2d, category, p.getBounds().getCenterX(), p.getBounds().getCenterY());
			
			innerCumulative += innerTriples;
						
			for (Record row : slice.asRecordIterable())
			{
				double outerTriples = (Long)row.get (valueColIdx);
				String shortName = (String)row.get(outerColIdx);
				
				g2d.setColor(cycleColors.get ((outerColor++) % cycleColors.size()));
				
				Path2D outerP = ringSegment (width / 2, height / 2, width / 2, width / 2 - 100, outerCumulative / sum, outerTriples / sum);
				g2d.fill(outerP);
				
				g2d.setColor(Color.BLACK);
				g2d.draw(outerP);
				
				double lx = cx + ((width / 2) - 50) * Math.cos ((outerCumulative + outerTriples / 2) * 2 * Math.PI / sum );
				double ly = cy - ((height / 2) - 50) * Math.sin ((outerCumulative + outerTriples / 2) * 2 * Math.PI / sum );
	
				Rectangle2D tBounds0 = g2d.getFontMetrics().getStringBounds(shortName, g2d);
				Rectangle2D tBounds = new Rectangle2D.Double(lx - tBounds0.getWidth(), ly - tBounds0.getHeight(), tBounds0.getWidth(), tBounds0.getHeight());
				
				if (outerP.contains(tBounds))
				{
					drawLabel (g2d, shortName, lx, ly);
				}
				
				outerCumulative += outerTriples;
			}
	
		}

	}
}
