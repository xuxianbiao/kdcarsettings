LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := SHARED_LIBRARIES
LOCAL_MODULE_SUFFIX := .so
LOCAL_MODULE := libebookdroid
LOCAL_SRC_FILES := libs/armeabi/$(LOCAL_MODULE).so
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)

LOCAL_STATIC_JAVA_LIBRARIES := eventbus

LOCAL_SRC_FILES := \
    $(call all-java-files-under) \
	src/com/driverlayer/kdos_driverServer/IECarDriver.aidl \
	src/com/driverlayer/kdos_driverServer/BlueDriver.aidl

LOCAL_PACKAGE_NAME := KdCarSetting
LOCAL_CERTIFICATE := platform
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := eventbus:libs/eventbus.jar

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))
