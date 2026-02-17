package it.vandenende.reactive

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.function.Consumer
import java.time.Duration
import tools.jackson.databind.ObjectMapper

data class TempReading(val sensor_id: Int, val temp: Double)
data class SensorData(
	val sensorId: Int,
	val temp: Double
)

@SpringBootApplication
class ReactiveProcessorApplication {

	@Bean
	fun processTemperature(): Consumer<Flux<TempReading>> {
		return Consumer { input ->
			input
				.window(Duration.ofSeconds(5))
				.flatMap { window ->
					window.collectList().map { list ->
						val avg = if (list.isNotEmpty()) list.map { it.temp }.average() else 0.0
						"📊 [STATISTIEK] Gemiddelde over 5 sec: ${"%.2f".format(avg)}°C (Aantal metingen: ${list.size})"
					}
				}
				.subscribe { println(it) }
		}
	}
}

fun main(args: Array<String>) {
	runApplication<ReactiveProcessorApplication>(*args)
}

@Service
class TemperatureProcessor(
	private val template: KafkaTemplate<String, String>,
	private val objectMapper: ObjectMapper // Laat Spring deze injecteren
) {
	private val logger = LoggerFactory.getLogger(javaClass)

	@KafkaListener(topics = ["iot-temp"], groupId = "processor-group")
	fun process(messages: Flux<String>) {
		messages
			.map { json ->
				objectMapper.readValue(json, SensorData::class.java)
			}
			.window(Duration.ofSeconds(1))
			.flatMap { windowFlux ->
				windowFlux.collectList().map { list -> calculateAverage(list) }
			}
			.filter { it != null }
			.subscribe { aggregated ->
				template.send("dashboard-data", objectMapper.writeValueAsString(aggregated))

				if (aggregated!!.temp > 25.0) {
					logger.warn("🔥 Kritieke hitte gedetecteerd: ${aggregated.temp}")
				}
			}
	}

	private fun calculateAverage(list: List<SensorData>): SensorData? {
		if (list.isEmpty()) return null
		val avgTemp = list.map { it.temp }.average()
		return SensorData(sensorId = list.first().sensorId, temp = avgTemp)
	}
}

