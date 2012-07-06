////////////////////////////////////////////////////////////////////////////////
//
//  Path Helper - Node.js Module
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

// Check for Windows to fix-up pathing issues
var windows = require('os').type().toLowerCase().indexOf("windows") != -1;

// Requires
var path  = require('path');
var fs = require('fs');

// Export class
exports.PathHelper = (function() {

  function PathHelper() {

  }

  /**
   * For windows directories. Converts "C:\Foo\" -> "C:\Foo"
   */
  PathHelper.trim = function(file) {
    if (file.indexOf('"') != -1) {
      return file.replace('"', '');
    }
    
    return file;
  };

  /**
   * Wraps the String in Quotations.
   */
  PathHelper.wrap = function(file) {
    return '"' + file + '"';
  };

  /**
   * Uses the fs and path modules to determine the real directory name
   */
  PathHelper.realDir = function(file) {
    return path.dirname(fs.realpathSync(file));
  };

  PathHelper.trimAndWrap = function(args, filter) {
    // If we're in a non-windows OS, just returns the args as they were
    if (!windows) {
      return args;
    }

    // If the filter is invalid, replace with a non-filter
    if ("function" !== typeof filter || filter.length != 1) {
      filter = function(arg) {
        return true;
      };
    }

    var result = args.concat();

    for (var i = 0; i < result.length; ++i) {
      result[i] = PathHelper.trim(result[i]);
      
      if (!filter(result[i])) {
        result[i] = PathHelper.wrap(result[i]);
      }
    }

    return result;
  };

  /**
   * Accepts an array of directory/file directives and returns the joined file name.
   */
  PathHelper.joinAndWrap = function(fileDirectives) {
    return PathHelper.wrap( path.join.apply(null, fileDirectives) );
  }

  return PathHelper;
})();

