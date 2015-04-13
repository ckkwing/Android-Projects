package com.jecfbagsx.android.utils;

import com.jecfbagsx.android.data.GenerateData;

public class DataManger {

	private static DataManger m_instance = null;
	private GenerateData m_generateData = null;

	public static synchronized DataManger getInstance() {
		if (m_instance == null) {
			m_instance = new DataManger();
		}
		return m_instance;
	}
	
	public void init()
	{
		m_generateData = new GenerateData();
	}
	
	public void uninit()
	{
		
	}
	
	public void reset()
	{
		m_generateData = null;
	}
	
	public GenerateData getGenerateData()
	{
		return m_generateData;
	}
	
	public void setGenerateData(GenerateData data)
	{
		m_generateData = null;
		m_generateData = data;
	}
	
}
