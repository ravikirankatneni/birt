/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.engine.layout.pdf;

import java.util.Iterator;
import java.util.List;

import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.content.IContent;
import org.eclipse.birt.report.engine.content.IHyperlinkAction;
import org.eclipse.birt.report.engine.layout.area.IArea;
import org.eclipse.birt.report.engine.layout.area.IImageArea;
import org.eclipse.birt.report.engine.layout.area.impl.ContainerArea;
import org.eclipse.birt.report.engine.layout.area.impl.PageArea;

public class PDFImageLMTest extends PDFLayoutTest
{

	/**
	 * Test case for bugzilla bug <a
	 * href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=168899">168899</a> :
	 * Report does not output to PDF with a chart inside of a grid.
	 * 
	 * @throws EngineException
	 */
	public void testOversizedImageInGrid( ) throws EngineException
	{
		String designFile = "org/eclipse/birt/report/engine/layout/pdf/168899.xml";
		IReportRunnable report = openReportDesign( designFile );
		List pageAreas = getPageAreas( report );

		assertEquals( 1, pageAreas.size( ) );
		PageArea pageArea = (PageArea) pageAreas.get( 0 );
		Iterator logicContainers = pageArea.getBody( ).getChildren( );
		assertTrue( logicContainers.hasNext( ) );
		ContainerArea blockContainer = (ContainerArea) logicContainers.next( );
		assertTrue( "Page body is not empty", !isEmpty( blockContainer ) );
	}

	/**
	 * Tests legend defined on chart are generated by the image layout engine.
	 * 
	 * @throws EngineException
	 */
	public void testPdfChartLegend( ) throws EngineException
	{
		String designFile = "org/eclipse/birt/report/engine/layout/pdf/PDFChartLegendTest.xml";
		IReportRunnable report = openReportDesign( designFile );
		List pageAreas = getPageAreas( report );

		assertEquals( 1, pageAreas.size( ) );
		PageArea pageArea = (PageArea) pageAreas.get( 0 );
		Iterator logicContainers = pageArea.getBody( ).getChildren( );
		ContainerArea blockContainer = (ContainerArea) logicContainers.next( );
		checkChart( 161589, 39140, 40920, 12009, "#bookmark", null, "",
				blockContainer );
		logicContainers.next( );
		checkChart(
				161589,
				39140,
				40920,
				12009,
				"run\\?__report=.*120358.rptdesign&__overwrite=true",
				null, "_self", (ContainerArea) logicContainers.next( ) );
		checkChart( 161589, 39140, 40920, 12009, "120358.rptdesign", null,
				"_self", (ContainerArea) logicContainers.next( ) );
	}

	private void checkChart( int x, int y, int width, int height,
			String hyperlink, String bookmark, String targetWindow,
			ContainerArea blockContainer )
	{
		ContainerArea chartParent1 = getChartParentIn( blockContainer );
		Iterator children = chartParent1.getChildren( );
		children.next( );
		checkChartLengend( (ContainerArea) children.next( ), x, y, width,
				height, hyperlink, bookmark, targetWindow );
	}

	private void checkChartLengend( ContainerArea map, int x, int y, int width,
			int height, String hyperlink, String bookmark, String targetWindow )
	{
		IContent mapContent = (IContent) map.getContent( );
		IHyperlinkAction link = mapContent.getHyperlinkAction( );
		assertEquals( x, map.getX( ) );
		assertEquals( y, map.getY( ) );
		assertEquals( width, map.getWidth( ) );
		assertEquals( height, map.getHeight( ) );
		assertTrue( link.getHyperlink( ).matches( hyperlink ) );
		assertEquals( bookmark, link.getBookmark( ) );
		assertEquals( targetWindow, link.getTargetWindow( ) );
	}

	private ContainerArea getChartParentIn( ContainerArea blockContainer )
	{
		ContainerArea parent = blockContainer;
		Iterator iterator = parent.getChildren( );
		while ( iterator.hasNext( ) )
		{
			IArea firstChild = (IArea) iterator.next( );
			if ( firstChild instanceof IImageArea )
			{
				return parent;
			}
			parent = (ContainerArea) firstChild;
			iterator = parent.getChildren( );
		}
		return null;
	}
}