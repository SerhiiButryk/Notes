# SkyTech 2019
# snet library
#

add_library(
    snet SHARED
    connection_receiver.cpp
    connection_receiver.h
    connection_state.cpp
    connection_state.h
    socket_handler.cpp
    socket_handler.h
    socket.cpp
    socket.h
)

target_include_directories(snet PUBLIC "${CMAKE_CURRENT_SOURCE_DIR/../..}")            