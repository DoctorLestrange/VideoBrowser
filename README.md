# Video Browser

[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-4E80EE?style=for-the-badge)](README.md)
[![Java](https://img.shields.io/badge/Java-25%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](pom.xml)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](pom.xml)
[![UI](https://img.shields.io/badge/UI-FlatLaf%20Dark-2C3E50?style=for-the-badge)](https://www.formdev.com/flatlaf/)
[![License](https://img.shields.io/badge/License-Permissive-brightgreen?style=for-the-badge)](LICENSE)

A modern, high-performance desktop application for analyzing, inspecting, and managing video metadata across storage drives. Built with Java Swing, FlatLaf, and powered by FFprobe.

---

## Highlights

- **Deep Metadata Extraction**: Reads resolution, video codec, container/stream bitrate, duration, and file size with microsecond precision.
- **Storage-Aware Performance**: Engineered for high-latency external USB drives and mechanical HDDs with zero UI freezing, in-memory pre-filtering, bounded concurrency, and spin-up delay protection.
- **Natural Numerical Sorting**: Table column headers sort by true numeric values (bytes, bps, total pixels, time) rather than alphanumeric string comparison.
- **Safe Substring Search**: Real-time filtering across all visible table columns without regex crashes on special symbols (`*`, `(`, `)`, `[`, `]`).
- **Library Export & Import**: Save scanned collections to JSON and reload them anytime with preserved absolute file paths for offline inspection and sharing.
- **Modern Dark Interface**: Polished UI powered by FlatLaf dark themes, responsive column sizing, and context menus.
- **Zero-Crash Fallback**: Gracefully defaults to an offline JSON viewing mode if FFprobe is not detected on the host system.

---

## One-Click Launchers

Convenient one-click launchers are provided for major operating systems:

- **Windows**: Double-click `VideoBrowser.cmd` (runs in background without a persistent console window).
- **macOS**: Double-click `VideoBrowser.command` in Finder.
- **Linux**: Run `./VideoBrowser.sh` or integrate `VideoBrowser.desktop` into your application menu.

---

## Prerequisites

- **Java Runtime / JDK**: Java 21+ or Java 25 LTS
- **FFmpeg / FFprobe**: Accessible on your system `PATH` (required for extracting media streams from disk)
- **Apache Maven**: Required only if building from source

---

## Building from Source

Build the executable shaded JAR package using Maven:

```bash
mvn clean package -DskipTests
```

The compiled standalone executable JAR will be located at:
```text
target/VideoBrowser-0.1.jar
```

To run manually from terminal:
```bash
java -jar target/VideoBrowser-0.1.jar
```

---

## Keyboard Shortcuts & Navigation

| Key Combination | Action |
|---|---|
| `Enter` (in directory bar) | Start extraction for the specified folder |
| `Ctrl + O` | Open and load a video metadata JSON file |
| `Ctrl + E` | Export currently loaded metadata to JSON |
| `Ctrl + C` (on table) | Copy selected filename(s) without extension |
| `Ctrl + L` | Toggle the log panel view |
| `Ctrl + F` | Toggle the search bar panel |
| `Escape` (in search bar) | Clear active search filter |
| `Double-Click` (row) | Open video file in system default player |
| `Right-Click` (row) | Context menu: open file, copy path, copy JSON |
| `Right-Click` (log) | Context menu: copy logs, clear logs |

---

## Supported Video Formats

Video Browser processes all common video container formats:
- `.mp4`, `.mkv`, `.avi`, `.mov`, `.flv`
- `.wmv`, `.webm`, `.m4v`, `.ts`, `.m2ts`
- `.vob`, `.ogv`, `.3gp`, `.mpg`, `.mpeg`

---

## Project Structure

```text
VideoBrowser/
├── pom.xml                 # Maven build configuration
├── nbactions.xml           # NetBeans IDE execution setup
├── VideoBrowser.cmd        # Windows launcher
├── VideoBrowser.command    # macOS launcher
├── VideoBrowser.sh         # Linux launcher
├── VideoBrowser.desktop    # Linux desktop menu entry
├── LICENSE                 # License terms
├── README.md               # Documentation & badges
└── src/
    ├── main/java/com/dl/videobrowser/
    │   ├── VideoBrowser.java       # Main application JFrame logic
    │   └── VideoBrowser.form       # GUI form layout
    └── test/java/com/dl/videobrowser/
        └── VideoBrowserTestSuite.java  # Comprehensive test suite
```

---

## License

This project is available under the terms in the [LICENSE](LICENSE) file.
