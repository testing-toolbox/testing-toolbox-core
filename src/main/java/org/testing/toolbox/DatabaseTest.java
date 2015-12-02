package org.testing.toolbox;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testing.toolbox.utils.FileUtils.deleteFileQuietly;
import static org.testing.toolbox.utils.FileUtils.getAbsolutePath;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.FilteredDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatDtdWriter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Abstract with methods that allow to check interactions with a database for
 * Spring project.
 * 
 * @author Idriss Neumann <neumann.idriss@gmail.com>
 */
public abstract class DatabaseTest extends IntegrationTest {
	protected static final DatabaseOperation DEFAULT_OPERATION = DatabaseOperation.INSERT;
	protected final String PURGE_DS = getTestCommonDataDir() + "schema_purge_ds.xml";
	protected final String SCHEMA = "public";

	@Autowired
	private DataSource dataSource;

	/**
	 * Getting database connection.
	 * 
	 * @param strSchema
	 * @return IDatabaseConnection
	 * @throws Exception
	 */
	public IDatabaseConnection getConnection(String strSchema) throws Exception {
		Connection con = dataSource.getConnection();
		IDatabaseConnection connection = new DatabaseConnection(con, strSchema);
		DatabaseConfig config = connection.getConfig();
		config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
		return connection;
	}

	/**
	 * Getting dataset from a file name.
	 * 
	 * @param filename
	 * @return IDataSet
	 * @throws IOException
	 * @throws Exception
	 */
	public IDataSet getDataSet(String filename) throws DataSetException, IOException {
		FlatXmlDataSetBuilder xmldataSourceBuilder = new FlatXmlDataSetBuilder();
		xmldataSourceBuilder.setCaseSensitiveTableNames(false);
		return xmldataSourceBuilder.build(new File(filename));
	}

	/**
	 * Getting dataset from XML stream.
	 * 
	 * @param strXml
	 * @return IDataSet
	 * @throws DataSetException
	 */
	public IDataSet getDataSetFromString(String strXml) throws DataSetException {
		InputStream stream = new ByteArrayInputStream(strXml.getBytes());
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setCaseSensitiveTableNames(false);
		return builder.build(stream);
	}

	/**
	 * Loading dataset with integrity order.
	 * 
	 * @param filename
	 * @param strSchema
	 * @return IDataSet
	 * @throws Exception
	 */
	public IDataSet getDataSet(String filename, String strSchema) throws Exception {
		IDataSet dataSet = getDataSet(filename);

		DatabaseSequenceFilter filter = new DatabaseSequenceFilter(getConnection(strSchema));
		IDataSet filtered = new FilteredDataSet(filter, dataSet);

		return filtered;
	}

	/**
	 * Launching SQL update/delete/insert query.
	 * 
	 * @param schema
	 * @param query
	 */
	public void execDb(String schema, String query) {
		try {
			getConnection(schema).getConnection().createStatement().execute(query);
		} catch (SQLException e) {
			failWithException(e);
		} catch (Exception e) {
			failWithException(e);
		}
	}

	/**
	 * Launching flat XML dataset.
	 * 
	 * @param dataSet
	 * @param schema
	 * @param operation
	 */
	private void execFlatXmlDataSet(IDataSet dataSet, String schema, DatabaseOperation operation) {
		try {
			operation.execute(getConnection(schema), dataSet);
		} catch (Exception ex) {
			failWithException(ex);
		}
	}

