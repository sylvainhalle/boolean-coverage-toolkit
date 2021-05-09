/*
    Graph-based MC/DC testing
    Copyright (C) 2021 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mcdclab.plot;

import java.util.Scanner;
import java.util.Vector;

import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot;
import ca.uqac.lif.mtnp.plot.gnuplot.ClusteredHistogram;
import ca.uqac.lif.mtnp.plot.gnuplot.GnuPlot;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TableTransformation;
import ca.uqac.lif.mtnp.table.TempTable;

/**
 * A "hack" over {@link ClusteredHistogram} so that the x-axis does not
 * display a caption at every tick.
 */
public class SpacedHistogram extends GnuPlot implements TwoDimensionalPlot
{
	/**
	 * The caption of the X axis
	 */
	protected String m_captionX = "";

	/**
	 * The caption of the Y axis
	 */
	protected String m_captionY = "";

	/**
	 * Whether the histogram is of type "row stacked".
	 * (see {@link #rowStacked()})
	 */
	protected boolean m_rowStacked = false;

	/**
	 * The width of the box in the histogram. A value of -1 means the
	 * default setting will be used.
	 */
	protected float m_boxWidth = 0.75f;

	/**
	 * The interval at which labels are displayed on the horizontal axis.
	 */
	protected int m_spacing = 2;

	/**
	 * Creates a new bar plot.
	 * @param spacing The interval at which labels are displayed on the
	 * horizontal axis
	 */
	public SpacedHistogram(int spacing)
	{
		super();
		m_spacing = spacing;
	}

	/**
	 * Creates a new bar plot from a table
	 * @param spacing The interval at which labels are displayed on the
	 * horizontal axis
	 * @param t The table
	 */
	public SpacedHistogram(int spacing, Table t)
	{
		super(t);
		m_spacing = spacing;
	}

	/**
	 * Creates a new bar plot from a table
	 * @param spacing The interval at which labels are displayed on the
	 * horizontal axis
	 * @param table
	 * @param transformation
	 */
	public SpacedHistogram(int spacing, Table table, TableTransformation transformation)
	{
		super(table, transformation);
		m_spacing = spacing;
	}

	/**
	 * Sets whether the histogram is of type "row stacked".
	 * Using the example given above, the rowstacked setting will rather
	 * produce this plot:
	 * <pre>
	 * |                     # video
	 * |                     $ audio
	 * |    @                @ text
	 * |    @         @
	 * |    $         @ 
	 * |    $         $ 
	 * |    #         # 
	 * +----+---------+-----&gt;
	 *   Firefox     IE
	 * </pre> 
	 * @return This plot
	 */
	public SpacedHistogram rowStacked()
	{
		m_rowStacked = true;
		return this;
	}

	/**
	 * Sets the box width of the histogram. This is equivalent to the
	 * <tt>boxwidth</tt> setting of Gnuplot.
	 * @param w The width (generally a value between 0 and 1)
	 * @return This plot
	 */
	public SpacedHistogram boxWidth(float w)
	{
		m_boxWidth = w;
		return this;
	}

	@Override
	public String toGnuplot(ImageType term, String lab_title, boolean with_caption)
	{
		TempTable tab = processTable(m_table.getDataTable());
		String[] columns = tab.getColumnNames();
		Vector<String> series = new Vector<String>();
		for (int i = 1; i < columns.length; i++)
		{
			series.add(columns[i]);
		}
		String csv_values_before = tab.toCsv(s_datafileSeparator, s_datafileMissing);
		// Add a last coulumn to count
		String csv_values = "";
		{
			Scanner scanner = new Scanner(csv_values_before);
			int i = 0;
			while (scanner.hasNextLine())
			{
				String line = scanner.nextLine();
				if (i == 0)
				{
					csv_values += line + ",Count\n";
				}
				else
				{
					csv_values += line + "," + i + "\n";
				}
				i++;
			}
			scanner.close();
		}
		// Build GP string from table
		StringBuilder out = new StringBuilder();
		out.append(getHeader(term, lab_title, with_caption));
		out.append("set xtics rotate out\n");
		out.append("set style data histogram\n");
		out.append("set xlabel \"").append(m_captionX).append("\"\n");
		out.append("set ylabel \"").append(m_captionY).append("\"\n");
		out.append("everyNth(countColumn,labelColumnNum,N) =((int(column(countColumn)) % N == 0) ? stringcolumn(labelColumnNum) : \"\");\n");
		if (m_rowStacked)
		{
			out.append("set style histogram rowstacked\n");
		}
		else
		{
			out.append("set style histogram clustered gap 1\n");
		}
		if (m_boxWidth > 0)
		{
			out.append("set boxwidth ").append(m_boxWidth).append("\n");
		}
		out.append("set auto x\n");
		out.append("set yrange [0:*]\n");
		out.append("set style fill border rgb \"black\"\n");
		out.append("plot");
		for (int i = 0; i < series.size(); i++)
		{
			if (i > 0)
			{
				out.append(", ");
			}
			String s_name = series.get(i);
			out.append(" \"-\" using ").append(i + 2).append(":xtic(everyNth(" + (series.size() + 2) + ",1," + m_spacing + ")) title \"").append(s_name).append("\" ").append(getFillColor(i));
		}
		out.append("\n");
		// In Gnuplot, if we use the special "-" filename, we must repeat
		// the data as many times as we use it in the plot command; it does not remember it
		for (int i = 0; i < series.size(); i++)
		{
			out.append(csv_values).append("end\n");
		}
		return out.toString();
	}

	@Override
	public TwoDimensionalPlot setCaption(Axis axis, String caption)
	{
		if (axis == Axis.X)
		{
			m_captionX = caption;
		}
		else
		{
			m_captionY = caption;
		}
		return this;
	}

	@Override
	public SpacedHistogram setLogscale(Axis axis)
	{
		// Does not make much sense for a histogram: do nothing
		return this;
	}

}