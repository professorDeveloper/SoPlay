# Minimum CMake version
cmake_minimum_required(VERSION 3.10)

# Match your Android Studio module name
project(native-lib)

# Tell CMake to build a shared library from your native source
add_library(
        native-lib                # library name
        SHARED                    # build type
        src/main/cpp/native-lib.cpp  # path to your .cpp
)

# Link against the Android log library
find_library(
        log-lib                   # variable
        log                       # name of the NDK log library
)

target_link_libraries(
        native-lib
        ${log-lib}
)
