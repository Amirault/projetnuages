package server.webservices.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.Arrays;

import javax.ws.rs.NotFoundException;

import org.junit.Test;

import server.imageprocessing.IImageProcessing;
import server.webservices.google.IGoogleSearchClient;
import server.webservices.nuage.NuageService;
import server.webservices.nuage.model.Catalog;
import server.webservices.nuage.services.ICatalogFactory;
import server.webservices.nuage.services.IFileHandler;

/**
 * Tests class for NuageService
 * 
 * @author Cedric Boulet Kessler
 */
public class NuageServiceTests {
	
	/**
	 * Fake path given for the test
	 */
	private static String imagePath = "myPath";

	/**
	 * Test for checking the catalog retrieval
	 */
	@Test
	public void returnGeneratedCatalog() {
		// Arrange - Create and populate a mocked ICatalogFactory
		Catalog catalog = new Catalog();
		ICatalogFactory mock = mock(ICatalogFactory.class);
		when(mock.createCatalog()).thenReturn(catalog);
		
		NuageService nuage = new NuageService(
				mock(IImageProcessing.class), 
				mock(IFileHandler.class), 
				mock, 
				mock(IGoogleSearchClient.class),
				null);
		
		// Act - Ask to give the current Catalog
		Catalog returnedCatalog = nuage.getCatalog();

		// Assert - Verify that the returned Catalog is the one from the CatalogFactory
		verify(mock, times(1)).createCatalog();
		assertSame(catalog, returnedCatalog);
	}
	
	/**
	 * Test for checking the image retrieval
	 */
	@Test
	public void returnAskedImage() {
		// Arrange - Create and populate a mocked IFileHandler
		String path1 = imagePath + "/image1.jpeg";
		String path2 = imagePath + "/image2.jpeg";
		IFileHandler mock = mock(IFileHandler.class);
		when(mock.scanDir(imagePath)).thenReturn(Arrays.asList(path1, path2));
		when(mock.loadFile(path1)).thenReturn(new File(path1));
		when(mock.loadFile(path2)).thenReturn(new File(path2));
		
		NuageService nuage = new NuageService(
				mock(IImageProcessing.class),
				mock,
				mock(ICatalogFactory.class), 
				mock(IGoogleSearchClient.class),
				imagePath);
		
		// Act - Ask to give the image 1
		File image = nuage.getImage("0");
		
		// Assert - Verify that the returned image is the first
		verify(mock, times(1)).scanDir(imagePath);
		verify(mock, times(1)).loadFile(path1);
		verify(mock, never()).loadFile(path2);
		assertEquals("image1.jpeg", image.getName());
	}
	
	@Test(expected = NotFoundException.class)
	public void return404NotFound() {
		// Arrage
		NuageService nuage = new NuageService(
				mock(IImageProcessing.class),
				mock(IFileHandler.class),
				mock(ICatalogFactory.class), 
				mock(IGoogleSearchClient.class),
				null);
		
		// Act - Ask to give an unknown image
		nuage.getImage("1");
		
		// Assert - being done by the Test annotation
	}

}
