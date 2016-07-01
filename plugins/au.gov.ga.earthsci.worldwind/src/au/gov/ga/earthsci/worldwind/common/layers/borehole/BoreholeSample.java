/*******************************************************************************
 * Copyright 2012 Geoscience Australia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package au.gov.ga.earthsci.worldwind.common.layers.borehole;

import java.awt.Color;

/**
 * Represents a sample in a borehole.
 * 
 * @author Michael de Hoog (michael.dehoog@ga.gov.au)
 */
public interface BoreholeSample
{
	/**
	 * @return {@link Borehole} that this sample is associated with
	 */
	Borehole getBorehole();

	/**
	 * @return Top measured depth of this sample (in positive meters)
	 */
	double getDepthFrom();

	/**
	 * @return Bottom measured depth of this sample (in positive meters)
	 */
	double getDepthTo();

	/**
	 * @return Color used to display this sample
	 */
	Color getColor();

	/**
	 * @return The display text associated with this sample; eg to show as a
	 *         tooltip
	 */
	String getText();

	/**
	 * @return A URL string to a website that describes this sample (null if
	 *         none)
	 */
	String getLink();
}
