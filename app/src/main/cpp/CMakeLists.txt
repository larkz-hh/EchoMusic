cmake_minimum_required(VERSION 3.22.1)
project(echoeq CXX)

add_library(echoeq SHARED native_eq.cpp)

find_library(log-lib log)
target_link_libraries(echoeq ${log-lib})
