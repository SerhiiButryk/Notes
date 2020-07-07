# SkyTech 2019
# exec entry
#

add_executable(
    server 
    core.cpp
    core_receiver.cpp
    core_receiver.h 
    registrat.cpp
    registrat.h 
    server_manager.cpp 
    server_manager.h
)

target_link_libraries(server PRIVATE common)
target_link_libraries(server PRIVATE sservices)
target_link_libraries(server PRIVATE net)
# target_link_libraries(server PRIVATE concurrency)
target_link_libraries(server PRIVATE scommon)
target_link_libraries(server PRIVATE snet)