# SkyTech 2019
# concurrency library
#

add_library(concurrency SHARED
            native_executor.h)

target_include_directories(concurrency PUBLIC "${CMAKE_CURRENT_SOURCE_DIR/..}")