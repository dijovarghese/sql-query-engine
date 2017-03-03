package com.dijo.projects;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {

	// Defining Tokens
	private static final String SELECT = "SELECT";
	private static final String FROM = "FROM";
	private static final String WHERE = "WHERE";
	private static ArrayList<String> tokens = new ArrayList<String>();

	// Defining Symbols
	private static final String ASTERISK = "*";
	private static final String LEFT_BRACE = "(";
	private static final String RIGHT_BRACE = ")";
	private static final String SEMICOLON = ";";
	private static final String COMMA = ",";
	private static ArrayList<String> symbols = new ArrayList<String>();

	// Defining Functions
	private static final String MAX = "MAX";
	private static final String MIN = "MIN";
	private static final String UNIQ = "UNIQ";
	private static ArrayList<String> functions = new ArrayList<String>();

	// Defining Operators
	private static final String ADD = "+";
	private static final String SUB = "-";
	private static final String MUL = "*";
	private static final String DIV = "/";
	private static final String EQ = "=";
	private static final String NOTEQ = "!=";
	private static final String NOTEQ2 = "<>";
	private static final String LT = "<";
	private static final String GT = ">";
	private static final String LTEQ = "<=";
	private static final String GTEQ = ">=";
	private static ArrayList<String> operators = new ArrayList<String>();

	private static final String AND = "AND";
	private static final String OR = "OR";
	private static ArrayList<String> logicalOperators = new ArrayList<String>();

	// Defining Column Names
	private static final String TITLE = "TITLE";
	private static final String BRAND = "BRAND";
	private static final String STORE = "STORE";
	private static final String PRICE = "PRICE";
	private static final String INSTOCK = "IN_STOCK";
	private static ArrayList<String> columnNames = new ArrayList<String>();

	// Defining Data Types
	private static final String INT = "INT";
	private static final String FLOAT = "FLOAT";
	private static final String STRING = "STRING";
	private static final String BOOLEAN = "BOOLEAN";
	private static ArrayList<String> dataTypes = new ArrayList<String>();

	// Defining Boolean Values
	private static final String BOOL_TRUE = "TRUE";
	private static final String BOOL_FALSE = "FALSE";
	private static ArrayList<String> booleanValues = new ArrayList<String>();

	// Defining Products Table
	private static int totalRows = 0;
	private static int totalColumns = 5;
	private static final String tableName = "PRODUCTS";
	private static List<Row> table = new ArrayList<Row>();
	private static Map<String, String> columnTypeMap = new HashMap<String, String>();
	private static Map<String, Integer> columnIndexMap = new HashMap<String, Integer>();
	private static String queryText = new String();
	private static ArrayList<String> queryTokens = new ArrayList<String>();
	private static int indexOfLastSelectedColumnToken = -1;
	private static int indexOfFROMToken = -1;
	private static int indexOfTableNameToken = -1;
	private static int indexOfWHEREToken = -1;
	private static List<Integer> selectedRows = new ArrayList<>();
	private static List<Integer> selectedColumns = new ArrayList<Integer>();
	private static Boolean SPECIAL_COLUMN_PRESENT = false;

	public static void main(String[] args) {

		initilizeEngine();
		if (loadDatabase(getDatabaseFile())) {
			startEngine(true);
		}
		finalizeEngine();
	}

	private static void initilizeEngine() {
		setTokens();
		setSymbols();
		setOperators();
		setLogicalOperators();
		setFunctions();
		setDataTypes();
		setBooleanValues();
		setColumnNames();
		setColumnTypeMap();
		setColumnIndexMap();
	}

	private static void finalizeEngine() {
		tokens.clear();
		symbols.clear();
		functions.clear();
		operators.clear();
		logicalOperators.clear();
		columnNames.clear();
		dataTypes.clear();
		table.clear();
		columnTypeMap.clear();
		columnIndexMap.clear();
		queryText = null;
		queryTokens.clear();		
		selectedRows.clear();
		selectedColumns.clear();		
	}

	private static void startEngine(boolean keepRunning) {
		System.out.println("Welcome to SQL Query Engine");
		do {
			try {
				queryText = "";
				queryTokens.clear();
				System.out.print("SQL> ");

				// Get SQL Query from User
				if ((queryText = ((new BufferedReader(new InputStreamReader(System.in))).readLine()).trim()
						.toUpperCase()).equals("EXIT"))
					keepRunning = false;
				else if (queryText.length() == 0)
					continue;

				// Starts processing SQL Query
				else {
					tokenizeQuery(); //System.out.println(queryTokens);
					validateQuery();
					executeQuery();
					flushQuery();
				}
			} catch (Exception e) {
				System.out.println("Error: An error occurred while processing the query. " + e.getMessage());
				keepRunning = false;
			}
		} while (keepRunning);
	}

	private static void flushQuery() {
		queryText = "";
		queryTokens.clear();
		SPECIAL_COLUMN_PRESENT = false;
		selectedRows.clear();
		selectedColumns.clear();
		indexOfLastSelectedColumnToken = -1;
		indexOfFROMToken = -1;
		indexOfTableNameToken = -1;
		indexOfWHEREToken = -1;
	}

	private static void executeQuery() throws Exception {
		selectRows();
		// System.out.println(selectedRows);

		selectColumns();
		// System.out.println(selectedColumns);

		displayQueryResponse();
	}

	private static void displayQueryResponse() throws Exception {

		// For special columns like 'MAX(TITLE)'
		if (SPECIAL_COLUMN_PRESENT) {
			if (queryTokens.get(1).equals(MAX)) {
				displayMAXofColumn(queryTokens.get(3));
			} else if (queryTokens.get(1).equals(MIN)) {
				displayMINofColumn(queryTokens.get(3));
			} else if (queryTokens.get(1).equals(UNIQ)) {
				displayUNIQofColumn(queryTokens.get(3));
			}
		} else {
			int i = 0;
			int j = 0;
			String headerRow = "";
			while (i < selectedColumns.size()) {
				headerRow = headerRow + columnNames.get(selectedColumns.get(i)) + "\t";
				i++;
			}
			System.out.println(headerRow);
			i = 0;
			String singleRow = "";
			while (i < selectedRows.size()) {
				j = 0;
				while (j < selectedColumns.size()) {
					singleRow = singleRow + (table.get(selectedRows.get(i)).column[j].data + "\t");
					j++;
				}
				System.out.println(singleRow);
				singleRow = "";
				i++;
			}
		}
	}

	private static void displayMAXofColumn(String columnName) throws Exception {
		String columnType = columnTypeMap.get(columnName);
		int columnIndex = columnIndexMap.get(columnName);
		if (columnType.equals(INT)) {
			List<Integer> valueList = new ArrayList<Integer>();
			int i = 0;
			while (i < selectedRows.size()) {
				valueList.add(Integer.parseInt(table.get(selectedRows.get(i)).column[columnIndex].data));
				i++;
			}
			Collections.sort(valueList);
			Collections.reverse(valueList);
			System.out.println(MAX + "(" + columnName + ")\n" + valueList.get(0));
		} else if (columnType.equals(FLOAT)) {
			List<Float> valueList = new ArrayList<Float>();
			int i = 0;
			while (i < selectedRows.size()) {
				valueList.add(Float.parseFloat(table.get(selectedRows.get(i)).column[columnIndex].data));
				i++;
			}
			Collections.sort(valueList);
			Collections.reverse(valueList);
			System.out.println(MAX + "(" + columnName + ")\n" + valueList.get(0));
		} else {
			throw new Exception("MAX() function not supported for column '" + columnName + "'");
		}
	}

	private static void displayMINofColumn(String columnName) throws Exception {
		String columnType = columnTypeMap.get(columnName);
		int columnIndex = columnIndexMap.get(columnName);
		if (columnType.equals(INT)) {
			List<Integer> valueList = new ArrayList<Integer>();
			int i = 0;
			while (i < selectedRows.size()) {
				valueList.add(Integer.parseInt(table.get(selectedRows.get(i)).column[columnIndex].data));
				i++;
			}
			Collections.sort(valueList);
			System.out.println(MIN + "(" + columnName + ")\n" + valueList.get(0));
		} else if (columnType.equals(FLOAT)) {
			List<Float> valueList = new ArrayList<Float>();
			int i = 0;
			while (i < selectedRows.size()) {
				valueList.add(Float.parseFloat(table.get(selectedRows.get(i)).column[columnIndex].data));
				i++;
			}
			Collections.sort(valueList);
			System.out.println(MIN + "(" + columnName + ")\n" + valueList.get(0));
		} else {
			throw new Exception("MIN() function not supported for column '" + columnName + "'");
		}
	}

	private static void displayUNIQofColumn(String columnName) throws Exception {
		int columnIndex = columnIndexMap.get(columnName);
		List<String> valueSet = new ArrayList<String>();
		int i = 0;
		while (i < selectedRows.size()) {
			if (!valueSet.contains(table.get(selectedRows.get(i)).column[columnIndex].data)) {
				valueSet.add(table.get(selectedRows.get(i)).column[columnIndex].data);
			}
			i++;
		}
		System.out.println(UNIQ + "(" + columnName + ")");
		i = 0;
		while (i < valueSet.size()) {
			System.out.print(valueSet.get(i) + "\n");
			i++;
		}
	}

	private static void selectColumns() {
		if (queryTokens.contains(ASTERISK)) {
			for (int i = 0; i < totalColumns; i++)
				selectedColumns.add(i);
		} else if (queryTokens.contains(MAX) || (queryTokens.contains(MIN)) || (queryTokens.contains(UNIQ))) {
			SPECIAL_COLUMN_PRESENT = true;			
		} else {
			int i = 1;
			String currentToken = queryTokens.get(i);
			while (!currentToken.equals(FROM)) {
				if (columnNames.contains(currentToken)) {
					selectedColumns.add(columnIndexMap.get(currentToken));
					i++;
					currentToken = queryTokens.get(i);
				}
			}
		}
	}

	private static void selectRows() throws Exception {

		int tokenIndex = indexOfWHEREToken + 1;
		String previousToken = "";
		String currentToken = "";
		String nextToken = "";
		List<List<Integer>> selectedRowLists = new ArrayList<List<Integer>>();
		List<String> resolvedQueryTokens = new ArrayList<String>();

		if (queryTokens.contains(WHERE)) {
			while (tokenIndex < queryTokens.size()) {
				previousToken = ((tokenIndex - 1) >= 0) ? queryTokens.get(tokenIndex - 1) : "";
				currentToken = queryTokens.get(tokenIndex);
				nextToken = ((tokenIndex + 1) < queryTokens.size()) ? queryTokens.get(tokenIndex + 1) : "";
				if (operators.contains(currentToken)) {
					if (columnNames.contains(previousToken)
							&& (isNumber(nextToken) || isString(nextToken) || booleanValues.contains(nextToken))) {
						selectedRowLists.add(getRowsForSimpleExpression(previousToken, currentToken, nextToken));
						resolvedQueryTokens.add(String.valueOf(selectedRowLists.size() - 1));
						tokenIndex += 2;
					} else
						throw new Exception(
								"Unable to calculate " + previousToken + " " + currentToken + " " + nextToken);
				} else if (currentToken.equals(LEFT_BRACE) || currentToken.equals(RIGHT_BRACE)
						|| logicalOperators.contains(currentToken)) {
					resolvedQueryTokens.add(currentToken);
					tokenIndex++;
				} else {
					tokenIndex++;
					continue;
				}
			}
			int i = 0;
			while (i < resolvedQueryTokens.size()) {
				previousToken = ((i - 1) >= 0) ? resolvedQueryTokens.get(i - 1) : "";
				currentToken = resolvedQueryTokens.get(i);
				nextToken = ((i + 1) < resolvedQueryTokens.size()) ? resolvedQueryTokens.get(i + 1) : "";
				if (logicalOperators.contains(currentToken)) {
					if (isNumber(previousToken) && isNumber(nextToken)) {
						if (currentToken.equals(OR)) {
							selectedRowLists.add(
									applyOROperationOnRowLists(selectedRowLists.get(Integer.parseInt(previousToken)),
											selectedRowLists.get(Integer.parseInt(nextToken))));
							resolvedQueryTokens.set(i - 1, String.valueOf(selectedRowLists.size() - 1));
							resolvedQueryTokens.remove(i);
							resolvedQueryTokens.remove(i);
							i--;
						} else if (currentToken.equals(AND)) {
							selectedRowLists.add(
									applyANDOperationOnRowLists(selectedRowLists.get(Integer.parseInt(previousToken)),
											selectedRowLists.get(Integer.parseInt(nextToken))));
							resolvedQueryTokens.set(i - 1, String.valueOf(selectedRowLists.size() - 1));
							resolvedQueryTokens.remove(i);
							resolvedQueryTokens.remove(i);
							i--;
						} else {
							throw new Exception(
									"Unable to calculate " + previousToken + " " + currentToken + " " + nextToken);
						}
					} else if ((isNumber(previousToken) && nextToken.equals(LEFT_BRACE))
							|| (previousToken.equals(RIGHT_BRACE) && isNumber(nextToken))) {
						i++;
					} else {
						throw new Exception(
								"Unable to calculate " + previousToken + " " + currentToken + " " + nextToken);
					}
				} else if (isNumber(currentToken)) {
					if (previousToken.equals(LEFT_BRACE) && nextToken.equals(RIGHT_BRACE)) {
						resolvedQueryTokens.set(i - 1, resolvedQueryTokens.get(i));
						resolvedQueryTokens.remove(i);
						resolvedQueryTokens.remove(i);
						i--;
					} else if (i == resolvedQueryTokens.size() - 1) {
						if (resolvedQueryTokens.size() == 1) {
							selectedRows = selectedRowLists.get(Integer.parseInt(resolvedQueryTokens.get(i)));
							break;
						} else if (resolvedQueryTokens.size() == 2) {
							throw new Exception(
									"Unable to calculate " + previousToken + " " + currentToken + " " + nextToken);
						} else {
							i++;
						}
					}
				}

				if (i == resolvedQueryTokens.size() - 1) {
					if (resolvedQueryTokens.size() == 1) {
						selectedRows = selectedRowLists.get(Integer.parseInt(resolvedQueryTokens.get(i)));
						break;
					} else if (resolvedQueryTokens.size() == 2) {
						throw new Exception(
								"Unable to calculate " + previousToken + " " + currentToken + " " + nextToken);
					} else {
						i = 0;
					}
				} else {
					i++;
				}
			}
		} else {
			for (int i = 0; i < totalRows; i++)
				selectedRows.add(i);
		}
	}

	private static List<Integer> applyOROperationOnRowLists(List<Integer> list1, List<Integer> list2) throws Exception {
		list1.removeAll(list2);
		list1.addAll(list2);
		return list1;
	}

	private static List<Integer> applyANDOperationOnRowLists(List<Integer> list1, List<Integer> list2)
			throws Exception {
		list1.retainAll(list2);
		return list1;
	}

	private static List<Integer> getRowsForSimpleExpression(String columnName, String operator, String columnData)
			throws Exception {

		int columnIndex = columnIndexMap.get(columnName);
		String columnType = columnTypeMap.get(columnName);
		String dataType = isNumber(columnData) ? FLOAT
				: (isString(columnData) ? STRING : (booleanValues.contains(columnData) ? BOOLEAN : ""));
		List<Integer> rowList = new ArrayList<Integer>();

		if (columnType.equals(INT) || columnType.equals(FLOAT)) {
			if (!dataType.equals(FLOAT)) {
				throw new Exception(
						"Data type mismatch for expression '" + columnName + " " + operator + " " + columnData + "'");
			} else {
				float data = Float.parseFloat(columnData);
				int i = 0;
				while (i < totalRows) {
					if (operator.equals(EQ)) {
						if (Float.parseFloat(table.get(i).column[columnIndex].data) == data) {
							rowList.add(i);
						}
					}

					else if (operator.equals(NOTEQ) || operator.equals(NOTEQ2)) {
						if (Float.parseFloat(table.get(i).column[columnIndex].data) != data) {
							rowList.add(i);
						}
					}

					else if (operator.equals(LT)) {
						if (Float.parseFloat(table.get(i).column[columnIndex].data) < data) {
							rowList.add(i);
						}
					}

					else if (operator.equals(GT)) {
						if (Float.parseFloat(table.get(i).column[columnIndex].data) > data) {
							rowList.add(i);
						}
					}

					else if (operator.equals(LTEQ)) {
						if (Float.parseFloat(table.get(i).column[columnIndex].data) <= data) {
							rowList.add(i);
						}
					}

					else if (operator.equals(GTEQ)) {
						if (Float.parseFloat(table.get(i).column[columnIndex].data) >= data) {
							rowList.add(i);
						}
					}

					else {
						throw new Exception(
								"Unable to perform operation '" + columnName + " " + operator + " " + columnData + "'");
					}
					i++;
				}
			}
		}

		else if (columnType.equals(STRING)) {
			if (!dataType.equals(STRING)) {
				throw new Exception(
						"Data type mismatch for expression '" + columnName + " " + operator + " " + columnData + "'");
			} else {
				String data = columnData.split("'")[1];
				int i = 0;
				while (i < totalRows) {
					if (operator.equals(EQ)) {
						if (table.get(i).column[columnIndex].data == data) {
							rowList.add(i);
						}
					}

					else if (operator.equals(NOTEQ) || operator.equals(NOTEQ2)) {
						if (table.get(i).column[columnIndex].data != data) {
							rowList.add(i);
						}
					}

					else {
						throw new Exception(
								"Unable to perform operation '" + columnName + " " + operator + " " + columnData + "'");
					}
					i++;
				}
			}
		}

		else if (columnType.equals(BOOLEAN)) {
			if (!dataType.equals(BOOLEAN)) {
				throw new Exception(
						"Data type mismatch for expression '" + columnName + " " + operator + " " + columnData + "'");
			} else {
				Boolean data = Boolean.parseBoolean(columnData);
				int i = 0;
				while (i < totalRows) {
					if (operator.equals(EQ)) {
						if (Boolean.parseBoolean(table.get(i).column[columnIndex].data) == data) {
							rowList.add(i);
						}
					}

					else if (operator.equals(NOTEQ) || operator.equals(NOTEQ2)) {
						if (Boolean.parseBoolean(table.get(i).column[columnIndex].data) != data) {
							rowList.add(i);
						}
					}

					else {
						throw new Exception(
								"Unable to perform operation '" + columnName + " " + operator + " " + columnData + "'");
					}
					i++;
				}
			}
		}

		else {
			throw new Exception(
					"Data type match failed for expression '" + columnName + " " + operator + " " + columnData + "'");
		}
		return rowList;
	}

	private static void validateQuery() throws Exception {
		checkForUnrecongnizedTokens();
		validateMandatoryTokens();
		validateNonMandatoryTokens(indexOfTableNameToken);
	}

	private static void validateNonMandatoryTokens(int indexOfTableNameToken) throws Exception {

		validateSEMICOLON();
		validateWHERE();
	}

	private static void validateWHERE() throws Exception {

		int tokenIndex = indexOfTableNameToken + 1;
		String currentToken = "";
		String previousToken = "";
		int openBraces = 0;
		if (queryTokens.contains(WHERE)) {

			// WHERE token should come after table name and there should be only
			// one WHERE token in the query
			if (queryTokens.get(tokenIndex).equals(WHERE) && queryTokens.lastIndexOf(WHERE) == tokenIndex) {

				// Validating and Processing the conditional statements
				indexOfWHEREToken = tokenIndex;
				tokenIndex++;
				while (tokenIndex < queryTokens.size()) {
					currentToken = queryTokens.get(tokenIndex);
					if (currentToken.equals(LEFT_BRACE)) {
						openBraces++;
						if (isNumber(previousToken) || isString(previousToken)
								|| booleanValues.contains(previousToken)) {
							throw new Exception("Invalid conditional statement. Error at tokens '" + previousToken
									+ "' and '" + currentToken + "'");
						}
					} else if (currentToken.equals(RIGHT_BRACE)) {
						if (openBraces > 0) {
							openBraces--;
							if (previousToken.equals(LEFT_BRACE) || operators.contains(previousToken)
									|| logicalOperators.contains(previousToken) || columnNames.contains(previousToken)
									|| booleanValues.contains(previousToken)) {
								throw new Exception("Invalid conditional statement. Error at tokens '" + previousToken
										+ "' and '" + currentToken + "'");
							}
						} else {
							throw new Exception("Braces not properly closed");
						}
					} else if (columnNames.contains(currentToken)) {
						if (currentToken.equals(RIGHT_BRACE) || columnNames.contains(previousToken)
								|| operators.contains(previousToken) || isNumber(previousToken)
								|| isString(previousToken) || booleanValues.contains(previousToken)) {
							throw new Exception("Invalid conditional statement. Error at tokens '" + previousToken
									+ "' and '" + currentToken + "'");
						}
					} else if (operators.contains(currentToken)) {
						if (previousToken.equals(LEFT_BRACE) || previousToken.equals(RIGHT_BRACE)
								|| operators.contains(previousToken) || logicalOperators.contains(previousToken)
								|| isNumber(previousToken) || isString(previousToken)
								|| booleanValues.contains(previousToken)) {
							throw new Exception("Invalid conditional statement. Error at tokens '" + previousToken
									+ "' and '" + currentToken + "'");
						}
					} else if (logicalOperators.contains(currentToken)) {
						if (previousToken.equals(LEFT_BRACE) || previousToken.equals(RIGHT_BRACE)
								|| operators.contains(previousToken) || logicalOperators.contains(previousToken)) {
							throw new Exception("Invalid conditional statement. Error at tokens '" + previousToken
									+ "' and '" + currentToken + "'");
						}
					} else if (isNumber(currentToken) || isString(currentToken)) {
						if (previousToken.equals(LEFT_BRACE) || logicalOperators.contains(previousToken)
								|| isNumber(previousToken) || isString(previousToken)
								|| columnNames.contains(previousToken) || booleanValues.contains(previousToken)) {
							throw new Exception("Invalid conditional statement. Error at tokens '" + previousToken
									+ "' and '" + currentToken + "'");
						}
					} else if (currentToken.equals(SEMICOLON)) {
						if (previousToken.equals(LEFT_BRACE) || operators.contains(previousToken)
								|| logicalOperators.contains(previousToken) || columnNames.contains(previousToken)) {
							throw new Exception("Invalid conditional statement. Error at tokens '" + previousToken
									+ "' and '" + currentToken + "'");
						}
					} else if (booleanValues.contains(currentToken)) {
						if (previousToken.equals(LEFT_BRACE) || logicalOperators.contains(previousToken)
								|| isNumber(previousToken) || isString(previousToken)
								|| columnNames.contains(previousToken) || booleanValues.contains(previousToken)) {
							throw new Exception("Invalid conditional statement. Error at tokens '" + previousToken
									+ "' and '" + currentToken + "'");
						}
					} else {
						throw new Exception("Unsupported token '" + currentToken + "'");
					}
					previousToken = currentToken;
					tokenIndex++;
				}
				if (openBraces != 0)
					throw new Exception("Braces not properly closed");
			} else
				throw new Exception("'WHERE' keyword not found where expected");
		} else {

			// If there is no WHERE token, then either SEMICOLON or table name
			// token will be the final token
			if (tokenIndex == queryTokens.size() || queryTokens.get(tokenIndex).equals(SEMICOLON))
				return;
			else
				throw new Exception("Select statement is not properly ended");
		}
	}

	private static void validateSEMICOLON() throws Exception {

		// SEMI COLON should be the very last token as well as the only SEMI
		// COLON token in the query if there exists one
		if (queryTokens.contains(SEMICOLON)) {
			int lastTokenIndex = (queryTokens.size() - 1);
			if (queryTokens.indexOf(SEMICOLON) == lastTokenIndex
					&& queryTokens.lastIndexOf(SEMICOLON) == lastTokenIndex) {
			} else
				throw new Exception("';' keyword not found where expected");
		}
	}

	private static void validateMandatoryTokens() throws Exception {
		validateSELECT();
		validateSelectedColumns();
		validateFROM();
		validateTableName();
	}

	private static void validateTableName() throws Exception {

		int tokenIndex = indexOfFROMToken + 1;

		// TABLE NAME should be the first token after the FROM token
		// as well as the only TABLE NAME token in the query
		if (queryTokens.get(tokenIndex).equals(tableName) && queryTokens.lastIndexOf(tableName) == tokenIndex)
			indexOfTableNameToken = tokenIndex;
		else
			throw new Exception("'FROM' keyword not found where expected");
	}

	private static void validateFROM() throws Exception {

		int tokenIndex = indexOfLastSelectedColumnToken + 1;

		// FROM should be the first token after the last selected column token
		// as well as the only FROM token in the query
		if (queryTokens.get(tokenIndex).equals(FROM) && queryTokens.lastIndexOf(FROM) == tokenIndex)
			indexOfFROMToken = tokenIndex;
		else
			throw new Exception("'FROM' keyword not found where expected");
	}

	private static void validateSelectedColumns() throws Exception {

		int tokenIndex = 1;
		String currentToken = queryTokens.get(tokenIndex);
		String previousToken = "";

		if (currentToken.equals(ASTERISK)) {

			// If the selected column token is '*', then 'FROM' token must be
			// next
			// Also there must be only one '*' token in the query
			if (queryTokens.get(2).equals(FROM) && queryTokens.lastIndexOf(ASTERISK) == 1) {
				indexOfLastSelectedColumnToken = 1; // Set token index of last
													// selected column
			} else
				throw new Exception("Invalid select statement. Error at token '" + queryTokens.get(2) + "'");
		} else if (columnNames.contains(currentToken)) {

			// Column names should be separated by commas in between (if more
			// columns are selected)
			for (tokenIndex++, currentToken = queryTokens.get(tokenIndex); !currentToken.equals(FROM)
					&& tokenIndex < queryTokens.size(); tokenIndex++, currentToken = queryTokens.get(tokenIndex)) {
				if (currentToken.equals(",") && columnNames.contains(queryTokens.get(tokenIndex + 1))) {
					previousToken = currentToken;
					continue;
				} else if (columnNames.contains(currentToken) && previousToken.equals(",")) {
					previousToken = currentToken;
					continue;
				} else
					throw new Exception("Invalid select statement. Error at token '" + currentToken + "'");
			}
			indexOfLastSelectedColumnToken = tokenIndex - 1; // Set token index
																// of last
																// selected
																// column
		} else if (functions.contains(currentToken)) {

			// Functions should be of the form --> FUNCTION_NAME(COLUMN_NAME)
			// there should be only one function call and that should be before
			// 'FROM' token
			if (queryTokens.get(tokenIndex + 1).equals(LEFT_BRACE)
					&& columnNames.contains(queryTokens.get(tokenIndex + 2))
					&& queryTokens.get(tokenIndex + 3).equals(RIGHT_BRACE)
					&& queryTokens.get(tokenIndex + 4).equals(FROM)) {
				indexOfLastSelectedColumnToken = tokenIndex + 3; // Set token
																	// index of
																	// last
																	// selected
																	// column
			} else
				throw new Exception("Invalid select statement. Error at token '" + currentToken + "'");
		} else {
			throw new Exception("Invalid select statement. Error at token '" + currentToken + "'");
		}
	}

	private static void validateSELECT() throws Exception {

		// SELECT should be the first token as well as the only SELECT token in
		// the query
		if (queryTokens.get(0).equals(SELECT) && queryTokens.lastIndexOf(SELECT) == 0) {
		} else
			throw new Exception("'SELECT' keyword not found where expected");
	}

	private static void checkForUnrecongnizedTokens() throws Exception {
		for (String token : queryTokens) {

			// Matches all Predefined Tokens, Numeric Values and String
			// Values of type 'sample string' including the leading and trailing
			// single quotes
			if (isPredefinedToken(token) || isNumber(token) || isString(token))
				continue;
			else
				throw new Exception("Unrecoginzed token '" + token + "'");
		}
	}

	private static boolean isString(String token) throws Exception {
		if (token.matches("^['].+[']$"))
			return true;
		else
			return false;

	}

	private static boolean isNumber(String token) throws Exception {
		if (token.matches("^[0-9]+([,.][0-9]+)?$"))
			return true;
		else
			return false;

	}

	private static boolean isPredefinedToken(String token) throws Exception {
		if (booleanValues.contains(token) || tokens.contains(token) || symbols.contains(token)
				|| operators.contains(token) || logicalOperators.contains(token) || functions.contains(token)
				|| columnNames.contains(token) || tableName.equals(token))
			return true;
		else
			return false;
	}

	private static void tokenizeQuery() throws Exception {
		String currentToken = new String();
		String previousToken = new String();
		StringTokenizer stringTokens = new StringTokenizer(queryText, "!;<>=,()*/+- ", true);
		while (stringTokens.hasMoreTokens()) {
			currentToken = stringTokens.nextToken();
			previousToken = null;
			if ((currentToken.trim().length() > 0)) {

				// Matches String tokens
				if (currentToken.startsWith("'") && !currentToken.endsWith("'")) {
					String newToken;
					do {
						newToken = stringTokens.nextToken();
						currentToken += newToken;
					} while (!newToken.endsWith("'"));
				}

				// Matches Multi-Character Operator Tokens
				if (queryTokens.size() > 1) {
					previousToken = queryTokens.get(queryTokens.size() - 1);

					// Matches NOT EQUAL "!=" token
					if (previousToken.equals("!") && currentToken.equals("=")) {
						previousToken += currentToken;
						queryTokens.set(queryTokens.size() - 1, previousToken);
					}

					// Matches NOT EQUAL "<>" token
					else if (previousToken.equals("<") && currentToken.equals(">")) {
						previousToken += currentToken;
						queryTokens.set(queryTokens.size() - 1, previousToken);
					}

					// Matches LESS THAN OR EQUAL TO "<=" token
					else if (previousToken.equals("<") && currentToken.equals("=")) {
						previousToken += currentToken;
						queryTokens.set(queryTokens.size() - 1, previousToken);
					}

					// Matches GREATER THAN OR EQUAL TO ">=" token
					else if (previousToken.equals(">") && currentToken.equals("=")) {
						previousToken += currentToken;
						queryTokens.set(queryTokens.size() - 1, previousToken);
					}

					else {
						queryTokens.add(currentToken);
					}
				} else {
					queryTokens.add(currentToken);
				}
			}
		}
	}

	private static void setSymbols() {
		symbols.add(ASTERISK);
		symbols.add(LEFT_BRACE);
		symbols.add(RIGHT_BRACE);
		symbols.add(SEMICOLON);
		symbols.add(COMMA);
	}

	private static void setFunctions() {
		functions.add(UNIQ);
		functions.add(MAX);
		functions.add(MIN);
	}

	private static void setColumnTypeMap() {
		columnTypeMap.put(TITLE, STRING);
		columnTypeMap.put(BRAND, INT);
		columnTypeMap.put(STORE, INT);
		columnTypeMap.put(PRICE, FLOAT);
		columnTypeMap.put(INSTOCK, BOOLEAN);
	}

	private static void setColumnIndexMap() {
		columnIndexMap.put(TITLE, 0);
		columnIndexMap.put(BRAND, 1);
		columnIndexMap.put(STORE, 2);
		columnIndexMap.put(PRICE, 3);
		columnIndexMap.put(INSTOCK, 4);
	}

	private static void setTokens() {
		tokens.add(SELECT);
		tokens.add(FROM);
		tokens.add(WHERE);
	}

	private static void setOperators() {
		operators.add(ADD);
		operators.add(SUB);
		operators.add(MUL);
		operators.add(DIV);
		operators.add(EQ);
		operators.add(NOTEQ);
		operators.add(NOTEQ2);
		operators.add(LT);
		operators.add(GT);
		operators.add(LTEQ);
		operators.add(GTEQ);
	}

	private static void setLogicalOperators() {
		logicalOperators.add(AND);
		logicalOperators.add(OR);
		// logicalOperators.add(NOT);
	}

	private static void setDataTypes() {
		dataTypes.add(INT);
		dataTypes.add(STRING);
		dataTypes.add(FLOAT);
		dataTypes.add(BOOLEAN);
	}

	private static void setBooleanValues() {
		booleanValues.add(BOOL_TRUE);
		booleanValues.add(BOOL_FALSE);
	}

	private static void setColumnNames() {
		columnNames.add(TITLE);
		columnNames.add(BRAND);
		columnNames.add(STORE);
		columnNames.add(PRICE);
		columnNames.add(INSTOCK);
	}

	private static boolean loadDatabase(String filePath) {
		try {
			if (filePath != null) {
				String line = null;
				BufferedReader reader = new BufferedReader(new FileReader(filePath));

				// Verify Column Names
				if ((line = reader.readLine()) != null) {
					int counter = 0;
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(line);
					scanner.useDelimiter(",");

					while (scanner.hasNext()) {
						if (columnNames.contains(scanner.next().toUpperCase()))
							counter++;
						else
							throw new Exception("Unidentified column name found!");
					}

					scanner.close();
					if (counter != totalColumns)
						throw new Exception("Mismatch in header column count");
				}

				// Fetch Table Data From Database File
				int counter = 0;
				while ((line = reader.readLine()) != null) {
					Row newRow = new Row(totalColumns);
					Scanner scanner = new Scanner(line);
					scanner.useDelimiter(",");
					int columnIndex = 0;
					while (scanner.hasNext()) {
						String columnData = scanner.next();

						if (columnIndex < totalColumns) {
							newRow.column[columnIndex] = new Column();
							newRow.column[columnIndex].data = new String(columnData);
							newRow.column[columnIndex].type = columnTypeMap.get(columnNames.get(columnIndex));
						} else {
							scanner.close();
							reader.close();
							throw new Exception("Mismatch in data column count");
						}
						columnIndex++;
					}
					counter++;
					scanner.close();
					columnIndex = 0;
					table.add(newRow);
				}
				totalRows = counter;
				reader.close();
				return true;
			} else
				throw new Exception("Unable to read database file");
		} catch (Exception e) {
			System.out.println("Error: Failed to load database.\n" + e.getMessage());
			return false;
		}
	}

	private static String getDatabaseFile() {
		String filePath = null;
		// C:\\Users\\dijot\\Desktop\\SQL Query Engine
		// Docs\\sql_engine_dataset.csv
		System.out.print("Enter absolute database file path: ");
		try {
			filePath = (new BufferedReader(new InputStreamReader(System.in))).readLine();
			File databaseFile = new File(filePath);
			if (!databaseFile.exists() || databaseFile.isDirectory())
				throw new IOException("Error: Failed to retrieve database file contents");
		} catch (Exception e) {
			filePath = null;
		}
		return filePath;
	}
}