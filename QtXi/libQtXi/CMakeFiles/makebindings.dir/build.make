# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.4

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/local/Cellar/cmake/3.4.3/bin/cmake

# The command to remove a file.
RM = /usr/local/Cellar/cmake/3.4.3/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi

# Include any dependencies generated for this target.
include CMakeFiles/makebindings.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/makebindings.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/makebindings.dir/flags.make

CMakeFiles/makebindings.dir/generator/types.cpp.o: CMakeFiles/makebindings.dir/flags.make
CMakeFiles/makebindings.dir/generator/types.cpp.o: generator/types.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/makebindings.dir/generator/types.cpp.o"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/makebindings.dir/generator/types.cpp.o -c /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/types.cpp

CMakeFiles/makebindings.dir/generator/types.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/makebindings.dir/generator/types.cpp.i"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/types.cpp > CMakeFiles/makebindings.dir/generator/types.cpp.i

CMakeFiles/makebindings.dir/generator/types.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/makebindings.dir/generator/types.cpp.s"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/types.cpp -o CMakeFiles/makebindings.dir/generator/types.cpp.s

CMakeFiles/makebindings.dir/generator/types.cpp.o.requires:

.PHONY : CMakeFiles/makebindings.dir/generator/types.cpp.o.requires

CMakeFiles/makebindings.dir/generator/types.cpp.o.provides: CMakeFiles/makebindings.dir/generator/types.cpp.o.requires
	$(MAKE) -f CMakeFiles/makebindings.dir/build.make CMakeFiles/makebindings.dir/generator/types.cpp.o.provides.build
.PHONY : CMakeFiles/makebindings.dir/generator/types.cpp.o.provides

CMakeFiles/makebindings.dir/generator/types.cpp.o.provides.build: CMakeFiles/makebindings.dir/generator/types.cpp.o


CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o: CMakeFiles/makebindings.dir/flags.make
CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o: generator/codeprinter.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o -c /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/codeprinter.cpp

CMakeFiles/makebindings.dir/generator/codeprinter.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/makebindings.dir/generator/codeprinter.cpp.i"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/codeprinter.cpp > CMakeFiles/makebindings.dir/generator/codeprinter.cpp.i

CMakeFiles/makebindings.dir/generator/codeprinter.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/makebindings.dir/generator/codeprinter.cpp.s"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/codeprinter.cpp -o CMakeFiles/makebindings.dir/generator/codeprinter.cpp.s

CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o.requires:

.PHONY : CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o.requires

CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o.provides: CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o.requires
	$(MAKE) -f CMakeFiles/makebindings.dir/build.make CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o.provides.build
.PHONY : CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o.provides

CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o.provides.build: CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o


CMakeFiles/makebindings.dir/generator/driver.cpp.o: CMakeFiles/makebindings.dir/flags.make
CMakeFiles/makebindings.dir/generator/driver.cpp.o: generator/driver.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building CXX object CMakeFiles/makebindings.dir/generator/driver.cpp.o"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/makebindings.dir/generator/driver.cpp.o -c /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/driver.cpp

CMakeFiles/makebindings.dir/generator/driver.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/makebindings.dir/generator/driver.cpp.i"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/driver.cpp > CMakeFiles/makebindings.dir/generator/driver.cpp.i

CMakeFiles/makebindings.dir/generator/driver.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/makebindings.dir/generator/driver.cpp.s"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/driver.cpp -o CMakeFiles/makebindings.dir/generator/driver.cpp.s

CMakeFiles/makebindings.dir/generator/driver.cpp.o.requires:

.PHONY : CMakeFiles/makebindings.dir/generator/driver.cpp.o.requires

CMakeFiles/makebindings.dir/generator/driver.cpp.o.provides: CMakeFiles/makebindings.dir/generator/driver.cpp.o.requires
	$(MAKE) -f CMakeFiles/makebindings.dir/build.make CMakeFiles/makebindings.dir/generator/driver.cpp.o.provides.build
