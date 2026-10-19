"""Configure job-local Gradle initialization and Maven Central's CFS mirror."""

import argparse
import os
from pathlib import Path
import shutil
import stat
import xml.etree.ElementTree as ET


FEED_ID = "SCC_PublicPackages"
FEED_URL = (
    "https://pkgs.dev.azure.com/skype/SCC/"
    "_packaging/SCC_PublicPackages/maven/v1"
)
INIT_SCRIPT = Path(__file__).resolve().parents[1] / "gradle" / "cfs.init.gradle"


def configure(gradle_home, settings_path):
    if settings_path.exists():
        tree = ET.parse(settings_path)
        root = tree.getroot()
    else:
        root = ET.Element("settings")
        tree = ET.ElementTree(root)

    namespace = root.tag.partition("}")[0][1:] if root.tag.startswith("{") else ""
    if namespace:
        ET.register_namespace("", namespace)

    def tag(name):
        return "{%s}%s" % (namespace, name) if namespace else name

    if root.tag != tag("settings"):
        raise ValueError("Expected a Maven settings.xml document")

    mirrors = root.find(tag("mirrors"))
    if mirrors is None:
        mirrors = ET.SubElement(root, tag("mirrors"))
    for mirror in list(mirrors):
        if mirror.findtext(tag("id")) == FEED_ID:
            mirrors.remove(mirror)
    mirror = ET.Element(tag("mirror"))
    for name, value in (
        ("id", FEED_ID),
        ("url", FEED_URL),
        ("mirrorOf", "central"),
    ):
        ET.SubElement(mirror, tag(name)).text = value
    mirrors.insert(0, mirror)

    # MavenAuthenticate preserves existing credentials; remove only our stale entry.
    servers = root.find(tag("servers"))
    if servers is not None:
        for server in list(servers):
            if server.findtext(tag("id")) == FEED_ID:
                servers.remove(server)

    settings_path.parent.mkdir(parents=True, exist_ok=True)
    settings_path.touch(mode=stat.S_IRUSR | stat.S_IWUSR, exist_ok=True)
    settings_path.chmod(stat.S_IRUSR | stat.S_IWUSR)
    tree.write(settings_path, encoding="utf-8", xml_declaration=True)

    init_directory = gradle_home / "init.d"
    init_directory.mkdir(parents=True, exist_ok=True)
    shutil.copyfile(INIT_SCRIPT, init_directory / INIT_SCRIPT.name)


def pipeline_value(value):
    return str(value).replace("%", "%AZP25").replace("\r", "%0D").replace("\n", "%0A")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--gradle-home", required=True, type=Path)
    parser.add_argument(
        "--maven-settings", type=Path, default=Path.home() / ".m2" / "settings.xml"
    )
    args = parser.parse_args()
    if os.environ.get("TF_BUILD", "").lower() != "true":
        raise RuntimeError("This setup is for Azure Pipelines jobs, not local user settings")
    if os.environ.get("SYSTEM_PULLREQUEST_ISFORK", "").lower() == "true":
        raise RuntimeError("Do not provision internal feed credentials for fork PR builds")

    configure(args.gradle_home, args.maven_settings)
    print(
        "##vso[task.setvariable variable=GRADLE_USER_HOME]%s"
        % pipeline_value(args.gradle_home)
    )
    print(
        "##vso[task.setvariable variable=CFS_MAVEN_SETTINGS]%s"
        % pipeline_value(args.maven_settings)
    )
    print("Configured CFS mirrors; MavenAuthenticate must supply the job credentials next.")


if __name__ == "__main__":
    main()
