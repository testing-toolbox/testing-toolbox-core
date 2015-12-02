package org.testing.toolbox;

import static org.junit.Assert.fail;

import java.io.File;

/**
 * Abstract class for tests.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 *
 */
public abstract class AbstractTest {

	/**
	 * Fail with exception.
	 * 
	 * @param Exception
	 *            e
	 */
	public void failWithException(Exception e) {
		e.printStackTrace();
		fail("Unexpected exception : " + e.getMessage());
	}

	/**
	 * Getting test data dir.
	 * 
	 * @return String
	 */
	public String getTestDataDir() {
		return "src" //
		        + File.separator //
		        + "test" //
		        + File.separator //
		        + "resources" //
		        + File.separator //
		        + "data" //
		        + File.separator //
		        + this.getClass().getSimpleName() //
		        + File.separator;
	}

	/**
	 * Getting common test data dir.
	 * 
	 * @return String
	 */
	public String getTestCommonDataDir() {
		return "src" //
		        + File.separator //
		        + "test" //
		        + File.separator //
		        + "resources" //
		        + File.separator //
		        + "data" //
		        + File.separator //
		        + "Common" //
		        + File.separator;
	}

	/**
	 * Getting test resources data dir.
	 * 
	 * @return String
	 */
	public String getTestResourcesDir() {
		return "src" //
		        + File.separator //
		        + "test" //
		        + File.separator //
		        + "resources" //
		        + File.separator;
	}

	/**
	 * Getting data dir.
	 * 
	 * @return String
	 */
	public String getDataDir() {
		return "src" //
		        + File.separator //
		        + "main" //
		        + File.separator //
		        + "resources" //
		        + File.separator;
	}
}
