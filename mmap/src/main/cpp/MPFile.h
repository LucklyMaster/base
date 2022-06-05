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
    size_t cache_size;
    int fd_cache;
    char* ptr_cache;
    //定义cache文件开头用于标识path_log存储路径长度的字节数量
    const int cache_head_size_byte_count = 3;
    size_t header_size;
    size_t write_content_size;

    ~MPFile();

    bool check_cache_ptr();

    string get_cache_header();

 public:
    long init(const string& cache_path, const string& log_path, size_t cache_size);

    void write_cache(const string& text, bool append = false);

    string read_cache(size_t offset = 0);

    size_t writeable_size();

    void flush(size_t offset = 0);

    void release();
};


#endif //MYBASE_MMAP_SRC_MAIN_CPP_MPFILE_H_
