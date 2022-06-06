cmake_minimum_required(VERSION 3.18.1)
project("mmapLog")

set(mpLogDir ${CMAKE_SOURCE_DIR}/mplog)
set(libPath ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})

include_directories(${mpLogDir})

file(GLOB mpLogSrc ${mpLogDir}/*.cpp ${mpLogDir}/*.c ${mpLogDir}/*.hpp ${mpLogDir}/*.h)
add_library(mpLog SHARED ${mpLogSrc})

find_library(log-lib log)
find_library(android-lib android)
target_link_libraries(mpLog ${android-lib} ${log-lib})