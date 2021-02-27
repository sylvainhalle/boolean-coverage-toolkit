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
package mcdclab;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.mcdc.Operator;
import edu.uta.cse.fireeye.console.ActsConsoleManager;
import edu.uta.cse.fireeye.service.engine.FireEye;
import edu.uta.cse.fireeye.service.exception.OperationServiceException;
import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.SUT;
import edu.uta.cse.fireeye.common.TestGenProfile;
import edu.uta.cse.fireeye.common.TestSet;

public class ActsTestGenerationExperiment extends TestGenerationExperiment
{
	/**
	 * The name of this tool
	 */
	public static final transient String NAME = "ACTS";
	
	/**
	 * The coverage strength
	 */
	protected int m_t;
	
	public ActsTestGenerationExperiment(Operator formula, String formula_name, int t) 
	{
		super(formula, formula_name);
		setInput(METHOD, NAME);
		m_t = t;
	}

	@Override
	public void execute() throws ExperimentException, InterruptedException 
	{
		long start = System.currentTimeMillis();
		SUT problem = buildSUT();
		TestGenProfile tgp = TestGenProfile.instance();
		TestSet ts = new TestSet();
		try
		{
			FireEye.generateTestSet(ts, problem);
		}
		catch (OperationServiceException e) 
		{
			throw new ExperimentException(e);
		}
		long end = System.currentTimeMillis();
		write(TIME, end - start);
		write(SIZE, ts.getNumOfTests());
	}
	
	protected SUT buildSUT()
	{
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		Set<String> vars = m_formula.getVariables();
		for (String v : vars)
		{
			Parameter p = new Parameter(v);
			p.addValue("0");
			p.addValue("1");
			params.add(p);
		}
		SUT sut = new SUT(params);
		return sut;
	}

}
