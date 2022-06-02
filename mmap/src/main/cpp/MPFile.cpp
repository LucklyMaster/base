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
    header_size = cache_head_size_byte_count + path_cache.size();
    //打开缓存文件
    fd_cache = open(path_cache.data(), O_CREAT | O_RDWR, S_IRWXU);
    //设置缓存文件大小
    ftruncate(fd_cache, page_size);
    //开启映射
    ptr_cache = static_cast<char*>(mmap(nullptr, page_size, PROT_READ | PROT_WRITE, MAP_SHARED, fd_cache, 0));
    if (ptr_cache == MAP_FAILED) {
        ptr_cache = nullptr;
        return -1;
    }
    //判断缓存中是否有脏数据
    const string& dirty_data = readCache(header_size);
    LOG_D("缓存数据长度：%d", dirty_data.size());
    if (!dirty_data.empty()) {
        LOG_D("脏数据 = %s", dirty_data.data());
        flush2File(dirty_data);
    }
    //清空cache，并写入log_path
    memset(ptr_cache, '\0', page_size);
    writeCache(get_cache_header());
    writeCache("测试", true);

    return 0;
}

void MPFile::writeCache(const string& text, bool append)
{
    if (!check_cache_ptr()) {
        return;
    }
    START_TIMER
    //如果是追加数据，指针位移
    if (append) {
        ptr_cache += ptr_cache_append_offset;
    }
    memcpy(ptr_cache, text.data(), text.size());
    //记录指针位移的位置
    ptr_cache_append_offset += text.size();
    END_TIMER("写入缓存")
}

string MPFile::get_cache_header()
{
    auto path_log_size = path_log.size();
    string log_byte_size = to_string(path_log_size);
    if (log_byte_size.size() < cache_head_size_byte_count) {
        //不足的长度部分，用空格代替
        for (int i = 0; i < cache_head_size_byte_count - log_byte_size.size(); ++i) {
            log_byte_size += " ";
        }
    }
    return log_byte_size + path_log;
}

string MPFile::readCache(size_t offset)
{
    if (!check_cache_ptr()) {
        return "";
    }
    char buffer[page_size];
    memcpy(buffer, ptr_cache + offset, page_size);
    return buffer;
}

bool MPFile::check_cache_ptr()
{
    return ptr_cache != nullptr;
}

void MPFile::flush2File(const string& text)
{

}
