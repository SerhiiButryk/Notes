#!/usr/bin/env python3
import json
import re
import subprocess
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
PROJECT = ROOT / "multiplatform" / "Notes"
OUT_DIR = ROOT / "images"
DOT_PATH = OUT_DIR / "package_structure.dot"
EXCALIDRAW_PATH = OUT_DIR / "package_structure_diagram.excalidraw"
PNG_PATH = OUT_DIR / "package_structure.png"

# Editable diagram text.
#
# Change these labels/descriptions, then run:
#   python3 tools/generate_module_diagram.py
#
# The script regenerates:
#   images/package_structure_diagram.excalidraw
#   images/package_structure.png
#   images/package_structure.dot
DIAGRAM_TITLE = "Project multi-module structure"
LEGEND_TEXT = "solid: main dependency    dashed: Android-only    dotted: test/baseline/target"
DOT_LEGEND_TEXT = "solid: main dependency   dashed: Android-only   dotted: test/baseline/target"
COLOR_LEGEND_TITLE = "Colors"
COLOR_LEGEND = [
    ("app", "App / entry modules"),
    ("feature", "Feature UI modules"),
    ("core", "Core contracts / platform glue"),
    ("data", "Data and service modules"),
    ("external", "External integrated module"),
]
DOT_COLOR_LEGEND_POS = (-1.4, 1.1)
EXCALIDRAW_COLOR_LEGEND_POS = (1100, 500)

MODULE_META = {
    ":androidApp": ("Android app", ""),
    ":desktopApp": ("Desktop app", ""),
    ":benchmark": ("Benchmark", ""),
    ":shared": ("Shared", ""),
    ":notes_ui": ("Notes UI", ""),
    ":auth_ui": ("Auth UI", ""),
    ":ui": ("UI core", ""),
    ":os": ("OS", ""),
    ":repo": ("Repo", ""),
    ":api": ("API", ""),
    ":local_db": (""),
    ":net": ("Net", ""),
    ":ext:services": (""),
    ":compose-rich-editor": ("Rich Editor", ""),
}

EDGE_STYLE = {
    "main": "solid",
    "android": "dashed",
    "androidTest": "dotted",
    "baselineProfile": "dotted",
    "target": "dotted",
}

ACCESSOR_OVERRIDES = {
    "authUi": ":auth_ui",
    "composeRichEditor": ":compose-rich-editor",
    "ext.services": ":ext:services",
    "localDb": ":local_db",
    "notesUi": ":notes_ui",
}

MODULE_POS = {
    ":androidApp": (160, 40),
    ":desktopApp": (500, 40),
    ":benchmark": (-160, 40),
    ":shared": (840, 40),
    ":notes_ui": (100, 220),
    ":auth_ui": (390, 220),
    ":ui": (690, 220),
    ":os": (970, 220),
    ":repo": (390, 400),
    ":api": (690, 400),
    ":compose-rich-editor": (-160, 400),
    ":local_db": (160, 580),
    ":net": (500, 580),
    ":ext:services": (840, 580),
}

COLOR = {
    "app": "#e7f5ff",
    "feature": "#ebfbee",
    "core": "#fff4e6",
    "data": "#f8f0fc",
    "external": "#f1f3f5",
}

CATEGORY = {
    ":androidApp": "app",
    ":desktopApp": "app",
    ":benchmark": "app",
    ":shared": "app",
    ":notes_ui": "feature",
    ":auth_ui": "feature",
    ":ui": "core",
    ":os": "core",
    ":api": "core",
    ":repo": "data",
    ":local_db": "data",
    ":net": "data",
    ":ext:services": "data",
    ":compose-rich-editor": "external",
}


