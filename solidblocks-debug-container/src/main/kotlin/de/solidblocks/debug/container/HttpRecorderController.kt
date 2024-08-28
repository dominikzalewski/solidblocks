package de.solidblocks.debug.container

import jakarta.servlet.http.HttpServletRequest
import java.time.Instant
import java.util.UUID
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class HttpRecorderController {

  data class Request(
      var id: UUID,
      var timestamp: Instant,
      var uri: String,
      var method: String,
      var headers: Map<String, String>,
      var remoteHost: String,
      var remotePort: Int,
      var scheme: String,
      var body: String?,
  )

  val requests = ArrayList<Request>()

  @RequestMapping("/http-recorder/requests", produces = [MediaType.TEXT_HTML_VALUE])
  fun requests(model: Model): String {
    model.addAttribute("requests", requests)
    return "http-recorder-requests"
  }

  @RequestMapping("/http-recorder", produces = [MediaType.TEXT_HTML_VALUE])
  fun recorder(): String = "http-recorder"

  @RequestMapping(value = ["*"])
  fun allRequests(request: HttpServletRequest): ResponseEntity<String> {
    val headers =
        request.headerNames.toList().associateWith {
          request.getHeaders(it).toList().joinToString(", ")
        }

    requests.add(
        Request(
            UUID.randomUUID(),
            Instant.now(),
            request.requestURI,
            request.method,
            headers,
            request.remoteHost,
            request.remotePort,
            request.scheme,
            request.reader.readText(),
        ),
    )
    return ResponseEntity.ok().build()
  }
}
