LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)

# OpenCV TODO: Turn these modules on for static linking during development
#OPENCV_CAMERA_MODULES:=off
#OPENCV_INSTALL_MODULES:=off
include /home/mintuhouse/a1_important/OpenCV-2.4.2-android-sdk/sdk/native/jni/OpenCV.mk
 
LOCAL_MODULE    := jni_part
LOCAL_SRC_FILES := jni_part.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS +=  -llog -ldl
 
include $(BUILD_SHARED_LIBRARY)