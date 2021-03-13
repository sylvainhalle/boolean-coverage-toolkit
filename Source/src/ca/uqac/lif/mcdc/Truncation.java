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
package ca.uqac.lif.mcdc;

/**
 * Transforms a {@link HologramNode} and its children into another one.
 */
public interface Truncation 
{
	/**
	 * Applies the transformation to a hologram node.
	 * @param n The root of the tree to transform
	 * @return The resulting tree, or <tt>null</tt> if the empty tree is
	 * produced. Note that in general, this tree should be a copy of the 
	 * original (i.e. no shared objects).
	 */
	/*@ null @*/ public HologramNode applyTo(/*@ not_null @*/ HologramNode n);
	
	/**
	 * Renders the object in <a href="http://www.w3.org/Math/">MathML</a>. Since
	 * transformations have a relatively complex notation, this offers a better
	 * way to display them in MathML-capable environments (such as a page from
	 * LabPal).
	 * @return A MathML string, without the surrounding <tt>&lt;math&gt;</tt>
	 * tags
	 */
	/*@ not_null @*/ public String toMathML();
}