def project_ref_to_module(ref: str) -> str:
    if ref.startswith("project("):
        return ref.split('"')[1]
    name = ref.removeprefix("projects.")
    if name in ACCESSOR_OVERRIDES:
        return ACCESSOR_OVERRIDES[name]
    if "." in name:
        return ":" + ":".join(ACCESSOR_OVERRIDES.get(part, part) for part in name.split("."))
    parts = re.findall(r"[A-Z]?[a-z0-9]+|[A-Z]+(?=[A-Z]|$)", name)
    if not parts:
        return ":" + name
    return ":" + "_".join(part.lower() for part in parts)


def extract_modules() -> list[str]:
    settings = (PROJECT / "settings.gradle.kts").read_text()
    modules = re.findall(r'include\("([^"]+)"\)', settings)
    return modules


def build_file_for(module: str) -> Path:
    if module == ":compose-rich-editor":
        return PROJECT / "ext" / "compose-rich-editor" / "richeditor-compose" / "build.gradle.kts"
    return PROJECT / module.removeprefix(":").replace(":", "/") / "build.gradle.kts"


def section_for(text: str, index: int) -> str:
    prefix = text[:index]
    matches = list(re.finditer(r"(?m)^\s*(commonMain|androidMain|jvmMain|androidTestImplementation|baselineProfile)\b", prefix))
    return matches[-1].group(1) if matches else "dependencies"


def extract_edges(modules: list[str]) -> list[tuple[str, str, str]]:
    edges = []
    known = set(modules)
    for module in modules:
        build = build_file_for(module)
        if not build.exists():
            continue
        text = build.read_text()
        for match in re.finditer(r"(projects\.[A-Za-z0-9.]+|project\(\"[^\"]+\"\))", text):
            target = project_ref_to_module(match.group(1))
            if target not in known:
                continue
            section = section_for(text, match.start())
            if section == "androidTestImplementation":
                kind = "androidTest"
            elif section == "baselineProfile":
                kind = "baselineProfile"
            elif section == "androidMain":
                kind = "android"
            else:
                kind = "main"
            edges.append((module, target, kind))
    # Benchmark's targetProjectPath is not a normal dependency, but it is part of the app graph.
    edges.append((":benchmark", ":androidApp", "target"))
    return sorted(set(edges))


def dot_id(module: str) -> str:
    return "m_" + re.sub(r"[^A-Za-z0-9_]", "_", module.strip(":"))


def module_meta(module: str) -> tuple[str, str]:
    meta = MODULE_META.get(module)
    if isinstance(meta, str):
        return meta or module.strip(":"), ""
    if isinstance(meta, (tuple, list)):
        if len(meta) == 1:
            return str(meta[0]), ""
        if len(meta) >= 2:
            return str(meta[0]), str(meta[1])
    return module.strip(":"), ""


def dot_escape(text: str) -> str:
    return text.replace("\\", "\\\\").replace('"', '\\"').replace("\n", "\\n")


def dot_html_escape(text: str) -> str:
    return (
        text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace('"', "&quot;")
    )


