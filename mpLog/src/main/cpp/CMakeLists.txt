cmake_minimum_required(VERSION 3.18.1)
project("mmapLog")

set(sourceSrc ${CMAKE_SOURCE_DIR})
set(libPath ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})

include_directories(${sourceSrc})

file(GLOB srcs ${sourceSrc}/*.cpp ${sourceSrc}/*.c)
file(GLOB hdrs ${sourceSrc}/*.hpp ${sourceSrc}/*.h)
add_library(mcLib SHARED ${srcs} ${hdrs})

find_library(log-lib log)
find_library(android-lib android)
target_link_libraries(mcLib ${android-lib} ${log-lib})