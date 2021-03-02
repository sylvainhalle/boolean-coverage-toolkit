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

import edu.uta.cse.fireeye.common.Parameter;
import edu.uta.cse.fireeye.common.Relation;
import edu.uta.cse.fireeye.common.SUT;
import edu.uta.cse.fireeye.common.TestGenProfile;
import edu.uta.cse.fireeye.common.TestSet;
import edu.uta.cse.fireeye.service.engine.BaseChoice;
import edu.uta.cse.fireeye.service.engine.BinaryBuilder;
import edu.uta.cse.fireeye.service.engine.Builder;
import edu.uta.cse.fireeye.service.engine.Bush;
import edu.uta.cse.fireeye.service.engine.CoverageChecker;
import edu.uta.cse.fireeye.service.engine.ForbesEngine;
import edu.uta.cse.fireeye.service.engine.Paintball;
import edu.uta.cse.fireeye.service.exception.OperationServiceException;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A "silent" version of the {@link edu.uta.cse.fireeye.service.engine.FireEye
 * FireEye} class contained in the ACTS test generation tool. This class has
 * been obtained creating a new class out of the decompiled source code of the
 * original <tt>FireEye</tt>. The modifications are as follows:
 * <ul>
 * <li>Instructions that print to the console have been redirected to a
 * configurable {@link PrintStream}</li>
 * </ul>
 */
public class SilentFireEye
{
	public static final String DEFAULT_OUTPUT_FILENAME = "output.txt";
	public static boolean verbose = false;

	public static TestSet generateTestSet(TestSet ts, SUT sut, PrintStream ps) throws OperationServiceException
	{
		TestSet rval = null;
		TestGenProfile profile = TestGenProfile.instance();

		if (verbose)
		{
			ps.println("Algorithm: " + profile.getAlgorithm());
			ps.println("DOI: " + profile.getDOI());
			ps.println("Mode: " + profile.getMode());
			ps.println("VUnit: " + profile.getVUnit());
			ps.println("Hunit: " + profile.getHUnit());
			ps.println("ProgressOn: " + profile.isProgressOn());
			ps.println("FastMode: " + profile.isFastMode());
		}

		ArrayList<Relation> relationsCopy = sut.getRelationManager().getRelations();
		ArrayList<Relation> relationsInUse = new ArrayList<Relation>();

		if (profile.getDOI() > 0)
		{
			if (profile.getDOI() > sut.getParams().size())
			{
				// Strength greater than number of params; nothing to do
				return null;
			}
			Relation e = new Relation(profile.getDOI(), sut.getParams());
			relationsInUse.add(e);
		}
		else
		{
			relationsInUse = new ArrayList<Relation>(relationsCopy);
			HashSet<Parameter> allParam = new HashSet<Parameter>();
			for (Relation r : relationsInUse)
			{
				allParam.addAll(r.getParams());
			}
		}
		if (relationsInUse.size() == 0)
		{
			Relation e = new Relation(2, sut.getParams());
			relationsInUse.add(e);
		}
		sut.getRelationManager().setRelations(relationsInUse);
		long start = System.currentTimeMillis();
		boolean isIgnoreConstraints = TestGenProfile.instance().isIgnoreConstraints();
		if (profile.getAlgorithm() == TestGenProfile.Algorithm.ipof)
		{
			Builder builder = new Builder(sut);
			rval = builder.generate(TestGenProfile.Algorithm.ipof);
		}
		else if (profile.getAlgorithm() == TestGenProfile.Algorithm.ipog_r)
		{
			Builder builder = new Builder(sut);
			rval = builder.generate(TestGenProfile.Algorithm.ipog_r);
		}
		else if (profile.getAlgorithm() == TestGenProfile.Algorithm.ipof2)
		{
			ForbesEngine forbes = new ForbesEngine(sut, 8);
			forbes.build();
			rval = forbes.getTestSet();
		}
		else if (profile.getAlgorithm() == TestGenProfile.Algorithm.paintball)
		{
			Paintball pb = new Paintball(sut, profile.getDOI(), profile.getMaxTries());
			rval = pb.build();
		}
		else if (profile.getAlgorithm() == TestGenProfile.Algorithm.bush)
		{
			Bush bush = new Bush(sut);
			bush.build();
			rval = new TestSet(sut.getParams(), sut.getOutputParameters());
			rval.addMatrix(bush.getMatrix());
		}
		else if (profile.getAlgorithm() == TestGenProfile.Algorithm.ipog_d)
		{
			BinaryBuilder builder = new BinaryBuilder(sut.getParams(), sut.getOutputParameters());
			rval = builder.getTestSet(sut.getOutputParameters());
		}
		else if (profile.getAlgorithm() == TestGenProfile.Algorithm.basechoice)
		{
			BaseChoice bc = new BaseChoice(sut);
			rval = bc.build();
		}
		else if (profile.getAlgorithm() == TestGenProfile.Algorithm.nil)
		{
			rval = sut.getExistingTestSet();
			if (rval == null)
			{
				rval = new TestSet();
			}
		}
		else if (profile.getAlgorithm() == TestGenProfile.Algorithm.ipog)
		{
			Builder builder = new Builder(sut);
			rval = builder.generate(TestGenProfile.Algorithm.ipog);
		}
		else
		{
			// Unknown algo
			//System.err.println("Unknown algorithm: " + profile.getAlgorithm());
		}
		TestGenProfile.instance().setIgnoreConstraints(isIgnoreConstraints);
		if (rval == null)
		{
			return null;
		}
		float duration = (System.currentTimeMillis() - start) / 1000.0F;
		sut.getRelationManager().setRelations(relationsCopy);
		if (verbose)
		{
			ps.println("Number of Tests\t: " + rval.getNumOfTests());
			ps.println("Time (seconds)\t: " + duration + " ");
		}
		rval.setGenerationTime(duration);

		if (profile.checkCoverage())
		{
			ps.println("\nCoverage Check:");

			CoverageChecker checker = new CoverageChecker(rval, sut, profile.getDOI());
			if (checker.check())
			{
				ps.println("Coverage has been verified!");
			}
			else
			{
				ps.println("Failed to verify coverage!");
			}
		}
		ps.println();
		return rval;
	}
}

/*
 * Location:
 * /home/sylvain/Workspaces/mcdc-lab/Source/dep/acts_3.1.jar!/edu/uta/cse/
 * fireeye/service/engine/FireEye.class Java compiler version: 6 (50.0) JD-Core
 * Version: 1.0.7
 */