def write_dot(modules: list[str], edges: list[tuple[str, str, str]]) -> None:
    graph_label = dot_escape(f"{DIAGRAM_TITLE}\n{DOT_LEGEND_TEXT}")
    lines = [
        "digraph NotesModules {",
        f"  graph [layout=neato, outputorder=edgesfirst, splines=false, bgcolor=\"white\", pad=0.4, margin=0.1, labelloc=t, label=\"{graph_label}\", fontname=\"Arial\", fontsize=18];",
        "  node [shape=box, style=\"rounded,filled\", fontname=\"Arial\", fontsize=14, fixedsize=true, width=2.45, height=0.95, color=\"#343a40\", penwidth=1.4];",
        "  edge [fontname=\"Arial\", fontsize=10, color=\"#495057\", arrowsize=0.75, penwidth=1.3];",
    ]
    for module in modules:
        label, desc = module_meta(module)
        fill = COLOR[CATEGORY.get(module, "core")]
        x, y = MODULE_POS[module]
        pos = f'{x / 95:.2f},{8.0 - y / 95:.2f}!'
        lines.append(f"  {dot_id(module)} [label=<{label}<BR/><FONT POINT-SIZE=\"9\">{module}<BR/>{desc}</FONT>>, fillcolor=\"{fill}\", pos=\"{pos}\"];")
    color_rows = [
        f'    <TR><TD BGCOLOR="{COLOR[category]}" WIDTH="18"></TD><TD ALIGN="LEFT">{dot_html_escape(text)}</TD></TR>'
        for category, text in COLOR_LEGEND
    ]
    color_table = "<\n  <TABLE BORDER=\"1\" CELLBORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"5\" COLOR=\"#343a40\" BGCOLOR=\"#ffffff\">\n"
    color_table += f'    <TR><TD COLSPAN="2"><B>{dot_html_escape(COLOR_LEGEND_TITLE)}</B></TD></TR>\n'
    color_table += "\n".join(color_rows)
    color_table += "\n  </TABLE>\n>"
    legend_x, legend_y = DOT_COLOR_LEGEND_POS
    lines.append(f'  color_legend [shape=plain, fixedsize=false, margin=0, label={color_table}, pos="{legend_x},{legend_y}!"];')
    for src, dst, kind in edges:
        attrs = []
        style = EDGE_STYLE.get(kind, "solid")
        if style != "solid":
            attrs += [f'style="{style}"']
        suffix = " [" + ", ".join(attrs) + "]" if attrs else ""
        lines.append(f"  {dot_id(src)} -> {dot_id(dst)}{suffix};")
    lines.append("}")
    DOT_PATH.write_text("\n".join(lines) + "\n")


def text_element(
    element_id: str,
    x: float,
    y: float,
    text: str,
    size: int = 18,
    color: str = "#1e1e1e",
    align: str = "center",
) -> dict:
    lines = text.split("\n")
    return {
        "id": element_id,
        "type": "text",
        "x": x,
        "y": y,
        "width": max(80, max(len(line) for line in lines) * size * 0.58),
        "height": len(lines) * size * 1.35,
        "angle": 0,
        "strokeColor": color,
        "backgroundColor": "transparent",
        "fillStyle": "solid",
        "strokeWidth": 1,
        "strokeStyle": "solid",
        "roughness": 0,
        "opacity": 100,
        "groupIds": [],
        "frameId": None,
        "roundness": None,
        "seed": 1,
        "version": 1,
        "versionNonce": 1,
        "isDeleted": False,
        "boundElements": None,
        "updated": 1,
        "link": None,
        "locked": False,
        "text": text,
        "fontSize": size,
        "fontFamily": 1,
        "textAlign": align,
        "verticalAlign": "middle",
        "containerId": None,
        "originalText": text,
        "autoResize": True,
        "lineHeight": 1.25,
    }


def rect_element(element_id: str, x: float, y: float, w: float, h: float, fill: str) -> dict:
    return {
        "id": element_id,
        "type": "rectangle",
        "x": x,
        "y": y,
        "width": w,
        "height": h,
        "angle": 0,
        "strokeColor": "#343a40",
        "backgroundColor": fill,
        "fillStyle": "solid",
        "strokeWidth": 1,
        "strokeStyle": "solid",
        "roughness": 0,
        "opacity": 100,
        "groupIds": [],
        "frameId": None,
        "roundness": {"type": 3},
        "seed": 1,
        "version": 1,
        "versionNonce": 1,
        "isDeleted": False,
        "boundElements": None,
        "updated": 1,
        "link": None,
        "locked": False,
    }


