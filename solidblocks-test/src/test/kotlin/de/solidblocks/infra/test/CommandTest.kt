package de.solidblocks.infra.test

import de.solidblocks.infra.test.command.CommandBuilder
import de.solidblocks.infra.test.command.runtimeShouldBeGreaterThan
import de.solidblocks.infra.test.command.runtimeShouldBeLessThan
import de.solidblocks.infra.test.command.shouldHaveExitCode
import de.solidblocks.infra.test.docker.DockerTestImage
import de.solidblocks.infra.test.docker.testDocker
import de.solidblocks.infra.test.output.*
import de.solidblocks.infra.test.script.ScriptBuilder
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.FieldSource
import testLocal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommandTest {

  val logger = KotlinLogging.logger {}

  private fun getCommandPath(path: String) =
      Path(this.javaClass.classLoader.getResource(path)!!.path).absolutePathString()

  private val docker = createDockerClient()

  companion object {
    val contexts = listOf(testLocal(), testDocker(DockerTestImage.UBUNTU_24))
  }

  @BeforeEach
  @BeforeAll
  fun cleanDockerImages() {
    docker
        .listContainersCmd()
        .exec()
        .filter { container ->
          Constants.dockerTestimageLabels.all { container.labels[it.key] == it.value }
        }
        .forEach {
          logger.info { "cleaning up container '${it.id}'" }
          try {
            docker.killContainerCmd(it.id).exec()
          } catch (e: Exception) {}
        }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testFailure(context: TestContext<CommandBuilder, ScriptBuilder>) {
    assertSoftly(context.command(getCommandPath("command-failure.sh")).runResult()) {
      it shouldHaveExitCode 1
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testFailureBuiltinCommand(context: TestContext<CommandBuilder, ScriptBuilder>) {
    assertSoftly(context.command("test", "-f", "invalid file").runResult()) {
      it shouldHaveExitCode 1
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testSuccessBuiltinCommand(context: TestContext<CommandBuilder, ScriptBuilder>) {
    assertSoftly(context.command("test", "-f", "/etc/passwd").runResult()) {
      it shouldHaveExitCode 0
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testSuccessRunResult(context: TestContext<CommandBuilder, ScriptBuilder>) {
    assertSoftly(context.command(getCommandPath("command-success.sh")).runResult()) {
      it shouldHaveExitCode 0
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testSucessRunAndResult(context: TestContext<CommandBuilder, ScriptBuilder>) {
    runBlocking {
      val run = context.command(getCommandPath("command-success.sh")).run()

      assertSoftly(run.result()) { it shouldHaveExitCode 0 }
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testExitCodeAssertionNegative(context: TestContext<CommandBuilder, ScriptBuilder>) {
    shouldThrow<AssertionError> {
      assertSoftly(context.command(getCommandPath("command-failure.sh")).runResult()) {
        it shouldHaveExitCode 99
      }
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testSuccess(context: TestContext<CommandBuilder, ScriptBuilder>) {
    assertSoftly(context.command(getCommandPath("command-success.sh")).runResult()) {
      it shouldHaveExitCode 0
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testStdout(context: TestContext<CommandBuilder, ScriptBuilder>) {
    assertSoftly(context.command(getCommandPath("command-stdout.sh")).runResult()) {
      it shouldHaveExitCode 0
      it stdoutShouldBe "stdout line 1\nstdout line 2\nstdout line 3\n"
      it stderrShouldBe ""
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testStderr(context: TestContext<CommandBuilder, ScriptBuilder>) {
    assertSoftly(context.command(getCommandPath("command-stderr.sh")).runResult()) {
      it shouldHaveExitCode 0
      it stdoutShouldBe ""
      it stderrShouldBe "stderr line 1\nstderr line 2\nstderr line 3\n"
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testOutput(context: TestContext<CommandBuilder, ScriptBuilder>) {
    assertSoftly(
        context.command(getCommandPath("command-output.sh")).runResult(),
    ) {
      it shouldHaveExitCode 0

      it outputShouldBe
          "stderr line 1\nstdout line 1\nstderr line 2\nstdout line 2\nstderr line 3\nstdout line 3\n"
      it outputShouldMatch ".*stderr line 1.*"
      it outputShouldMatch ".*stderr line 2.*"
      it outputShouldMatch ".*stderr line 3.*"
      it outputShouldMatch ".*stdout line 1.*"
      it outputShouldMatch ".*stdout line 2.*"
      it outputShouldMatch ".*stdout line 3.*"

      it stderrShouldBe "stderr line 1\nstderr line 2\nstderr line 3\n"
      it stderrShouldMatch ".*stderr line 1.*"
      it stderrShouldMatch ".*stderr line 2.*"
      it stderrShouldMatch ".*stderr line 3.*"

      it stdoutShouldBe "stdout line 1\nstdout line 2\nstdout line 3\n"
      it stdoutShouldMatch ".*stdout line 1.*"
      it stdoutShouldMatch ".*stdout line 2.*"
      it stdoutShouldMatch ".*stdout line 3.*"
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testTimeoutExceeded(context: TestContext<CommandBuilder, ScriptBuilder>) {
    measureTime {
      assertSoftly(
          context.command(getCommandPath("command-timeout.sh")).timeout(2.seconds).runResult(),
      ) {
        it shouldHaveExitCode 137
        it runtimeShouldBeLessThan 4.seconds
        it runtimeShouldBeGreaterThan 1.seconds
      }
    } shouldBeLessThan 4.seconds
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testWaitForOutputRunResult(context: TestContext<CommandBuilder, ScriptBuilder>) {
    assertSoftly(
        context
            .command(getCommandPath("command-waitfor.sh"))
            .assert {
              it.waitForOutput(".*marker 1.*")
              it.waitForOutput(".*marker 2.*")
              it.waitForOutput(".*marker 3.*")
            }
            .runResult(),
    ) {
      it outputShouldMatch ".*marker 2.*"
      it shouldHaveExitCode 0
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testWaitForOutputRunResultNegative(context: TestContext<CommandBuilder, ScriptBuilder>) {
    val exception =
        shouldThrow<RuntimeException> {
          context
              .command(getCommandPath("command-waitfor.sh"))
              .assert { it.waitForOutput(".*marker 4.*", 5.seconds) }
              .runResult()
        }
    exception.message shouldBe ("timeout of 5s exceeded waiting for log line '.*marker 4.*'")
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testWaitForOutputRunAndResult(context: TestContext<CommandBuilder, ScriptBuilder>) {
    runBlocking {
      val run = context.command(getCommandPath("command-waitfor.sh")).run()

      run.waitForOutput(".*marker 1.*")
      run.waitForOutput(".*marker 2.*")
      run.waitForOutput(".*marker 3.*")

      assertSoftly(run.result()) { it shouldHaveExitCode 0 }
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testStdIn(context: TestContext<CommandBuilder, ScriptBuilder>) {
    assertSoftly(
        context
            .command(getCommandPath("command-stdin.sh"))
            .assert {
              it.waitForOutput(".*marker 1.*") { "a" }
              it.waitForOutput(".*marker 2.*") { "b" }
              it.waitForOutput(".*marker 3.*") { "c" }
            }
            .runResult(),
    ) {
      it shouldHaveExitCode 0
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  fun testWaitForOutputNotMatchedRunResult(context: TestContext<CommandBuilder, ScriptBuilder>) {
    runBlocking {
      val run = context.command(getCommandPath("command-waitfor.sh")).run()

      assertSoftly(run.result()) { it shouldHaveExitCode 0 }
    }
  }

  @ParameterizedTest
  @FieldSource("contexts")
  @Disabled("could leak CI info via env")
  fun testEnvironmentVariables(context: TestContext<CommandBuilder, ScriptBuilder>) {
    runBlocking {
      val run = context.command("env").env("ABC" to "DEF").run()

      assertSoftly(run.result()) {
        it shouldHaveExitCode 0
        it stdoutShouldMatch ".*ABC=DEF.*"
      }
    }
  }
}
