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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.server.TemplatePageCallback;
import ca.uqac.lif.mcdc.Operator;
import mcdclab.NaturalOrderComparator;
import mcdclab.OperatorProvider;

public class AllFormulasCallback extends TemplatePageCallback
{
	/**
	 * The provider containing all the experiments in the lab.
	 */
	protected transient OperatorProvider m_provider;
	
	/**
	 * Creates a new instance of the callback.
	 * @param lab The lab
	 * @param provider The provider containing all the experiments in the lab
	 */
	public AllFormulasCallback(Laboratory lab, OperatorProvider provider)
	{
		super("/formulas", lab, null);
		m_filename = "resource/index.html";
		m_provider = provider;
	}
	
	@Override
	public String fill(String s, Map<String, String> params, boolean is_offline)
	{ 
		StringBuilder contents = new StringBuilder();
		contents.append("<p>This page gives the list of all the Boolean expressions that are processed in the experiments of this lab.</p>\n");
		String[] names = m_provider.getNames();
		List<String> tcas_names = new ArrayList<String>();
		List<String> faa_names = new ArrayList<String>();
		List<String> random_names = new ArrayList<String>();
		for (String name : names)
		{
			if (name.startsWith("TCAS"))
			{
				tcas_names.add(name);
			}
			if (name.startsWith("FAA"))
			{
				faa_names.add(name);
			}
			if (name.startsWith("Random"))
			{
				random_names.add(name);
			}
		}
		contents.append("<h3>TCAS formulas</h3>\n");
		contents.append("<p>TCAS Boolean Predicates in Minimal DNF. The formulas are as taken from\n");
		contents.append("Appendix B of Kaminski and Amman, ICST 2009.</p>\n");
		printTable(tcas_names, contents);
		contents.append("<h3>FAA formulas</h3>\n");
		contents.append("<p>The formulas are a sample taken from Appendix C of:\n" + 
				" <blockquote>\n" + 
				" J.J. Chilenski. <i>An Investigation of Three Forms of the Modified Condition\n" + 
				" Decision Coverage (MCDC) Criterion</i>. Technical Report DOT/FAA/AR-01/18,\n" + 
				" April 2001.\n" + 
				" </blockquote>\n" + 
				" In each formula, the unique conditions appearing in the original expressions\n" + 
				" have been replaced by atomic variables starting from letter <i>a</i>. </p>\n");
		printTable(faa_names, contents);
		contents.append("<h3>Random formulas</h3>\n");
		contents.append("<p>Randomly generated formulas in DNF. These formulas are generated using a\n");
		contents.append("PRNG; every run of the lab with the same starting seed will result in the same\n");
		contents.append("formulas being produced.</p>\n");
		printTable(random_names, contents);
		s = s.replace("{%TITLE%}", "Formulas included in the benchmark");
		s = s.replace("{%LAB_DESCRIPTION%}", contents.toString());
		return s;
	}
	
	protected void printTable(List<String> names, StringBuilder contents)
	{
		Collections.sort(names, NaturalOrderComparator.NUMERICAL_ORDER);
		contents.append("<table border=\"1\">\n");
		contents.append("<tr><th>Name</th><th>Number of variables</th><th>Nesting depth</th><th>Size</th><th>Expression</th></tr>\n");
		for (String name : names)
		{
			Operator op = m_provider.getFormula(name);
			contents.append("<tr><td>").append(name).append("</td><td>").append(op.getVariables().size()).append("</td><td>");
			contents.append(op.getDepth()).append("</td><td>").append(op.getSize()).append("</td><td>");
			contents.append(op.toString()).append("</td></tr>\n");
		}
		contents.append("</table>\n");
	}

}
