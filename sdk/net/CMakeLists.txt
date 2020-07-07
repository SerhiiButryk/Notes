# SkyTech 2019
# net library
#

add_library(
    net SHARED
    constants.cpp
    constants.h
    http_client.h
    http_client.cpp
    json_parser.cpp
    json_parser.h
)
            
target_include_directories(net PUBLIC "${CMAKE_CURRENT_SOURCE_DIR/..}")            