.PHONY : CMakeFiles/makebindings.dir/generator/driver.cpp.o.provides

CMakeFiles/makebindings.dir/generator/driver.cpp.o.provides.build: CMakeFiles/makebindings.dir/generator/driver.cpp.o


CMakeFiles/makebindings.dir/generator/lexer.cpp.o: CMakeFiles/makebindings.dir/flags.make
CMakeFiles/makebindings.dir/generator/lexer.cpp.o: generator/lexer.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Building CXX object CMakeFiles/makebindings.dir/generator/lexer.cpp.o"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/makebindings.dir/generator/lexer.cpp.o -c /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/lexer.cpp

CMakeFiles/makebindings.dir/generator/lexer.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/makebindings.dir/generator/lexer.cpp.i"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/lexer.cpp > CMakeFiles/makebindings.dir/generator/lexer.cpp.i

CMakeFiles/makebindings.dir/generator/lexer.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/makebindings.dir/generator/lexer.cpp.s"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/lexer.cpp -o CMakeFiles/makebindings.dir/generator/lexer.cpp.s

CMakeFiles/makebindings.dir/generator/lexer.cpp.o.requires:

.PHONY : CMakeFiles/makebindings.dir/generator/lexer.cpp.o.requires

CMakeFiles/makebindings.dir/generator/lexer.cpp.o.provides: CMakeFiles/makebindings.dir/generator/lexer.cpp.o.requires
	$(MAKE) -f CMakeFiles/makebindings.dir/build.make CMakeFiles/makebindings.dir/generator/lexer.cpp.o.provides.build
.PHONY : CMakeFiles/makebindings.dir/generator/lexer.cpp.o.provides

CMakeFiles/makebindings.dir/generator/lexer.cpp.o.provides.build: CMakeFiles/makebindings.dir/generator/lexer.cpp.o


CMakeFiles/makebindings.dir/generator/parser.cpp.o: CMakeFiles/makebindings.dir/flags.make
CMakeFiles/makebindings.dir/generator/parser.cpp.o: generator/parser.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/CMakeFiles --progress-num=$(CMAKE_PROGRESS_5) "Building CXX object CMakeFiles/makebindings.dir/generator/parser.cpp.o"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/makebindings.dir/generator/parser.cpp.o -c /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/parser.cpp

CMakeFiles/makebindings.dir/generator/parser.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/makebindings.dir/generator/parser.cpp.i"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/parser.cpp > CMakeFiles/makebindings.dir/generator/parser.cpp.i

CMakeFiles/makebindings.dir/generator/parser.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/makebindings.dir/generator/parser.cpp.s"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/parser.cpp -o CMakeFiles/makebindings.dir/generator/parser.cpp.s

CMakeFiles/makebindings.dir/generator/parser.cpp.o.requires:

.PHONY : CMakeFiles/makebindings.dir/generator/parser.cpp.o.requires

CMakeFiles/makebindings.dir/generator/parser.cpp.o.provides: CMakeFiles/makebindings.dir/generator/parser.cpp.o.requires
	$(MAKE) -f CMakeFiles/makebindings.dir/build.make CMakeFiles/makebindings.dir/generator/parser.cpp.o.provides.build
.PHONY : CMakeFiles/makebindings.dir/generator/parser.cpp.o.provides

CMakeFiles/makebindings.dir/generator/parser.cpp.o.provides.build: CMakeFiles/makebindings.dir/generator/parser.cpp.o


CMakeFiles/makebindings.dir/generator/bindgen.cpp.o: CMakeFiles/makebindings.dir/flags.make
CMakeFiles/makebindings.dir/generator/bindgen.cpp.o: generator/bindgen.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/CMakeFiles --progress-num=$(CMAKE_PROGRESS_6) "Building CXX object CMakeFiles/makebindings.dir/generator/bindgen.cpp.o"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++   $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/makebindings.dir/generator/bindgen.cpp.o -c /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/bindgen.cpp

