////////////////////////////////////////////////////////////////////////////////
//
//  Coffee-Graph
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
////////////////////////////////////////////////////////////////////////////////
//
//  Coffee-Graph
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

package bolt.web.coffee.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains utility methods for working with {@code File} objects.
 *
 * @author Matt Bolt
 */
public class FileHelper {

    /**
     * Determines whether or not the {@code File} instance has the file type specified.
     *
     * @param file The {@code File} instance to check.
     *
     * @param type The {@link FileType} to check the file against.
     *
     * @return {@code true} if the file is of the specified file type.
     */
    public static boolean isFileType(File file, FileType type) {
        String fileExt = fileExtensionFor(file);

        return null != fileExt && fileExt.equals(type.getFileExtension());
    }

    /**
     * This method extracts the file extension, should it exist, and returns the extension {@code String}
     * <strong>without</strong> the {@code "."}
     *
     * @param file The {@code File} to determine the extension for
     *
     * @return A {@code String} representing the file extension of the {@code File} instance.
     */
    public static String fileExtensionFor(File file) {
        String fileName = file.getName();
        if (fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return null;
    }

    /**
     * This method wraps all the horrid Java 6 IO APIs for extracting a {@code String} from a text file.
     *
     * @param file The text {@code File} to load.
     *
     * @return A {@code String} representation of the contents of the {@code File}
     */
    public static String loadText(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringWriter writer = new StringWriter();

            try {
                String line = reader.readLine();
                while(null != line) {
                    writer.append(line).append("\n");
                    line = reader.readLine();
                }

                return writer.toString();
            } finally {
                reader.close();
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

            return "";
        }
    }

    /**
     * @see {@link #collectFiles(java.io.File[], FileType...)}
     */
    public static List<File> collectFiles(List<File> files, FileType... fileTypes) {
        return collectFiles(files.toArray(new File[files.size()]), fileTypes);
    }

    /**
     * This method recursively collects a list of files from any directories within the {@code List} of {@code File}
     * instances, while also filtering the files for specific file types.
     *
     * @param files The array of {@code File} instances to add to the list.
     *
     * @param fileTypes The {@code FileType}s to look for.
     *
     * @return A full list of all the {@code File} instances.
     */
    public static List<File> collectFiles(File[] files, FileType... fileTypes) {
        List<File> fileList = new ArrayList<File>();

        for (File file : files) {
            if (file.isDirectory()) {
                fileList.addAll( collectFiles(file.listFiles(), fileTypes) );
                continue;
            }

            for (FileType type : fileTypes) {
                if (isFileType(file, type)) {
                    fileList.add(file);
                    break;
                }
            }
        }

        return fileList;
    }

    public static String stripFileType(String fileName) {
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    public static String substituteFileType(String fileName, FileType type) {
        fileName = stripFileType(fileName);
        return fileName + "." + type.getFileExtension();
    }
}