	/**
	 * Launching replacement dataset.
	 * 
	 * @param dataSet
	 * @param param
	 * @param schema
	 * @param operation
	 */
	private void execReplacementDataSet(IDataSet dataSet, Map<String, String> param, String schema, DatabaseOperation operation) {
		try {
			ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);

			Set<String> keys = param.keySet();
			Iterator<String> it = keys.iterator();

			String key;
			while (it.hasNext()) {
				key = it.next();
				replacementDataSet.addReplacementObject(key, param.get(key));
			}

			replacementDataSet.addReplacementObject("${NULL}", null);
			replacementDataSet.addReplacementObject("${null}", null);
			operation.execute(getConnection(schema), replacementDataSet);

		} catch (Exception e) {
			failWithException(e);
		}
	}

	/**
	 * Lanching flat xml data set from string XML stream.
	 * 
	 * @param strXML
	 * @param schema
	 * @param operation
	 */
	public void execFlatXmlDataSetFromString(String strXML, String schema, DatabaseOperation operation) {
		try {
			execFlatXmlDataSet(getDataSetFromString(strXML), schema, operation);
		} catch (DataSetException ex) {
			failWithException(ex);
		}
	}

	/**
	 * Launching flat xml dataset from string XML stream.
	 * 
	 * @param strXML
	 * @param schema
	 */
	public void execFlatXmlDataSetFromString(String strXML, String schema) {
		execFlatXmlDataSetFromString(strXML, schema, DEFAULT_OPERATION);
	}

	/**
	 * Launching flat xml dataset from XML file.
	 * 
	 * @param pathXMLFile
	 * @param schema
	 */
	public void execFlatXmlDataSetFromFile(String pathXMLFile, String schema) {
		execFlatXmlDataSetFromFile(pathXMLFile, schema, DEFAULT_OPERATION);
	}

	/**
	 * Launching flat xml dataset from XML file.
	 * 
	 * @param pathXMLFile
	 * @param schema
	 * @param operation
	 */
	public void execFlatXmlDataSetFromFile(String pathXMLFile, String schema, DatabaseOperation operation) {
		try {
			execFlatXmlDataSet(getDataSet(pathXMLFile), schema, operation);
		} catch (DataSetException | IOException e) {
			failWithException(e);
		}
	}

	/**
	 * Launching replacement dataset from XML string.
	 * 
	 * @param strXML
	 * @param param
	 * @param schema
	 */
	public void execReplacementDataSetFromString(String strXML, Map<String, String> param, String schema) {
		execReplacementDataSetFromString(strXML, param, schema, DEFAULT_OPERATION);
	}

	/**
	 * Launching replacement dataset from XML string.
	 * 
	 * @param strXML
	 * @param param
	 * @param schema
	 * @param operation
	 */
	public void execReplacementDataSetFromString(String strXML, Map<String, String> param, String schema, DatabaseOperation operation) {
		try {
			execReplacementDataSet(getDataSetFromString(strXML), param, schema, operation);
		} catch (DataSetException ex) {
			failWithException(ex);
		}
	}

	/**
	 * Launching replacement dataset from XML file.
	 * 
	 * @param pathXMLFile
	 * @param param
	 * @param schema
	 */
	public void execReplacementDataSetFromFile(String pathXMLFile, Map<String, String> param, String schema) {
		execReplacementDataSetFromFile(pathXMLFile, param, schema, DEFAULT_OPERATION);
	}

	/**
	 * Launching replacement dataset from XML file.
	 * 
	 * @param pathXMLFile
	 * @param param
	 * @param schema
	 * @param operation
	 */
	public void execReplacementDataSetFromFile(String pathXMLFile, Map<String, String> param, String schema, DatabaseOperation operation) {
		try {
			execReplacementDataSet(getDataSet(pathXMLFile), param, schema, operation);
		} catch (DataSetException | IOException e) {
			failWithException(e);
		}
	}

	/**
	 * Getting dataset from tables.
	 * 
	 * @param tables
	 * @param nameOfXml
	 * @param schema
	 */
	public void getDataSetFromTables(List<String> tables, String nameOfXml, String schema) {
		Map<String, String> queryByTables = new HashMap<String, String>();
		for (String tableName : tables) {
			queryByTables.put(tableName, null);
		}
		generateDataSet(queryByTables, nameOfXml, schema);
	}

	/**
	 * Getting dataset from a single table.
	 * 
	 * @param tableName
	 * @param nameOfXml
	 * @param schema
	 */
	public void getDataSetFromTable(String tableName, String nameOfXml, String schema) {
		Map<String, String> queryByTables = new HashMap<String, String>();
		queryByTables.put(tableName, null);
		generateDataSet(queryByTables, nameOfXml, schema);
	}

	/**
	 * Generating dataset from SQL queries.
	 * 
	 * @param queryByTables
	 * @param nameOfXml
	 * @param schema
	 * @return IDataSet
	 */
	public IDataSet generateDataSet(Map<String, String> queryByTables, String nameOfXml, String schema) {
		QueryDataSet dataSet = null;

		try {
			dataSet = new QueryDataSet(getConnection(schema));
			for (Map.Entry<String, String> entry : queryByTables.entrySet()) {
				if (StringUtils.isBlank(entry.getValue())) {
					dataSet.addTable(entry.getKey());
				} else {
					dataSet.addTable(entry.getKey(), entry.getValue());
				}
			}

			File dstXmlFile = new File(getTestDataDir() + nameOfXml);
			File dstDirectory = dstXmlFile.getParentFile();

			if (!dstDirectory.isDirectory()) {
				if (dstDirectory.exists()) {
					fail("Le répertoire n'existe pas et ne peut être créé");
				} else {
					dstDirectory.mkdirs();
				}
			}

			OutputStream out = new FileOutputStream(dstXmlFile);
			FlatXmlDataSet.write(dataSet, out);
			out.close();
		} catch (Exception e) {
			failWithException(e);
		}

		return dataSet;
	}

	/**
	 * Generating dataset from SQL queries.
	 * 
	 * @param queryByTables
	 * @param nameOfXml
	 * @param nameOfDtd
	 * @param schema
	 * @return IDataSet
	 */
	public IDataSet generateDataSet(Map<String, String> queryByTables, String nameOfXml, String nameOfDtd, String schema) {
		IDataSet dataSet = generateDataSet(queryByTables, nameOfXml, schema);

		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(getTestDataDir() + nameOfDtd));
			FlatDtdWriter writer = new FlatDtdWriter(out);
			writer.write(dataSet);
		} catch (IOException | DataSetException ex) {
			failWithException(ex);
		}

		return dataSet;
	}

	/**
	 * Getting tmp dataset name.
	 * 
	 * @param tables
	 * @return String
	 */
	private String getNameTmpDataSet(List<String> tables) {
		String tmpNameDataSet = "tmp_" + getClass().getSimpleName() + "_";
		for (String tableName : tables) {
			tmpNameDataSet += tableName + "_";
		}
		tmpNameDataSet += "dataset.xml";
		return tmpNameDataSet;
	}

	/**
	 * Getting nb occurs from Xpath query.
	 * 
	 * @param nameOfXML
	 * @param strXpath
	 * @return Integer
	 * @throws Exception
	 */
	private Integer getNbOccurrenceFromXpath(String nameOfXML, String strXpath) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(getTestDataDir() + nameOfXML);
			XPathFactory xpathfactory = XPathFactory.newInstance();
			XPath xpath = xpathfactory.newXPath();
			XPathExpression expr = xpath.compile(strXpath);
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			return nodes.getLength();
		} catch (Exception e) {
			failWithException(e);
			return null;
		}
	}

	/**
	 * Assert nb occurence of rows in a database.
	 * 
	 * @param schema
	 * @param tables
	 * @param lstLinesExpected
	 * @param Integer
	 *            nbOccurence
	 */
	public void assertOccrurrenceDataSet(String schema, List<String> tables, List<ExpectedLineDataSet> lstLinesExpected, Integer nbOccurence) {
		String nameOfXML = getNameTmpDataSet(tables);
		getDataSetFromTables(tables, nameOfXML, schema);
		for (ExpectedLineDataSet line : lstLinesExpected) {
			assertEquals("Problème avec le nombre d'occurrence de " + line.buildXpath(), nbOccurence, getNbOccurrenceFromXpath(nameOfXML, line.buildXpath()));
		}

		deleteFileQuietly(getAbsolutePath(getTestDataDir() + nameOfXML));
	}

	/**
	 * Assert that a dataset contains expected of row(s) in a database.
	 * 
	 * @param schema
	 * @param tables
	 * @param lstLinesExpected
	 */
	public void assertContainDataSet(String schema, List<String> tables, List<ExpectedLineDataSet> lstLinesExpected) {
		assertOccrurrenceDataSet(schema, tables, lstLinesExpected, 1);
	}

	/**
	 * Assert that a dataset contains expected of a row in a database.
	 * 
	 * @param schema
	 * @param tables
	 * @param lineExpected
	 */
	public void assertContainDataSet(String schema, List<String> tables, ExpectedLineDataSet lineExpected) {
		List<ExpectedLineDataSet> lstLines = new ArrayList<ExpectedLineDataSet>();
		lstLines.add(lineExpected);

		assertContainDataSet(schema, tables, lstLines);
	}

	/**
	 * Assert that a dataset contains expected of a row in a database.
	 * 
	 * @param schema
	 * @param tableName
	 * @param lineExpected
	 */
	public void assertContainDataSet(String schema, String tableName, ExpectedLineDataSet lineExpected) {
		List<String> tables = new ArrayList<String>();
		tables.add(tableName);

		List<ExpectedLineDataSet> lstLines = new ArrayList<ExpectedLineDataSet>();
		lstLines.add(lineExpected);

		assertContainDataSet(schema, tables, lstLines);
	}

	/**
	 * Assert that a dataset doesn't contain unexpected of row(s) in a database.
	 * 
	 * @param schema
	 * @param tables
	 * @param lstLinesExpected
	 */
	public void assertNotContainDataSet(String schema, List<String> tables, List<ExpectedLineDataSet> lstLinesExpected) {
		assertOccrurrenceDataSet(schema, tables, lstLinesExpected, 0);
	}

	/**
	 * Assert that a dataset doesn't contain unexpected of a row in a database.
	 * 
	 * @param schema
	 * @param tables
	 * @param lineExpected
	 */
	public void assertNotContainDataSet(String schema, List<String> tables, ExpectedLineDataSet lineExpected) {
		List<ExpectedLineDataSet> lstLines = new ArrayList<ExpectedLineDataSet>();
		lstLines.add(lineExpected);

		assertNotContainDataSet(schema, tables, lstLines);
	}

	/**
	 * Assert that a dataset doesn't contain unexpected of a row in a database.
	 * 
	 * @param schema
	 * @param tableName
	 * @param lineExpected
	 */
	public void assertNotContainDataSet(String schema, String tableName, ExpectedLineDataSet lineExpected) {
		List<String> tables = new ArrayList<String>();
		tables.add(tableName);

		List<ExpectedLineDataSet> lstLines = new ArrayList<ExpectedLineDataSet>();
		lstLines.add(lineExpected);

		assertNotContainDataSet(schema, tables, lstLines);
	}
}
