package coyote.commons;

//import static org.junit.Assert.*;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Class FileUtilTest
 */
public class FileUtilTest {
    private static final String S = File.separator;


    /**
     * Method testFileToStringOne
     */
    @Test
    public void testFileToStringOne() {
        assertTrue(FileUtil.stringToFile("This is test named testFileToStringOne", "test.txt"), "testFileToStringOne");

        if (FileUtil.fileToString("test.txt") == null) {
            fail("File not found");
        }

        try {
            FileUtil.deleteFile("test.txt");
        } catch (Exception ex) {
        }
    }


    /**
     * Method testFileToStringTwo
     */
    @Test
    public void testFileToStringTwo() {
        assertEquals("", FileUtil.fileToString("Does-Not-Exist.txt"), "Nothing should have been read.");
    }


    /**
     * Method testStringToFileOne
     */
    @Test
    public void testStringToFileOne() {
        String filename = "StringToFile.txt";
        try {
            assertTrue(FileUtil.stringToFile("This is a test", filename), "StringToFile");
        } finally {
            new File(filename).delete();
        }
    }


    /**
     * Method testGetAllFiles
     */
    @Test
    public void testGetAllFiles() {

        // Should return no files in the current directory with .JAVA extension
        try {
            List<File> list = FileUtil.getAllFiles(".", "java", false);
            assertEquals(0, list.size());
        } catch (Exception e) {
            fail("getAllFiles: " + e.getMessage());
        }

        // Should return all the files in the current directory
        try {
            List<File> list = FileUtil.getAllFiles(".", null, false);
            assertTrue(list.size() > 0);
        } catch (Exception e) {
            fail("getAllFiles: " + e.getMessage());
        }

        // Should return many files in the current directory with .JAVA extension as recurse is set to true
        try {
            List<File> list = FileUtil.getAllFiles(".", "java", true);
            assertTrue(list.size() > 0);
            //for( File file : list ) System.out.println( ">" + file.getAbsolutePath() );
        } catch (Exception e) {
            fail("getAllFiles: " + e.getMessage());
        }

    }


    /**
     * Method testTouch
     */
    @Test
    public void testTouch() {
        try {
            String filename = "C:\\WINNT\\Profiles\\cotes.000\\pub\\data\\comments.txt";
            File subject = new File(filename);
            FileUtil.touch(subject);
        } catch (Exception e) {
            fail("testTouch: " + e.getMessage());
        }
    }


    /**
     * Method testFinals
     */
    @Test
    public void testFinals() {
        System.out.println("Home: " + FileUtil.HOME);
        assertTrue(FileUtil.HOME.equalsIgnoreCase(System.getProperty("user.home")));
        System.out.println("Home Directory: " + FileUtil.HOME_DIR);
        System.out.println("Home URI: " + FileUtil.HOME_DIR_URI);

        System.out.println("Current: " + FileUtil.CURRENT);
        assertTrue(FileUtil.CURRENT.equalsIgnoreCase(System.getProperty("user.dir")));
        System.out.println("Current Directory: " + FileUtil.CURRENT_DIR);
        System.out.println("Current URI: " + FileUtil.CURRENT_DIR_URI);
    }


    /**
     * Method testGetBase
     */
    @Test
    public void testGetBase() {
        System.out.println("GetBase directory: " + FileUtil.CURRENT);
        System.out.println("GetBase   results: " + FileUtil.getBase(FileUtil.CURRENT));
    }


    /**
     * Method testGetPath1
     */
    @Test
    public void testGetPath1() {
        String filename = FileUtil.CURRENT + FileUtil.FILE_SEPARATOR + "README.TXT";
        assertEquals(FileUtil.getPath(filename), FileUtil.CURRENT + FileUtil.FILE_SEPARATOR);
    }


    /**
     * Method testGetPath2
     */
    @Test
    public void testGetPath2() {
        String filename = "/export/home/sdcote/find.txt";
        String path = FileUtil.FILE_SEPARATOR + "export" + FileUtil.FILE_SEPARATOR + "home" + FileUtil.FILE_SEPARATOR + "sdcote" + FileUtil.FILE_SEPARATOR;
        assertEquals(FileUtil.getPath(filename), path);
    }


