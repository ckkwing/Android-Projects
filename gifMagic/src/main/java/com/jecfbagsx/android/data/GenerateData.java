package com.jecfbagsx.android.data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenerateData implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	
	private float[] m_colorMatrixArray = new float [] {  
	        1,0,0,0,0,  
	        0,1,0,0,0,  
	        0,0,1,0,0,  
	        0,0,0,1,0, 
	        0,0,0,0,1
	    }; 
	
	private Map<String, Float> m_degreeMap = new LinkedHashMap<String, Float>();
	
	public GenerateData()
	{
	}
	
	public GenerateData(float[] colorMatrixArray)
	{
		this(); 
        copyMatrix(colorMatrixArray);
	}
	
	public void addItem(String url, float degree)
	{
		m_degreeMap.put(url, degree);
	}
	
	public void clear()
	{
		m_degreeMap.clear();
	}
	
	public Map<String, Float> getDegreeMap() {
		return m_degreeMap;
	}
	
	public float[] getColorMatrixArray()
	{
		return m_colorMatrixArray;
	}
	
	public void setColorMatrixArray(float[] colorMatrixArray) {
		copyMatrix(colorMatrixArray);
	}
	
	private void copyMatrix(float[] matrix)
    {  
        int length = m_colorMatrixArray.length;  
        for (int i = 0; i < length; i++)  
        {  
        	m_colorMatrixArray[i] = matrix[i];  
        }  
    }  
}
