#include "jni_part.h"
#include "bmpfmt.h"
#include <android/log.h>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>
#include <string>
#include <stdio.h>
#include "main.cpp"
#include <stdio.h>
#include <stdlib.h>
using namespace std;
using namespace cv;


#define ANDROID_LOG_VERBOSE ANDROID_LOG_DEBUG
#define LOG_TAG "CVJNI"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGV(...) __android_log_print(ANDROID_LOG_SILENT, LOG_TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

JNIEXPORT jint JNICALL Java_org_projectakshara_android_finman_OpenCVActivity_processBill (
   JNIEnv *env,
   jobject obj,
   //jobject info,      // Bill object instantiation
   jstring jpath
){
	LOGD("processBill Entered");

	struct billDetail bill;
	jclass clazz;
	jfieldID fid;
	jmethodID mid;
	jobject info = obj;
	const char * path = new char[100];
	path = env->GetStringUTFChars(jpath,0);

	getBillDetails(&bill,path);
	LOGV("TEST1");
	LOGV(path);

	//clazz = env->GetObjectClass(info);
	clazz = env->FindClass("org/projectakshara/android/finman/OpenCVActivity");
	LOGV("TMP0");
	if (0 == clazz)	{
		LOGV("GetObjectClass returned 0\n");
		LOGE("No Class Returned\n");
		return(-1);
	}
	/*
	clazz = env->FindClass("org/projectakshara/android/finman/Bill");
	LOGV("asdf");
	mid = env->GetMethodID ( clazz, "init","(I)V");  //(put init inbetween greater than, less than brackets)
	LOGV("dda");
	jobject info = env->NewObject(clazz, mid, (jint) 2);*/
	LOGV("TEST2");


	fid = env->GetFieldID(clazz,"nItems","I");
	LOGV("dsfds");
	// This next line is where the power is hidden. Directly change even private fields within java objects. Nasty!
	int a = env->GetIntField(info,fid);
	char b[50];
	myitoa(a,b,40);
	LOGV(b);
	env->SetIntField(info,fid,bill.nItems);
	LOGV("TEST4");

	jstring name = env->NewStringUTF(bill.name);
	if (name == NULL)	{
	  clazz = env->FindClass("java/lang/OutOfMemoryError");
	  env->ThrowNew(clazz,NULL);
	  return (-1);
	}
	fid = env->GetFieldID(clazz,"name","Ljava/lang/String;");
	env->SetObjectField(info,fid,name);
	LOGV("TEST3");

	jobjectArray jItemNames;
	jstring jItemName;
	jItemNames = (jobjectArray) env->NewObjectArray(5,
											 env->FindClass("java/lang/String"),
											 env->NewStringUTF(""));
	for(int i=0;i<bill.nItems;i++) {
	        env->SetObjectArrayElement(jItemNames,i,env->NewStringUTF(bill.itemName[i]));
	}
	fid = env->GetFieldID(clazz,"itemName","[Ljava/lang/String;");
	env->SetObjectField(info,fid,jItemNames);
	LOGV("TEST5");

	jintArray jItemPrices;
	jItemPrices = env->NewIntArray(bill.nItems);
	if (jItemPrices == NULL) {
		return (-1); // out of memory error thrown
	}
	env->SetIntArrayRegion(jItemPrices, 0, bill.nItems, bill.itemPrice);
	fid = env->GetFieldID(clazz,"itemPrice","[I");
	env->SetObjectField(info,fid,jItemPrices);
	LOGV("TEST5");

	fid = env->GetFieldID(clazz,"total","I");
	env->SetIntField(info,fid,bill.total);
	LOGV("TEST6");

	fid = env->GetFieldID(clazz,"imgPath","Ljava/lang/String;");
	env->SetObjectField(info,fid,jpath);
	LOGV("TEST7");

	return 0;
}