    /**
     * Method testGetPath3
     */
    @Test
    public void testGetPath3() {
        String filename = "export/home/sdcote/find.txt";
        String path = "export" + FileUtil.FILE_SEPARATOR + "home" + FileUtil.FILE_SEPARATOR + "sdcote" + FileUtil.FILE_SEPARATOR;
        assertEquals(FileUtil.getPath(filename), path);
    }


    /**
     * Method testGetPath4
     */
    @Test
    public void testGetPath4() {
        String filename = "clean//extra//delimiters////from//this/path.txt";
        String path = "clean" + FileUtil.FILE_SEPARATOR + "extra" + FileUtil.FILE_SEPARATOR + "delimiters" + FileUtil.FILE_SEPARATOR + "from" + FileUtil.FILE_SEPARATOR + "this" + FileUtil.FILE_SEPARATOR;
        assertEquals(FileUtil.getPath(filename), path);
    }


    /**
     * Method testGetPath5
     */
    @Test
    public void testGetPath5() {
        String filename = "clean//mixed\\delimiters//\\//\\from\\this/path.txt";
        String path = "clean" + FileUtil.FILE_SEPARATOR + "mixed" + FileUtil.FILE_SEPARATOR + "delimiters" + FileUtil.FILE_SEPARATOR + "from" + FileUtil.FILE_SEPARATOR + "this" + FileUtil.FILE_SEPARATOR;
        assertEquals(FileUtil.getPath(filename), path);
    }


    /**
     * Method testGetFilename
     */
    @Test
    public void testGetFilename() {
        String filename = "clean//mixed\\delimiters//\\//\\from\\this/path.txt";
        String file = "path.txt";
        assertEquals(FileUtil.getFile(filename), file);
    }


    /**
     * Method testGetJavaFile1
     */
    @Test
    public void testGetJavaFile1() {
        String classname = "coyote.commons.util.FileUtil.class";
        assertEquals("FileUtil.class", FileUtil.getJavaFile(classname));
    }


    /**
     * Method testGetJavaFile2
     */
    @Test
    public void testGetJavaFile2() {
        String classname = "coyote.commons.util.FileUtil";
        assertEquals("FileUtil.class", FileUtil.getJavaFile(classname));
    }


    /**
     * Method testRemoveRelations1
     */
    @Test
    public void testRemoveRelations1() {
        String path = "/export/home/sdcote/projects/BusStress/bin/../cfg/busconnector.xml";
        String expected = S + "export" + S + "home" + S + "sdcote" + S + "projects" + S + "BusStress" + S + "cfg" + S + "busconnector.xml";
        String normal = FileUtil.removeRelations(path);

        assertEquals(normal, expected);
    }


    /**
     * Method testRemoveRelations2
     */
    @Test
    public void testRemoveRelations2() {
        String path = "C:\\sdcote\\eclipse\\workspace\\BusStress\\bin\\..";
        String expected = "C:" + S + "sdcote" + S + "eclipse" + S + "workspace" + S + "BusStress";
        String normal = FileUtil.removeRelations(path);
        System.out.println("PATH1=" + path);
        System.out.println("PATH2=" + normal);

        assertEquals(normal, expected);
    }


    /**
     * Method testRemoveRelations3
     */
    @Test
    public void testRemoveRelations3() {
        String path = "C:\\sdcote\\eclipse\\workspace\\BusStress\\..\\..";
        String expected = "C:" + S + "sdcote" + S + "eclipse";
        String normal = FileUtil.removeRelations(path);

        assertEquals(normal, expected);
    }


    /**
     * Method testRemoveRelations4
     */
    @Test
    public void testRemoveRelations4() {
        String path = "C:\\sdcote\\eclipse\\.\\workspace\\BusStress\\..\\..";
        String expected = "C:" + S + "sdcote" + S + "eclipse";
        String normal = FileUtil.removeRelations(path);

        assertEquals(normal, expected);
    }


    /**
     * Make sure we preserve the trailing separator.
     * <p>
     * We only remove the relations, not any extra file separators that the user
     * may want or expect. Makse sure the trailing separator remains.
     *
     */
    @Test
    public void testRemoveRelations5() {
        String path = "C:\\sdcote\\eclipse\\.\\workspace\\BusStress\\..\\..\\";
        String expected = "C:" + S + "sdcote" + S + "eclipse" + S;
        String normal = FileUtil.removeRelations(path);

        // System.out.println("PATH1="+path);
        // System.out.println("PATH2="+normal);
        assertEquals(normal, expected);
    }


