//
// Created by MasterChan on 2022-5-30.
//

#include "MPFile.h"
#include <fcntl.h>
#include <jni.h>
#include "Log.h"
#include <sys/stat.h>

MPFile::~MPFile()
{
    munmap(ptr_cache, cache_size);
    close(fd_cache);
}

long MPFile::init(const string& cache_path, const string& log_path, size_t cache_size)
{
    path_cache = cache_path;
    path_log = log_path;
    this->cache_size = cache_size;
    header_size = cache_head_size_byte_count + path_cache.size();
    //打开缓存文件
    fd_cache = open(path_cache.data(), O_CREAT | O_RDWR, S_IRWXU);
    //设置缓存文件大小
    ftruncate(fd_cache, cache_size);
    //开启映射
    ptr_cache = static_cast<char*>(mmap(nullptr, cache_size, PROT_READ | PROT_WRITE, MAP_SHARED, fd_cache, 0));
    if (ptr_cache == MAP_FAILED) {
        ptr_cache = nullptr;
        return -1;
    }
    //判断缓存中是否有脏数据
    const string& dirty_data = read_cache(header_size);
    if (!dirty_data.empty()) {
        write_content_size = dirty_data.size();
        flush(header_size);
    }
    //清空cache，并写入log_path
    memset(ptr_cache, '\0', cache_size);
    auto header = get_cache_header();
    write_cache(header);
    ptr_cache += header.size();
    return 0;
}

void MPFile::write_cache(const string& text, bool append)
{
    if (!check_cache_ptr()) {
        return;
    }
    //如果是追加数据，指针位移
    if (append) {
        ptr_cache += write_content_size;
    }
    memcpy(ptr_cache, text.data(), text.size());

    //记录指针位移的位置
    write_content_size += text.size();
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

string MPFile::read_cache(size_t offset)
{
    if (!check_cache_ptr()) {
        return "";
    }
    char buffer[cache_size];
    memcpy(buffer, ptr_cache + offset, cache_size);
    return buffer;
}

bool MPFile::check_cache_ptr()
{
    return ptr_cache != nullptr;
}

size_t MPFile::writeable_size()
{
    return cache_size - write_content_size;
}

void MPFile::flush(size_t offset)
{
    START_TIMER
    auto file = fopen(path_log.data(), "ab+");
    fwrite(ptr_cache + offset, write_content_size, 1, file);
    fflush(file);
    fclose(file);
    memset(ptr_cache + offset, '\0', write_content_size);
    write_content_size = 0;
    END_TIMER("写出文件到磁盘：")
}

void MPFile::release()
{
    flush();
    delete this;
}