import importlib.util
import os
from pathlib import Path
import stat
import subprocess
import tempfile
import unittest
from unittest.mock import patch
import xml.etree.ElementTree as ET


ROOT = Path(__file__).resolve().parents[3]
SPEC = importlib.util.spec_from_file_location(
    "configure_cfs", ROOT / "eng" / "scripts" / "configure_cfs.py"
)
CFS = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(CFS)
WRAPPER = ROOT / "azure-communication-ui" / (
    "gradlew.bat" if os.name == "nt" else "gradlew"
)
INIT_SCRIPT = ROOT / "eng" / "gradle" / "cfs.init.gradle"
NAMESPACE = "http://maven.apache.org/SETTINGS/1.0.0"


class ConfigureCfsTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.directory = Path(self.temp.name)
        self.settings = self.directory / "maven" / "settings.xml"
        self.gradle_home = self.directory / "gradle"

    def test_creates_mirror_and_installs_init_script(self):
        CFS.configure(self.gradle_home, self.settings)
        root = ET.parse(self.settings).getroot()
        mirror = root.find("mirrors/mirror")
        self.assertEqual(mirror.findtext("id"), CFS.FEED_ID)
        self.assertEqual(mirror.findtext("url"), CFS.FEED_URL)
        self.assertEqual(mirror.findtext("mirrorOf"), "central")
        self.assertEqual(
            (self.gradle_home / "init.d" / INIT_SCRIPT.name).read_bytes(),
            INIT_SCRIPT.read_bytes(),
        )
        self.assertIsNone(root.find("servers"))
        if os.name != "nt":
            self.assertEqual(stat.S_IMODE(self.settings.stat().st_mode), 0o600)

    def test_preserves_other_settings_and_replaces_stale_feed_credentials(self):
        self.settings.parent.mkdir()
        self.settings.write_text(
            '<settings xmlns="%s"><servers>'
            "<server><id>csc</id><password>existing-publishing-test-token</password></server>"
            "<server><id>SCC_PublicPackages</id><password>stale-test-token</password></server>"
            "</servers><mirrors><mirror><id>other</id><mirrorOf>central</mirrorOf>"
            "<url>https://example.invalid/maven</url></mirror></mirrors>"
            "<profiles><profile><id>existing</id></profile></profiles></settings>"
            % NAMESPACE,
            encoding="utf-8",
        )
        CFS.configure(self.gradle_home, self.settings)
        CFS.configure(self.gradle_home, self.settings)
        root = ET.parse(self.settings).getroot()
        ns = {"m": NAMESPACE}
        servers = root.findall("m:servers/m:server", ns)
        self.assertEqual([s.findtext("m:id", namespaces=ns) for s in servers], ["csc"])
        self.assertEqual(
            servers[0].findtext("m:password", namespaces=ns),
            "existing-publishing-test-token",
        )
        mirrors = root.findall("m:mirrors/m:mirror", ns)
        self.assertEqual(
            [m.findtext("m:id", namespaces=ns) for m in mirrors],
            [CFS.FEED_ID, "other"],
        )
        self.assertEqual(root.findtext("m:profiles/m:profile/m:id", namespaces=ns), "existing")

    def test_rejects_invalid_settings_without_overwriting_them(self):
        self.settings.parent.mkdir()
        for content in ("<settings", "<not-settings/>"):
            with self.subTest(content=content):
                self.settings.write_text(content, encoding="utf-8")
                with self.assertRaises((ET.ParseError, ValueError)):
                    CFS.configure(self.gradle_home, self.settings)
                self.assertEqual(self.settings.read_text(encoding="utf-8"), content)

    def test_rejects_fork_build_before_provisioning(self):
        with patch.dict(os.environ, {"TF_BUILD": "true", "SYSTEM_PULLREQUEST_ISFORK": "true"}):
            with patch("sys.argv", ["configure_cfs.py", "--gradle-home", str(self.gradle_home)]):
                with patch.object(CFS, "configure") as configure:
                    with self.assertRaisesRegex(RuntimeError, "fork PR"):
                        CFS.main()
                    configure.assert_not_called()

    def test_rejects_non_pipeline_invocation(self):
        with patch.dict(os.environ, {"TF_BUILD": "false"}):
            with patch("sys.argv", ["configure_cfs.py", "--gradle-home", str(self.gradle_home)]):
                with self.assertRaisesRegex(RuntimeError, "Azure Pipelines"):
                    CFS.main()

    def test_escapes_pipeline_variable_values(self):
        self.assertEqual(CFS.pipeline_value("a%b\r\nc"), "a%AZP25b%0D%0Ac")

    def test_setup_template_uses_the_pipeline_repository(self):
        for name in ("ci.yml", "release.yml"):
            with self.subTest(pipeline=name):
                pipeline = (ROOT / "eng" / "pipelines" / name).read_text(encoding="utf-8")
                self.assertRegex(
                    pipeline,
                    r"(?m)^\s+- template: /eng/pipelines/templates/cfs\.yml@self$",
                )

    def test_private_deployment_requires_successful_validation(self):
        pipeline = (ROOT / "eng" / "pipelines" / "release.yml").read_text(encoding="utf-8")
        self.assertRegex(
            pipeline,
            r"displayName: deploy to maven\n\s+"
            r"condition: and\(succeeded\(\), eq\(variables\['isPrivateRelease'\], true\)\)",
        )

    def test_validation_mode_guards_publishing_and_its_credentials(self):
        pipeline = (ROOT / "eng" / "pipelines" / "release.yml").read_text(encoding="utf-8")
        self.assertIn("name: validationOnly", pipeline)
        self.assertIn("  type: boolean\n  default: false", pipeline)
        guard = "        - ${{ if not(parameters.validationOnly) }}:\n"
        self.assertEqual(pipeline.count(guard), 1)
        guarded_steps = pipeline.split(guard, 1)[1]
        self.assertIn("          - task: MavenAuthenticate@0", guarded_steps)
        self.assertIn("              artifactsFeeds: csc", guarded_steps)
        self.assertIn("                mvn deploy:deploy-file", guarded_steps)
        self.assertNotIn("mvn deploy:deploy-file", pipeline.split(guard, 1)[0])


class GradleCfsTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.directory = Path(self.temp.name)
        self.settings = self.directory / "maven-settings.xml"
        self.env = os.environ.copy()
        self.env["CFS_MAVEN_SETTINGS"] = str(self.settings)
        if not self.env.get("GRADLE_USER_HOME"):
            raise RuntimeError("Set GRADLE_USER_HOME to an isolated validation cache")
        (self.directory / "settings.gradle").write_text(
            "pluginManagement { repositories { gradlePluginPortal(); google(); mavenCentral() } }\n"
            "dependencyResolutionManagement { repositories { mavenCentral() } }\n"
            "rootProject.name = 'cfs-configuration-test'\n",
            encoding="utf-8",
        )
        (self.directory / "build.gradle").write_text(
            """
buildscript { repositories { mavenCentral(); google() } }
plugins { id 'base' }
repositories {
    mavenCentral()
    google()
    maven { url 'https://plugins.gradle.org/m2/' }
    maven {
        name = 'preservedPrivateFeed'
        url 'https://pkgs.dev.azure.com/example/_packaging/unchanged/maven/v1'
    }
}
tasks.register('verifyCfs') {
    doLast {
        def feed = 'https://pkgs.dev.azure.com/skype/SCC/_packaging/SCC_PublicPackages/maven/v1'
        def settings = gradle.settings
        def rewritten = [
            buildscript.repositories,
            settings.pluginManagement.repositories,
            settings.dependencyResolutionManagement.repositories,
            repositories.findAll { it.name != 'preservedPrivateFeed' }
        ]
        rewritten.each { repos ->
            // Gradle also adds plugin wrappers; their underlying plugin repositories are checked above.
            def mavenRepos = repos.findAll {
                it instanceof org.gradle.api.artifacts.repositories.MavenArtifactRepository
            }
            assert !mavenRepos.isEmpty()
            mavenRepos.each { repo ->
                assert repo.url.toString() == feed
                assert repo.credentials.username == 'AzureDevOps'
                assert repo.credentials.password == 'local-test-placeholder'
                assert repo.authentication.findByName('basic') != null
            }
        }
        assert repositories.getByName('preservedPrivateFeed').url.toString() ==
            'https://pkgs.dev.azure.com/example/_packaging/unchanged/maven/v1'
        println 'CFS_REPOSITORIES_VERIFIED'
    }
}
""",
            encoding="utf-8",
        )

    def run_gradle(self):
        return subprocess.run(
            [
                str(WRAPPER),
                "--no-daemon",
                "--console=plain",
                "--init-script",
                str(INIT_SCRIPT),
                "-p",
                str(self.directory),
                "verifyCfs",
            ],
            env=self.env,
            cwd=ROOT,
            text=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            timeout=180,
        )

    def test_mirrors_dependency_buildscript_and_plugin_repositories(self):
        self.settings.write_text(
            '<settings xmlns="%s"><servers><server><id>SCC_PublicPackages</id>'
            "<username>AzureDevOps</username><password>local-test-placeholder</password>"
            "</server></servers></settings>" % NAMESPACE,
            encoding="utf-8",
        )
        result = self.run_gradle()
        self.assertEqual(result.returncode, 0, result.stdout)
        self.assertIn("CFS_REPOSITORIES_VERIFIED", result.stdout)
        self.assertNotIn("local-test-placeholder", result.stdout)

    def test_fails_closed_when_authentication_is_missing(self):
        self.settings.write_text("<settings/>", encoding="utf-8")
        result = self.run_gradle()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("Run MavenAuthenticate@0 for SCC_PublicPackages", result.stdout)

    def test_fails_closed_when_settings_file_is_missing(self):
        result = self.run_gradle()
        self.assertNotEqual(result.returncode, 0)
        self.assertIn("CFS_MAVEN_SETTINGS must reference", result.stdout)


if __name__ == "__main__":
    unittest.main()
