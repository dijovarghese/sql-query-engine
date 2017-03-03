package com.dijo.projects;

import java.util.ArrayList;
import java.util.List;

public class Record
{
	//title, brand, store, price, inStock
	
	/*private String title;
	private int brand;
	private int store;
	private float price;
	private boolean inStock;*/
	
	/*private final String INT = "INT";
	private final String STRING = "STRING";
	private final String FLOAT = "FLOAT";
	private final String BOOLEAN = "BOOLEAN";
	
	private final String TITLE = "TITLE";
	private final String BRAND = "BRAND";
	private final String STORE = "STORE";
	private final String PRICE = "PRICE";
	private final String INSTOCK = "INSTOCK";*/
	
	List<Column> column;
	
	
	public Record(new ArrayList<Column>())
	{
		column = new Column[columnCount];
		
		/*column[0].value = null;
		column[0].dataType = STRING;
		column[0].columnName = TITLE;		
		
		column[1].value = null;
		column[1].dataType = INT;
		column[1].columnName = BRAND;		
		
		column[2].value = null;
		column[2].dataType = INT;
		column[2].columnName = STORE;		
		
		column[3].value = null;
		column[3].dataType = FLOAT;
		column[3].columnName = PRICE;		
		
		column[4].value = null;
		column[4].dataType = BOOLEAN;
		column[4].columnName = INSTOCK;*/		
	}
	
	/*class Title
	{
		String value;
		final String columnName = "TITLE";
		
		String getValue() { return value; }
		void setValue(String value) { this.value = value; }
		String getColumnName() { return columnName; }
	}

	class Brand
	{
		int value;
		final String columnName = "BRAND";

		int getValue() { return value; }
		void setValue(int value) { this.value = value; }
		String getColumnName() { return columnName; }
	}

	class Store
	{
		int value;
		final String columnName = "STORE";

		int getValue() { return value; }
		void setValue(int value) { this.value = value; }
		String getColumnName() { return columnName; }
	}

	class Price
	{
		float value;
		final String columnName = "PRICE";

		float getValue() { return value; }
		void setValue(int value) { this.value = value; }
		String getColumnName() { return columnName; }
	}

	class InStock
	{
		boolean value;
		final String columnName = "INSTOCK";

		boolean getValue() { return value; }
		void setValue(boolean value) { this.value = value; }
		String getColumnName() { return columnName; }
	}*/
	
	/*Title title = new Title();
	Brand brand = new Brand();
	Store store = new Store();
	Price price = new Price();
	InStock inStock = new InStock();*/
	
	/*public void setTitle(String title)
	{
		
	}*/

	/*public int getBrand()
	{
		return this.brand.value;
	}

	public void setBrand(int brand)
	{
		this.brand.value = brand;
	}

	public int getStore()
	{
		return store.value;
	}

	public void setStore(int store)
	{
		this.store.value = store;
	}

	public float getPrice()
	{
		return price.value;
	}

	public void setPrice(float price)
	{
		this.price.value = price;
	}
	
	public boolean getInStock()
	{
		return inStock.value;
	}

	public void setInStock(boolean inStock)
	{
		this.inStock.value = inStock;
	}*/
	
	@Override
	public String toString()
	{
		//return title.value + "\t" + brand.value + "\t" + store.value + "\t" + price.value + "\t" + inStock.value + "\n";
		return "Sample Table Record";
	}
}