import de.solidblocks.infra.test.TestContext
import de.solidblocks.infra.test.command.CommandBuilder
import de.solidblocks.infra.test.command.CommandRunner
import de.solidblocks.infra.test.command.ProcessResult
import de.solidblocks.infra.test.local.LocalScriptBuilder
import de.solidblocks.infra.test.log
import de.solidblocks.infra.test.output.OutputLine
import de.solidblocks.infra.test.output.OutputType
import de.solidblocks.infra.test.script.ScriptBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.io.Closeable
import java.lang.Thread.sleep
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import kotlin.time.TimeSource

class LocalCommandBuilder(command: Array<String>) : CommandBuilder(command) {

    private val runners = mutableListOf<CommandRunner>()

    private fun killProcessAndWait(process: Process) {
        sleep(100)

        Runtime.getRuntime().exec("kill -SIGINT ${process.pid()}");
        process.descendants().forEach { it.destroyForcibly() }

        while (process.isAlive) {
            sleep(10)
        }
    }

    override suspend fun createCommandRunner(start: TimeSource.Monotonic.ValueTimeMark) =
        object : CommandRunner {
            override suspend fun runCommand(
                command: Array<String>,
                envs: Map<String, String>,
                stdin: Channel<String>,
                output: (entry: OutputLine) -> Unit
            ) = withContext(Dispatchers.IO) {
                async {
                    val processBuilder = ProcessBuilder(command.toList())
                    processBuilder.environment().putAll(envs)
                    workingDir?.toFile().let {
                        processBuilder.directory(it)
                    }

                    val process = processBuilder.start()
                    val stdinWriter = process.outputWriter()

                    log(start - start, "starting command '${command.joinToString(" ")}'")

                    launch {
                        while (process.isAlive && !stdin.isClosedForReceive) {
                            yield()
                            val result = stdin.tryReceive()
                            if (result.isSuccess) {
                                result.getOrNull()?.let {
                                    stdinWriter.write(it)
                                    stdinWriter.newLine()
                                    stdinWriter.flush()
                                }
                            }
                        }
                    }

                    try {
                        launch {
                            process.inputStream.bufferedReader(Charset.defaultCharset()).lineSequence().asFlow().flowOn(
                                Dispatchers.IO
                            )
                                .collect {
                                    val timestamp = TimeSource.Monotonic.markNow() - start
                                    val entry = OutputLine(timestamp, it, OutputType.stdout)
                                    output.invoke(entry)
                                }
                        }

                        launch {
                            process.errorStream.bufferedReader(Charset.defaultCharset()).lineSequence().asFlow().flowOn(
                                Dispatchers.IO
                            )
                                .collect {
                                    val timestamp = TimeSource.Monotonic.markNow() - start
                                    val entry = OutputLine(timestamp, it, OutputType.stderr)
                                    output.invoke(entry)
                                }
                        }

                        if (!process.waitFor(timeout.inWholeSeconds, TimeUnit.SECONDS)) {
                            log(start, "timeout for command exceeded (${timeout})")
                        }

                        /*
                        if (!stdin.isClosedForSend) {
                            stdin.cancel()
                        }
                        */

                        val end = TimeSource.Monotonic.markNow()
                        killProcessAndWait(process)

                        ProcessResult(
                            exitCode = process.exitValue(),
                            runtime = end.minus(start)
                        )
                    } finally {
                        killProcessAndWait(process)
                    }
                }
            }

            override fun close() {
            }

        }

    override fun close() {
        runners.forEach { it.close() }
    }
}

class LocalTestContext : TestContext<LocalCommandBuilder, LocalScriptBuilder> {

    private val resources = mutableListOf<Closeable>()

    override fun command(vararg command: String) = LocalCommandBuilder(command.toList().toTypedArray()).apply {
        resources.add(this)
    }

    override fun script() = LocalScriptBuilder().apply {
        resources.add(this)
    }

    override fun toString(): String {
        return "LocalTestContext()"
    }

    override fun close() {
        resources.forEach { it.close() }
    }
}


fun testLocal() = LocalTestContext()


/*
suspend fun executeShellCommand(
    command: String,
    vararg args: String
): CommandResult =
    executeCommand(
        Path.of("/bin/sh"),
        "-c",
        (listOf(command) + args)
            .joinToString(" ")
    )
*/