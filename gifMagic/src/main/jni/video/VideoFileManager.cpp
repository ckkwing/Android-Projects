/*
 * VideoFileManager.cpp
 *
 *  Created on: 2011-9-13
 *      Author: gshe
 */

#include "VideoFileManager.h"
#include <jni.h>
#include <stdio.h>
#include "VideoFile.h"
#define RECORD_MAX_NUMBER 100

int VideoFileManager::sIndexStarted = 0;

VideoFileManager::VideoFileManager() {
	mpMap = NULL;
}

VideoFileManager::~VideoFileManager() {
	releaseMap();
}

void VideoFileManager::releaseMap() {
	if (mpMap != NULL) {
		__tagVideoFilesMap *p = mpMap;

		for(int i = 0; i<RECORD_MAX_NUMBER; i++)
		{
			if (p->iUsageFlag != 0 && p->pFile != NULL)
			{
				delete p->pFile;
			}
			p++;
		}
		delete mpMap;
	}
}

bool VideoFileManager::init() {
	if (mpMap) {
		delete mpMap;
	}mpMap = new __tagVideoFilesMap[RECORD_MAX_NUMBER];
	if (mpMap == NULL) {
		return false;
	}
	memset(mpMap, 0, sizeof(__tagVideoFilesMap)*RECORD_MAX_NUMBER);
	return true;
}

int VideoFileManager::putVideoFile(VideoFile* pFile) {
	int iPos = -1;
	__tagVideoFilesMap *p = mpMap;
	for(int i = 0; i<RECORD_MAX_NUMBER; i++)
	{
		if (p->iUsageFlag ==0)
		{
			p->pFile = pFile;
			p->index = sIndexStarted++;
			p->iUsageFlag = 1;
			iPos = p->index;
			break;
		}
		p++;
	}

	return iPos;
}

VideoFile* VideoFileManager::getVideoFile(int index) {
	VideoFile* pFile = NULL;
	__tagVideoFilesMap *p = mpMap;
	for(int i = 0; i<RECORD_MAX_NUMBER; i++)
	{
		if (p->iUsageFlag != 0 && p->index == index)
		{
			pFile = p->pFile;
			break;
		}
		p++;
	}
	return pFile;
}

void VideoFileManager::removeVideoFile(int index) {
	__tagVideoFilesMap *p = mpMap;
	for(int i = 0; i<RECORD_MAX_NUMBER; i++)
	{
		if (p->iUsageFlag != 0 && p->index == index)
		{
			delete (p->pFile);
			p->index = -1;
			p->iUsageFlag = 0;
			p->pFile = NULL;
			break;
		}
		p++;
	}
}

