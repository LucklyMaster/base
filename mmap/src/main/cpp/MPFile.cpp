//
// Created by MasterChan on 2022-5-30.
//

#include "MPFile.h"
#include <fcntl.h>
#include <jni.h>
#include "Log.h"
#include <sys/stat.h>

//long MPFile::open(const char* path)
//{
//    fd = open(path, O_CREAT | O_RDWR, S_IRWXU);
//    ftruncate(fd, page_size);
//    ptr = static_cast<char*>(mmap(nullptr, page_size, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0));
//    if (ptr == MAP_FAILED) {
//        return -1;
//    }
//    return 0;
//}

long MPFile::init(string& cache_path, string& log_path)
{
    path_cache = cache_path;
    path_log = log_path;
    //打开缓存文件
    fd_cache = open(path_cache.data(), O_CREAT | O_RDWR, S_IRWXU);
    //设置缓存文件大小
    ftruncate(fd_cache, page_size);
    //开启映射
    ptr_cache = static_cast<char*>(mmap(nullptr, page_size, PROT_READ | PROT_WRITE, MAP_SHARED, fd_cache, 0));
    //判断缓存中是否有脏数据
//    ptr_cache

    return 0;
}

void MPFile::write(string& text, bool append)
{
    std::string path = "sdcard/0/user/test.txt";
    path += "\n";

    memcpy(ptr_cache, path.data(), path.size());
    memcpy(ptr_cache + path.size(), path.data(), path.size());

//    struct stat fileInfo{};
//    fstat(fd_cache, &fileInfo);
//    auto buffered_size = fileInfo.st_size;
//    string buff[page_size];
    int len = lseek(fd_cache, 0, SEEK_END);
    char buff[len];
    memcpy(buff, ptr_cache, len);
    LOG_D("size = %s", buff);
    string a = buff;
    LOG_D("size = %d", a.size());
//    std::string content = strlen(path);
//    memcpy(ptr, to_string(a).data(), 0);
//    string head = to_string(22) + " ";
//    memcpy(ptr, head.data(), 3);
//    memcpy(ptr + 3, path.data(), path.size());
//
//    char buff[page_size];
//    memcpy(buff, ptr, page_size);
//    LOG_D("data = %s", buff);
}

int MPFile::get_cache_head_size()
{
    auto path_log_size = path_log.size();
    string log_byte_size = to_string(path_log_size);
    string byte_count_size = to_string(cache_head_size_byte_count);
    if (log_byte_size.size() < byte_count_size.size()) {
        //不足的长度部分，用空格代替
        for (int i = 0; i < byte_count_size.size() - log_byte_size.size(); ++i) {
            log_byte_size += " ";
        }
    }
    return 0;
}