    /**
     * Method testRemoveRelations6
     */
    @Test
    public void testRemoveRelations6() {
        String path = "/../../../../..";
        String expected = S;
        String normal = FileUtil.removeRelations(path);

        System.out.println("PATH1=" + path);
        System.out.println("PATH2=" + normal);
        assertEquals(normal, expected);
    }


    /**
     * Method testGetHash
     */
    @Test
    public void testGetHash() throws Exception {
        File file = new File("testHash.txt");
        try {
            FileUtil.stringToFile("This is a test for hash.", file.getAbsolutePath());
            byte[] hash = FileUtil.getHash(file);
            assertNotNull(hash);
            assertTrue(hash.length > 0);

            byte[] hash2 = FileUtil.getHash(file);
            assertArrayEquals(hash, hash2);

            File file2 = new File("testHash2.txt");
            try {
                FileUtil.stringToFile("This is a different test for hash.", file2.getAbsolutePath());
                byte[] hash3 = FileUtil.getHash(file2);
                assertFalse(Arrays.equals(hash, hash3));
            } finally {
                file2.delete();
            }
        } finally {
            file.delete();
        }
    }


    /**
     * Method testGetFileAge
     */
    @Test
    public void testGetFileAge() throws Exception {
        File file = new File("testAge.txt");
        try {
            FileUtil.touch(file);
            long age = FileUtil.getFileAge(file);
            assertTrue(age > 0);
            assertTrue(Math.abs(System.currentTimeMillis() - age) < 10000); // within 10 seconds

            File dir = new File("testAgeDir");
            try {
                dir.mkdir();
                File subFile = new File(dir, "subFile.txt");
                FileUtil.touch(subFile);
                long dirAge = FileUtil.getFileAge(dir);
                assertEquals(subFile.lastModified(), dirAge);
            } finally {
                FileUtil.deleteDirectory(dir);
            }
        } finally {
            file.delete();
        }
    }


    /**
     * Method testGetFiles
     */
    @Test
    public void testGetFiles() throws Exception {
        File dir = new File("testGetFilesDir");
        try {
            dir.mkdir();
            File file1 = new File(dir, "file1.txt");
            File file2 = new File(dir, "file2.log");
            FileUtil.touch(file1);
            FileUtil.touch(file2);

            List<File> files = FileUtil.getFiles(dir);
            assertEquals(2, files.size());

            // The pattern is applied to the ENTIRE file path.
            // So we need to match the path.
            files = FileUtil.getFiles(dir, ".*file1\\.txt");
            assertEquals(1, files.size());
            assertEquals("file1.txt", files.get(0).getName());

            File subDir = new File(dir, "subDir");
            subDir.mkdir();
            File file3 = new File(subDir, "file3.txt");
            FileUtil.touch(file3);

            files = FileUtil.getFiles(dir, true);
            assertEquals(3, files.size()); // file1, file2, file3 (getFiles only returns files)

            files = FileUtil.getFiles(dir, ".*\\.txt", true);
            assertEquals(2, files.size());
        } finally {
            FileUtil.deleteDirectory(dir);
        }
    }


    /**
     * Method testBackup
     */
    @Test
    public void testBackup() throws Exception {
        File file = new File("testBackup.txt");
        try {
            FileUtil.stringToFile("Original content", file.getAbsolutePath());
            FileUtil.createBackup(file);
            File backup = new File(file.getAbsolutePath() + ".1");
            assertTrue(backup.exists(), "Backup file " + backup.getAbsolutePath() + " should exist");
            assertEquals("Original content", FileUtil.fileToString(backup));

            FileUtil.stringToFile("New content", file.getAbsolutePath());
            FileUtil.createGenerationalBackup(file, 3);
            File genBackup = new File(file.getAbsolutePath() + ".1");
            assertTrue(genBackup.exists());
            assertEquals("New content", FileUtil.fileToString(genBackup));
        } finally {
            file.delete();
            new File(file.getAbsolutePath() + ".1").delete();
            new File(file.getAbsolutePath() + ".2").delete();
        }
    }


