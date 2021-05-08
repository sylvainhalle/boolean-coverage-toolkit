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
package mcdclab.server;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.lif.labpal.CommandRunner;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.NumberHelper;
import ca.uqac.lif.labpal.Experiment.Status;
import ca.uqac.lif.labpal.server.TemplatePageCallback;
import ca.uqac.lif.mcdc.CategoryCoverage;
import ca.uqac.lif.mcdc.HologramDotRenderer;
import ca.uqac.lif.mcdc.HologramNode;
import mcdclab.experiment.HittingSetTestGenerationExperiment;

/**
 * Displays the holograms produced by the evaluation of a formula, for a set
 * of tree transformations. This page requires the presence of GraphViz on the
 * host machine; otherwise a message will be shown saying that the trees
 * cannot be represented graphically.
 */
public class HologramViewCallback extends TemplatePageCallback
{
	/**
	 * A flag that determines if GraphViz is present in the system.
	 */
	public static final transient boolean s_graphvizPresent = checkGraphViz();

	/**
	 * A regex pattern to remove the XML preamble in GraphViz-generated SVG
	 * documents
	 */
	protected static final Pattern s_xmlPattern = Pattern.compile("<svg .*</svg>", Pattern.DOTALL);

	/**
	 * Creates a new instance of the callback.
	 * @param lab The lab
	 */
	public HologramViewCallback(Laboratory lab)
	{
		super("/holograms", lab, null);
		m_filename = "resource/index.html";
	}

	/**
	 * Fills the content of the page.
	 * @param contents The string builder where the contents of the page will be printed
	 * @param params Any parameters fetched from the page's URL when it was called
	 * @param is_offline Set to <tt>true</tt> to generate the page for offline use
	 */
	protected void fill(StringBuilder contents, Map<String, String> params, boolean is_offline)
	{
		if (!s_graphvizPresent)
		{
			contents.append("<p>GraphViz is not present on your system. Trees cannot be shown.</p>");
		}
		String s_id = params.get("").trim();
		boolean with_cardinality = false;
		if (s_id.contains("/"))
		{
			with_cardinality = true;
			s_id = s_id.substring(0, s_id.indexOf("/"));
		}
		if (!NumberHelper.isNumeric(s_id))
		{
			contents.append("<p>Invalid experiment ID.</p>");
			return;
		}
		int exp_id = Integer.parseInt(s_id);
		Experiment e = m_lab.getExperiment(exp_id);
		if (!(e instanceof HittingSetTestGenerationExperiment))
		{
			contents.append("<p>This experiment does not use the technique of equivalence classes.</p>");
			return;
		}
		HittingSetTestGenerationExperiment hstge = (HittingSetTestGenerationExperiment) e;
		if (hstge.getStatus() != Status.DONE)
		{
			contents.append("<p>This experiment has not run yet. Please <a href=\"/experiment/").append(exp_id).append("\">run the experiment</a> first.</p>");
			return;
		}
		contents.append("<p>The original expression is:</p>\n\n");
		contents.append("<blockquote>").append(hstge.getFormula()).append("</blockquote>\n\n");
		contents.append("<p>Here is the set of all equivalence classes induced by the set of tree transformations of this coverage criterion.</p>\n");
		if (!with_cardinality)
		{
			contents.append("<p>You can also view this set as <a href=\"/holograms/").append(exp_id).append("/partition\">partitions with their cardinality</a> (needs recalculation).</p>");
			Set<HologramNode> trees = hstge.getTrees();
			for (HologramNode tree : trees)
			{
				renderTree(contents, tree, true);
			}
		}
		else
		{
			CategoryCoverage cov = new CategoryCoverage(hstge.getTruncations());
			List<Map<HologramNode,Integer>> distros = cov.getCategoryDistribution(hstge.getFormula());
			for (int i = 0; i < distros.size(); i++)
			{
				Map<HologramNode,Integer> distro = distros.get(i);
				contents.append("<h3>Transformation <math>").append(hstge.getTruncations()[i].toMathML()).append("</math></h3>\n\n");
				if (distro.isEmpty())
				{
					contents.append("<p>All valuations produce the empty tree.</p>\n");
				}
				else
				{
					contents.append("<table border=\"1\">\n<tr><th>Category</th><th>Size</th></tr>\n");
					for (Map.Entry<HologramNode,Integer> entry : distro.entrySet())
					{
						contents.append("<tr><td>");
						renderTree(contents, entry.getKey(), true);
						contents.append("</td><td>").append(entry.getValue()).append("</td></tr>\n");
					}
					contents.append("</table>\n");
				}
			}
		}
	}

	/**
	 * Prints an SVG element produced by calling GraphViz on a {@link HologramNode}.
	 * @param contents The contents where the SVG element should be printed 
	 * @param tree The tree to render as an SVG
	 * @param inline Set to <tt>true</tt> to produce only the SVG tag (for inline
	 * SVG inside another HTML document)
	 */
	protected void renderTree(StringBuilder contents, HologramNode tree, boolean inline)
	{
		if (tree == null)
		{
			// Empty tree, do nothing
			return;
		}
		// Render tree as a GraphViz document
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		HologramDotRenderer hdr = new HologramDotRenderer();
		hdr.render(tree, ps);
		// Create picture out of this document
		String svg_contents = runDot(baos.toString());
		if (inline)
		{
			Matcher mat = s_xmlPattern.matcher(svg_contents);
			if (mat.find())
			{
				svg_contents = mat.group(0);
			}
		}
		contents.append(svg_contents);
	}

	/**
	 * Runs GraphViz on a DOT file to render its contents in SVG format.
	 * @param contents The contents of the DOT file.
	 * @return A string consisting of an SVG picture.
	 */
	protected String runDot(String contents)
	{
		CommandRunner runner = new CommandRunner(new String[] {"dot", "-Tsvg"}, contents);
		runner.run();
		String out = new String(runner.getBytes());
		return out;
	}

	@Override
	public String fill(String s, Map<String, String> params, boolean is_offline)
	{
		StringBuilder contents = new StringBuilder();
		fill(contents, params, is_offline);
		s = s.replace("{%TITLE%}", "Graphical representation of equivalence classes");
		s = s.replace("{%LAB_DESCRIPTION%}", contents.toString());
		return s;
	}

	/**
	 * Checks if GraphViz is present in the system by attempting to run it.
	 * @return <tt>true</tt> if GraphViz is found, <tt>false</tt> otherwise
	 */
	protected static boolean checkGraphViz()
	{
		CommandRunner runner = new CommandRunner(new String[] {"dot", "-V"});
		runner.run();
		return runner.getErrorCode() == 0;
	}
}
