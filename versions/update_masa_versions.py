#!/usr/bin/env python3
import re
import sys
from pathlib import Path
from urllib.request import urlopen


TARGET_KEYS = {
    "malilib_version": "malilib",
    "tweakeroo_version": "tweakeroo",
    "litematica_version": "litematica",
    "itemscroller_version": "itemscroller",
    "minihud_version": "minihud",
}
TARGET_VERSION_PREFIXES = ("1.21", "26")

BASE_URL = "https://masa.dy.fi/maven/sakura-ryoko/fi/dy/masa"
LINE_RE = re.compile(r"^(\s*)([a-z_]+_version)(\s*=\s*)(.+?)(\s*)$")
FI_DY_RE = re.compile(r"^fi\.dy\.masa\.[^:]+:[^:]+-fabric-([^:]+):(.+)$")
JITPACK_RE = re.compile(r"^com\.github\.sakura-ryoko:[^:]+:([0-9]+(?:\.[0-9]+){1,2})-.+$")
LATEST_RE = re.compile(r"<latest>([^<]+)</latest>")
RELEASE_RE = re.compile(r"<release>([^<]+)</release>")


def read_url_text(url: str) -> str:
    with urlopen(url, timeout=20) as resp:
        return resp.read().decode("utf-8")


def parse_mc_tag(current_value: str) -> str:
    match = FI_DY_RE.match(current_value)
    if match:
        return match.group(1)

    match = JITPACK_RE.match(current_value)
    if match:
        return match.group(1)

    raise ValueError(f"Cannot parse mc tag from value: {current_value}")


def fetch_latest_version(module: str, mc_tag: str, cache: dict) -> str:
    cache_key = (module, mc_tag)
    if cache_key in cache:
        return cache[cache_key]

    artifact = f"{module}-fabric-{mc_tag}"
    url = f"{BASE_URL}/{module}/{artifact}/maven-metadata.xml"
    xml = read_url_text(url)

    match = LATEST_RE.search(xml) or RELEASE_RE.search(xml)
    if not match:
        raise ValueError(f"Cannot find latest/release in metadata: {url}")

    latest = match.group(1).strip()
    cache[cache_key] = latest
    return latest


def build_new_coordinate(module: str, mc_tag: str, version: str) -> str:
    return f"fi.dy.masa.{module}:{module}-fabric-{mc_tag}:{version}"


def update_gradle_properties(path: Path, cache: dict) -> tuple[int, int]:
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines(keepends=False)
    total_seen = 0
    total_changed = 0

    for i, line in enumerate(lines):
        match = LINE_RE.match(line)
        if not match:
            continue

        indent, key, eq_part, current_value, tail_ws = match.groups()
        module = TARGET_KEYS.get(key)
        if not module:
            continue

        total_seen += 1
        mc_tag = parse_mc_tag(current_value.strip())
        latest = fetch_latest_version(module, mc_tag, cache)
        new_value = build_new_coordinate(module, mc_tag, latest)
        new_line = f"{indent}{key}{eq_part}{new_value}{tail_ws}"

        if new_line != line:
            lines[i] = new_line
            total_changed += 1

    if total_changed > 0:
        path.write_text("\n".join(lines) + "\n", encoding="utf-8")

    return total_seen, total_changed


def iter_target_files(root: Path):
    versions_dir = root / "versions"
    for child in sorted(versions_dir.iterdir()):
        if not child.is_dir():
            continue
        name = child.name
        if any(name.startswith(prefix) for prefix in TARGET_VERSION_PREFIXES):
            gradle_file = child / "gradle.properties"
            if gradle_file.exists():
                yield gradle_file


def main() -> int:
    root = Path(__file__).resolve().parent.parent
    cache = {}
    any_file = False
    total_seen = 0
    total_changed = 0

    for gradle_file in iter_target_files(root):
        any_file = True
        seen, changed = update_gradle_properties(gradle_file, cache)
        total_seen += seen
        total_changed += changed
        print(f"[{gradle_file.parent.name}] seen={seen} changed={changed}")

    if not any_file:
        print("No target gradle.properties files found under versions/", file=sys.stderr)
        return 1

    print(f"Done. total_seen={total_seen} total_changed={total_changed}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
