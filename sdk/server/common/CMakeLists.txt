# SkyTech 2019
# scommon library
#

add_library(
    scommon SHARED
    constants.cpp
    constants.h
)           

target_include_directories(scommon PUBLIC "${CMAKE_CURRENT_SOURCE_DIR/../..}") 