def arrow_element(element_id: str, src: str, dst: str, kind: str) -> dict:
    sx, sy = MODULE_POS[src]
    dx, dy = MODULE_POS[dst]
    w, h = 220, 86
    start = (sx + w / 2, sy + h)
    end = (dx + w / 2, dy)
    if dy < sy:
        start = (sx + w / 2, sy)
        end = (dx + w / 2, dy + h)
    elif abs(dy - sy) < 10:
        start = (sx + w, sy + h / 2)
        end = (dx, dy + h / 2)
        if dx < sx:
            start = (sx, sy + h / 2)
            end = (dx + w, dy + h / 2)
    style = EDGE_STYLE.get(kind, "solid")
    return {
        "id": element_id,
        "type": "arrow",
        "x": start[0],
        "y": start[1],
        "width": end[0] - start[0],
        "height": end[1] - start[1],
        "angle": 0,
        "strokeColor": "#495057",
        "backgroundColor": "transparent",
        "fillStyle": "solid",
        "strokeWidth": 1,
        "strokeStyle": style,
        "roughness": 0,
        "opacity": 100,
        "groupIds": [],
        "frameId": None,
        "roundness": {"type": 2},
        "seed": 1,
        "version": 1,
        "versionNonce": 1,
        "isDeleted": False,
        "boundElements": None,
        "updated": 1,
        "link": None,
        "locked": False,
        "points": [[0, 0], [end[0] - start[0], end[1] - start[1]]],
        "lastCommittedPoint": None,
        "startBinding": None,
        "endBinding": None,
        "startArrowhead": None,
        "endArrowhead": "arrow",
    }


def write_excalidraw(modules: list[str], edges: list[tuple[str, str, str]]) -> None:
    elements = []
    elements.append(text_element("title", 268, -90, DIAGRAM_TITLE, 30))
    elements.append(text_element("legend", 450, -35, LEGEND_TEXT, 15, "#495057"))
    for module in modules:
        x, y = MODULE_POS[module]
        w, h = 220, 86
        label, desc = module_meta(module)
        elements.append(rect_element("rect_" + dot_id(module), x, y, w, h, COLOR[CATEGORY.get(module, "core")]))
        elements.append(text_element("txt_" + dot_id(module), x + 14, y + 14, f"{label}\n{module}\n{desc}", 14))
    for idx, (src, dst, kind) in enumerate(edges):
        elements.append(arrow_element(f"edge_{idx}", src, dst, kind))

    legend_x, legend_y = EXCALIDRAW_COLOR_LEGEND_POS
    elements.append(rect_element("color_legend_box", legend_x, legend_y, 285, 190, "#ffffff"))
    elements.append(text_element("color_legend_title", legend_x + 18, legend_y + 16, COLOR_LEGEND_TITLE, 16, "#1e1e1e", "left"))
    for index, (category, text) in enumerate(COLOR_LEGEND):
        row_y = legend_y + 50 + index * 26
        elements.append(rect_element(f"color_legend_swatch_{category}", legend_x + 18, row_y, 18, 18, COLOR[category]))
        elements.append(text_element(f"color_legend_text_{category}", legend_x + 48, row_y - 1, text, 13, "#343a40", "left"))

    doc = {
        "type": "excalidraw",
        "version": 2,
        "source": "https://excalidraw.com",
        "elements": elements,
        "appState": {"gridSize": None, "viewBackgroundColor": "#ffffff"},
        "files": {},
    }
    EXCALIDRAW_PATH.write_text(json.dumps(doc, indent=2) + "\n")


def main() -> None:
    modules = extract_modules()
    edges = extract_edges(modules)
    write_dot(modules, edges)
    write_excalidraw(modules, edges)
    subprocess.run(["neato", "-Tpng", str(DOT_PATH), "-o", str(PNG_PATH)], check=True)
    print("Modules:")
    for module in modules:
        print(f"  {module}")
    print("Edges:")
    for src, dst, kind in edges:
        print(f"  {src} -> {dst} ({kind})")
    print(f"Wrote {EXCALIDRAW_PATH}")
    print(f"Wrote {DOT_PATH}")
    print(f"Wrote {PNG_PATH}")


if __name__ == "__main__":
    main()
