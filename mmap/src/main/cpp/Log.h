
#ifndef MYBASE_MMAP_SRC_MAIN_CPP_LOG_H_
#define MYBASE_MMAP_SRC_MAIN_CPP_LOG_H_

#define __DEBUG
#ifdef __DEBUG

#include <android/log.h>
#include <mutex>
#include <string>

#define LOG_E(...) __android_log_print(ANDROID_LOG_ERROR,"MPLog",__VA_ARGS__);
#define LOG_D(...) __android_log_print(ANDROID_LOG_DEBUG,"MPLog",__VA_ARGS__);

class Timer {
    std::chrono::system_clock::time_point _start;
    std::string name;

 public:
    Timer(){}

    void start()
    {
        _start = std::chrono::system_clock::now();
    }

    void end(std::string text)
    {
        std::chrono::system_clock::time_point end = std::chrono::system_clock::now();
        std::chrono::duration<double> diff = end - _start;
        LOG_D("%s, used time = %f ms\n", text.c_str(), diff.count() * 1000);
    }

    ~Timer(){}
};

static Timer gtm;
#define START_TIMER gtm.start();
#define END_TIMER(str) gtm.end(str);
#else
#define  LOG_E(...);
#define  LOG_D(...);
#define START_TIMER ;
#define END_TIMER(str) ;
#endif


#endif //MYBASE_MMAP_SRC_MAIN_CPP_LOG_H_
