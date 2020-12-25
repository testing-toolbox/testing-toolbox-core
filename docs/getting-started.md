# Installation with maven

## Maven repository

```xml
<repository>
    <id>testing-toolbox-mvn-repo</id>
    <url>https://raw.github.com/testing-toolbox/testing-toolbox-core/mvn-repo/</url>
    <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
    </snapshots>
</repository>
```

## Maven dependency

```xml
<dependency>
    <groupId>testing-toolbox</groupId>
    <artifactId>testing-toolbox-core</artifactId>
    <version>${testing-toolbox-core.version}</version>
</dependency>
```

# How to use

## Initializing your tests classes

Your tests classes must extends the abstract `DatabaseTest` to use the dataset features below.

If you don't use PostgreSQL as database you must set the `dataTypeFactory` in your subclase with the factory that corresponds to your database.

## Injecting datasource in your test

You must inject a `javax.sql.DataSource` in your test. You can use any IoC framework which is `javax.inject` compliant to do it (Spring with the `SpringJUnit4ClassRunner` for example, or weld, hk2, guice, etc).

## Inserting data from flat XML dataset

```java
private final String SCHEMA = "public";
private final String INSERT_DATA = getTestDataDir() + "insert_data_ds.xml";

/**
 * Inserting data from flat XML dataset
 */
@Before
public void init() throws Exception {
    execFlatXmlDataSetFromFile(INSERT_DATA, SCHEMA, DatabaseOperation.CLEAN_INSERT);
}
```

The xml dataset which correspond:

```xml
<?xml version="1.0" encoding="UTF-8"?>  
<dataset>  
    <MY_TABLE COLUMN_NAME="Rabbit" COLUMN_SURNAME="Roger" /> 
</dataset>
```

Note : You can use `execFlatXmlDataSetFromString` instead of `execFlatXmlDataSetFromFile` if you want to use an embeded String dataset instead of a XML file.

## Deleting data from flat XML dataset

```java
private final String SCHEMA = "public";
private final String DELETE_DATA = getTestDataDir() + "delete_data_ds.xml";

/**
 * Deleting data from flat XML dataset
 */
@Before
public void init() throws Exception {
    execFlatXmlDataSetFromFile(DELETE_DATA, SCHEMA, DatabaseOperation.DELETE_ALL);
}
```

## Inserting data from replacement XML dataset

```java
private final String SCHEMA = "public";
private final String INSERT_DATA = getTestDataDir() + "insert_data_ds.xml";

/**
 * Inserting data from replacement XML dataset
 */
@Before
public void init() throws Exception {

    // List of replacements
    Replacements replacements = Replacements.newInstance()
        .add("${name}", "rabbit")
        .add("${surname}", "rogger");

    execReplacementDataSetFromFile(INSERT_DATA, replacements, SCHEMA, DatabaseOperation.CLEAN_INSERT);
}
```

The xml dataset which correspond:

```xml
<?xml version="1.0" encoding="UTF-8"?>  
<dataset>  
    <MY_TABLE COLUMN_NAME="${name}" COLUMN_SURNAME="${surname}" COLUMN_AGE="${null}" /> 
</dataset>
```

Notes: 

By default `${null}` and `${NULL}` are auto replaced by a null value.

You can use `execReplacementDataSetFromString` instead of `execReplacementDataSetFromFile` if you want to use an embeded String dataset instead of a XML file.

## Asserting that a row exists on database

```java
private final String SCHEMA = "public";

/**
 * Asserting adding data.
 */
@Test
public final void testCreateNominal() {
    service.create(member);

    String tableName = SCHEMA + ".USER_ACCOUNT";
    ExpectedLineDataSet lineExpected = ExpectedLineDataSet.newInstance(tableName)
        .add("name", "rabbit");
        .add("surname", "rogger");

    assertContainDataSet(SCHEMA, tableName, lineExpected);
}
```
Notes: 

If you want to assert that this line doen't exists in the database, you can use `assertNotContainDataSet` instead of `assertContainDataSet`.

If you want the assert the exact number of occurrences of your `ExpectedLineDataSet`, you can use `assertOccrurrenceDataSet` instead.

## Executing SQL insert/update/delete queries

You can use the `void execDb(String schema, String query)` to do that.
