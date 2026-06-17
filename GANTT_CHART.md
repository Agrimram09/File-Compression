# File Compressor & Decompressor - Project Gantt Chart & Roadmap

This document outlines the structured 6-week development lifecycle for the **File Compressor & Decompressor** Java application, showing tasks, dependencies, mapping to the codebase (`Main.java`, `Compressor.java`, `Decompressor.java`), and current progress.

---

## 1. Project Development Timeline (Gantt Chart)

### Mermaid Format
```mermaid
gantt
    title File Compressor & Decompressor Project Timeline
    dateFormat  YYYY-MM-DD
    axisFormat  %b-%d
    
    section Phase 1: Planning & Design
    Requirement Analysis             :active, p1_1, 2026-06-16, 3d
    Architecture Design              :p1_2, after p1_1, 2d
    UI Mockups & Themes              :p1_3, after p1_2, 2d
    
    section Phase 2: Core Engine
    Recursive ZIP Archiving          :p2_1, after p1_3, 3d
    Safe ZIP Decompression           :p2_2, after p2_1, 3d
    Image Compression & Scaling      :p2_3, after p2_1, 4d
    Audio Quality Downsampling       :p2_4, after p2_3, 2d
    
    section Phase 3: GUI Development
    Main Layout & Custom Panels      :p3_1, after p2_4, 4d
    Custom Modern UI Elements        :p3_2, after p3_1, 3d
    Light/Dark Theme Switching       :p3_3, after p3_2, 3d
    
    section Phase 4: Integration
    Asynchronous Threading           :p4_1, after p3_3, 3d
    Progress & Previews Link         :p4_2, after p4_1, 3d
    IO Operations & File Save        :p4_3, after p4_2, 2d
    
    section Phase 5: Testing & Release
    Functional & Edge Testing        :p5_1, after p4_3, 4d
    Vulnerability Validation         :p5_2, after p5_1, 2d
    Documentation & Reporting        :p5_3, after p5_2, 4d
```

### PlantUML Format
```plantuml
@startgantt
title File Compressor & Decompressor Project Timeline

[Requirement Analysis] lasts 3 days
[Architecture Design] lasts 2 days
[UI Mockups & Themes] lasts 2 days
[Recursive ZIP Archiving] lasts 3 days
[Safe ZIP Decompression] lasts 3 days
[Image Compression & Scaling] lasts 4 days
[Audio Quality Downsampling] lasts 2 days
[Main Layout & Custom Panels] lasts 4 days
[Custom Modern UI Elements] lasts 3 days
[Light/Dark Theme Switching] lasts 3 days
[Asynchronous Threading] lasts 3 days
[Progress & Previews Link] lasts 3 days
[IO Operations & File Save] lasts 2 days
[Functional & Edge Testing] lasts 4 days
[Vulnerability Validation] lasts 2 days
[Documentation & Reporting] lasts 4 days

[Architecture Design] starts after [Requirement Analysis]'s end
[UI Mockups & Themes] starts after [Architecture Design]'s end
[Recursive ZIP Archiving] starts after [UI Mockups & Themes]'s end
[Safe ZIP Decompression] starts after [Recursive ZIP Archiving]'s end
[Image Compression & Scaling] starts after [Recursive ZIP Archiving]'s end
[Audio Quality Downsampling] starts after [Image Compression & Scaling]'s end
[Main Layout & Custom Panels] starts after [Audio Quality Downsampling]'s end
[Custom Modern UI Elements] starts after [Main Layout & Custom Panels]'s end
[Light/Dark Theme Switching] starts after [Custom Modern UI Elements]'s end
[Asynchronous Threading] starts after [Light/Dark Theme Switching]'s end
[Progress & Previews Link] starts after [Asynchronous Threading]'s end
[IO Operations & File Save] starts after [Progress & Previews Link]'s end
[Functional & Edge Testing] starts after [IO Operations & File Save]'s end
[Vulnerability Validation] starts after [Functional & Edge Testing]'s end
[Documentation & Reporting] starts after [Vulnerability Validation]'s end

-- Phase 1: Planning & Design --
[Requirement Analysis] is colored in LightBlue
[Architecture Design] is colored in LightBlue
[UI Mockups & Themes] is colored in LightBlue

-- Phase 2: Core Engine --
[Recursive ZIP Archiving] is colored in LightGreen
[Safe ZIP Decompression] is colored in LightGreen
[Image Compression & Scaling] is colored in LightGreen
[Audio Quality Downsampling] is colored in LightGreen

-- Phase 3: GUI Development --
[Main Layout & Custom Panels] is colored in LightSalmon
[Custom Modern UI Elements] is colored in LightSalmon
[Light/Dark Theme Switching] is colored in LightSalmon

-- Phase 4: Integration --
[Asynchronous Threading] is colored in LightCyan
[Progress & Previews Link] is colored in LightCyan
[IO Operations & File Save] is colored in LightCyan

-- Phase 5: Testing & Release --
[Functional & Edge Testing] is colored in Lavender
[Vulnerability Validation] is colored in Lavender
[Documentation & Reporting] is colored in Lavender
@endgantt
```

