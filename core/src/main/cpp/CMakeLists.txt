cmake_minimum_required(VERSION 4.1.2)

project("bugitkeys")

add_library(
   bugitkeys
   SHARED
   native-lib.cpp
)

find_library(
   log-lib
   log
)

target_link_libraries(
   bugitkeys
   ${log-lib}
)