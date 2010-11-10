package org.jims.modules.crossbow.publisher;

import org.jims.modules.crossbow.publisher.FlowMBeanPublisher;
import org.jims.modules.crossbow.publisher.Publisher;
import org.jims.modules.crossbow.publisher.exception.NotPublishedException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author cieplik
 */
public class FlowMBeanPublisherTest {

	public FlowMBeanPublisherTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}


	@Before
	public void setUp() {
		publisher = new FlowMBeanPublisher( null );
	}

	@After
	public void tearDown() {
	}


	@Test( expected = NotPublishedException.class )
	public void testUnpublishNotPublishedObject() throws NotPublishedException {
		publisher.unpublish( new String( "ID" ) );
	}


	Publisher publisher;

}