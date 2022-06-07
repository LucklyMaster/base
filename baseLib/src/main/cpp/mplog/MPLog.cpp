//
// Created by MasterChan on 2022-5-30.
//

#include "MPLog.h"
#include <fcntl.h>
#include <jni.h>
#include "../common/Log.h"
#include <sys/stat.h>

MPLog::~MPLog() {
	munmap(ptr_cache, size_cache);
	close(fd_cache);
}

long MPLog::init(const string &cache_path, const string &log_path, size_t cache_size) {
	path_cache = cache_path;
	path_log = log_path;
	size_cache = cache_size;
	header_size = cache_head_size_byte_count + path_cache.size();
	//打开缓存文件
	fd_cache = open(path_cache.data(), O_CREAT | O_RDWR, S_IRWXU);
	//设置缓存文件大小
	ftruncate(fd_cache, cache_size);
	//开启映射
	ptr_cache = static_cast<char *>(mmap(
			nullptr,
			cache_size,
			PROT_READ | PROT_WRITE,
			MAP_SHARED,
			fd_cache,
			0
	));
	if (ptr_cache == MAP_FAILED) {
		ptr_cache = nullptr;
		return -10001;
	}
	//判断缓存中是否有脏数据
	const string &dirty_data = read_cache(header_size);
	if (!dirty_data.empty()) {
		flush(dirty_data);
	}
	//清空cache，并写入log_path
	memset(ptr_cache, '\0', cache_size);
	auto header = get_cache_header();
	memcpy(ptr_cache, header.data(), header.size());
	ptr_cache += header_size;
	return 0;
}

void MPLog::write_cache(const string &text, bool append) {
	if (!check_cache_ptr()) {
		return;
	}
	//如果是追加数据，指针位移
	if (append) {
		memcpy(ptr_cache + write_content_size, text.data(), text.size());
	} else {
		memcpy(ptr_cache, text.data(), text.size());
	}

	//记录指针位移的位置
	write_content_size += text.size();
}

string MPLog::get_cache_header() {
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

string MPLog::read_cache(size_t offset) {
	if (!check_cache_ptr()) {
		return "";
	}
	char buffer[size_cache];
	try {
		memcpy(buffer, ptr_cache + offset, size_cache);
	}
	catch (...) {
	}
	return buffer;
}

bool MPLog::check_cache_ptr() {
	return ptr_cache != nullptr;
}

size_t MPLog::writeable_size() {
	return size_cache - write_content_size;
}

void MPLog::flush() {
	START_TIMER
	auto file = fopen(path_log.data(), "ab+");
	fwrite(ptr_cache, write_content_size, 1, file);
	fflush(file);
	fclose(file);
	memset(ptr_cache, '\0', write_content_size);
	write_content_size = 0;
	END_TIMER("写出文件到磁盘")
}

void MPLog::flush(const string &content) {
	START_TIMER
	auto file = fopen(path_log.data(), "ab+");
	fwrite(content.data(), content.size(), 1, file);
	fflush(file);
	fclose(file);
	memset(ptr_cache, '\0', content.size());
	END_TIMER("写出文件到磁盘")
}

void MPLog::release() {
	flush();
	delete this;
}
