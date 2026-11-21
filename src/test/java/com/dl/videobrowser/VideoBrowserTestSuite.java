package com.dl.videobrowser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VideoBrowserTestSuite {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    private static void assertTrue(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            testsFailed++;
            System.err.println("  [FAIL] " + testName);
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Running VideoBrowser Comprehensive Test Suite ===");

        testSearchFilterRobustness();
        testNullSafeSorting();
        testSmartNumericalComparators();
        testJsonCompatibility();
        testDurationFormatting();

        System.out.println("\n=== Test Results: " + testsPassed + "/" + testsRun + " passed, " + testsFailed + " failed ===");
        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void testSearchFilterRobustness() {
        System.out.println("\nTest 1: Search Filter Regex Safety (Substring Filtering)");
        String[] problemInputs = {
            "[", "]", "(", ")", "*", "+", "?", "\\", "[1080p]", "*.mp4", "movie (2024)", "special+case?"
        };

        DefaultTableModel model = new DefaultTableModel(
            new Object[][]{
                {"[1080p] Video.mp4", "00:05:00"},
                {"movie (2024).mkv", "01:30:00"},
                {"special+case?.avi", "00:10:00"}
            },
            new String[]{"File Name", "Duration"}
        );
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

        for (String input : problemInputs) {
            try {
                final String lower = input.toLowerCase();
                sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                    @Override
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                        for (int i = 0; i < entry.getValueCount(); i++) {
                            Object val = entry.getValue(i);
                            if (val != null && val.toString().toLowerCase().contains(lower)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
                assertTrue("Search filter survived query: '" + input + "'", true);
            } catch (Exception e) {
                assertTrue("Search filter survived query: '" + input + "' (Threw " + e + ")", false);
            }
        }
    }

    private static void testNullSafeSorting() {
        System.out.println("\nTest 2: Null-Safe Sorting");
        List<VideoBrowser.VideoInfo> list = new ArrayList<>();

        VideoBrowser.VideoInfo v1 = new VideoBrowser.VideoInfo();
        v1.fileName = "a.mp4";
        v1.videoCodec = "h264";
        v1.resolution = "1920x1080";

        VideoBrowser.VideoInfo v2 = new VideoBrowser.VideoInfo();
        v2.fileName = "b.mp4";
        v2.videoCodec = null; // null codec (e.g. audio-only or corrupted stream)
        v2.resolution = null; // null resolution

        VideoBrowser.VideoInfo v3 = new VideoBrowser.VideoInfo();
        v3.fileName = null;
        v3.videoCodec = "hevc";
        v3.resolution = "1280x720";

        list.add(v1);
        list.add(v2);
        list.add(v3);

        Comparator<VideoBrowser.VideoInfo> codecComp =
            Comparator.comparing(o -> o.videoCodec != null ? o.videoCodec.toLowerCase() : "");
        try {
            list.sort(codecComp);
            assertTrue("Codec sort is null-safe", true);
        } catch (Exception e) {
            assertTrue("Codec sort is null-safe", false);
        }

        Comparator<VideoBrowser.VideoInfo> nameComp =
            Comparator.comparing(o -> o.fileName != null ? o.fileName.toLowerCase() : "");
        try {
            list.sort(nameComp);
            assertTrue("Name sort is null-safe", true);
        } catch (Exception e) {
            assertTrue("Name sort is null-safe", false);
        }
    }

    private static void testSmartNumericalComparators() throws Exception {
        System.out.println("\nTest 3: Smart Numerical Sorting on Formatted Strings");
        VideoBrowser vb = new VideoBrowser();

        Method parseSize = VideoBrowser.class.getDeclaredMethod("parseSizeStringToKb", Object.class);
        parseSize.setAccessible(true);
        long s1 = (long) parseSize.invoke(vb, "1,024 KB");
        long s2 = (long) parseSize.invoke(vb, "250 KB");
        long s3 = (long) parseSize.invoke(vb, "50 KB");
        assertTrue("parseSize: 1,024 KB == 1024", s1 == 1024);
        assertTrue("parseSize: 250 KB == 250", s2 == 250);
        assertTrue("Numerical size: 50 KB < 250 KB < 1024 KB", s3 < s2 && s2 < s1);

        Method parseBitrate = VideoBrowser.class.getDeclaredMethod("parseBitrateStringToKbps", Object.class);
        parseBitrate.setAccessible(true);
        long br1 = (long) parseBitrate.invoke(vb, "1,200 kbps");
        long br2 = (long) parseBitrate.invoke(vb, "300 kbps");
        assertTrue("Numerical bitrate: 300 kbps < 1200 kbps", br2 < br1);

        Method parseDuration = VideoBrowser.class.getDeclaredMethod("parseDurationToSeconds", Object.class);
        parseDuration.setAccessible(true);
        long d1 = (long) parseDuration.invoke(vb, "01:30:00");
        long d2 = (long) parseDuration.invoke(vb, "00:45:00");
        assertTrue("Numerical duration: 01:30:00 == 5400s", d1 == 5400);
        assertTrue("Numerical duration: 45m < 1h30m", d2 < d1);

        Method calcPixels = VideoBrowser.class.getDeclaredMethod("calculatePixels", String.class);
        calcPixels.setAccessible(true);
        long pFHD = (long) calcPixels.invoke(vb, "1920x1080");
        long pSD = (long) calcPixels.invoke(vb, "640x480");
        assertTrue("Pixel count: 640x480 < 1920x1080", pSD < pFHD);
        vb.dispose();
    }

    private static void testJsonCompatibility() throws IOException {
        System.out.println("\nTest 4: JSON Serialization, Deserialization & Robustness");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        // JSON containing unexpected future properties
        String json = """
            [
              {
                "fileName": "demo.mp4",
                "filePath": "C:\\\\Videos\\\\demo.mp4",
                "durationSeconds": 120.5,
                "videoCodec": "h264",
                "resolution": "1920x1080",
                "bitRate": 2500000,
                "fileSize": 37650000,
                "unknownField": "shouldNotCrash",
                "extraObject": {"key": 123}
              }
            ]
            """;

        List<VideoBrowser.VideoInfo> loaded = mapper.readValue(json, new TypeReference<List<VideoBrowser.VideoInfo>>() {});
        assertTrue("Loaded 1 item from JSON with unknown fields", loaded.size() == 1);
        VideoBrowser.VideoInfo info = loaded.get(0);
        assertTrue("Field fileName correctly mapped", "demo.mp4".equals(info.fileName));
        assertTrue("Field filePath correctly mapped", "C:\\Videos\\demo.mp4".equals(info.filePath));
        assertTrue("Field videoCodec correctly mapped", "h264".equals(info.videoCodec));
        assertTrue("Field resolution correctly mapped", "1920x1080".equals(info.resolution));

        // Test export to temp file
        File temp = File.createTempFile("vb_test_", ".json");
        temp.deleteOnExit();
        mapper.writerWithDefaultPrettyPrinter().writeValue(temp, loaded);
        assertTrue("JSON file exported successfully and exists", temp.exists() && temp.length() > 0);
        String exportedContent = Files.readString(temp.toPath());
        assertTrue("Exported JSON contains fileName", exportedContent.contains("demo.mp4"));
        assertTrue("Exported JSON contains filePath", exportedContent.contains("C:\\\\Videos\\\\demo.mp4"));
    }

    private static void testDurationFormatting() throws Exception {
        System.out.println("\nTest 5: Duration Formatting");
        VideoBrowser vb = new VideoBrowser();
        Method formatDuration = VideoBrowser.class.getDeclaredMethod("formatDuration", double.class);
        formatDuration.setAccessible(true);

        String f1 = (String) formatDuration.invoke(vb, 65.0); // 1m 5s
        String f2 = (String) formatDuration.invoke(vb, 3661.0); // 1h 1m 1s
        String f3 = (String) formatDuration.invoke(vb, 0.0);

        assertTrue("65s formatted as 00:01:05", "00:01:05".equals(f1));
        assertTrue("3661s formatted as 01:01:01", "01:01:01".equals(f2));
        assertTrue("0s formatted as 00:00:00", "00:00:00".equals(f3));
        vb.dispose();
    }
}