CMakeFiles/makebindings.dir/generator/bindgen.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/makebindings.dir/generator/bindgen.cpp.i"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/bindgen.cpp > CMakeFiles/makebindings.dir/generator/bindgen.cpp.i

CMakeFiles/makebindings.dir/generator/bindgen.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/makebindings.dir/generator/bindgen.cpp.s"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/generator/bindgen.cpp -o CMakeFiles/makebindings.dir/generator/bindgen.cpp.s

CMakeFiles/makebindings.dir/generator/bindgen.cpp.o.requires:

.PHONY : CMakeFiles/makebindings.dir/generator/bindgen.cpp.o.requires

CMakeFiles/makebindings.dir/generator/bindgen.cpp.o.provides: CMakeFiles/makebindings.dir/generator/bindgen.cpp.o.requires
	$(MAKE) -f CMakeFiles/makebindings.dir/build.make CMakeFiles/makebindings.dir/generator/bindgen.cpp.o.provides.build
.PHONY : CMakeFiles/makebindings.dir/generator/bindgen.cpp.o.provides

CMakeFiles/makebindings.dir/generator/bindgen.cpp.o.provides.build: CMakeFiles/makebindings.dir/generator/bindgen.cpp.o


# Object files for target makebindings
makebindings_OBJECTS = \
"CMakeFiles/makebindings.dir/generator/types.cpp.o" \
"CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o" \
"CMakeFiles/makebindings.dir/generator/driver.cpp.o" \
"CMakeFiles/makebindings.dir/generator/lexer.cpp.o" \
"CMakeFiles/makebindings.dir/generator/parser.cpp.o" \
"CMakeFiles/makebindings.dir/generator/bindgen.cpp.o"

# External object files for target makebindings
makebindings_EXTERNAL_OBJECTS =

makebindings: CMakeFiles/makebindings.dir/generator/types.cpp.o
makebindings: CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o
makebindings: CMakeFiles/makebindings.dir/generator/driver.cpp.o
makebindings: CMakeFiles/makebindings.dir/generator/lexer.cpp.o
makebindings: CMakeFiles/makebindings.dir/generator/parser.cpp.o
makebindings: CMakeFiles/makebindings.dir/generator/bindgen.cpp.o
makebindings: CMakeFiles/makebindings.dir/build.make
makebindings: CMakeFiles/makebindings.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/CMakeFiles --progress-num=$(CMAKE_PROGRESS_7) "Linking CXX executable makebindings"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/makebindings.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/makebindings.dir/build: makebindings

.PHONY : CMakeFiles/makebindings.dir/build

CMakeFiles/makebindings.dir/requires: CMakeFiles/makebindings.dir/generator/types.cpp.o.requires
CMakeFiles/makebindings.dir/requires: CMakeFiles/makebindings.dir/generator/codeprinter.cpp.o.requires
CMakeFiles/makebindings.dir/requires: CMakeFiles/makebindings.dir/generator/driver.cpp.o.requires
CMakeFiles/makebindings.dir/requires: CMakeFiles/makebindings.dir/generator/lexer.cpp.o.requires
CMakeFiles/makebindings.dir/requires: CMakeFiles/makebindings.dir/generator/parser.cpp.o.requires
CMakeFiles/makebindings.dir/requires: CMakeFiles/makebindings.dir/generator/bindgen.cpp.o.requires

.PHONY : CMakeFiles/makebindings.dir/requires

CMakeFiles/makebindings.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/makebindings.dir/cmake_clean.cmake
.PHONY : CMakeFiles/makebindings.dir/clean

CMakeFiles/makebindings.dir/depend:
	cd /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi /Users/owen/projects/CS4120/pa/pa7/pa7/QtXi/libQtXi/CMakeFiles/makebindings.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/makebindings.dir/depend