    /**
     * Method testMoveRenameFile
     */
    @Test
    public void testMoveRenameFile() throws Exception {
        File src = new File("testMoveSrc.txt");
        File dest = new File("testMoveDest.txt");
        try {
            FileUtil.stringToFile("Move me", src.getAbsolutePath());
            FileUtil.moveFile(src, dest);
            assertFalse(src.exists());
            assertTrue(dest.exists());
            assertEquals("Move me", FileUtil.fileToString(dest));

            File renamed = new File("testRenamed.txt");
            try {
                FileUtil.renameFile(dest.getAbsolutePath(), renamed.getAbsolutePath());
                assertFalse(dest.exists());
                assertTrue(renamed.exists());
            } finally {
                renamed.delete();
            }
        } finally {
            src.delete();
            dest.delete();
        }
    }


    /**
     * Method testFormatSizeBytes
     */
    @Test
    public void testFormatSizeBytes() {
        assertEquals("500 bytes", FileUtil.formatSizeBytes(500));
        assertEquals("1.00 KB", FileUtil.formatSizeBytes(1024));
        assertEquals("1.00 MB", FileUtil.formatSizeBytes(1024 * 1024));
        assertEquals("1.00 GB", FileUtil.formatSizeBytes(1024 * 1024 * 1024));
    }


    /**
     * Method testValidateMethods
     */
    @Test
    public void testValidateMethods() throws Exception {
        String dirName = "testValidateDir";
        File dir = new File(dirName);
        dir.mkdir();
        try {
            File validatedDir = FileUtil.validateDirectory(dirName);
            assertNotNull(validatedDir);
            assertTrue(validatedDir.exists());
            assertTrue(validatedDir.isDirectory());

            File file = new File(dir, "testValidateFile.txt");
            FileUtil.touch(file);
            try {
                // validateFileName requires absolute path
                File validatedFile = FileUtil.validateFileName(file.getAbsolutePath());
                assertNotNull(validatedFile);
                assertEquals("testValidateFile.txt", validatedFile.getName());
            } finally {
                file.delete();
            }
        } finally {
            dir.delete();
        }
    }


    /**
     * Method testResolveFile
     */
    @Test
    public void testResolveFile() {
        File base = new File("/home/user");
        File resolved = FileUtil.resolveFile(base, "docs/manual.pdf");
        // Result depends on OS, but should contain the parts
        assertTrue(resolved.getAbsolutePath().contains("home") || resolved.getAbsolutePath().contains("user"));
        assertTrue(resolved.getAbsolutePath().contains("docs"));
        assertTrue(resolved.getAbsolutePath().contains("manual.pdf"));
    }


    /**
     * Method testNormalize
     */
    @Test
    public void testNormalize() {
        String path = "dir1/../dir2/file.txt";
        String normalized = FileUtil.normalizePath(path);
        assertTrue(normalized.contains("dir2"));
        assertFalse(normalized.contains("dir1/.."));
    }

    /**
     * Method testAppend
     */
    @Test
    public void testAppend() throws Exception {
        File file = new File("testAppend.txt");
        try {
            FileUtil.write(file, "Line 1\n".getBytes());
            FileUtil.append(file, "Line 2\n".getBytes(), false);
            String content = FileUtil.fileToString(file);
            assertTrue(content.contains("Line 1"));
            assertTrue(content.contains("Line 2"));
        } finally {
            file.delete();
        }
    }

    /**
     * Method testClearDir
     */
    @Test
    public void testClearDir() throws Exception {
        File dir = new File("testClearDir");
        dir.mkdir();
        File file = new File(dir, "file.txt");
        FileUtil.touch(file);
        File subDir = new File(dir, "sub");
        subDir.mkdir();
        File subFile = new File(subDir, "subfile.txt");
        FileUtil.touch(subFile);

        try {
            FileUtil.clearDir(dir, false, true);
            assertTrue(dir.exists());
            // clearDir with clrsub=true should clear subdirs
            assertTrue(subDir.exists());
            assertEquals(0, subDir.listFiles().length);

            FileUtil.touch(file);
            FileUtil.clearDir(dir, true, true);
            assertFalse(dir.exists());
        } finally {
            FileUtil.deleteDirectory(dir);
        }
    }

}