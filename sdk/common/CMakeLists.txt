# SkyTech 2019
# common library
#

add_library(
    common SHARED
    base64.cpp
    base64.h
    connection_observer.h
    exception.cpp
    exception.h
    log.cpp
    log.h
    observable_subject.h
)

# PUBLIC means that everyone who is consuming library have to add includes 
# So everyone who adds this lib also inherits includes 
target_include_directories(common PUBLIC "${CMAKE_CURRENT_SOURCE_DIR/..}")