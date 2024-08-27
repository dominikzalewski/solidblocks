package de.solidblocks.infra.test.docker

import com.github.dockerjava.api.async.ResultCallback
import com.github.dockerjava.api.model.Frame
import com.github.dockerjava.api.model.StreamType
import de.solidblocks.infra.test.LogType
import de.solidblocks.infra.test.log
import de.solidblocks.infra.test.output.OutputLine
import de.solidblocks.infra.test.output.OutputType
import java.io.Closeable
import kotlin.time.TimeSource

abstract class BaseDockerResultCallback(
    private val start: TimeSource.Monotonic.ValueTimeMark,
    private val output: (entry: OutputLine) -> Unit,
) : ResultCallback<Frame> {

  override fun close() {}

  override fun onStart(closeable: Closeable) {}

  override fun onNext(frame: Frame) {
    val payload = frame.payload.decodeToString()
    log(
        start,
        payload,
        when (frame.streamType) {
          StreamType.STDOUT -> LogType.STDOUT
          StreamType.STDERR -> LogType.STDERR
          else -> {
            throw RuntimeException("unsupported docker log stream type: ${frame.streamType}")
          }
        },
    )

    payload
        .lines()
        .dropLastWhile { it.isEmpty() }
        .forEach {
          output.invoke(
              OutputLine(
                  TimeSource.Monotonic.markNow() - start,
                  it,
                  when (frame.streamType) {
                    StreamType.STDOUT -> OutputType.STDOUT
                    StreamType.STDERR -> OutputType.STDERR
                    else -> {
                      throw RuntimeException(
                          "unsupported docker log stream type: ${frame.streamType}")
                    }
                  },
              ),
          )
        }
  }
}
