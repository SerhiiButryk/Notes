# SkyTech 2019
# scommon library
#

set(INC_PROJECT_INTERFACE_PATH "../interface")

add_library(
    sservices SHARED
    iml_children_process_operator.cpp
    iml_children_process_operator.h
    impl_channel_service.cpp
    impl_channel_service.h
    impl_exit_service.cpp
    impl_exit_service.h
    impl_resource_service.cpp
    impl_resource_service.h
    ${INC_PROJECT_INTERFACE_PATH}/service_channel.h
    ${INC_PROJECT_INTERFACE_PATH}/service_exit.h
    ${INC_PROJECT_INTERFACE_PATH}/service_resource.h
    ${INC_PROJECT_INTERFACE_PATH}/service_waite.h
)           

target_include_directories(sservices PUBLIC "${CMAKE_CURRENT_SOURCE_DIR/../..}") 