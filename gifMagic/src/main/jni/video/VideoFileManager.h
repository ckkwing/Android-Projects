/*
 * VideoFileManager.h
 *
 *  Created on: 2011-9-13
 *      Author: gshe
 */

#ifndef VIDEOFILEMANAGER_H_
#define VIDEOFILEMANAGER_H_

class VideoFile;

typedef struct __tagVideoFilesMap
{
	int iUsageFlag;
	int index;
	VideoFile* pFile;
};

class VideoFileManager {
public:
	VideoFileManager();
	virtual ~VideoFileManager();
	bool init();
	int putVideoFile(VideoFile* pFile);
	VideoFile* getVideoFile(int index);
	void removeVideoFile(int index);


private:
	void releaseMap();

private:
	static int sIndexStarted;
	__tagVideoFilesMap* mpMap;
};

#endif /* VIDEOFILEMANAGER_H_ */
