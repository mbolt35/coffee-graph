////////////////////////////////////////////////////////////////////////////////
//
//  Java Helper - Node.js Module
//  Copyright(C) 2012 Matt Bolt
//
//  Permission is hereby granted, free of charge, to any person obtaining a
//  copy of this software and associated documentation files (the "Software"),
//  to deal in the Software without restriction, including without limitation
//  the rights to use, copy, modify, merge, publish, distribute, sublicense,
//  and/or sell copies of the Software, and to permit persons to whom the
//  Software is furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
//
////////////////////////////////////////////////////////////////////////////////

// Environment Variables to Check
var JAVA_HOME = "JAVA_HOME";
var CLASSPATH = "CLASSPATH";
var PATH = "PATH";

// Check for Windows to fix-up pathing issues
var windows = require('os').type().toLowerCase().indexOf("windows") != -1;

// Requires
var path = require('path');
var fs = require('fs');
var util = require('util');
var execute = require('child_process').exec;

// Export the JavaHelper class
exports.JavaHelper = (function() {

  function JavaHelper() {

  }

  /**
   * Converts all keys in the map to upper case as part of a new mapping 
   * and returns it.
   */
  function toUpperCase(map) {
    var ucase = {};

    for (var key in map) {
      ucase[key.toUpperCase()] = map[key];
    }

    return ucase;
  }

  /**
   * Looks for the value parameter in each string provided in the strings
   * parameter.
   */
  function arrayContains(value, strings) {
    for (var i = 0; i < strings.length; ++i) {
      if (contains(strings[i], value)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if at least one of the strings contains the value.
   */
  function contains(string, value) {
    return "undefined" !== typeof string && string.indexOf(value) != -1;
  }

  /**
   * Returns the java executable file name for the appropriate OS.
   */
  function getJavaFilename() {
    var javaExe = "/java";
    if (windows) {
      javaExe += ".exe";
    }
    return javaExe;
  }

  /**
   * Splits the path directories into an array
   */
  function splitPaths(paths) {
    if (typeof paths === "undefined") {
      return [];
    }
    return paths.split(windows ? ';' : ':');
  }

  /**
   * Checks to see if the java executable exists on the path synchronously.
   */
  function isValidJavaPath(javaPath) {
    return fs.existsSync(path.join(javaPath, getJavaFilename()));
  }

  /**
   * Generic execution callback, which just forwards the program output
   * appropriately.
   */
  function onApplicationOutput(error, stdout, stderr) {
    if (error) {
      console.error(error);
    } else if (stdout) {
      console.log(stdout);
    } else if (stderr) {
      console.error(stderr);
    }
  }

  /**
   * This method determines if the java runtime executable exists
   * on the user's path or not. 
   */
  JavaHelper.isJavaAvailable = function() {
    var env = toUpperCase(process.env);

    var javaHomeEnv = env[JAVA_HOME];
    var classpathEnv = env[CLASSPATH];
    var pathEnv = env[PATH];

    if (typeof javaHomeEnv !== "undefined") {
      return arrayContains(javaHomeEnv, [ pathEnv, classpathEnv ]);
    }

    var paths = splitPaths(pathEnv);
    paths = paths.concat(splitPaths(classpathEnv));

    for (var i = 0; i < paths.length; ++i) {
      if (isValidJavaPath(paths[i])) {
        return true;
      }
    }

    return false;
  };

  /**
   * This method executes java -jar on the jar parameter provided with the arguments 
   * provided. The output of the application is forwarded to the appropriate output
   * stream.
   */
  JavaHelper.executeJar = function(jar, args) {
    if (util.isArray(args)) {
      args = args.join(' ');
    }
    args = " " + args;
      
    execute("java -jar " + jar + args, onApplicationOutput);
  };

  return JavaHelper;

})();
