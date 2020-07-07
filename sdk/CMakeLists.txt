# --- SkyTech 2019 --- #

#  --- Top dir ---  #

cmake_minimum_required(VERSION 3.14)

set(INC_PROJECT_COMMON_PATH ".")
set(INC_PROJECT_SERVER_PATH "server/")

set(CMAKE_CXX_FLAGS  "${CMAKE_CXX_FLAGS} -pthread -std=c++14")
set(CMAKE_EXE_LINKER_FLAGS  "${CMAKE_EXE_LINKER_FLAGS}")

# target libraries
add_subdirectory(${INC_PROJECT_COMMON_PATH}/common)
add_subdirectory(${INC_PROJECT_COMMON_PATH}/net)
# add_subdirectory(concurrency)
add_subdirectory(${INC_PROJECT_SERVER_PATH}/net)
add_subdirectory(${INC_PROJECT_SERVER_PATH}/common)
add_subdirectory(${INC_PROJECT_SERVER_PATH}/core/impl_services)

# target executable
add_subdirectory(${INC_PROJECT_SERVER_PATH}/core)