---


## 2. Detailed Project Plan Breakdown

The following table details the milestones, tasks, estimated durations, dependencies, and their corresponding files in the project.

| Phase | Task ID | Task Name | Duration | Dependencies | Target File(s) | Description |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Phase 1: Planning & Design** | **P1.1** | Requirement Analysis | 3 Days | None | `README.md`, `TODO.md` | Defining scopes (support for Images, ZIP, and WAV compression formats) and establishing system design parameters. |
| | **P1.2** | Architecture Design | 2 Days | P1.1 | - | Drawing class diagrams and determining dependencies (e.g., using `javax.swing`, `java.util.zip`, `javax.imageio`). |
| | **P1.3** | UI Mockups & Theme Config | 2 Days | P1.2 | `Main.java` | Defining typography (Inter font family) and light/dark color palette tokens (`Theme` static inner classes). |
| **Phase 2: Core Engine Development** | **P2.1** | Recursive ZIP Archiving | 3 Days | P1.3 | `Compressor.java` | Implementing directory tree traversal and multi-file recursive compressing into `ZipOutputStream`. |
| | **P2.2** | Safe ZIP Decompression | 3 Days | P2.1 | `Decompressor.java` | Implementing extraction logic with safety mechanisms against the Zip Slip vulnerability. |
| | **P2.3** | Image Compression & Scaling | 4 Days | P2.1 | `Compressor.java` | Implementing JPEG compression ratios using quality sliders, down-scaling large pictures for buffer safety. |
| | **P2.4** | Audio Quality Downsampling | 2 Days | P2.3 | `Compressor.java` | Downsampling WAV files using custom audio formatting (8kHz, 8-bit mono conversion). |
| **Phase 3: GUI Development** | **P3.1** | Layout & Structure | 4 Days | P2.4 | `Main.java` | Creating a split-view dashboard (Original Preview card on left, Output Preview card on right) using Swing components. |
| | **P3.2** | Custom Modern Components | 3 Days | P3.1 | `Main.java` | Creating custom components like `ModernButton` (anti-aliased rounded edges, interactive hover highlights) and `DashedBorder`. |
| | **P3.3** | Theme Toggle Implementation | 3 Days | P3.2 | `Main.java` | Connecting state switches to dynamically paint components between Light and Dark mode styles. |
| **Phase 4: Integration & Binding** | **P4.1** | Asynchronous Threading | 3 Days | P3.3 | `Main.java` | Implementing background worker threads (`new Thread().start()`) for compression to prevent UI thread lockups. |
| | **P4.2** | Preview & Progress Binding | 3 Days | P4.1 | `Main.java` | Linking real-time progress bars to compress tasks and loading thumbnails in pre/post compression view. |
| | **P4.3** | File Save & Export | 2 Days | P4.2 | `Main.java` | Implementing save dialogs to export files from temporary storage to the user-specified destination. |
| **Phase 5: Testing & Release** | **P5.1** | Functional & Edge Testing | 4 Days | P4.3 | - | Testing directory compression, large files, and compressed ratio calculations. |
| | **P5.2** | Vulnerability Validation | 2 Days | P5.1 | `Decompressor.java` | Running test suites against malicious ZIP archives to confirm the Zip Slip block. |
| | **P5.3** | Documentation & Reporting | 4 Days | P5.2 | `README.md` | Finalizing instructions, compilation guides, and project report structure. |

---

## 3. Project Implementation Status Check

- [x] **Planning & Design** (P1.1 - P1.3): Complete.
- [x] **Core Engine Logic** (P2.1 - P2.4): Complete.
- [x] **Swing Interface Layout** (P3.1 - P3.3): Complete.
- [x] **Process Integration & Threading** (P4.1 - P4.3): Complete.
- [ ] **Comprehensive Testing Suite** (P5.1 - P5.2): Pending final edge cases.
- [x] **Base Documentation** (P5.3): Done (`README.md` and project comments are configured).

> **Note:** The project codebase is highly modularized, with custom UI themes (Light/Dark toggles) and secure zip handling (protecting from directory traversal attacks) already fully built in. 
