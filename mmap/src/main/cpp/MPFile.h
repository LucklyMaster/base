//
// Created by MasterChan on 2022-5-30.
//

#ifndef MYBASE_MMAP_SRC_MAIN_CPP_MPFILE_H_
#define MYBASE_MMAP_SRC_MAIN_CPP_MPFILE_H_

#include <string>
#include <sys/mman.h>
#include <unistd.h>

using namespace std;

class MPFile {

 private:
    string path_cache;
    string path_log;
    int fd_cache;
    char* ptr_cache;
    int page_size = getpagesize();
    //定义cache文件开头用于标识path_log存储路径长度的字节数量
    const int cache_head_size_byte_count = 3;

    int get_cache_head_size();

 public:
    long init(string& cache_path, string& log_path);

    void write(string& text, bool append);
};


#endif //MYBASE_MMAP_SRC_MAIN_CPP_MPFILE_H_
