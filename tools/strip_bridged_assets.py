#!/usr/bin/env python3
from __future__ import annotations

import argparse
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]
RESOURCES = REPO / "src/main/resources"
MANIFESTS = [
    RESOURCES / "assets/alphaver/asset-sidecar.properties",
    RESOURCES / "assets/alphaver/block-sidecar.properties",
    RESOURCES / "assets/alphaver/item-sidecar.properties",
    RESOURCES / "assets/alphaver/look-sidecar.properties",
]


def bridged_paths() -> list[str]:
    paths: list[str] = []
    for manifest in MANIFESTS:
        if not manifest.is_file():
            continue
        for line in manifest.read_text(encoding="utf-8").splitlines():
            line = line.strip()
            if not line or line.startswith(("#", "!")) or " = " not in line:
                continue
            _, _, value = line.partition(" = ")
            for path in value.split(","):
                path = path.strip()
                if path:
                    paths.append(path)
    return paths


def main() -> int:
    parser = argparse.ArgumentParser()
    mode = parser.add_mutually_exclusive_group()
    mode.add_argument("--check", action="store_true",
                      help="exit non-zero if any bridged file is present; change nothing")
    mode.add_argument("--dry-run", action="store_true", help="list what would be deleted")
    args = parser.parse_args()

    paths = bridged_paths()
    present = [p for p in paths if (RESOURCES / p).is_file()]

    if args.check:
        if present:
            print(f"FAIL: {len(present)} file(s) the sidecar is supposed to supply are in the tree.")
            print("These are the original's art and must not be committed or shipped:")
            for path in present[:40]:
                print(f"  {path}")
            if len(present) > 40:
                print(f"  ... and {len(present) - 40} more")
            print("\nRun: python tools/strip_bridged_assets.py")
            return 1
        print(f"OK: none of the {len(paths)} bridged files are in the tree.")
        return 0

    if not present:
        print(f"nothing to do -- none of the {len(paths)} bridged files are present.")
        return 0

    if args.dry_run:
        for path in present:
            print(f"would delete {path}")
        print(f"\n{len(present)} file(s)")
        return 0

    touched: set[Path] = set()
    for path in present:
        target = RESOURCES / path
        target.unlink()
        touched.add(target.parent)

    removed_dirs = 0
    for directory in sorted(touched, key=lambda d: len(d.parts), reverse=True):
        current = directory
        while current != RESOURCES and current.is_dir() and not any(current.iterdir()):
            current.rmdir()
            removed_dirs += 1
            current = current.parent

    print(f"deleted {len(present)} file(s)"
          + (f" and {removed_dirs} empty director{'y' if removed_dirs == 1 else 'ies'}" if removed_dirs else ""))
    return 0


if __name__ == "__main__":
    sys.exit(main())
